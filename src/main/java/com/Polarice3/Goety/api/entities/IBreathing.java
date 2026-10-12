package com.Polarice3.Goety.api.entities;

import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public interface IBreathing {

    boolean isBreathing();

    void setBreathing(boolean flag);

    void doBreathing(Entity target);

    default Vec3 baseStartPos(LivingEntity entityLiving) {
        return new Vec3(entityLiving.getX(), (entityLiving.getY() + (entityLiving.getBbHeight() * 0.5D)), entityLiving.getZ());
    }

    static void breathAttack(ParticleOptions particleOptions, IBreathing breathing, LivingEntity entityLiving, int pParticleAmount, double pVelocity, double angle){
        Vec3 look = entityLiving.getLookAngle();
        Vec3 pos = breathing.baseStartPos(entityLiving);

        double dist = 0.9D;
        double px = pos.x + look.x * dist;
        double py = pos.y + look.y * dist;
        double pz = pos.z + look.z * dist;

        double velocity = pVelocity + entityLiving.getRandom().nextDouble() * pVelocity;
        for (int i = 0; i < pParticleAmount; i++) {
            double offset = 0.15D;
            double dx = entityLiving.getRandom().nextDouble() * 2.0D * offset - offset;
            double dy = entityLiving.getRandom().nextDouble() * 2.0D * offset - offset;
            double dz = entityLiving.getRandom().nextDouble() * 2.0D * offset - offset;

            Vec3 randomVec = new Vec3(entityLiving.getRandom().nextDouble() * 2.0D * angle - angle, entityLiving.getRandom().nextDouble() * 2.0D * angle - angle, entityLiving.getRandom().nextDouble() * 2.0D * angle - angle).normalize();
            Vec3 result = (look.normalize().scale(3.0D).add(randomVec)).normalize().scale(velocity);
            if (entityLiving.level instanceof ServerLevel serverLevel){
                ServerParticleUtil.sendAlwaysVisibleParticles(serverLevel, particleOptions, px + dx, py + dy, pz + dz, 0, result.x, result.y, result.z, 1.0F);
            }
        }
    }
}
