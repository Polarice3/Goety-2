package com.Polarice3.Goety.common.entities.projectiles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Based on WaveEntity by @AlexModGuy: <a href="https://github.com/AlexModGuy/AlexsCaves/blob/main/src/main/java/com/github/alexmodguy/alexscaves/server/entity/item/WaveEntity.java">...</a>
 **/
public abstract class AbstractWave extends SpellEntity {
    private static final EntityDataAccessor<Boolean> SLAMMING = SynchedEntityData.defineId(AbstractWave.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> LIFESPAN = SynchedEntityData.defineId(AbstractWave.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> WAITING_TICKS = SynchedEntityData.defineId(AbstractWave.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> Y_ROT = SynchedEntityData.defineId(AbstractWave.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> WAVE_SCALE = SynchedEntityData.defineId(AbstractWave.class, EntityDataSerializers.FLOAT);
    private float slamProgress;
    private float prevSlamProgress;
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;
    public int activeWaveTicks;
    public boolean isShrink = false;
    public int shrinking = 40;

    public AbstractWave(EntityType p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(SLAMMING, false);
        this.getEntityData().define(LIFESPAN, 10);
        this.getEntityData().define(WAITING_TICKS, 0);
        this.getEntityData().define(Y_ROT, 0.0F);
        this.getEntityData().define(WAVE_SCALE, 1.0F);
    }

    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("Lifespan")) {
            this.setLifespan(compoundTag.getInt("Lifespan"));
        }
    }

    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("Lifespan", this.getLifespan());
    }

    public float getStepHeight() {
        return 2.0F;
    }

    public float getYRot() {
        return this.entityData.get(Y_ROT);
    }

    public void setYRot(float f) {
        this.entityData.set(Y_ROT, f);
    }

    public int getLifespan() {
        return this.entityData.get(LIFESPAN);
    }

    public void setLifespan(int time) {
        this.entityData.set(LIFESPAN, time);
    }

    public int getWaitingTicks() {
        return this.entityData.get(WAITING_TICKS);
    }

    public void setWaitingTicks(int time) {
        this.entityData.set(WAITING_TICKS, time);
    }

    public boolean isSlamming() {
        return this.entityData.get(SLAMMING);
    }

    public void setSlamming(boolean bool) {
        this.entityData.set(SLAMMING, bool);
    }

    public float getWaveScale() {
        return this.entityData.get(WAVE_SCALE);
    }

    public void setWaveScale(float waveScale) {
        this.entityData.set(WAVE_SCALE, waveScale);
    }

    public float getSlamAmount(float partialTicks) {
        return (this.prevSlamProgress + (this.slamProgress - this.prevSlamProgress) * partialTicks) * 0.1F;
    }

    public void spawnParticleAt(float yOffset, float zOffset, float xOffset, ParticleOptions particleType) {
        Vec3 vec3 = new Vec3(xOffset, yOffset, zOffset).yRot((float) Math.toRadians(-this.getYRot()));
        this.level.addParticle(particleType, this.getX() + vec3.x, this.getY() + vec3.y, this.getZ() + vec3.z, this.getDeltaMovement().x, 0.1F, this.getDeltaMovement().z);
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    public void tick() {
        super.tick();
        this.prevSlamProgress = this.slamProgress;
        if (this.isWaitingTick()) {
            return;
        }
        if (this.isSlamming() && this.slamProgress < 10.0F) {
            this.slamProgress += 1.0F;
        }
        if (this.isSlamming() && this.slamProgress >= 10.0F) {
            this.isShrink = true;
        }
        if (this.isShrink) {
            this.shrinkingTick();
        }
        if (!this.isNoGravity() && !this.isInWaterOrBubble()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, (double) -0.04F, 0.0D));
        }
        float f = Math.min(this.activeWaveTicks / 10.0F, 1.0F);
        Vec3 directionVec = new Vec3(0, 0, f * f * 0.2F).yRot((float) Math.toRadians(-this.getYRot()));
        if (this.level.isClientSide) {
            if (this.lSteps > 0) {
                double d5 = this.getX() + (this.lx - this.getX()) / (double) this.lSteps;
                double d6 = this.getY() + (this.ly - this.getY()) / (double) this.lSteps;
                double d7 = this.getZ() + (this.lz - this.getZ()) / (double) this.lSteps;
                this.setYRot(Mth.wrapDegrees((float) this.lyr));
                this.setXRot(this.getXRot() + (float) (this.lxr - (double) this.getXRot()) / (float) this.lSteps);
                --this.lSteps;
                this.setPos(d5, d6, d7);
            } else {
                this.reapplyPosition();
            }
        } else {
            this.reapplyPosition();
            this.setRot(this.getYRot(), this.getXRot());
        }
        if (!this.level.isClientSide) {
            this.attackEntities(getSlamAmount(1.0F) * 2.0F + 1.0F + this.getWaveScale());
        }
        Vec3 vec3 = this.getDeltaMovement().scale(0.9F).add(directionVec);
        this.move(MoverType.SELF, vec3);
        this.setDeltaMovement(vec3.multiply(0.99F, 0.98F, 0.99F));
        if (this.activeWaveTicks > this.getLifespan() || this.activeWaveTicks > 10 && this.getDeltaMovement().horizontalDistance() < 0.04F) {
            this.setSlamming(true);
        }
        this.activeWaveTicks++;
    }

    public boolean isWaitingTick() {
        if (this.getWaitingTicks() > 0) {
            if (!this.level.isClientSide) {
                this.setWaitingTicks(this.getWaitingTicks() - 1);
            }
            this.setInvisible(true);
            return true;
        } else {
            if (this.isInvisible()){
                this.setInvisible(false);
            }
            return false;
        }
    }

    public void shrinkingTick() {
        if (this.getWaveScale() <= 0.0F) {
            this.discard();
        } else {
            this.setWaveScale(this.getWaveScale() - 0.1F);
        }
    }

    public void attackEntities(float scale){
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
        if (WAVE_SCALE.equals(dataAccessor)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(dataAccessor);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        float newDim = Math.max((this.getWaveScale() - 1.0F) * 2.0F, 0.0F);
        return super.getDimensions(pose)
                .scale(this.getWaveScale() + newDim,
                        this.getWaveScale() + newDim);
    }

    @Override
    public void lerpTo(double x, double y, double z, float yr, float xr, int steps, boolean b) {
        this.lx = x;
        this.ly = y;
        this.lz = z;
        this.lyr = yr;
        this.lxr = xr;
        this.lSteps = steps;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    @Override
    public void lerpMotion(double lerpX, double lerpY, double lerpZ) {
        this.lxd = lerpX;
        this.lyd = lerpY;
        this.lzd = lerpZ;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

}
