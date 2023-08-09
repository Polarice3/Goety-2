package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class AbstractTrap extends Entity {
    private int duration = 600;
    public LivingEntity owner;
    private UUID ownerUniqueId;
    private static final EntityDataAccessor<ParticleOptions> DATA_PARTICLE = SynchedEntityData.defineId(AbstractTrap.class, EntityDataSerializers.PARTICLE);

    public AbstractTrap(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_PARTICLE, ParticleTypes.ENTITY_EFFECT);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("Duration")) {
            this.duration = compound.getInt("Duration");
        }
        if (compound.hasUUID("Owner")) {
            this.ownerUniqueId = compound.getUUID("Owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Duration", this.duration);
        if (this.ownerUniqueId != null) {
            compound.putUUID("Owner", this.ownerUniqueId);
        }
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int durationIn) {
        this.duration = durationIn;
    }

    public void setOwner(@Nullable LivingEntity ownerIn) {
        this.owner = ownerIn;
        this.ownerUniqueId = ownerIn == null ? null : ownerIn.getUUID();
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUniqueId != null && this.level instanceof ServerLevel) {
            Entity entity = ((ServerLevel)this.level).getEntity(this.ownerUniqueId);
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity)entity;
            }
        }

        return this.owner;
    }

    public float radius(){
        return 3.0F;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getParticle() != null) {
            if (this.level instanceof ServerLevel serverWorld) {
                ParticleOptions iparticledata = this.getParticle();
                ServerParticleUtil.circularParticles(serverWorld, iparticledata, this.getX(), this.getY(), this.getZ(), this.radius());
            }
        }
    }

    @Nullable
    public ParticleOptions getParticle() {
        return this.getEntityData().get(DATA_PARTICLE);
    }

    public void setParticle(ParticleOptions pParticleData) {
        this.getEntityData().set(DATA_PARTICLE, pParticleData);
    }

    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
