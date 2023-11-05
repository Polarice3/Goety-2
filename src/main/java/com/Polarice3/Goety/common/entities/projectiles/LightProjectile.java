package com.Polarice3.Goety.common.entities.projectiles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

/**
 * Light Projectile based of codes from @baileyholl
 */
public abstract class LightProjectile extends Arrow {
    public LightProjectile(EntityType<? extends LightProjectile> p_i50147_1_, Level p_i50147_2_) {
        super(p_i50147_1_, p_i50147_2_);
    }

    public LightProjectile(final Level world, final double x, final double y, final double z) {
        super(world, x, y, z);
    }

    public LightProjectile(final Level world, final LivingEntity shooter) {
        super(world, shooter);
    }

    @Override
    public void tick() {
        Vec3 Vec3 = this.getDeltaMovement();

        this.xOld = this.getX();
        this.yOld = this.getY();
        this.zOld = this.getZ();

        if (this.tickCount % 600 == 0){
            this.discard();
        }

        if (this.inGround) {
            this.inGround = false;
            this.setDeltaMovement(this.getDeltaMovement());
        }

        Vec3 Vec32 = this.position();
        Vec3 Vec33 = Vec32.add(Vec3);

        HitResult raytraceresult = this.level.clip(new ClipContext(Vec32, Vec33, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this));
        if (raytraceresult.getType() != HitResult.Type.MISS) {
            Vec33 = raytraceresult.getLocation();
        }

        EntityHitResult entityraytraceresult = this.findHitEntity(Vec32, Vec33);
        if (entityraytraceresult != null) {
            raytraceresult = entityraytraceresult;
        }

        if (raytraceresult instanceof EntityHitResult) {
            Entity entity = ((EntityHitResult)raytraceresult).getEntity();
            Entity entity1 = this.getOwner();
            if (entity instanceof Player && entity1 instanceof Player && !((Player)entity1).canHarmPlayer((Player)entity)) {
                raytraceresult = null;
            }
        }

        if (raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS  && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
            this.onHit(raytraceresult);
            this.hasImpulse = true;
        }

        double x = this.getX() + Vec3.x;
        double y = this.getY() + Vec3.y;
        double z = this.getZ() + Vec3.z;

        this.setPos(x,y,z);

        if(level.isClientSide) {
            double d0 = this.getX() + Vec3.x;
            double d1 = this.getY() + Vec3.y;
            double d2 = this.getZ() + Vec3.z;
            this.level.addParticle(sourceParticle(), this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F, 0.0F);
            this.level.addParticle(trailParticle(), d0 + level.random.nextDouble()/2, d1 + 0.5D, d2 + level.random.nextDouble()/2, 0.0D, 0.0D, 0.0D);
        }
    }

    protected void onHitEntity(EntityHitResult pResult) {
        if (!this.level.isClientSide) {
            Entity target = pResult.getEntity();
            Entity owner = this.getOwner();
            if (owner instanceof LivingEntity) {
                if (target instanceof LivingEntity){
                    LivingEntity livingTarget = (LivingEntity) target;
                    if (livingTarget.isAlive()){
                        livingTarget.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200));
                    }
                }
            } else {
                if (target instanceof LivingEntity){
                    LivingEntity livingTarget = (LivingEntity) target;
                    if (livingTarget.isAlive()){
                        livingTarget.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200));
                    }
                }
            }
        }
        this.discard();
    }

    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        BlockPos pos = pResult.getBlockPos().relative(pResult.getDirection());
        if (this.getOwner() != null) {
            if (this.level.getBlockState(pos).getMaterial().isReplaceable() && this.level.isUnobstructed(LightBlock().defaultBlockState(), pos, CollisionContext.of(this.getOwner()))) {
                this.level.setBlockAndUpdate(pos, LightBlock().defaultBlockState());
            }
        }
        this.discard();
    }

    public void shootFromRotation(Entity entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy) {
        float f = -Mth.sin(rotationYawIn * ((float)Math.PI / 180F)) * Mth.cos(rotationPitchIn * ((float)Math.PI / 180F));
        float f1 = -Mth.sin((rotationPitchIn + pitchOffset) * ((float)Math.PI / 180F));
        float f2 = Mth.cos(rotationYawIn * ((float)Math.PI / 180F)) * Mth.cos(rotationPitchIn * ((float)Math.PI / 180F));
        this.shoot(f, f1, f2, velocity, inaccuracy);
        Vec3 vec3d = entityThrower.getLookAngle();
        this.setDeltaMovement(this.getDeltaMovement().add(vec3d.x, vec3d.y, vec3d.z));
    }

    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        Vec3 vec3d = (new Vec3(x, y, z)).normalize().add(this.random.nextGaussian() * (double)0.0075F * (double)inaccuracy, this.random.nextGaussian() * (double)0.0075F * (double)inaccuracy, this.random.nextGaussian() * (double)0.0075F * (double)inaccuracy).scale(velocity);
        this.setDeltaMovement(vec3d);
        float f = Mth.sqrt(getHorizontalDistanceSqr(vec3d));
        this.setYRot((float)(Mth.atan2(vec3d.x, vec3d.z) * (double)(180F / (float)Math.PI)));
        this.setXRot((float)(Mth.atan2(vec3d.y, f) * (double)(180F / (float)Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    public static float getHorizontalDistanceSqr(Vec3 pVector) {
        return (float) (pVector.x * pVector.x + pVector.z * pVector.z);
    }

    public abstract ParticleOptions sourceParticle();

    public abstract ParticleOptions trailParticle();

    public abstract Block LightBlock();

    @Override
    public abstract EntityType<?> getType();

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.SNOW_GOLEM_AMBIENT;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    public boolean isOnFire() {
        return false;
    }

    public boolean isPickable() {
        return false;
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        return false;
    }
}
