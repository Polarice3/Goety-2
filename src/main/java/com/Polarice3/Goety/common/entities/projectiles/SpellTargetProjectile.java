package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.utils.EntityFinder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class SpellTargetProjectile extends Projectile {
    protected static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(SpellTargetProjectile.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Integer> OWNER_CLIENT_ID = SynchedEntityData.defineId(SpellTargetProjectile.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Optional<UUID>> TARGET_UNIQUE_ID = SynchedEntityData.defineId(SpellTargetProjectile.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Integer> TARGET_CLIENT_ID = SynchedEntityData.defineId(SpellTargetProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> DATA_EXTRA_DAMAGE = SynchedEntityData.defineId(SpellTargetProjectile.class, EntityDataSerializers.FLOAT);
    public boolean staff = false;

    public SpellTargetProjectile(EntityType<? extends Projectile> p_37248_, Level p_37249_) {
        super(p_37248_, p_37249_);
    }

    protected void defineSynchedData() {
        this.entityData.define(OWNER_UNIQUE_ID, Optional.empty());
        this.entityData.define(OWNER_CLIENT_ID, -1);
        this.entityData.define(TARGET_UNIQUE_ID, Optional.empty());
        this.entityData.define(TARGET_CLIENT_ID, -1);
        this.entityData.define(DATA_EXTRA_DAMAGE, 0.0F);
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

        if (compound.contains("OwnerClient")){
            this.setOwnerClientId(compound.getInt("OwnerClient"));
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

        if (compound.contains("TargetClient")){
            this.setTargetClientId(compound.getInt("TargetClient"));
        }
        if (compound.contains("ExtraDamage")) {
            this.setExtraDamage(compound.getFloat("ExtraDamage"));
        }
        if (compound.contains("staff")) {
            this.staff = compound.getBoolean("staff");
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getOwnerId() != null) {
            compound.putUUID("Owner", this.getOwnerId());
        }
        if (this.getOwnerClientId() > -1) {
            compound.putInt("OwnerClient", this.getOwnerClientId());
        }
        if (this.getTargetId() != null) {
            compound.putUUID("Target", this.getTargetId());
        }
        if (this.getTargetClientId() > -1) {
            compound.putInt("TargetClient", this.getTargetClientId());
        }
        compound.putFloat("ExtraDamage", this.getExtraDamage());
        compound.putBoolean("staff", this.isStaff());
    }

    public void setStaff(boolean staff){
        this.staff = staff;
    }

    public boolean isStaff(){
        return this.staff;
    }

    @Nullable
    public LivingEntity getOwner() {
        if (!this.level.isClientSide){
            UUID uuid = this.getOwnerId();
            return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(uuid);
        } else {
            return this.level.getEntity(this.getOwnerClientId()) instanceof LivingEntity living ? living : null;
        }
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.getOwnerId();
    }

    @Nullable
    public UUID getOwnerId() {
        return this.entityData.get(OWNER_UNIQUE_ID).orElse((UUID)null);
    }

    public void setOwnerId(@Nullable UUID p_184754_1_) {
        this.entityData.set(OWNER_UNIQUE_ID, Optional.ofNullable(p_184754_1_));
    }

    public int getOwnerClientId(){
        return this.entityData.get(OWNER_CLIENT_ID);
    }

    public void setOwnerClientId(int id){
        this.entityData.set(OWNER_CLIENT_ID, id);
    }

    @Nullable
    public LivingEntity getTarget() {
        if (!this.level.isClientSide){
            UUID uuid = this.getTargetId();
            return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(uuid);
        } else {
            return this.level.getEntity(this.getTargetClientId()) instanceof LivingEntity living ? living : null;
        }
    }

    @Nullable
    public UUID getTargetId() {
        return this.entityData.get(TARGET_UNIQUE_ID).orElse((UUID)null);
    }

    public void setTargetId(@Nullable UUID p_184754_1_) {
        this.entityData.set(TARGET_UNIQUE_ID, Optional.ofNullable(p_184754_1_));
    }

    public int getTargetClientId(){
        return this.entityData.get(TARGET_CLIENT_ID);
    }

    public void setTargetClientId(int id){
        this.entityData.set(TARGET_CLIENT_ID, id);
    }

    public void setOwner(LivingEntity livingEntity){
        if (livingEntity != null) {
            this.setOwnerId(livingEntity.getUUID());
            this.setOwnerClientId(livingEntity.getId());
        }
    }

    public void setTarget(@Nullable LivingEntity livingEntity) {
        if (livingEntity != null) {
            this.setTargetId(livingEntity.getUUID());
            this.setTargetClientId(livingEntity.getId());
        }
    }

    public float getExtraDamage() {
        return this.entityData.get(DATA_EXTRA_DAMAGE);
    }

    public void setExtraDamage(float pDamage) {
        this.entityData.set(DATA_EXTRA_DAMAGE, pDamage);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
