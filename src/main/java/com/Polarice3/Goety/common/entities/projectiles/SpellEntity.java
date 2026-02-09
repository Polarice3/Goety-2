package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.ISpellEntity;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class SpellEntity extends Entity implements OwnableEntity, ISpellEntity {
    protected static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(SpellEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Integer> OWNER_CLIENT_ID = SynchedEntityData.defineId(SpellEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Optional<UUID>> TARGET_UNIQUE_ID = SynchedEntityData.defineId(SpellEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Integer> TARGET_CLIENT_ID = SynchedEntityData.defineId(SpellEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> DATA_EXTRA_DAMAGE = SynchedEntityData.defineId(SpellEntity.class, EntityDataSerializers.FLOAT);
    public boolean staff = false;
    public ItemStack castingStaff = ItemStack.EMPTY;

    public SpellEntity(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    protected void defineSynchedData() {
        this.entityData.define(OWNER_UNIQUE_ID, Optional.empty());
        this.entityData.define(OWNER_CLIENT_ID, -1);
        this.entityData.define(TARGET_UNIQUE_ID, Optional.empty());
        this.entityData.define(TARGET_CLIENT_ID, -1);
        this.entityData.define(DATA_EXTRA_DAMAGE, 0.0F);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("Owner")) {
            this.setOwnerId(compound.getUUID("Owner"));
        }

        if (compound.contains("OwnerClient")){
            this.setOwnerClientId(compound.getInt("OwnerClient"));
        }

        if (compound.hasUUID("Target")) {
            this.setTargetId(compound.getUUID("Target"));
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
        if (compound.contains("CastingStaff")) {
            this.setCastingStaff(ItemStack.of(compound.getCompound("CastingStaff")));
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
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
        if (!this.getCastingStaff().isEmpty()) {
            compound.put("CastingStaff", this.getCastingStaff().save(new CompoundTag()));
        }
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
            int id = this.getOwnerClientId();
            return id <= -1 ? null : this.level.getEntity(this.getOwnerClientId()) instanceof LivingEntity living ? living : null;
        }
    }

    @Nullable
    @Override
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
            int id = this.getTargetClientId();
            return id <= -1 ? null : this.level.getEntity(this.getTargetClientId()) instanceof LivingEntity living ? living : null;
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

    public ItemStack getCastingStaff() {
        return this.castingStaff;
    }

    public void setCastingStaff(ItemStack castingStaff) {
        this.castingStaff = castingStaff;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getOwner() != null){
            if (this.getOwner().isDeadOrDying()){
                if (this.getOwner().getType().is(Tags.EntityTypes.BOSSES)) {
                    this.discard();
                } else if (MobUtil.getOwner(this.getOwner()) != null) {
                    this.setOwner(MobUtil.getOwner(this.getOwner()));
                }
            }
        }
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
