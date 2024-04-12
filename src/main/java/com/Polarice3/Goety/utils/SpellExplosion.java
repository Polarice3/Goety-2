package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.common.entities.projectiles.FireTornado;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SpellExplosion {

    public SpellExplosion(Level level, Entity source, DamageSource damageSource, double x, double y, double z, float radius) {
        this(level, source, damageSource, x, y, z, radius, 0);
    }

    public SpellExplosion(Level level, Entity source, DamageSource damageSource, BlockPos blockPos, float radius, float damage){
        this(level, source, damageSource, blockPos.getX(), blockPos.getY(), blockPos.getZ(), radius, damage);
    }

    public SpellExplosion(Level level, Entity source, DamageSource damageSource, double x, double y, double z, float radius, float damage){
        float f2 = radius * 2.0F;
        Vec3 vec3 = new Vec3(x, y, z);
        for (Entity entity : explosionRangeEntities(level, source, x, y, z, radius)) {
            double d12 = Math.sqrt(entity.distanceToSqr(vec3)) / (double) f2;
            if (d12 <= 1.0D) {
                double d5 = entity.getX() - x;
                double d7 = entity.getEyeY() - y;
                double d9 = entity.getZ() - z;
                double d13 = Math.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
                if (d13 != 0.0D) {
                    d5 /= d13;
                    d7 /= d13;
                    d9 /= d13;
                    double d14 = (double) getSeenPercent(vec3, entity);
                    double d10 = (1.0D - d12) * d14;
                    float actualDamage = damage == 0 ? (float) ((int) ((d10 * d10 + d10) / 2.0D * 7.0D * (double) f2 + 1.0D)) : damage;
                    boolean hurt = true;
                    if (damageSource.is(DamageTypeTags.IS_EXPLOSION) && entity.ignoreExplosion()){
                        hurt = false;
                    } else if (damageSource.getEntity() != null){
                        if (MobUtil.areAllies(damageSource.getEntity(), entity) || entity == damageSource.getEntity()){
                            hurt = false;
                        }
                    } else if (source != null){
                        if (MobUtil.areAllies(source, entity) || entity == source){
                            hurt = false;
                        }
                    }
                    if (hurt) {
                        this.explodeHurt(entity, damageSource, d5, d7, d9, d10, actualDamage);
                    }
                }
            }
        }
    }

    public void explodeHurt(Entity target, DamageSource damageSource, double x, double y, double z, double seen, float actualDamage){
        target.hurt(damageSource, actualDamage);
        double d11 = seen;
        if (target instanceof LivingEntity) {
            d11 = ProtectionEnchantment.getExplosionKnockbackAfterDampener((LivingEntity) target, seen);
        }
        if (damageSource.is(DamageTypes.MAGIC)){
            if (target instanceof FireTornado fireTornado){
                fireTornado.trueRemove();
            }
        }

        if (target instanceof LivingEntity) {
            MobUtil.push(target, x * d11, y * d11, z * d11);
        }
    }

    public static List<Entity> explosionRangeEntities(Level level, Entity source, BlockPos blockPos, float range){
        return explosionRangeEntities(level, source, blockPos.getX(), blockPos.getY(), blockPos.getZ(), range);
    }

    public static List<Entity> explosionRangeEntities(Level level, Entity source, double x, double y, double z, float radius){
        float f2 = radius * 2.0F;
        int k1 = Mth.floor(x - (double)f2 - 1.0D);
        int l1 = Mth.floor(x + (double)f2 + 1.0D);
        int i2 = Mth.floor(y - (double)f2 - 1.0D);
        int i1 = Mth.floor(y + (double)f2 + 1.0D);
        int j2 = Mth.floor(z - (double)f2 - 1.0D);
        int j1 = Mth.floor(z + (double)f2 + 1.0D);
        return level.getEntities(source, new AABB((double)k1, (double)i2, (double)j2, (double)l1, (double)i1, (double)j1));
    }

    public static float getSeenPercent(Vec3 vector, Entity target) {
        AABB aabb = target.getBoundingBox();
        double d0 = 1.0D / ((aabb.maxX - aabb.minX) * 2.0D + 1.0D);
        double d1 = 1.0D / ((aabb.maxY - aabb.minY) * 2.0D + 1.0D);
        double d2 = 1.0D / ((aabb.maxZ - aabb.minZ) * 2.0D + 1.0D);
        double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
        double d4 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;
        if (!(d0 < 0.0D) && !(d1 < 0.0D) && !(d2 < 0.0D)) {
            int i = 0;
            int j = 0;

            for(double d5 = 0.0D; d5 <= 1.0D; d5 += d0) {
                for(double d6 = 0.0D; d6 <= 1.0D; d6 += d1) {
                    for(double d7 = 0.0D; d7 <= 1.0D; d7 += d2) {
                        double d8 = Mth.lerp(d5, aabb.minX, aabb.maxX);
                        double d9 = Mth.lerp(d6, aabb.minY, aabb.maxY);
                        double d10 = Mth.lerp(d7, aabb.minZ, aabb.maxZ);
                        Vec3 vec3 = new Vec3(d8 + d3, d9, d10 + d4);
                        if (target.level.clip(new ClipContext(vec3, vector, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, target)).getType() == HitResult.Type.MISS) {
                            ++i;
                        }

                        ++j;
                    }
                }
            }

            return (float)i / (float)j;
        } else {
            return 0.0F;
        }
    }
}
