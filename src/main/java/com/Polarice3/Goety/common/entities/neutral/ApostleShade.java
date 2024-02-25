package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.common.entities.hostile.cultists.SpellCastingCultist;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class ApostleShade extends SpellCastingCultist {
    private static final EntityDataAccessor<Float> SPIN = SynchedEntityData.defineId(ApostleShade.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Byte> SHADE_FLAGS = SynchedEntityData.defineId(ApostleShade.class, EntityDataSerializers.BYTE);
    public UUID dealingPlayer;
    public int castingTime;
    public int checkTime;
    public int coolTime;

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
        this.entityData.define(SHADE_FLAGS, (byte)0);
        this.entityData.define(SPIN, 0.0F);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putUUID("DealingPlayer", this.dealingPlayer);
        compound.putInt("CheckTime", this.checkTime);
        compound.putInt("CoolTime", this.coolTime);
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
        if (compound.contains("CoolTime")){
            this.setCoolTime(compound.getInt("CoolTime"));
        }
    }

    private boolean getShadeFlag(int mask) {
        int i = this.entityData.get(SHADE_FLAGS);
        return (i & mask) != 0;
    }

    private void setShadeFlag(int mask, boolean value) {
        int i = this.entityData.get(SHADE_FLAGS);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(SHADE_FLAGS, (byte)(i & 255));
    }

    public boolean isPleased(){
        return this.getShadeFlag(1);
    }

    public boolean isIdle(){
        return this.getShadeFlag(2);
    }

    public boolean isMiffed(){
        return this.getShadeFlag(4);
    }

    public void setMood(int mood){
        if (mood == 0){
            this.setShadeFlag(1, true);
            this.setShadeFlag(2, false);
            this.setShadeFlag(4, false);
        } else if (mood == 2){
            this.setShadeFlag(1, false);
            this.setShadeFlag(2, false);
            this.setShadeFlag(4, true);
        } else {
            this.setShadeFlag(1, false);
            this.setShadeFlag(2, true);
            this.setShadeFlag(4, false);
        }
    }

    public void setSpin(float spin){
        this.entityData.set(SPIN, spin);
    }

    public float getSpin(){
        return this.entityData.get(SPIN);
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
        return this.coolTime > 0;
    }

    public int getCoolTime() {
        return this.coolTime;
    }

    public void setCoolTime(int coolTime){
        this.coolTime = coolTime;
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
        if (this.checkTime > 0){
            --this.checkTime;
        }
        if (this.coolTime > 0){
            --this.coolTime;
        }
        if (this.level.isClientSide) {
            if (this.isAlive()) {
                if (this.getSpin() < 3.14F) {
                    this.setSpin(this.getSpin() + 0.01F);
                } else {
                    this.setSpin(-3.14F);
                }
            }
            for(int i = 0; i < 2; ++i) {
                this.level.addParticle(ParticleTypes.LARGE_SMOKE, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
