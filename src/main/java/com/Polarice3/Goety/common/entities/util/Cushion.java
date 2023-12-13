package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class Cushion extends Entity {
    private static final EntityDataAccessor<Integer> DATA_RADIUS = SynchedEntityData.defineId(Cushion.class, EntityDataSerializers.INT);
    public float initialRadius = 3.0F;
    public int lifeTicks = MathHelper.secondsToTicks(10);

    public Cushion(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_RADIUS, 0);
    }

    public void setRadius(int p_19713_) {
        if (!this.level.isClientSide) {
            this.getEntityData().set(DATA_RADIUS, Mth.clamp(p_19713_, 0, 32));
        }
    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    public int getRadius() {
        return this.getEntityData().get(DATA_RADIUS);
    }

    public void increaseLifeSpan(int extra){
        this.lifeTicks += MathHelper.secondsToTicks(extra);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.contains("LifeTicks")){
            this.lifeTicks = pCompound.getInt("LifeTicks");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("LifeTicks", this.lifeTicks);
    }

    @Override
    public void tick() {
        super.tick();
        --this.lifeTicks;
        if (!this.level.isClientSide) {
            if (!this.isNoGravity()) {
                MobUtil.moveDownToGround(this);
            }
            if (this.lifeTicks <= 0){
                this.discard();
            } else if (this.lifeTicks < MathHelper.secondsToTicks(9)){
                if (this.level instanceof ServerLevel serverLevel){
                    ServerParticleUtil.addGroundAuraParticles(serverLevel, ParticleTypes.ENCHANT, this, this.initialRadius + this.getRadius());
                    ServerParticleUtil.circularParticles(serverLevel, ParticleTypes.CLOUD, this, this.initialRadius + this.getRadius());
                }
                for (LivingEntity livingentity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.0D, 3.0D, 1.0D))) {
                    if (livingentity != null && livingentity.isAlive()) {
                        livingentity.resetFallDistance();
                    }
                }
            }
        }
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_19729_) {
        if (DATA_RADIUS.equals(p_19729_)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(p_19729_);
    }

    public EntityDimensions getDimensions(Pose p_19721_) {
        return EntityDimensions.scalable((this.initialRadius + this.getRadius()) * 2.0F, 0.5F);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
