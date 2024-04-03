package com.Polarice3.Goety.common.entities.projectiles;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;

public abstract class MagicProjectile extends WaterHurtingProjectile {
    public float boltSpeed = 0.0F;

    protected MagicProjectile(EntityType<? extends AbstractHurtingProjectile> p_36833_, Level p_36834_) {
        super(p_36833_, p_36834_);
    }

    public MagicProjectile(EntityType<? extends AbstractHurtingProjectile> p_36817_, double p_36818_, double p_36819_, double p_36820_, double p_36821_, double p_36822_, double p_36823_, Level p_36824_) {
        super(p_36817_, p_36818_, p_36819_, p_36820_, p_36821_, p_36822_, p_36823_, p_36824_);
    }

    public MagicProjectile(EntityType<? extends AbstractHurtingProjectile> p_36826_, LivingEntity p_36827_, double p_36828_, double p_36829_, double p_36830_, Level p_36831_) {
        super(p_36826_, p_36827_, p_36828_, p_36829_, p_36830_, p_36831_);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("BoltSpeed")){
            this.boltSpeed = compound.getFloat("BoltSpeed");
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("BoltSpeed", this.boltSpeed);
    }

    public void setBoltSpeed(int increase){
        this.boltSpeed += (float) increase / 50;
    }

    protected float getInertia() {
        return 0.82F + Math.min(this.boltSpeed, 0.18F);
    }
}
