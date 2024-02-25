package com.Polarice3.Goety.common.entities.ally.undead.skeleton;

import com.Polarice3.Goety.common.entities.ModEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SkeletonServant extends AbstractSkeletonServant {
    private static final EntityDataAccessor<Boolean> DATA_STRAY_CONVERSION_ID = SynchedEntityData.defineId(SkeletonServant.class, EntityDataSerializers.BOOLEAN);
    private int inPowderSnowTime;
    private int conversionTime;

    public SkeletonServant(EntityType<? extends AbstractSkeletonServant> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_STRAY_CONVERSION_ID, false);
    }

    public boolean isFreezeConverting() {
        return this.getEntityData().get(DATA_STRAY_CONVERSION_ID);
    }

    public void setFreezeConverting(boolean p_149843_) {
        this.entityData.set(DATA_STRAY_CONVERSION_ID, p_149843_);
    }

    public boolean isShaking() {
        return this.isFreezeConverting();
    }

    public void tick() {
        if (!this.level.isClientSide && this.isAlive() && !this.isNoAi()) {
            if (this.isFreezeConverting()) {
                --this.conversionTime;
                if (this.conversionTime < 0) {
                    this.doFreezeConversion();
                }
            } else if (this.isInPowderSnow) {
                ++this.inPowderSnowTime;
                if (this.inPowderSnowTime >= 140) {
                    this.startFreezeConversion(300);
                }
            } else {
                this.inPowderSnowTime = -1;
            }
        }

        super.tick();
    }

    public void addAdditionalSaveData(CompoundTag p_149836_) {
        super.addAdditionalSaveData(p_149836_);
        p_149836_.putInt("StrayConversionTime", this.isFreezeConverting() ? this.conversionTime : -1);
    }

    public void readAdditionalSaveData(CompoundTag p_149833_) {
        super.readAdditionalSaveData(p_149833_);
        if (p_149833_.contains("StrayConversionTime", 99) && p_149833_.getInt("StrayConversionTime") > -1) {
            this.startFreezeConversion(p_149833_.getInt("StrayConversionTime"));
        }

    }

    private void startFreezeConversion(int p_149831_) {
        this.conversionTime = p_149831_;
        this.entityData.set(DATA_STRAY_CONVERSION_ID, true);
    }

    protected void doFreezeConversion() {
        this.convertTo(ModEntityType.STRAY_SERVANT.get(), true);
        if (!this.isSilent()) {
            this.level.levelEvent((Player)null, 1048, this.blockPosition(), 0);
        }

    }

    public boolean canFreeze() {
        return false;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.SKELETON_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.SKELETON_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.SKELETON_STEP;
    }

}
