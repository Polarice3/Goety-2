package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.hostile.SkullLord;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class SkullLaser extends Mob {
    private static final EntityDataAccessor<Optional<UUID>> SKULL_LORD = SynchedEntityData.defineId(SkullLaser.class, EntityDataSerializers.OPTIONAL_UUID);
    private int duration = 600;
    private int durationOnUse;

    public SkullLaser(EntityType<? extends Mob> p_i48576_1_, Level p_i48576_2_) {
        super(p_i48576_1_, p_i48576_2_);
    }

    public float getStepHeight() {
        return 1.0F;
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23F);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SKULL_LORD, Optional.empty());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        this.duration = pCompound.getInt("Duration");
        this.durationOnUse = pCompound.getInt("DurationOnUse");

        UUID uuid;
        if (pCompound.hasUUID("skullLord")) {
            uuid = pCompound.getUUID("skullLord");
        } else {
            String s = pCompound.getString("skullLord");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (uuid != null) {
            try {
                this.setSkullLordUUID(uuid);
            } catch (Throwable ignored) {
            }
        }

    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("Duration", this.duration);
        pCompound.putInt("DurationOnUse", this.durationOnUse);

        if (this.getSkullLordUUID() != null) {
            pCompound.putUUID("skullLord", this.getSkullLordUUID());
        }
    }

    @Nullable
    public SkullLord getSkullLord() {
        try {
            UUID uuid = this.getSkullLordUUID();
            if (uuid != null){
                if (EntityFinder.getLivingEntityByUuiD(uuid) instanceof SkullLord){
                    return (SkullLord) EntityFinder.getLivingEntityByUuiD(uuid);
                }
            }
            return null;
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    @Nullable
    public UUID getSkullLordUUID() {
        return this.entityData.get(SKULL_LORD).orElse(null);
    }

    public void setSkullLordUUID(UUID uuid){
        this.entityData.set(SKULL_LORD, Optional.ofNullable(uuid));
    }

    public void setSkullLord(SkullLord skullLord){
        this.setSkullLordUUID(skullLord.getUUID());
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
        if (!this.level.isClientSide) {
            if (this.getSkullLord() == null || this.getSkullLord().isDeadOrDying() || this.getSkullLord().isInvulnerable()) {
                if (this.tickCount % 20 == 0) {
                    this.discard();
                }
            } else {
                AttributeInstance gravity = this.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get());
                if (gravity != null) {
                    gravity.setBaseValue(0.8D);
                }
                if (this.getSkullLord().isHalfHealth()) {
                    Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(0.3D);
                    if (this.level.getDifficulty() == Difficulty.HARD) {
                        this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 1, false, false));
                    }
                }
                if (this.getSkullLord().getTarget() != null) {
                    this.setTarget(this.getSkullLord().getTarget());
                } else {
                    if (this.tickCount % 20 == 0) {
                        this.discard();
                    }
                }
                if (this.getTarget() != null) {
                    if (this.tickCount >= 20) {
                        if (this.tickCount % 2 == 0) {
                            this.level.explode(this, this.getX(), this.getY(), this.getZ(), 0.25F, Level.ExplosionInteraction.NONE);
                        }
                        this.moveControl.setWantedPosition(this.getTarget().getX(), this.getY(), this.getTarget().getZ(), 1.0F);
                        for (LivingEntity livingEntity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.5F))) {
                            if (livingEntity != this.getSkullLord() && MobUtil.areNotFullAllies(this.getSkullLord(), livingEntity) && livingEntity != this) {
                                this.getTarget().hurt(damageSources().indirectMagic(this, this.getSkullLord()), (float) this.getSkullLord().getAttributeBaseValue(Attributes.ATTACK_DAMAGE));
                                this.getTarget().addEffect(new MobEffectInstance(GoetyEffects.SAPPED.get(), 100));
                            }
                        }
                    }
                }
            }
            if (this.tickCount >= this.getDuration()) {
                if (this.getSkullLord() != null) {
                    this.getSkullLord().boneLordRegen = (50 + this.random.nextInt(100));
                }
                this.discard();
            }
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
