package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.ExplosionUtil;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class SnapFungus extends ThrowableFungus {

    public SnapFungus(EntityType<? extends ThrowableFungus> p_37466_, Level p_37467_) {
        super(p_37466_, p_37467_);
    }

    public SnapFungus(double p_37457_, double p_37458_, double p_37459_, Level p_37460_) {
        super(ModEntityType.SNAP_FUNGUS.get(), p_37457_, p_37458_, p_37459_, p_37460_);
    }

    public SnapFungus(LivingEntity p_37463_, Level p_37464_) {
        super(ModEntityType.SNAP_FUNGUS.get(), p_37463_, p_37464_);
    }

    protected void onHit(HitResult p_37406_) {
        super.onHit(p_37406_);
        if (!this.level.isClientSide) {
            ExplosionUtil.fungusExplode(this.level, this, this.getX(), this.getY(), this.getZ(), 1.25F, this.isOnFire());
            if (this.level.random.nextFloat() <= 0.25F){
                AreaEffectCloud areaEffectCloud = new AreaEffectCloud(this.level, this.getX(), this.getY(), this.getZ());
                if (p_37406_.getType() == HitResult.Type.ENTITY){
                    EntityHitResult result1 = (EntityHitResult) p_37406_;
                    areaEffectCloud.setPos(result1.getEntity().position());
                }
                areaEffectCloud.setRadius(1.0F);
                areaEffectCloud.setRadiusOnUse(-0.5F);
                areaEffectCloud.setWaitTime(10);
                areaEffectCloud.setDuration(areaEffectCloud.getDuration() / 2);
                areaEffectCloud.setRadiusPerTick(-areaEffectCloud.getRadius() / (float)areaEffectCloud.getDuration());
                areaEffectCloud.addEffect(new MobEffectInstance(MobEffects.POISON, MathHelper.secondsToTicks(11)));
                this.level.addFreshEntity(areaEffectCloud);
            }
            this.discard();
        }
    }
}
