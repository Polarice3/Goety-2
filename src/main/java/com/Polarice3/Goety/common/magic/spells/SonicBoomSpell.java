package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.magic.Spells;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SonicBoomSpell extends Spells {

    public int SoulCost() {
        return SpellConfig.SonicBoomCost.get();
    }

    public int CastDuration() {
        return SpellConfig.SonicBoomDuration.get();
    }

    public SoundEvent CastingSound() {
        return SoundEvents.WARDEN_SONIC_CHARGE;
    }

    @Override
    public void WandResult(ServerLevel worldIn, LivingEntity entityLiving) {
        HitResult rayTraceResult = this.rayTrace(worldIn, entityLiving, 15, 3);
        if (rayTraceResult instanceof EntityHitResult){
            Entity target = ((EntityHitResult) rayTraceResult).getEntity();
            if (target instanceof LivingEntity livingEntity) {
                Vec3 vec3 = entityLiving.position().add(0.0D, (double) 1.6F, 0.0D);
                Vec3 vec31 = livingEntity.getEyePosition().subtract(vec3);
                Vec3 vec32 = vec31.normalize();

                for (int i = 1; i < Mth.floor(vec31.length()) + 7; ++i) {
                    Vec3 vec33 = vec3.add(vec32.scale((double) i));
                    worldIn.sendParticles(ParticleTypes.SONIC_BOOM, vec33.x, vec33.y, vec33.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }

                worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.NEUTRAL, 3.0F, 1.0F);
                livingEntity.hurt(DamageSource.sonicBoom(entityLiving), 10.0F);
                double d1 = 0.5D * (1.0D - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                double d0 = 2.5D * (1.0D - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                livingEntity.push(vec32.x() * d0, vec32.y() * d1, vec32.z() * d0);
            }
        } else {
            Vec3 srcVec = new Vec3(entityLiving.getX(), entityLiving.getEyeY(), entityLiving.getZ());
            Vec3 lookVec = entityLiving.getViewVector(1.0F);
            Vec3 destVec = srcVec.add(lookVec.x * 15, lookVec.y * 15, lookVec.z * 15);
            for(int i = 1; i < Math.floor(destVec.length()) + 7; ++i) {
                Vec3 vector3d2 = srcVec.add(lookVec.scale((double)i));
                worldIn.sendParticles(ParticleTypes.SONIC_BOOM, vector3d2.x, vector3d2.y, vector3d2.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
            if (MobUtil.getSingleTarget(worldIn, entityLiving) != null){
                if (MobUtil.getSingleTarget(worldIn, entityLiving) instanceof LivingEntity target1) {
                    float damage = SpellConfig.SonicBoomDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
                    target1.hurt(DamageSource.sonicBoom(entityLiving), damage);
                    double d0 = target1.getX() - entityLiving.getX();
                    double d1 = target1.getZ() - entityLiving.getZ();
                    double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
                    MobUtil.push(target1, d0 / d2 * 4.0D, 0.2D, d1 / d2 * 4.0D);
                }
            }
        }
        //        this.IncreaseInfamy(SpellConfig.SonicBoomInfamyChance.get(), (PlayerEntity) entityLiving);
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.NEUTRAL, 3.0F, 1.0F);
    }
}
