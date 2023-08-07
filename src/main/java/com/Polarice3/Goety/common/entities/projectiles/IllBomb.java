package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

/**
 * Bouncing mechanic based from codes by @ModdingLegacy used in The Conjurer.
 */
public class IllBomb extends ThrowableProjectile {
    private static final EntityDataAccessor<Integer> BOUNCE_TIMES = SynchedEntityData.defineId(IllBomb.class, EntityDataSerializers.INT);

    public IllBomb(EntityType<? extends ThrowableProjectile> p_37466_, Level p_37467_) {
        super(p_37466_, p_37467_);
    }

    public IllBomb(double p_37457_, double p_37458_, double p_37459_, Level p_37460_) {
        super(ModEntityType.ILL_BOMB.get(), p_37457_, p_37458_, p_37459_, p_37460_);
    }

    public IllBomb(LivingEntity p_37463_, Level p_37464_) {
        super(ModEntityType.ILL_BOMB.get(), p_37463_, p_37464_);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(BOUNCE_TIMES, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("BounceTimes", this.getBounceTimes());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        this.setBounceTimes(compound.getInt("BounceTimes"));
    }

    public int getBounceTimes() {
        return this.entityData.get(BOUNCE_TIMES);
    }

    public void setBounceTimes(int bounce) {
        this.entityData.set(BOUNCE_TIMES, bounce);
    }

    @Override
    protected void onHit(HitResult result) {
        HitResult.Type raytraceresult$type = result.getType();
        if (raytraceresult$type == HitResult.Type.BLOCK) {
            BlockHitResult hitResult = (BlockHitResult) result;
            BlockState blockstate = this.level.getBlockState(hitResult.getBlockPos());
            if (!blockstate.getCollisionShape(this.level, hitResult.getBlockPos()).isEmpty()) {
                Direction face = hitResult.getDirection();
                blockstate.onProjectileHit(this.level, blockstate, hitResult, this);
                Vec3 vec3 = this.getDeltaMovement();
                double motionX = vec3.x();
                double motionY = vec3.y();
                double motionZ = vec3.z();
                if (face.getAxis() == Direction.Axis.X) {
                    motionX = -motionX;
                } else if (face.getAxis() == Direction.Axis.Z) {
                    motionZ = -motionZ;
                } else if (face.getAxis().isVertical()) {
                    motionY = -motionY;
                }
                this.setDeltaMovement(motionX, motionY, motionZ);
                if (this.tickCount > 25 || this.getBounceTimes() >= 1) {
                    this.explode();
                } else {
                    this.playSound(SoundEvents.CHAIN_STEP, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                    this.setBounceTimes(this.getBounceTimes() + 1);
                }
            }
        } else if (raytraceresult$type == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) result).getEntity();
            if (!this.level.isClientSide) {
                if (canHitEntity(entity)) {
                    if (entity instanceof LivingEntity living) {
                        if (living.isBlocking()) {
                            IllBomb snowball = new IllBomb(living, this.level);
                            snowball.shootFromRotation(living, living.getXRot(), living.getYRot(), 0.0F, 1.5F, 1.0F);
                            this.level.addFreshEntity(snowball);
                            MobUtil.hurtUsedShield(living, 1);
                            this.level.broadcastEntityEvent(living, (byte)29);
                            this.discard();
                        } else {
                            this.explode();
                        }
                    } else {
                        this.explode();
                    }
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount > 300 || (this.tickCount >= 25 && this.getBounceTimes() >= 1)) {
            this.explode();
        } else {
            if (this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().scale(1.01D));
            }
            for (int i = 0; i < 2; ++i) {
                this.level.addParticle(ParticleTypes.SMOKE, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), this.random.nextGaussian() * 0.1D, 0.0D, this.random.nextGaussian() * 0.1D);
            }
        }

    }

    public void explode(){
        if (!this.level.isClientSide) {
            Entity owner = this.getOwner();
            if (this.level.random.nextFloat() >= 0.25F) {
                this.level.explode(owner != null ? owner : this, this.getX(), this.getY(), this.getZ(), 2.0F, Explosion.BlockInteraction.NONE);
            } else {
                if (this.level instanceof ServerLevel serverLevel){
                    serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 0);
                    this.playSound(SoundEvents.GENERIC_EXPLODE);
                }
                for (int i = 0; i < 8 + this.level.random.nextInt(16); ++i){
                    Projectile arrow = new FireworkRocketEntity(this.level, this.getProjectile(), owner != null ? owner : this, this.getX(), this.getY(), this.getZ(), true);
                    float yaw = this.random.nextFloat() * 360;
                    float pitch = this.random.nextFloat() * 90 - 75;
                    arrow.shootFromRotation(this, yaw, pitch, 0.0F, 3.0F, 0.1F);
                    this.level.addFreshEntity(arrow);
                }
            }
            this.discard();
        }
    }

    @Override
    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null && (this.getOwner().isAlliedTo(pEntity) || pEntity.isAlliedTo(this.getOwner()))){
            return false;
        } else {
            return super.canHitEntity(pEntity);
        }
    }

    public ItemStack getProjectile() {
        int difficulty = this.level.getCurrentDifficultyAt(this.blockPosition()).getDifficulty().getId();
        return MobUtil.createFirework(difficulty * 2, DyeColor.values());
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
