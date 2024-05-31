package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;

public class NecroBolt extends SpellHurtingProjectile {
    private final float rotSpeed;
    public float roll;
    public float oRoll;
    public float getGlow;
    public float glowAmount = 0.05F;
    private final Vec3[] trailPositions = new Vec3[64];
    private int trailPointer = -1;

    public NecroBolt(EntityType<? extends SpellHurtingProjectile> p_36833_, Level p_36834_) {
        super(p_36833_, p_36834_);
        this.rotSpeed = 0.05F;
        this.roll = (float)Math.random() * ((float)Math.PI * 2F);
    }

    public NecroBolt(double pX, double pY, double pZ, double pXPower, double pYPower, double pZPower, Level pLevel) {
        super(ModEntityType.NECRO_BOLT.get(), pX, pY, pZ, pXPower, pYPower, pZPower, pLevel);
        this.rotSpeed = 0.05F;
        this.roll = (float)Math.random() * ((float)Math.PI * 2F);
    }

    public NecroBolt(LivingEntity pShooter, double pXPower, double pYPower, double pZPower, Level pLevel) {
        super(ModEntityType.NECRO_BOLT.get(), pShooter, pXPower, pYPower, pZPower, pLevel);
        this.rotSpeed = 0.05F;
        this.roll = (float)Math.random() * ((float)Math.PI * 2F);
    }

    public boolean isOnFire() {
        return false;
    }

    @Override
    protected float getInertia() {
        return 0.9F + Math.min(this.boltSpeed, 0.1F);
    }

    protected void onHitEntity(EntityHitResult p_37626_) {
        super.onHitEntity(p_37626_);
        if (!this.level.isClientSide) {
            float baseDamage = SpellConfig.NecroBoltDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
            Entity entity = p_37626_.getEntity();
            Entity entity1 = this.getOwner();
            boolean flag;
            if (entity1 instanceof LivingEntity livingentity) {
                if (livingentity instanceof Mob mob){
                    if (mob.getAttribute(Attributes.ATTACK_DAMAGE) != null && mob.getAttributeValue(Attributes.ATTACK_DAMAGE) > 0){
                        baseDamage = (float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    }
                }
                baseDamage += this.getExtraDamage();
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
                serverLevel.sendParticles(ModParticleTypes.SUMMON_TRAIL.get(), d0, d1, d2, 1, 0.0F, 0.0F, 0.0F, 0);
            }
            this.discard();
        }
    }

    public void tick() {
        super.tick();
        this.oRoll = this.roll;
        this.roll += (float)Math.PI * this.rotSpeed * 2.0F;
        this.glow();
        if (this.tickCount >= MathHelper.secondsToTicks(20)){
            this.discard();
        }
        Entity entity = this.getOwner();
        if (this.level.isClientSide || (entity == null || !entity.isRemoved()) && this.level.hasChunkAt(this.blockPosition())) {
            Vec3 vec3 = this.getDeltaMovement();
            double d0 = this.getX() - vec3.x;
            double d1 = this.getY() - vec3.y;
            double d2 = this.getZ() - vec3.z;
            this.level.addParticle(ModParticleTypes.SUMMON_TRAIL.get(),
                    d0 + ((this.level.random.nextDouble() / 2) * (this.level.random.nextIntBetweenInclusive(-1, 1))),
                    d1 + 0.5D,
                    d2 + ((this.level.random.nextDouble() / 2) * (this.level.random.nextIntBetweenInclusive(-1, 1))),
                    0.0D, 0.0D, 0.0D);
        }
        Vec3 trailAt = this.position().add(0, this.getBbHeight() / 2F, 0);
        if (this.trailPointer == -1) {
            Arrays.fill(trailPositions, trailAt);
        }
        if (++this.trailPointer == this.trailPositions.length) {
            this.trailPointer = 0;
        }
        this.trailPositions[this.trailPointer] = trailAt;
    }

    /**
     * Ripped Trail effect from @AlexModGuy: <a href="https://github.com/AlexModGuy/AlexsCaves/blob/main/src/main/java/com/github/alexmodguy/alexscaves/server/entity/item/WaterBoltEntity.java">...</a>
     */
    public Vec3 getTrailPosition(int pointer, float partialTick) {
        if (this.isRemoved()) {
            partialTick = 1.0F;
        }
        int i = this.trailPointer - pointer & 63;
        int j = this.trailPointer - pointer - 1 & 63;
        Vec3 d0 = this.trailPositions[j];
        Vec3 d1 = this.trailPositions[i].subtract(d0);
        return d0.add(d1.scale(partialTick));
    }

    public boolean hasTrail() {
        return this.trailPointer != -1;
    }

    private void glow() {
        this.getGlow = Mth.clamp(this.getGlow + this.glowAmount, 1, 1.5F);
        if (this.getGlow == 1 || this.getGlow == 1.5F) {
            this.glowAmount *= -1;
        }
    }

    protected ParticleOptions getTrailParticle() {
        return ModParticleTypes.NONE.get();
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
