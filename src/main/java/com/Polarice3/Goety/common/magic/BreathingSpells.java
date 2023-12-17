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

    public ParticleOptions getDragonParticle(LivingEntity livingEntity){
        return this.getParticle();
    }

    public abstract void showWandBreath(LivingEntity entityLiving);

    public void breathAttack(LivingEntity entityLiving, double pVelocity, double pSpread){
        this.breathAttack(entityLiving, 2, pVelocity, pSpread);
    }

    public void breathAttack(LivingEntity entityLiving, int pParticleAmount, double pVelocity, double pSpread){
        Vec3 look = entityLiving.getLookAngle();

        double dist = 0.9;
        double px = entityLiving.getX() + look.x * dist;
        double py = entityLiving.getEyeY() + look.y * dist;
        double pz = entityLiving.getZ() + look.z * dist;

        if (pParticleAmount <= 0){
            pParticleAmount = 1;
        }

        for (int i = 0; i < pParticleAmount; i++) {
            double spread = pSpread + entityLiving.getRandom().nextDouble() * (pSpread/2);
            double velocity = pVelocity + entityLiving.getRandom().nextDouble() * pVelocity;

            Vec3 vecSpread = new Vec3(
                    entityLiving.getRandom().nextGaussian() * 0.007499999832361937D * spread,
                    entityLiving.getRandom().nextGaussian() * 0.007499999832361937D * spread,
                    entityLiving.getRandom().nextGaussian() * 0.007499999832361937D * spread);
            Vec3 vec3 = look.add(vecSpread).multiply(velocity, velocity, velocity);
            entityLiving.level.addAlwaysVisibleParticle(getParticle(), px, py, pz, vec3.x, vec3.y, vec3.z);
        }
    }

    public void dragonBreathAttack(LivingEntity entityLiving, double pVelocity){
        this.dragonBreathAttack(entityLiving, 10, pVelocity);
    }

    public void dragonBreathAttack(LivingEntity entityLiving, int pParticleAmount, double pVelocity){
        Vec3 look = entityLiving.getLookAngle();

        double dist = 0.9D;
        double px = entityLiving.getX() + look.x * dist;
        double py = entityLiving.getEyeY() + look.y * dist;
        double pz = entityLiving.getZ() + look.z * dist;

        double velocity = pVelocity + entityLiving.getRandom().nextDouble() * pVelocity;
        for (int i = 0; i < pParticleAmount; i++) {
            double offset = 0.15D;
            double dx = entityLiving.getRandom().nextDouble() * 2.0D * offset - offset;
            double dy = entityLiving.getRandom().nextDouble() * 2.0D * offset - offset;
            double dz = entityLiving.getRandom().nextDouble() * 2.0D * offset - offset;

            double angle = 0.5D;
            Vec3 randomVec = new Vec3(entityLiving.getRandom().nextDouble() * 2.0D * angle - angle, entityLiving.getRandom().nextDouble() * 2.0D * angle - angle, entityLiving.getRandom().nextDouble() * 2.0D * angle - angle).normalize();
            Vec3 result = (look.normalize().scale(3.0D).add(randomVec)).normalize().scale(velocity);
            entityLiving.level.addAlwaysVisibleParticle(getDragonParticle(entityLiving), px + dx, py + dy, pz + dz, result.x, result.y, result.z);
        }
    }
}
