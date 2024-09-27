package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.utils.EntityFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class VampireBat extends Bat implements IOwned {
    protected static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(VampireBat.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Optional<UUID>> TARGET_ID = SynchedEntityData.defineId(VampireBat.class, EntityDataSerializers.OPTIONAL_UUID);
    @Nullable
    private BlockPos targetPosition;

    public VampireBat(EntityType<? extends Bat> p_27412_, Level p_27413_) {
        super(p_27412_, p_27413_);
        this.setResting(false);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.ATTACK_DAMAGE, 1.0F);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER_UNIQUE_ID, Optional.empty());
        this.entityData.define(TARGET_ID, Optional.empty());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        UUID uuid;
        if (compound.hasUUID("Owner")) {
            uuid = compound.getUUID("Owner");
        } else {
            String s = compound.getString("Owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (uuid != null) {
            try {
                this.setOwnerId(uuid);
            } catch (Throwable ignored) {
            }
        }

        UUID uuid2;
        if (compound.hasUUID("Target")) {
            uuid2 = compound.getUUID("Target");
        } else {
            String s = compound.getString("Target");
            uuid2 = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (uuid2 != null) {
            try {
                this.setTargetId(uuid2);
            } catch (Throwable ignored) {
            }
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getOwnerId() != null) {
            compound.putUUID("Owner", this.getOwnerId());
        }

        if (this.getTargetId() != null) {
            compound.putUUID("Target", this.getTargetId());
        }
    }

    public LivingEntity getTrueOwner() {
        try {
            UUID uuid = this.getOwnerId();
            return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(uuid);
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    @javax.annotation.Nullable
    public UUID getOwnerId() {
        return this.entityData.get(OWNER_UNIQUE_ID).orElse((UUID)null);
    }

    public void setOwnerId(@Nullable UUID p_184754_1_) {
        this.entityData.set(OWNER_UNIQUE_ID, Optional.ofNullable(p_184754_1_));
    }

    public void setTrueOwner(@Nullable LivingEntity livingEntity){
        if (livingEntity != null) {
            this.setOwnerId(livingEntity.getUUID());
        }
    }

    @Override
    public void setHostile(boolean hostile) {
    }

    @Override
    public boolean isHostile() {
        return !(this.getTrueOwner() instanceof Player);
    }

    public LivingEntity getTarget() {
        try {
            UUID uuid = this.getTargetId();
            return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(uuid);
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    @javax.annotation.Nullable
    public UUID getTargetId() {
        return this.entityData.get(TARGET_ID).orElse((UUID)null);
    }

    public void setTargetId(@Nullable UUID p_184754_1_) {
        this.entityData.set(TARGET_ID, Optional.ofNullable(p_184754_1_));
    }

    public void setTarget(LivingEntity livingEntity){
        if (livingEntity != null && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
            this.setTargetId(livingEntity.getUUID());
        }
    }

    protected void doPush(Entity entity) {
        if (!this.level.isClientSide){
            if (entity instanceof LivingEntity livingEntity){
                if (!(livingEntity instanceof Bat) && livingEntity != this.getTrueOwner()){
                    livingEntity.hurt(DamageSource.mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    this.callBats(livingEntity);
                }
            }
        }
    }

    protected void pushEntities() {
        List<Entity> list = this.level.getEntities(this, this.getBoundingBox(), EntitySelector.pushableBy(this));
        if (!list.isEmpty()) {
            for (Entity entity : list) {
                this.doPush(entity);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource p_27424_, float p_27425_) {
        if (p_27424_.getEntity() instanceof LivingEntity living){
            if (this.getTarget() == null && living != this.getTrueOwner() && !(living instanceof Bat)){
                this.setTarget(living);
                this.callBats(living);
            }
        }
        return super.hurt(p_27424_, p_27425_);
    }

    public void callBats(LivingEntity livingEntity){
        for (VampireBat vampireBat : this.level.getEntitiesOfClass(VampireBat.class, this.getBoundingBox().inflate(10.0D))){
            if (vampireBat.getTarget() == null && livingEntity != this.getTrueOwner() && !(livingEntity instanceof Bat)){
                vampireBat.setTarget(livingEntity);
            }
        }
    }

    protected void customServerAiStep() {
        if (this.targetPosition != null && (!this.level.isEmptyBlock(this.targetPosition) || this.targetPosition.getY() <= this.level.getMinBuildHeight())) {
            this.targetPosition = null;
        }

        if (this.targetPosition == null || this.random.nextInt(30) == 0 || this.targetPosition.closerToCenterThan(this.position(), 2.0D)) {
            if (this.getTarget() != null && this.getTarget().isAlive()){
                BlockPos blockPos = new BlockPos(this.getTarget().getEyePosition());
                this.targetPosition = blockPos.offset((double)this.random.nextInt(2) - (double)this.random.nextInt(2), (double)this.random.nextInt(2), (double)this.random.nextInt(2) - (double)this.random.nextInt(2));
            } else {
                this.targetPosition = new BlockPos(this.getX() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7), this.getY() + (double)this.random.nextInt(6) - 2.0D, this.getZ() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7));
            }
        }

        BlockPos blockPos = this.targetPosition;
        if (blockPos != null) {
            double d0 = (double) blockPos.getX() + 0.5D - this.getX();
            double d1 = (double) blockPos.getY() + 0.1D - this.getY();
            double d2 = (double) blockPos.getZ() + 0.5D - this.getZ();
            Vec3 vec3 = this.getDeltaMovement();
            Vec3 vec31 = vec3.add((Math.signum(d0) * 0.5D - vec3.x) * (double) 0.1F, (Math.signum(d1) * (double) 0.7F - vec3.y) * (double) 0.1F, (Math.signum(d2) * 0.5D - vec3.z) * (double) 0.1F);
            this.setDeltaMovement(vec31);
            float f = (float) (Mth.atan2(vec31.z, vec31.x) * (double) (180F / (float) Math.PI)) - 90.0F;
            float f1 = Mth.wrapDegrees(f - this.getYRot());
            this.zza = 0.5F;
            this.setYRot(this.getYRot() + f1);
        }
    }

}
