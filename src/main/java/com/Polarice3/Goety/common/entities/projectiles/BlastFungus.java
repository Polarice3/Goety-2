package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.ExplosionUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class BlastFungus extends ThrowableFungus {

    public BlastFungus(EntityType<? extends ThrowableFungus> p_37466_, Level p_37467_) {
        super(p_37466_, p_37467_);
    }

    public BlastFungus(double p_37457_, double p_37458_, double p_37459_, Level p_37460_) {
        super(ModEntityType.BLAST_FUNGUS.get(), p_37457_, p_37458_, p_37459_, p_37460_);
    }

    public BlastFungus(LivingEntity p_37463_, Level p_37464_) {
        super(ModEntityType.BLAST_FUNGUS.get(), p_37463_, p_37464_);
    }

    protected void onHit(HitResult p_37406_) {
        super.onHit(p_37406_);
        if (!this.level.isClientSide) {
            ExplosionUtil.fungusExplode(this.level, this, this.getX(), this.getY(), this.getZ(), 2.5F, this.isOnFire());
            this.discard();
        }
    }
}
