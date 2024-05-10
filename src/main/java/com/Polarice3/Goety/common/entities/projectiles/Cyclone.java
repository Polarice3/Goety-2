package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Cyclone extends SpellHurtingProjectile {
    protected static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(Cyclone.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Optional<UUID>> TARGET_UNIQUE_ID = SynchedEntityData.defineId(Cyclone.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(Cyclone.class, EntityDataSerializers.FLOAT);
    private int lifespan;
    private int totalLife = 600;
    private int spun = 0;

    public Cyclone(EntityType<? extends SpellHurtingProjectile> p_i50173_1_, Level p_i50173_2_) {
        super(p_i50173_1_, p_i50173_2_);
        this.noPhysics = false;
        this.lifespan = 0;
    }

    public Cyclone(Level level, LivingEntity shooter, double xPower, double yPower, double zPower) {
        super(ModEntityType.CYCLONE.get(), shooter, xPower, yPower, zPower, level);
        this.noPhysics = false;
        this.lifespan = 0;
    }

    public Cyclone(Level p_i1795_1_, double p_i1795_2_, double p_i1795_4_, double p_i1795_6_, double p_i1795_8_, double p_i1795_10_, double p_i1795_12_) {
        super(ModEntityType.CYCLONE.get(), p_i1795_2_, p_i1795_4_, p_i1795_6_, p_i1795_8_, p_i1795_10_, p_i1795_12_, p_i1795_1_);
        this.noPhysics = false;
        this.lifespan = 0;
    }

    @Override
    protected float getInertia() {
        return 0.68F + Math.min(this.boltSpeed, 0.32F);
    }

    public int getTotalLife() {
        return totalLife;
    }

    public void setTotalLife(int totalLife) {
        this.totalLife = totalLife;
    }

    public int getLifespan() {
        return lifespan;
    }

    public void setLifespan(int lifespan) {
        this.lifespan = lifespan;
    }

    public int getSpun(){
        return spun;
    }

    public void setSpun(int spun){
        this.spun = spun;
    }

    public LivingEntity getTrueOwner() {
        try {
            UUID uuid = this.getOwnerId();
            return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(uuid);
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    @Nullable
    public UUID getOwnerId() {
        return this.entityData.get(OWNER_UNIQUE_ID).orElse((UUID)null);
    }

    public void setOwnerId(@Nullable UUID p_184754_1_) {
        this.entityData.set(OWNER_UNIQUE_ID, Optional.ofNullable(p_184754_1_));
    }

    public void setOwner(LivingEntity livingEntity){
        if (livingEntity != null) {
            this.setOwnerId(livingEntity.getUUID());
        }
    }

    @Nullable
    public LivingEntity getTarget() {
        try {
            UUID uuid = this.getTargetId();
            return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(uuid);
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    @Nullable
    public UUID getTargetId() {
        return this.entityData.get(TARGET_UNIQUE_ID).orElse((UUID)null);
    }

    public void setTargetId(@Nullable UUID p_184754_1_) {
        this.entityData.set(TARGET_UNIQUE_ID, Optional.ofNullable(p_184754_1_));
    }

    public void setTarget(@Nullable LivingEntity livingEntity) {
        if (livingEntity != null) {
            this.setTargetId(livingEntity.getUUID());
        }
    }

    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            if (this.getLifespan() < getTotalLife()) {
                ++this.lifespan;
            } else {
                this.trueRemove();
            }
            if (this.getTrueOwner() != null){
                if (this.getTarget() != null){
                    LivingEntity livingentity = this.getTarget();
                    double d1 = livingentity.getX() - this.getX();
                    double d2 = livingentity.getY(0.5D) - this.getY(0.5D);
                    double d3 = livingentity.getZ() - this.getZ();
                    if (this.tickCount % 50 == 0) {
                        this.fakeRemove(d1, d2, d3);
                    }
                }
            }
            int maxSpun = 80;
            if (this.getSpun() >= maxSpun) {
                this.trueRemove();
            }
            if (this.tickCount % 40 == 0) {
                this.playSound(ModSounds.FLIGHT.get(), 0.5F, 0.5F);
            }
            List<LivingEntity> targets = new ArrayList<>();
            for (LivingEntity entity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(this.getSize()))) {
                if (this.getTrueOwner() != null) {
                    if (entity != this.getTrueOwner() && !MobUtil.areAllies(this.getTrueOwner(), entity)) {
                        targets.add(entity);
                    }
                } else {
                    targets.add(entity);
                }
            }
            if (!targets.isEmpty()) {
                for (LivingEntity entity : targets) {
                    if (MobUtil.validEntity(entity)) {
                        this.suckInMobs(entity);
                    }
                }
            }
        }
    }

    public void trueRemove(){
        this.setLifespan(this.getTotalLife());
        this.remove();
    }

    public void fakeRemove(double x, double y, double z){
        Cyclone cyclone = new Cyclone(this.level, this.getTrueOwner(), x, y, z);
        cyclone.setOwner(this.getTrueOwner());
        cyclone.setTarget(this.getTarget());
        cyclone.setLifespan(this.getLifespan());
        cyclone.setTotalLife(this.getTotalLife());
        cyclone.setSpun(this.getSpun());
        cyclone.setSize(this.getSize());
        cyclone.setPos(this.getX(), this.getY(), this.getZ());
        this.level.addFreshEntity(cyclone);
        this.remove();
    }

    public void remove() {
        if (!this.level.isClientSide){
            if (this.getLifespan() >= this.getTotalLife()) {
                ServerLevel serverWorld = (ServerLevel) this.level;
                for (int k = 0; k < 50; ++k) {
                    float f2 = random.nextFloat() * 4.0F;
                    float f1 = random.nextFloat() * ((float) Math.PI * 2F);
                    double d1 = Mth.cos(f1) * f2;
                    double d2 = 0.01D + random.nextDouble() * 0.5D;
                    double d3 = Mth.sin(f1) * f2;
                    serverWorld.sendParticles(ParticleTypes.CLOUD, this.getX() + d1 * 0.1D, this.getY() + 0.3D, this.getZ() + d3 * 0.1D, 0, d1, d2, d3, 0.5F);
                }
            }
        }
        this.discard();
    }

    /**
     * Based on EntityDuster lift codes from @AlexModGuy's Alex's Mobs.
     */
    private void suckInMobs(LivingEntity livingEntity) {
        ++this.spun;
        float radius = this.getSize() + ((this.spun % 20) * 0.05F);
        float knockBack = (float) Mth.clamp((1.0D - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)), 0, 1);
        float angle = (this.spun % 20) * -0.25F;
        double f0 = this.getX() + radius * Mth.sin((float) (Math.PI + angle));
        double f1 = this.getZ() + radius * Mth.cos(angle);
        double d0 = (f0 - livingEntity.getX()) * knockBack;
        double d1 = (f1 - livingEntity.getZ()) * knockBack;
        if (this.xPower != 0 || this.yPower != 0 || this.zPower != 0){
            if (this.getTrueOwner() != null) {
                this.fakeRemove(0, 0, 0);
            }
        }
        if (this.getTarget() == null || this.getTarget().isDeadOrDying()){
            this.setTarget(livingEntity);
        }

        MobUtil.twister(livingEntity, d0, 0.1 * knockBack, d1);
    }

    public void setSize(float p_33109_) {
        this.entityData.set(DATA_RADIUS, Mth.clamp(p_33109_, 0, 64));
    }

    public float getSize() {
        return this.entityData.get(DATA_RADIUS);
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

    protected void defineSynchedData() {
        this.entityData.define(OWNER_UNIQUE_ID, Optional.empty());
        this.entityData.define(TARGET_UNIQUE_ID, Optional.empty());
        this.entityData.define(DATA_RADIUS, 1.0F);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_33134_) {
        if (DATA_RADIUS.equals(p_33134_)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(p_33134_);
    }

    public EntityDimensions getDimensions(Pose p_33113_) {
        float i = this.getSize();
        EntityDimensions entitydimensions = super.getDimensions(p_33113_);
        float f = (entitydimensions.width + (0.2F * i)) / entitydimensions.width;
        return entitydimensions.scale(f);
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

        if (compound.contains("Lifespan")) {
            this.setLifespan(compound.getInt("Lifespan"));
        }
        if (compound.contains("TotalLife")) {
            this.setTotalLife(compound.getInt("TotalLife"));
        }
        if (compound.contains("Spun")){
            this.setSpun(compound.getInt("Spun"));
        }
        if (compound.contains("Size")){
            this.setSize(compound.getFloat("Size"));
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
        compound.putInt("Lifespan", this.getLifespan());
        compound.putInt("TotalLife", this.getTotalLife());
        compound.putInt("Spun", this.getSpun());
        compound.putFloat("Size", this.getSize());
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.CLOUD;
    }

    protected boolean canHitEntity(Entity p_36842_) {
        return super.canHitEntity(p_36842_) && !p_36842_.noPhysics;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
