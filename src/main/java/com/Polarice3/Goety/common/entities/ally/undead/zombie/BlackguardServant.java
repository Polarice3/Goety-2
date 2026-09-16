package com.Polarice3.Goety.common.entities.ally.undead.zombie;

import com.Polarice3.Goety.api.entities.IShielded;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.ShieldedMeleeGoal;
import com.Polarice3.Goety.common.entities.ai.ShieldedPreMeleeGoal;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class BlackguardServant extends ZombieServant implements IShielded {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(BlackguardServant.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> HAS_SHIELD = SynchedEntityData.defineId(BlackguardServant.class, EntityDataSerializers.BOOLEAN);
    public int attackTick;
    public int shieldHealth = AttributesConfig.BlackguardServantShield.get();
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState standAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();

    public BlackguardServant(EntityType<? extends Summoned> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public void attackGoal() {
        this.goalSelector.addGoal(1, new ShieldedMeleeGoal<>(this) {
            public void playPreAttack() {
                this.mob.playSound(ModSounds.BLACKGUARD_PRE_ATTACK.get(), this.mob.getSoundVolume() + 1.0F, this.mob.getVoicePitch());
            }

            public void playSmash() {
                this.mob.playSound(ModSounds.BLACKGUARD_SMASH.get(), this.mob.getSoundVolume() + 1.0F, this.mob.getVoicePitch());
            }
        });
        this.goalSelector.addGoal(4, new ShieldedPreMeleeGoal<>(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.BlackguardServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23F)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.BlackguardServantDamage.get())
                .add(Attributes.ATTACK_KNOCKBACK, 1.5F)
                .add(Attributes.ARMOR, AttributesConfig.BlackguardServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.BlackguardServantToughness.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.BlackguardServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.BlackguardServantDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.BlackguardServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS), AttributesConfig.BlackguardServantToughness.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_SHIELD, true);
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.readShieldedData(pCompound);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        this.addShieldedData(pCompound);
    }

    @Override
    public Predicate<Entity> summonPredicate() {
        return entity -> entity instanceof BlackguardServant;
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return SpellConfig.BlackguardLimit.get();
    }

    private boolean getFlag(int mask) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & mask) != 0;
    }

    private void setFlags(int mask, boolean value) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(DATA_FLAGS_ID, (byte)(i & 255));
    }

    public boolean hasShield(){
        return this.entityData.get(HAS_SHIELD);
    }

    public void setShield(boolean shield){
        this.entityData.set(HAS_SHIELD, shield);
    }

    public int getShieldHealth(){
        return this.shieldHealth;
    }

    public void setShieldHealth(int shieldHealth){
        this.shieldHealth = shieldHealth;
    }

    @Override
    public int getMaxShieldHealth() {
        return AttributesConfig.BlackguardServantShield.get();
    }

    public boolean isMeleeAttacking() {
        return this.getFlag(1);
    }

    public void setMeleeAttacking(boolean attacking) {
        this.setFlags(1, attacking);
        this.attackTick = 0;
        this.level.broadcastEntityEvent(this, (byte) 5);
    }

    @Override
    public int getAttackTick() {
        return this.attackTick;
    }

    public void setAttackTick(int attackTick) {
        this.attackTick = attackTick;
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    protected boolean convertsInWater() {
        return false;
    }

    public EntityType<?> getVariant(Level level, BlockPos blockPos){
        return ModEntityType.BLACKGUARD_SERVANT.get();
    }

    @Override
    public boolean canWearArmor() {
        return false;
    }

    @Override
    public boolean canHaveWeapon() {
        return false;
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficulty) {
    }

    public List<AnimationState> getAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.standAnimationState);
        animationStates.add(this.attackAnimationState);
        return animationStates;
    }

    public void stopAllAnimations(){
        for (AnimationState animationState : this.getAnimations()){
            animationState.stop();
        }
    }

    @Override
    public void die(DamageSource pCause) {
        this.playSound(ModSounds.DOUBLE_AXE_IMPACT_SHING.get(), 1.5F * 0.5F * (this.getRandom().nextIntBetweenInclusive(5, 10) * 0.1F), (this.getRandom().nextBoolean() ? 0.65F : 0.6F) * (this.getRandom().nextIntBetweenInclusive(5, 7) * 0.1F));
        this.playSound(ModSounds.DIRT_SHATTER_THREE.get(), 1.5F * 0.25F * (this.getRandom().nextIntBetweenInclusive(7, 10) * 0.1F), (this.getRandom().nextBoolean() ? 0.65F : 0.6F) * (this.getRandom().nextIntBetweenInclusive(7, 10) * 0.1F));
        this.playSound(ModSounds.HAMMER_SHIMMER_IMPACT_FOUR.get(), 1.5F * 0.4F * (this.getRandom().nextIntBetweenInclusive(5, 10) * 0.1F), (this.getRandom().nextBoolean() ? 0.65F : 0.6F) * (this.getRandom().nextIntBetweenInclusive(5, 7) * 0.1F));
        this.playSound(ModSounds.PLATE_DROP.get(), this.getSoundVolume(), this.getVoicePitch());
        super.die(pCause);
    }

    protected void playHurtSound(DamageSource p_21160_) {
        SoundEvent soundevent = this.getHurtSound(p_21160_);
        if (soundevent != null) {
            this.playSound(soundevent, 0.8F, this.getRandom().nextBoolean() ? 0.9F : 0.8F);
        }
        this.ambientSoundTime = -this.getAmbientSoundInterval();
        this.playSound(ModSounds.DOUBLE_AXE_IMPACT_SHING.get(), 0.8F * 0.5F, this.getRandom().nextIntBetweenInclusive(5, 7) * 0.1F);
        this.playSound(ModSounds.DIRT_SHATTER_THREE.get(), 0.8F * 0.25F * (this.getRandom().nextIntBetweenInclusive(7, 10) * 0.1F), this.getRandom().nextIntBetweenInclusive(7, 10) * 0.1F);
        this.playSound(ModSounds.HAMMER_SHIMMER_IMPACT_FOUR.get(), 0.8F * 0.4F * (this.getRandom().nextIntBetweenInclusive(5, 10) * 0.1F), this.getRandom().nextIntBetweenInclusive(5, 7) * 0.1F);
        this.playSound(ModSounds.PLATE.get(), this.getSoundVolume(), this.getVoicePitch());
    }

    protected SoundEvent getStepSound() {
        return ModSounds.BLACKGUARD_STEP.get();
    }

    @Override
    public void setBaby(boolean pChildZombie) {
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    public void tick() {
        super.tick();
        if (this.level.isClientSide){
            if (this.isAlive()){
                this.idleAnimationState.animateWhen(!this.isMeleeAttacking() && !this.isStaying() && !this.isMoving(), this.tickCount);
                this.standAnimationState.animateWhen(!this.isMeleeAttacking() && this.isStaying() && !this.isMoving(), this.tickCount);
                if (!this.isMeleeAttacking()) {
                    this.attackAnimationState.stop();
                }
            }
        }
        if (this.isMeleeAttacking()) {
            ++this.attackTick;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return this.hurtShielded(source, amount, () -> super.hurt(source, amount));
    }

    @Override
    public void knockback(double p_147241_, double p_147242_, double p_147243_) {
        if (!this.hasShield()) {
            super.knockback(p_147241_, p_147242_, p_147243_);
        }
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 4 || p_21375_ == 5 || p_21375_ == 6){
            this.handleShieldedEvent(p_21375_);
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    @Override
    public void animateAttack() {
        this.stopAllAnimations();
        this.attackAnimationState.start(this.tickCount);
    }

    protected double getAttackReachSqr(LivingEntity enemy) {
        return this.getShieldedAttackReachSqr(enemy);
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Item item = itemstack.getItem();
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (item == Items.ROTTEN_FLESH && this.getHealth() < this.getMaxHealth()) {
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                this.heal(2.0F);
                if (this.level instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        serverLevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                    }
                }
                return InteractionResult.SUCCESS;
            }
            if (!this.hasShield() && this.isShieldRepair(itemstack) && this.getTarget() == null && this.hurtTime <= 0) {
                return this.repairShield(pPlayer, itemstack);
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }
}
