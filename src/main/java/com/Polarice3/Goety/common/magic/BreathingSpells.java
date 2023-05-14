package com.Polarice3.Goety.common.magic;

import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Breathing Spells Codes based of codes from @TeamTwilight
 */
public abstract class BreathingSpells extends EverChargeSpells{

    public List<Entity> getTarget(LivingEntity livingEntity, double range) {
        return MobUtil.getTargets(livingEntity.level, livingEntity, range, 3.0D);
    }

    public abstract ParticleOptions getParticle();

    public abstract void showWandBreath(LivingEntity entityLiving);

    public abstract void showStaffBreath(LivingEntity entityLiving);

        public void breathAttack(LivingEntity entityLiving, double pVelocity, double pSpread){
        Vec3 look = entityLiving.getLookAngle();

        double dist = 0.9;
        double px = entityLiving.getX() + look.x * dist;
        double py = entityLiving.getEyeY() + look.y * dist;
        double pz = entityLiving.getZ() + look.z * dist;

        for (int i = 0; i < 2; i++) {
            double dx = look.x;
            double dy = look.y;
            double dz = look.z;

            double spread = pSpread + entityLiving.getRandom().nextDouble() * (pSpread/2);
            double velocity = pVelocity + entityLiving.getRandom().nextDouble() * pVelocity;

            dx += entityLiving.getRandom().nextGaussian() * 0.007499999832361937D * spread;
            dy += entityLiving.getRandom().nextGaussian() * 0.007499999832361937D * spread;
            dz += entityLiving.getRandom().nextGaussian() * 0.007499999832361937D * spread;
            dx *= velocity;
            dy *= velocity;
            dz *= velocity;

            entityLiving.level.addAlwaysVisibleParticle(getParticle(), px, py, pz, dx, dy, dz);
        }
    }
}
