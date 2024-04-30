package com.Polarice3.Goety.common.entities.projectiles;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;

public abstract class SpellHurtingProjectile extends WaterHurtingProjectile {
    public static final EntityDataAccessor<Float> DATA_EXTRA_DAMAGE = SynchedEntityData.defineId(SpellHurtingProjectile.class, EntityDataSerializers.FLOAT);
    public float boltSpeed = 0.0F;

    protected SpellHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> p_36833_, Level p_36834_) {
        super(p_36833_, p_36834_);
    }

    public SpellHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> p_36817_, double p_36818_, double p_36819_, double p_36820_, double p_36821_, double p_36822_, double p_36823_, Level p_36824_) {
        super(p_36817_, p_36818_, p_36819_, p_36820_, p_36821_, p_36822_, p_36823_, p_36824_);
    }

    public SpellHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> p_36826_, LivingEntity p_36827_, double p_36828_, double p_36829_, double p_36830_, Level p_36831_) {
        super(p_36826_, p_36827_, p_36828_, p_36829_, p_36830_, p_36831_);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_EXTRA_DAMAGE, 0.0F);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("BoltSpeed")){
            this.boltSpeed = compound.getFloat("BoltSpeed");
        }
        if (compound.contains("ExtraDamage")) {
            this.setExtraDamage(compound.getFloat("ExtraDamage"));
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("BoltSpeed", this.boltSpeed);
        compound.putFloat("ExtraDamage", this.getExtraDamage());
    }

    public void setBoltSpeed(int increase){
        this.boltSpeed += (float) increase / 50;
    }

    public float getExtraDamage() {
        return this.entityData.get(DATA_EXTRA_DAMAGE);
    }

    public void setExtraDamage(float pDamage) {
        this.entityData.set(DATA_EXTRA_DAMAGE, pDamage);
    }

    protected float getInertia() {
        return 0.82F + Math.min(this.boltSpeed, 0.18F);
    }
}
