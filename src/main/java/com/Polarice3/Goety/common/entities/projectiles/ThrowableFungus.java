package com.Polarice3.Goety.common.entities.projectiles;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;

public class ThrowableFungus extends ThrowableProjectile {

    protected ThrowableFungus(EntityType<? extends ThrowableProjectile> p_37466_, Level p_37467_) {
        super(p_37466_, p_37467_);
    }

    protected ThrowableFungus(EntityType<? extends ThrowableProjectile> p_37456_, double p_37457_, double p_37458_, double p_37459_, Level p_37460_) {
        super(p_37456_, p_37457_, p_37458_, p_37459_, p_37460_);
    }

    protected ThrowableFungus(EntityType<? extends ThrowableProjectile> p_37462_, LivingEntity p_37463_, Level p_37464_) {
        super(p_37462_, p_37463_, p_37464_);
    }

    @Override
    protected void defineSynchedData() {
    }
}
