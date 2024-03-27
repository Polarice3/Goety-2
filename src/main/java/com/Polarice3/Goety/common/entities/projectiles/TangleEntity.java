package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class TangleEntity extends Entity {
    protected int warmupDelayTicks;
    protected int activeTick;
    public int lifeSpan = 80;
    @Nullable
    protected LivingEntity target;
    @Nullable
    protected UUID targetUUID;
    @Nullable
    protected LivingEntity owner;
    @Nullable
    protected UUID ownerUUID;

    public TangleEntity(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public TangleEntity(EntityType<?> p_19870_, Level p_19871_, @Nullable LivingEntity owner, Entity target) {
        this(p_19870_, p_19871_);
        this.setOwner(owner);
        this.setPos(target.position());
    }

    public TangleEntity(EntityType<?> p_19870_, Level p_19871_, @Nullable LivingEntity owner, BlockPos blockPos) {
        this(p_19870_, p_19871_);
        this.setOwner(owner);
        this.setPos(blockPos.getCenter());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.contains("Warmup")) {
            this.warmupDelayTicks = pCompound.getInt("Warmup");
        }
        if (pCompound.contains("ActiveTick")) {
            this.activeTick = pCompound.getInt("ActiveTick");
        }
        if (pCompound.contains("LifeSpan")) {
            this.lifeSpan = pCompound.getInt("LifeSpan");
        }
        if (pCompound.hasUUID("Target")) {
            this.targetUUID = pCompound.getUUID("Target");
        }
        if (pCompound.hasUUID("Owner")) {
            this.ownerUUID = pCompound.getUUID("Owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("Warmup", this.warmupDelayTicks);
        pCompound.putInt("ActiveTick", this.activeTick);
        pCompound.putInt("LifeSpan", this.lifeSpan);
        if (this.targetUUID != null) {
            pCompound.putUUID("Target", this.targetUUID);
        }
        if (this.ownerUUID != null) {
            pCompound.putUUID("Owner", this.ownerUUID);
        }
    }

    public void setLifeSpan(int lifeSpan){
        this.lifeSpan = lifeSpan;
    }

    public int getLifeSpan(){
        return this.lifeSpan;
    }

    public void setTarget(@Nullable LivingEntity p_36939_) {
        this.target = p_36939_;
        this.targetUUID = p_36939_ == null ? null : p_36939_.getUUID();
    }

    @Nullable
    public LivingEntity getTarget() {
        if (this.target == null && this.targetUUID != null && this.level instanceof ServerLevel) {
            Entity entity = ((ServerLevel)this.level).getEntity(this.targetUUID);
            if (entity instanceof LivingEntity) {
                this.target = (LivingEntity)entity;
            }
        }

        return this.target;
    }

    public void setOwner(@Nullable LivingEntity p_36939_) {
        this.owner = p_36939_;
        this.ownerUUID = p_36939_ == null ? null : p_36939_.getUUID();
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

    @Override
    public boolean isInvisible() {
        return super.isInvisible() || this.activeTick < 1;
    }

    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            MobUtil.moveDownToGround(this);
            if (this.warmupDelayTicks > 0){
                --this.warmupDelayTicks;
            } else {
                ++this.activeTick;
                this.level.broadcastEntityEvent(this, (byte) 6);
                this.entangleTick();
            }
        }
    }

    public void entangleTick(){
        if (this.activeTick == 1){
            this.burst();
        } else {
            if (this.activeTick < 20){
                this.findTarget();
            }
            if (this.activeTick < this.lifeSpan) {
                this.tangleTarget();
            } else if (this.activeTick == this.lifeSpan) {
                this.burrow();
            } else if (this.activeTick >= (this.lifeSpan + 20)){
                this.discard();
            }
        }
    }

    public void burst(){
    }

    public void findTarget(){
        if (this.getTarget() == null) {
            for (LivingEntity livingEntity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())) {
                if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
                    if (!MobUtil.areAllies(livingEntity, this.getOwner())) {
                        this.setTarget(livingEntity);
                    }
                }
            }
        }
    }

    public void tangleTarget(){
        if (this.getTarget() != null){
            this.getTarget().setPos(this.position());
            this.getTarget().addEffect(new MobEffectInstance(GoetyEffects.TANGLED.get(), 2, 0, false, false, false));
        }
    }

    public void burrow(){
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 6){
            ++this.activeTick;
        } else {
            super.handleEntityEvent(pId);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
