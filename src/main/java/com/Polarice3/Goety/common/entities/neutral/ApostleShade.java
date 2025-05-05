package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.common.effects.brew.BrewEffectInstance;
import com.Polarice3.Goety.common.entities.hostile.cultists.SpellCastingCultist;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BrewUtils;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class ApostleShade extends SpellCastingCultist {
    protected static final EntityDataAccessor<String> SHADE_MOOD = SynchedEntityData.defineId(ApostleShade.class, EntityDataSerializers.STRING);
    public static String PLEASED_MOOD = "Pleased";
    public static String IDLE_MOOD = "Idle";
    public static String MIFFED_MOOD = "Miffed";
    public UUID dealingPlayer;
    public int castingTime;
    public int checkTime;
    public int moodTime;

    public ApostleShade(EntityType<? extends SpellCastingCultist> type, Level p_i48551_2_) {
        super(type, p_i48551_2_);
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 3.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SHADE_MOOD, "Idle");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putUUID("DealingPlayer", this.dealingPlayer);
        compound.putInt("CheckTime", this.checkTime);
        compound.putInt("MoodTime", this.moodTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("DealingPlayer")){
            this.dealingPlayer = compound.getUUID("DealingPlayer");
        }
        if (compound.contains("CheckTime")){
            this.setCheckTime(compound.getInt("CheckTime"));
        }
        if (compound.contains("MoodTime")){
            this.setMoodTime(compound.getInt("MoodTime"));
        }
    }

    public boolean isPleased(){
        return this.entityData.get(SHADE_MOOD).equals(PLEASED_MOOD);
    }

    public boolean isIdle(){
        return this.entityData.get(SHADE_MOOD).equals(IDLE_MOOD);
    }

    public boolean isMiffed(){
        return this.entityData.get(SHADE_MOOD).equals(MIFFED_MOOD);
    }

    public void setMood(String mood){
        this.entityData.set(SHADE_MOOD, mood);
    }

    @Override
    public boolean canJoinRaid() {
        return false;
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return ModSounds.APOSTLE_CAST_SPELL.get();
    }

    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Player){
            return super.hurt(source, 1);
        }
        return source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    public void die(DamageSource cause) {
        if (!this.level.isClientSide) {
            for (int i = 0; i < this.level.random.nextInt(35) + 10; ++i) {
                ServerParticleUtil.smokeParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getEyeY(), this.getZ(), this.level);
            }
        }
        this.discard();
    }

    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_, DamageSource damageSource) {
        return false;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance p_21197_) {
        return false;
    }

    public CultistArmPose getArmPose() {
        if (this.isSpellcasting()){
            return CultistArmPose.SPELLCASTING;
        } else if (this.isChecking()){
            return CultistArmPose.ITEM;
        } else {
            return CultistArmPose.CROSSED;
        }
    }

    protected boolean canRide(Entity pEntity) {
        return false;
    }

    public Player getDealingPlayer(){
        if (this.dealingPlayer != null){
            return this.level.getPlayerByUUID(this.dealingPlayer);
        }
        return null;
    }

    public void setDealingPlayer(Player player){
        this.dealingPlayer = player.getUUID();
    }

    public boolean isCoolDown(){
        return this.moodTime > 0;
    }

    public int getMoodTime() {
        return this.moodTime;
    }

    public void setMoodTime(int moodTime){
        this.moodTime = moodTime;
    }

    public boolean isChecking(){
        return this.checkTime > 0;
    }

    public int getCheckTime() {
        return this.checkTime;
    }

    public void setCheckTime(int checkTime){
        this.checkTime = checkTime;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.setDeltaMovement(Vec3.ZERO);
        if (this.castingTime > 0){
            --this.castingTime;
        }
        if (this.getCheckTime() > 0){
            --this.checkTime;
        }
        if (this.getMoodTime() > 0){
            --this.moodTime;
        }
        if (this.level.isClientSide) {
            for(int i = 0; i < 2; ++i) {
                this.level.addParticle(ParticleTypes.LARGE_SMOKE, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
            }
        }
        ItemStack itemStack = this.getMainHandItem();
        if (this.isChecking()){
            if (BrewUtils.isEmpty(itemStack)){
                this.setCheckTime(0);
                this.setMood(MIFFED_MOOD);
            }
        } else {
            if (!itemStack.isEmpty()){
                int i = 0;
                for (BrewEffectInstance instance : BrewUtils.getBrewEffects(itemStack)){
                    ++i;
                    if (instance.getAmplifier() > 0){
                        i += instance.getAmplifier();
                    }

                    if (instance.getDuration() > 20){
                        i += Mth.floor(instance.getDuration() / 20.0F);
                    }
                }
                for (MobEffectInstance instance : PotionUtils.getMobEffects(itemStack)){
                    ++i;
                    if (instance.getAmplifier() > 0){
                        i += instance.getAmplifier();
                    }

                    if (instance.getDuration() > 20){
                        i += Mth.floor(instance.getDuration() / 20.0F);
                    }
                }
                i += BrewUtils.getAreaOfEffect(itemStack);
                i += (int) BrewUtils.getLingering(itemStack);
                i += BrewUtils.getQuaffLevel(itemStack);
                i += BrewUtils.getVelocityLevel(itemStack);
                if (BrewUtils.getAquatic(itemStack)){
                    i += 2;
                }
                if (BrewUtils.getFireProof(itemStack)){
                    i += 10;
                }
                if (BrewUtils.isLingering(itemStack)){
                    i += 5;
                }
                if (BrewUtils.isGas(itemStack)){
                    i += 10;
                }
                if (i >= this.getRandom().nextIntBetweenInclusive(10, 20)){
                    this.setMood(PLEASED_MOOD);
                    this.setMoodTime(MathHelper.minecraftDayToTicks(3));
                } else {
                    this.setMood(MIFFED_MOOD);
                    this.setMoodTime(MathHelper.minecraftDayToTicks(1));
                }
            }
        }
        if (this.getMoodTime() > 0){
            --this.moodTime;
        } else if (!this.isIdle()){
            this.setMood(IDLE_MOOD);
        }
    }

    @Override
    protected InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Item item = itemstack.getItem();
        if (this.isIdle() && !this.isChecking()){
            if (!BrewUtils.isEmpty(itemstack)){
                this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.split(1));
                this.setCheckTime(MathHelper.secondsToTicks(5));
                this.setDealingPlayer(pPlayer);
            } else {
                return InteractionResult.FAIL;
            }
        } else if (this.isPleased()){
            if (this.getDealingPlayer() == pPlayer){

            } else {
                return InteractionResult.FAIL;
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }
}
