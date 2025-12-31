package com.Polarice3.Goety.api.magic;

import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;

/**
 * Breathing Spells Codes based of codes from @TeamTwilight
 */
public interface IBreathingSpell extends IChargingSpell{

    default int Cooldown() {
        return 0;
    }

    default boolean everCharge() {
        return true;
    }

    default void useParticle(Level worldIn, LivingEntity caster, ItemStack stack) {
    }

    default List<Entity> getBreathTarget(LivingEntity livingEntity, double range) {
        return MobUtil.getTargets(livingEntity.level, livingEntity, range, 3.0D);
    }

    default List<Entity> getBreathTarget(LivingEntity livingEntity, double range, Predicate<? super Entity> predicate) {
        return MobUtil.getTargets(livingEntity.level, livingEntity, range, 3.0D, predicate);
    }

    @Deprecated
    default void showWandBreath(LivingEntity entityLiving) {

    }

    @Deprecated
    default void showWandBreath(LivingEntity caster, SpellStat spellStat) {
        this.showWandBreath(caster, ItemStack.EMPTY, spellStat);
    }

    default void showWandBreath(LivingEntity caster, ItemStack staff, SpellStat spellStat) {

    }

    default void breathAttack(ParticleOptions particleOptions, LivingEntity entityLiving, double pVelocity, double pSpread){
        this.breathAttack(particleOptions, entityLiving, false, 2, pVelocity, pSpread);
    }

    default void breathAttack(ParticleOptions particleOptions, LivingEntity entityLiving, boolean spreadOut, double pVelocity, double pSpread){
        this.breathAttack(particleOptions, entityLiving, spreadOut, 2, pVelocity, pSpread);
    }

    default void breathAttack(ParticleOptions particleOptions, LivingEntity entityLiving, boolean spreadOut, int pParticleAmount, double pVelocity, double pSpread){
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
                    entityLiving.getRandom().nextGaussian() * 0.0075D * spread,
                    entityLiving.getRandom().nextGaussian() * 0.0075D * spread,
                    entityLiving.getRandom().nextGaussian() * 0.0075D * spread);
            Vec3 vec3 = look.add(vecSpread).multiply(velocity, velocity, velocity);
            Vec3 pos = new Vec3(px, py, pz);
            if (spreadOut){
                pos = pos.add(entityLiving.getRandom().nextGaussian() / 2, entityLiving.getRandom().nextGaussian() / 2, entityLiving.getRandom().nextGaussian() / 2);
            }
            if (entityLiving.level instanceof ServerLevel serverLevel){
                serverLevel.sendParticles(particleOptions, pos.x, pos.y, pos.z, 0, vec3.x, vec3.y, vec3.z, 1.0F);
            } else {
                entityLiving.level.addAlwaysVisibleParticle(particleOptions, pos.x, pos.y, pos.z, vec3.x, vec3.y, vec3.z);
            }
        }
    }

    default void dragonBreathAttack(ParticleOptions particleOptions, LivingEntity entityLiving, double pVelocity){
        this.dragonBreathAttack(particleOptions, entityLiving, 10, pVelocity);
    }

    default void dragonBreathAttack(ParticleOptions particleOptions, LivingEntity entityLiving, int pParticleAmount, double pVelocity){
        this.dragonBreathAttack(particleOptions, entityLiving, pParticleAmount, pVelocity, 0.5D);
    }

    default void dragonBreathAttack(ParticleOptions particleOptions, LivingEntity entityLiving, int pParticleAmount, double pVelocity, double angle){
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

            Vec3 randomVec = new Vec3(entityLiving.getRandom().nextDouble() * 2.0D * angle - angle, entityLiving.getRandom().nextDouble() * 2.0D * angle - angle, entityLiving.getRandom().nextDouble() * 2.0D * angle - angle).normalize();
            Vec3 result = (look.normalize().scale(3.0D).add(randomVec)).normalize().scale(velocity);
            if (entityLiving.level instanceof ServerLevel serverLevel){
                serverLevel.sendParticles(particleOptions, px + dx, py + dy, pz + dz, 0, result.x, result.y, result.z, 1.0F);
            } else {
                entityLiving.level.addAlwaysVisibleParticle(particleOptions, px + dx, py + dy, pz + dz, result.x, result.y, result.z);
            }
        }
    }
}
