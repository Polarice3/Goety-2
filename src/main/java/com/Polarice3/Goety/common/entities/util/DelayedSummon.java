package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class DelayedSummon extends Entity {
    protected static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(DelayedSummon.class, EntityDataSerializers.OPTIONAL_UUID);
    public Entity entity;
    public boolean preMade;
    public boolean noPos;
    public int lifeSpan = 20;

    public DelayedSummon(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public DelayedSummon(Level pLevel, Vec3 pPos, Entity pEntity, boolean preMade, boolean noPos, @Nullable LivingEntity pOwner){
        this(ModEntityType.DELAYED_SUMMON.get(), pLevel);
        this.setPos(pPos.x, pPos.y, pPos.z);
        this.entity = pEntity;
        this.preMade = preMade;
        this.noPos = noPos;
        this.setTrueOwner(pOwner);
    }

    public DelayedSummon(Level pLevel, BlockPos pPos, Entity pEntity, boolean preMade, boolean noPos, @Nullable LivingEntity pOwner){
        this(ModEntityType.DELAYED_SUMMON.get(), pLevel);
        this.setPos(pPos.getX(), pPos.getY(), pPos.getZ());
        this.entity = pEntity;
        this.preMade = preMade;
        this.noPos = noPos;
        this.setTrueOwner(pOwner);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(OWNER_UNIQUE_ID, Optional.empty());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        UUID uuid;
        if (pCompound.hasUUID("Owner")) {
            uuid = pCompound.getUUID("Owner");
        } else {
            String s = pCompound.getString("Owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (uuid != null) {
            try {
                this.setOwnerId(uuid);
            } catch (Throwable ignored) {
            }
        }
        this.preMade = pCompound.getBoolean("preMade");
        this.noPos = pCompound.getBoolean("noPos");
        this.lifeSpan = pCompound.getInt("lifeSpan");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        if (this.getOwnerId() != null) {
            pCompound.putUUID("Owner", this.getOwnerId());
        }
        pCompound.putBoolean("preMade", this.preMade);
        pCompound.putBoolean("noPos", this.noPos);
        pCompound.putInt("lifeSpan", this.lifeSpan);
    }

    @Nullable
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

    public void setTrueOwner(@Nullable LivingEntity livingEntity){
        if (livingEntity != null) {
            this.setOwnerId(livingEntity.getUUID());
        }
    }

    public void setLifeSpan(int lifeSpan){
        this.lifeSpan = lifeSpan;
    }

    public int getLifeSpan() {
        if (this.lifeSpan == 0){
            return 20;
        }
        return this.lifeSpan;
    }

    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    public void tick() {
        super.tick();
        if (!this.isNoGravity()) {
            MobUtil.moveDownToGround(this);
        }
        if (this.level instanceof ServerLevel serverWorld) {
            if (this.tickCount == this.getLifeSpan()){
                if (this.entity != null){
                    if (this.noPos) {
                        this.entity.setPos(this.getX(), this.getY(), this.getZ());
                    }
                    if (this.preMade) {
                        if (this.entity instanceof TamableAnimal && this.getOwnerId() != null) {
                            ((TamableAnimal) this.entity).setOwnerUUID(this.getOwnerId());
                        }
                        if (this.entity instanceof IOwned && this.getTrueOwner() != null) {
                            ((IOwned) this.entity).setTrueOwner(this.getTrueOwner());
                        }
                        if (this.entity instanceof Mob) {
                            ((Mob) this.entity).finalizeSpawn(serverWorld, this.level.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                            if (this.getTrueOwner() != null && this.getTrueOwner() instanceof Mob) {
                                if (((Mob) this.getTrueOwner()).getTarget() != null) {
                                    ((Mob) this.entity).setTarget(((Mob) this.getTrueOwner()).getTarget());
                                }
                            }
                        }
                    }
                    serverWorld.addFreshEntity(entity);
                }
            }
        }
        if (this.tickCount >= this.getLifeSpan()){
            this.discard();
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
