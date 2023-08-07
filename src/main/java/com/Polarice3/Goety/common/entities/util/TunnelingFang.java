package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class TunnelingFang extends Mob {
    private static final EntityDataAccessor<Optional<UUID>> OWNER = SynchedEntityData.defineId(TunnelingFang.class, EntityDataSerializers.OPTIONAL_UUID);
    private int duration = 600;
    private int coolDown = 0;
    private int durationOnUse;

    public TunnelingFang(EntityType<? extends Mob> p_i48576_1_, Level p_i48576_2_) {
        super(p_i48576_1_, p_i48576_2_);
    }

    public float getStepHeight() {
        return 1.0F;
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35F);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER, Optional.empty());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.contains("Duration")) {
            this.duration = pCompound.getInt("Duration");
        }
        if (pCompound.contains("DurationOnUse")) {
            this.durationOnUse = pCompound.getInt("DurationOnUse");
        }
        if (pCompound.contains("CoolDown")) {
            this.coolDown = pCompound.getInt("CoolDown");
        }

        UUID uuid;
        if (pCompound.hasUUID("owner")) {
            uuid = pCompound.getUUID("owner");
        } else {
            String s = pCompound.getString("owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (uuid != null) {
            try {
                this.setOwnerUUID(uuid);
            } catch (Throwable ignored) {
            }
        }

    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("Duration", this.duration);
        pCompound.putInt("DurationOnUse", this.durationOnUse);
        pCompound.putInt("CoolDown", this.coolDown);

        if (this.getOwnerID() != null) {
            pCompound.putUUID("owner", this.getOwnerID());
        }
    }

    @Nullable
    public LivingEntity getOwner() {
        try {
            UUID uuid = this.getOwnerID();
            if (uuid != null){
                return EntityFinder.getLivingEntityByUuiD(uuid);
            }
            return null;
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    @Nullable
    public UUID getOwnerID() {
        return this.entityData.get(OWNER).orElse(null);
    }

    public void setOwnerUUID(UUID uuid){
        this.entityData.set(OWNER, Optional.ofNullable(uuid));
    }

    public void setOwner(LivingEntity livingEntity){
        this.setOwnerUUID(livingEntity.getUUID());
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int durationIn) {
        this.duration = durationIn;
    }

    public void makeStuckInBlock(BlockState pState, Vec3 pMotionMultiplier) {
        super.makeStuckInBlock(pState, Vec3.ZERO);
    }

    @Override
    public void tick() {
        super.tick();
        ++this.durationOnUse;
        if (this.getOwner() == null || this.getOwner().isDeadOrDying()){
            if (this.durationOnUse % 20 == 0){
                this.discard();
            }
        } else {
            AttributeInstance gravity = this.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get());
            if (gravity != null){
                gravity.setBaseValue(0.8D);
            }
            if (this.getOwner() instanceof Mob mob && mob.getTarget() != null){
                this.setTarget(mob.getTarget());
            } else if (this.getTarget() == null) {
                if (this.durationOnUse % 20 == 0){
                    this.discard();
                }
            }
            if (this.coolDown > 0){
                --this.coolDown;
            }
            if (this.getDeltaMovement().horizontalDistanceSqr() > (double)2.5000003E-7F) {
                int i = Mth.floor(this.getX());
                int j = Mth.floor(this.getY() - (double)0.2F);
                int k = Mth.floor(this.getZ());
                BlockPos pos = new BlockPos(i, j, k);
                BlockState blockstate = this.level.getBlockState(pos);
                if (!blockstate.isAir()) {
                    this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate).setPos(pos), this.getX() + ((double)this.random.nextFloat() - 0.5D) * (double)this.getBbWidth(), this.getY() + 0.1D, this.getZ() + ((double)this.random.nextFloat() - 0.5D) * (double)this.getBbWidth(), 4.0D * ((double)this.random.nextFloat() - 0.5D), 0.5D, ((double)this.random.nextFloat() - 0.5D) * 4.0D);
                }
            }
            if (this.getTarget() != null){
                if (this.durationOnUse >= 20) {
                    this.moveControl.setWantedPosition(this.getTarget().getX(), this.getY(), this.getTarget().getZ(), 1.0F);
                    if (this.coolDown <= 0) {
                        LivingEntity target = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox()).stream()
                                .filter(livingEntity -> livingEntity != this.getOwner()
                                && livingEntity != this
                                && !this.getOwner().isAlliedTo(livingEntity)
                                && !livingEntity.isAlliedTo(this.getOwner())
                                && this.getBoundingBox().intersects(livingEntity.getBoundingBox()))
                                .findFirst().orElse(null);
                        if (target != null){
                            WandUtil.spawnFangs(this, this.getX(), this.getZ(), this.getY(), this.getY() + 1.0D, this.getYRot(), 1);
                            this.coolDown = 25;
                        }
                    }
                }
            }
        }
        if (this.durationOnUse >= this.getDuration()) {
            this.discard();
        }
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        return false;
    }

    public boolean causeFallDamage(float p_146828_, float p_146829_, DamageSource p_146830_) {
        return false;
    }

    @Override
    public boolean ignoreExplosion() {
        return true;
    }

    public boolean isPushable() {
        return false;
    }

    protected void doPush(Entity pEntity) {
    }

    protected void pushEntities() {
    }

    protected boolean isAffectedByFluids() {
        return false;
    }

}
