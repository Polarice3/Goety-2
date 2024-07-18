package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.ShockwaveParticleOption;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class ElectroOrb extends SpellThrowableProjectile {
    public double xd;
    public double yd;
    public double zd;

    public ElectroOrb(EntityType<? extends SpellThrowableProjectile> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public ElectroOrb(Level pLevel, LivingEntity pOwner, @Nullable LivingEntity pTarget){
        super(ModEntityType.ELECTRO_ORB.get(), pOwner, pLevel);
        if (pTarget != null) {
            if (!MobUtil.areAllies(pOwner, pTarget)) {
                this.setTarget(pTarget);
            }
        }
    }

    protected float getGravity() {
        if (this.getTarget() == null){
            return super.getGravity();
        } else {
            return 0.0F;
        }
    }

    public void tick() {
        super.tick();
        if (this.getTarget() != null) {
            double distance = this.distanceToSqr(this.getTarget());
            Vec3 vec3 = this.getTargetingVec(distance);
            this.setDeltaMovement(vec3);
        }
        if (this.tickCount >= MathHelper.secondsToTicks(10)){
            this.discard();
        }
    }

    private Vec3 getTargetingVec(double distance) {
        if (this.getTarget() != null) {
            double dx = (this.getTarget().getX() - this.getX()) / distance;
            double dy = (this.getTarget().getBoundingBox().minY + this.getTarget().getBbHeight() * 0.6D - this.getY()) / distance;
            double dz = (this.getTarget().getZ() - this.getZ()) / distance;
            double d2 = 0.2D;
            this.xd += dx * d2;
            this.yd += dy * d2;
            this.zd += dz * d2;
            this.xd = Mth.clamp(this.xd, -0.25D, 0.25D);
            this.yd = Mth.clamp(this.yd, -0.25D, 0.25D);
            this.zd = Mth.clamp(this.zd, -0.25D, 0.25D);
            return new Vec3(this.xd, this.yd, this.zd);
        }
        return Vec3.ZERO;
    }

    protected void onHit(HitResult hitResult) {
        if (!this.level.isClientSide) {
            DamageSource damageSource = ModDamageSource.SHOCK;
            float damage = SpellConfig.ElectroOrbDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get().floatValue();
            if (this.getOwner() != null) {
                damageSource = ModDamageSource.indirectShock(this, this.getOwner());
                if (this.getOwner() instanceof Mob mob && mob.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                    damage = (float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE);
                }
            }
            damage += this.getExtraDamage();
            if (hitResult instanceof EntityHitResult result) {
                Entity entity = result.getEntity();
                entity.hurt(damageSource, damage);
                if (this.isStaff()) {
                    float chance = 0.05F;
                    if (this.level.isThundering() && this.level.isRainingAt(entity.blockPosition())) {
                        chance += 0.25F;
                    }
                    if (entity instanceof LivingEntity livingEntity && this.level.random.nextFloat() <= chance) {
                        livingEntity.addEffect(new MobEffectInstance(GoetyEffects.SPASMS.get(), MathHelper.secondsToTicks(5)));
                    }
                }
            }
            this.finalizeExplosion();
            this.playSound(ModSounds.THUNDERBOLT.get(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            this.discard();
        }
    }

    public void finalizeExplosion() {
        if (this.level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticleTypes.ELECTRIC_EXPLODE.get(), this.getX(), this.getY(), this.getZ(), 1, 1.0D, 0.0D, 0.0D, 1.0F);
            if (this.isStaff()) {
                DamageSource damageSource = ModDamageSource.SHOCK;
                int radius = 1;
                float damage = SpellConfig.ElectroOrbDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get().floatValue();
                if (this.getOwner() != null) {
                    damageSource = ModDamageSource.indirectShock(this, this.getOwner());
                    if (this.getOwner() instanceof Mob mob && mob.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                        damage = (float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    }
                }
                for (int i = -radius; i < radius; ++i){
                    for (int k = -radius; k < radius; ++k){
                        BlockPos blockPos = this.blockPosition().offset(i, 0, k);
                        serverLevel.sendParticles(ModParticleTypes.ELECTRIC.get(), blockPos.getX(), blockPos.getY() + 0.5F, blockPos.getZ(), 0, 0, 0.04D, 0, 0.5F);
                    }
                }
                ColorUtil colorUtil = new ColorUtil(0xfef597);
                serverLevel.sendParticles(new ShockwaveParticleOption(0, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 5, 0, true), this.getX(), this.getY() + 0.5F, this.getZ(), 0, 0, 0, 0, 0);
                new SpellExplosion(serverLevel, this, damageSource, this.blockPosition(), radius, damage){
                    @Override
                    public void explodeHurt(Entity target, DamageSource damageSource, double x, double y, double z, double seen, float actualDamage) {
                        if (target instanceof LivingEntity target1){
                            super.explodeHurt(target, damageSource, x, y, z, seen, actualDamage);
                            float chance = 0.05F;
                            if (serverLevel.isThundering() && serverLevel.isRainingAt(target1.blockPosition())){
                                chance += 0.25F;
                            }
                            if (serverLevel.random.nextFloat() <= chance){
                                target1.addEffect(new MobEffectInstance(GoetyEffects.SPASMS.get(), MathHelper.secondsToTicks(5)));
                            }
                        }
                    }
                };
                serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.REDSTONE_EXPLODE.get(), this.getSoundSource(), 1.0F, 1.0F);
            }
        }
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null){
            if (pEntity == this.getOwner()){
                return false;
            }
            if (this.getOwner() instanceof Mob mob && mob.getTarget() == pEntity){
                return super.canHitEntity(pEntity);
            } else {
                if(this.getOwner().isAlliedTo(pEntity) || pEntity.isAlliedTo(this.getOwner())){
                    return false;
                }
                if (pEntity instanceof IOwned owned0 && this.getOwner() instanceof IOwned owned1){
                    return !MobUtil.ownerStack(owned0, owned1);
                }
            }
        }
        return super.canHitEntity(pEntity);
    }
}
