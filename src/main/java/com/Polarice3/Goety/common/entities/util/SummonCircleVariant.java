package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SummonCircleVariant extends Entity {
    protected static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(SummonCircleVariant.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(SummonCircleVariant.class, EntityDataSerializers.INT);
    public static final Map<Integer, ResourceLocation> TEXTURE_BY_TYPE = Util.make(Maps.newHashMap(), (map) -> {
        map.put(0, Goety.location("textures/entity/summon_earth/1.png"));
        map.put(1, Goety.location("textures/entity/summon_earth/2.png"));
        map.put(2, Goety.location("textures/entity/summon_earth/3.png"));
        map.put(3, Goety.location("textures/entity/summon_earth/4.png"));
        map.put(4, Goety.location("textures/entity/summon_earth/5.png"));
        map.put(5, Goety.location("textures/entity/summon_earth/6.png"));
        map.put(6, Goety.location("textures/entity/summon_earth/7.png"));
        map.put(7, Goety.location("textures/entity/summon_earth/8.png"));
        map.put(8, Goety.location("textures/entity/summon_earth/9.png"));
        map.put(9, Goety.location("textures/entity/summon_earth/10.png"));
    });
    public Entity entity;
    public int lifeSpan = 20;

    public SummonCircleVariant(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public SummonCircleVariant(Level pLevel, Vec3 pPos, Entity pEntity, LivingEntity pOwner){
        this(ModEntityType.SUMMON_FIERY.get(), pLevel);
        this.setPos(pPos.x, pPos.y, pPos.z);
        this.entity = pEntity;
        this.setTrueOwner(pOwner);
    }

    public SummonCircleVariant(Level pLevel, BlockPos pPos, Entity pEntity, LivingEntity pOwner){
        this(ModEntityType.SUMMON_FIERY.get(), pLevel);
        this.setPos(pPos.getX(), pPos.getY(), pPos.getZ());
        this.entity = pEntity;
        this.setTrueOwner(pOwner);
    }

    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(OWNER_UNIQUE_ID, Optional.empty());
        this.entityData.define(DATA_TYPE_ID, 0);
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
        this.lifeSpan = pCompound.getInt("lifeSpan");
        this.setAnimation(pCompound.getInt("Animation"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        if (this.getOwnerId() != null) {
            pCompound.putUUID("Owner", this.getOwnerId());
        }
        pCompound.putInt("lifeSpan", this.lifeSpan);
        pCompound.putInt("Animation", this.getAnimation());
    }

    public ResourceLocation getResourceLocation() {
        return TEXTURE_BY_TYPE.getOrDefault(this.getAnimation(), TEXTURE_BY_TYPE.get(0));
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

    public void setTrueOwner(LivingEntity livingEntity){
        this.setOwnerId(livingEntity.getUUID());
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

    public int getAnimation() {
        return this.entityData.get(DATA_TYPE_ID);
    }

    public void setAnimation(int pType) {
        this.entityData.set(DATA_TYPE_ID, pType);
    }

    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    public void tick() {
        super.tick();
        int frame = this.lifeSpan / TEXTURE_BY_TYPE.size();
        if (this.tickCount > frame && this.tickCount % frame == 0 && this.getAnimation() < 9){
            this.setAnimation(this.getAnimation() + 1);
        }
        if (!this.isNoGravity()) {
            MobUtil.moveDownToGround(this);
        }
        if (this.level instanceof ServerLevel serverWorld) {
            if (this.tickCount == this.getLifeSpan()){
                if (this.entity != null){
                    this.entity.setPos(this.getX(), this.getY(), this.getZ());
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
                    serverWorld.addFreshEntity(entity);
                }
            }
            if (this.tickCount < this.getLifeSpan()){
                double d0 = (double) this.getX() + this.random.nextDouble();
                double d1 = (double) this.getY();
                double d2 = (double) this.getZ() + this.random.nextDouble();
                serverWorld.sendParticles(ParticleTypes.LAVA, d0, d1, d2, 0, 0.0D, 0.0D, 0.0D, 0.5F);
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
