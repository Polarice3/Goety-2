package com.Polarice3.Goety.common.entities.projectiles;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;

public abstract class ExplosiveProjectile extends Fireball {
    public static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(ExplosiveProjectile.class, EntityDataSerializers.BOOLEAN);

    public ExplosiveProjectile(EntityType<? extends ExplosiveProjectile> p_i50166_1_, Level p_i50166_2_) {
        super(p_i50166_1_, p_i50166_2_);
    }

    public ExplosiveProjectile(EntityType<? extends ExplosiveProjectile> p_i50167_1_, double p_i50167_2_, double p_i50167_4_, double p_i50167_6_, double p_i50167_8_, double p_i50167_10_, double p_i50167_12_, Level p_i50167_14_) {
        super(p_i50167_1_, p_i50167_2_, p_i50167_4_, p_i50167_6_, p_i50167_8_, p_i50167_10_, p_i50167_12_, p_i50167_14_);
    }

    public ExplosiveProjectile(EntityType<? extends ExplosiveProjectile> p_i50168_1_, LivingEntity p_i50168_2_, double p_i50168_3_, double p_i50168_5_, double p_i50168_7_, Level p_i50168_9_) {
        super(p_i50168_1_, p_i50168_2_, p_i50168_3_, p_i50168_5_, p_i50168_7_, p_i50168_9_);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DANGEROUS, this.defaultDangerous());
    }

    public boolean defaultDangerous(){
        return false;
    }

    public boolean isDangerous() {
        return this.entityData.get(DATA_DANGEROUS);
    }

    public void setDangerous(boolean pDangerous) {
        this.entityData.set(DATA_DANGEROUS, pDangerous);
    }

    public abstract void setExplosionPower(float pExplosionPower);

    public abstract float getExplosionPower();

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("ExplosionPower", this.getExplosionPower());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("ExplosionPower", 99)) {
            this.setExplosionPower(pCompound.getFloat("ExplosionPower"));
        }

    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null && pEntity == this.getOwner()){
            return false;
        } else {
            return super.canHitEntity(pEntity);
        }
    }

}
