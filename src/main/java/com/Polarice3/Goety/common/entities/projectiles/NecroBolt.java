package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class NecroBolt extends AbstractHurtingProjectile {

    public NecroBolt(EntityType<? extends AbstractHurtingProjectile> p_36833_, Level p_36834_) {
        super(p_36833_, p_36834_);
    }

    public NecroBolt(double pX, double pY, double pZ, double pXPower, double pYPower, double pZPower, Level pLevel) {
        super(ModEntityType.NECRO_BOLT.get(), pX, pY, pZ, pXPower, pYPower, pZPower, pLevel);
    }

    public NecroBolt(LivingEntity pShooter, double pXPower, double pYPower, double pZPower, Level pLevel) {
        super(ModEntityType.NECRO_BOLT.get(), pShooter, pXPower, pYPower, pZPower, pLevel);
    }

    protected float getInertia() {
        return 0.82F;
    }

    public boolean isOnFire() {
        return false;
    }

    protected void onHitEntity(EntityHitResult p_37626_) {
        super.onHitEntity(p_37626_);
        if (!this.level.isClientSide) {
            float baseDamage = SpellConfig.NecroBoltDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
            Entity entity = p_37626_.getEntity();
            Entity entity1 = this.getOwner();
            boolean flag;
            if (entity1 instanceof LivingEntity livingentity) {
                if (WandUtil.enchantedFocus(livingentity)){
                    baseDamage += WandUtil.getLevels(ModEnchantments.POTENCY.get(), livingentity);
                }
                flag = entity.hurt(DamageSource.indirectMagic(this, livingentity), baseDamage);
                if (flag) {
                    if (entity.isAlive()) {
                        this.doEnchantDamageEffects(livingentity, entity);
                    }
                }
            } else {
                flag = entity.hurt(DamageSource.MAGIC, baseDamage);
            }

            if (flag && entity instanceof LivingEntity) {
                double x = this.getX();
                double z = this.getZ();
                if (entity1 != null){
                    x = entity1.getX();
                    z = entity1.getZ();
                }
                ((LivingEntity) entity).knockback(1.0F, x - entity.getX(), z - entity.getZ());
            }

        }
    }

    protected void onHitBlock(BlockHitResult p_230299_1_) {
        super.onHitBlock(p_230299_1_);
        if (!this.level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) this.level;
            for (int p = 0; p < 8; ++p) {
                double d0 = (double)this.getX() + this.level.random.nextDouble();
                double d1 = (double)this.getY() + this.level.random.nextDouble();
                double d2 = (double)this.getZ() + this.level.random.nextDouble();
                serverLevel.sendParticles(ModParticleTypes.NECRO_EFFECT.get(), d0, d1, d2, 0, 0.45, 0.45, 0.45, 0.5F);
                serverLevel.sendParticles(ModParticleTypes.SUMMON.get(), d0, d1, d2, 0, 0.45, 0.45, 0.45, 0.5F);
            }
            this.discard();
        }
    }

    public void tick() {
        super.tick();
        if (this.tickCount >= MathHelper.secondsToTicks(20)){
            this.discard();
        }
        Entity entity = this.getOwner();
        if (this.level.isClientSide || (entity == null || !entity.isRemoved()) && this.level.hasChunkAt(this.blockPosition())) {
            Vec3 vec3 = this.getDeltaMovement();
            double d0 = this.getX() - vec3.x;
            double d1 = this.getY() - vec3.y;
            double d2 = this.getZ() - vec3.z;
            this.level.addParticle(ModParticleTypes.SUMMON.get(), d0, d1 + 0.3D, d2, 0.0D, 0.0D, 0.0D);
        }
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null && (this.getOwner().isAlliedTo(pEntity) || pEntity.isAlliedTo(this.getOwner()) || (this.getOwner() instanceof Enemy && pEntity instanceof Enemy))){
            return false;
        } else if (pEntity instanceof Owned owned0 && this.getOwner() instanceof Owned owned1){
            return !MobUtil.ownerStack(owned0, owned1);
        } else {
            return super.canHitEntity(pEntity);
        }
    }

    protected ParticleOptions getTrailParticle() {
        return ModParticleTypes.NECRO_BOLT.get();
    }

    public boolean isPickable() {
        return false;
    }

    public boolean hurt(DamageSource p_37616_, float p_37617_) {
        return false;
    }

    protected boolean shouldBurn() {
        return false;
    }
}
