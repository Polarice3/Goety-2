package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.MobUtil;
import com.google.common.collect.Maps;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DragonBreathCloud extends Entity implements TraceableEntity {
    private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(DragonBreathCloud.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> DATA_WAITING = SynchedEntityData.defineId(DragonBreathCloud.class, EntityDataSerializers.BOOLEAN);
    private static final float MAX_RADIUS = 32.0F;
    private static final float MINIMAL_RADIUS = 0.5F;
    private static final float DEFAULT_RADIUS = 3.0F;
    public static final float DEFAULT_WIDTH = 6.0F;
    public static final float HEIGHT = 0.5F;
    private final Map<Entity, Integer> victims = Maps.newHashMap();
    private int duration = 600;
    private int waitTime = 20;
    private int reapplicationDelay = 20;
    private int durationOnUse;
    private float damage = 12.0F;
    private float radiusOnUse;
    private float radiusPerTick;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;

    public DragonBreathCloud(EntityType<? extends DragonBreathCloud> p_19704_, Level p_19705_) {
        super(p_19704_, p_19705_);
        this.noPhysics = true;
    }

    public DragonBreathCloud(Level p_19707_, double p_19708_, double p_19709_, double p_19710_) {
        this(ModEntityType.DRAGON_BREATH_CLOUD.get(), p_19707_);
        this.setPos(p_19708_, p_19709_, p_19710_);
    }

    protected void defineSynchedData() {
        this.getEntityData().define(DATA_RADIUS, 3.0F);
        this.getEntityData().define(DATA_WAITING, false);
    }

    protected void readAdditionalSaveData(CompoundTag p_19727_) {
        this.tickCount = p_19727_.getInt("Age");
        this.duration = p_19727_.getInt("Duration");
        this.waitTime = p_19727_.getInt("WaitTime");
        this.reapplicationDelay = p_19727_.getInt("ReapplicationDelay");
        this.durationOnUse = p_19727_.getInt("DurationOnUse");
        this.damage = p_19727_.getFloat("Damage");
        this.radiusOnUse = p_19727_.getFloat("RadiusOnUse");
        this.radiusPerTick = p_19727_.getFloat("RadiusPerTick");
        this.setRadius(p_19727_.getFloat("Radius"));
        if (p_19727_.hasUUID("Owner")) {
            this.ownerUUID = p_19727_.getUUID("Owner");
        }

    }

    protected void addAdditionalSaveData(CompoundTag p_19737_) {
        p_19737_.putInt("Age", this.tickCount);
        p_19737_.putInt("Duration", this.duration);
        p_19737_.putInt("WaitTime", this.waitTime);
        p_19737_.putInt("ReapplicationDelay", this.reapplicationDelay);
        p_19737_.putInt("DurationOnUse", this.durationOnUse);
        p_19737_.putFloat("Damage", this.damage);
        p_19737_.putFloat("RadiusOnUse", this.radiusOnUse);
        p_19737_.putFloat("RadiusPerTick", this.radiusPerTick);
        p_19737_.putFloat("Radius", this.getRadius());
        if (this.ownerUUID != null) {
            p_19737_.putUUID("Owner", this.ownerUUID);
        }

    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_19729_) {
        if (DATA_RADIUS.equals(p_19729_)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(p_19729_);
    }

    public void setRadius(float p_19713_) {
        if (!this.level.isClientSide) {
            this.getEntityData().set(DATA_RADIUS, Mth.clamp(p_19713_, 0.0F, 32.0F));
        }
    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    public float getRadius() {
        return this.getEntityData().get(DATA_RADIUS);
    }

    protected void setWaiting(boolean p_19731_) {
        this.getEntityData().set(DATA_WAITING, p_19731_);
    }

    public boolean isWaiting() {
        return this.getEntityData().get(DATA_WAITING);
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int p_19735_) {
        this.duration = p_19735_;
    }

    public float getRadiusOnUse() {
        return this.radiusOnUse;
    }

    public void setRadiusOnUse(float p_19733_) {
        this.radiusOnUse = p_19733_;
    }

    public float getRadiusPerTick() {
        return this.radiusPerTick;
    }

    public void setRadiusPerTick(float p_19739_) {
        this.radiusPerTick = p_19739_;
    }

    public float getDamage() {
        return this.damage;
    }

    public void setDamage(float p_19739_) {
        this.damage = p_19739_;
    }

    public int getDurationOnUse() {
        return this.durationOnUse;
    }

    public void setDurationOnUse(int p_146786_) {
        this.durationOnUse = p_146786_;
    }

    public int getWaitTime() {
        return this.waitTime;
    }

    public void setWaitTime(int p_19741_) {
        this.waitTime = p_19741_;
    }

    public void setOwner(@Nullable LivingEntity p_19719_) {
        this.owner = p_19719_;
        this.ownerUUID = p_19719_ == null ? null : p_19719_.getUUID();
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level instanceof ServerLevel) {
            Entity entity = ((ServerLevel)this.level).getEntity(this.ownerUUID);
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity)entity;
            }
        }

        return this.owner;
    }

    public void tick() {
        super.tick();
        boolean flag = this.isWaiting();
        float f = this.getRadius();
        if (this.level.isClientSide) {
            if (flag && this.random.nextBoolean()) {
                return;
            }

            int i;
            float f1;
            if (flag) {
                i = 2;
                f1 = 0.2F;
            } else {
                i = Mth.ceil((float)Math.PI * f * f);
                f1 = f;
            }

            for(int j = 0; j < i; ++j) {
                float f2 = this.random.nextFloat() * ((float)Math.PI * 2F);
                float f3 = Mth.sqrt(this.random.nextFloat()) * f1;
                double d0 = this.getX() + (double)(Mth.cos(f2) * f3);
                double d2 = this.getY();
                double d4 = this.getZ() + (double)(Mth.sin(f2) * f3);
                double d5;
                double d6;
                double d7;
                if (flag) {
                    d5 = 0.0D;
                    d6 = 0.0D;
                    d7 = 0.0D;
                } else {
                    d5 = (0.5D - this.random.nextDouble()) * 0.15D;
                    d6 = (double)0.01F;
                    d7 = (0.5D - this.random.nextDouble()) * 0.15D;
                }

                this.level.addAlwaysVisibleParticle(ParticleTypes.DRAGON_BREATH, d0, d2, d4, d5, d6, d7);
            }
        } else {
            if (this.tickCount >= this.waitTime + this.duration) {
                this.discard();
                return;
            }

            boolean flag1 = this.tickCount < this.waitTime;
            if (flag != flag1) {
                this.setWaiting(flag1);
            }

            if (flag1) {
                return;
            }

            if (this.radiusPerTick != 0.0F) {
                f += this.radiusPerTick;
                if (f < 0.5F) {
                    this.discard();
                    return;
                }

                this.setRadius(f);
            }

            if (this.tickCount % 5 == 0) {
                this.victims.entrySet().removeIf((p_287380_) -> {
                    return this.tickCount >= p_287380_.getValue();
                });
                List<LivingEntity> list1 = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
                if (!list1.isEmpty()) {
                    for(LivingEntity livingentity : list1) {
                        if (!this.victims.containsKey(livingentity) && !MobUtil.areAllies(livingentity, this.getOwner())) {
                            double d8 = livingentity.getX() - this.getX();
                            double d1 = livingentity.getZ() - this.getZ();
                            double d3 = d8 * d8 + d1 * d1;
                            if (d3 <= (double)(f * f)) {
                                this.victims.put(livingentity, this.tickCount + this.reapplicationDelay);

                                livingentity.hurt(this.damageSources().dragonBreath(), this.getDamage());

                                if (this.radiusOnUse != 0.0F) {
                                    f += this.radiusOnUse;
                                    if (f < 0.5F) {
                                        this.discard();
                                        return;
                                    }

                                    this.setRadius(f);
                                }

                                if (this.durationOnUse != 0) {
                                    this.duration += this.durationOnUse;
                                    if (this.duration <= 0) {
                                        this.discard();
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    public EntityDimensions getDimensions(Pose p_19721_) {
        return EntityDimensions.scalable(this.getRadius() * 2.0F, 0.5F);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
