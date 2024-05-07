package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.client.CBeamPacket;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Beam codes based on ArtifactBeamEntity on @Thelnfamous1's Dungeon Gears
 */
public abstract class AbstractBeam extends Entity implements IEntityAdditionalSpawnData {
    public static final double MAX_RAYTRACE_DISTANCE = 64;
    public boolean itemBase;
    public LivingEntity owner;
    public UUID ownerUUID;
    public final float beamWidth = 0.2F;

    public AbstractBeam(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
        if (owner != null) {
            this.ownerUUID = owner.getUUID();
            this.updatePositionAndRotation();
        }
    }

    @Override
    public void tick() {
        LivingEntity owner = getOwner();
        if (this.level.isClientSide) {
            if (this.owner instanceof Player) {
                ModNetwork.INSTANCE.sendToServer(new CBeamPacket(this));
            }
            this.updatePositionAndRotation();
        }

        if (owner != null && owner.isAlive() && SpellConfig.CorruptionImmobile.get()){
            owner.setDeltaMovement(0, owner.getDeltaMovement().y, 0);
            owner.xxa = 0.0F;
            owner.zza = 0.0F;
        }

        if (!this.level.isClientSide) {
            if (owner == null || !owner.isAlive() || (this.itemBase && !owner.isUsingItem())) {
                this.discard();
                return;
            }

            this.updatePositionAndRotation();
            Set<LivingEntity> entities = new HashSet<>();
            AABB aabb = new AABB(this.position(), this.position()).inflate(this.beamWidth);
            double distanceToDestination = beamTraceDistance(MAX_RAYTRACE_DISTANCE, 1.0f, false);
            double distanceTraveled = 0;
            while (!(this.position().distanceTo(aabb.getCenter()) > distanceToDestination) && !(this.position().distanceTo(aabb.getCenter()) > MAX_RAYTRACE_DISTANCE)) {
                entities.addAll(this.level.getEntitiesOfClass(LivingEntity.class, aabb, canHitEntity(owner)));
                distanceTraveled += 1.0D;
                Vec3 viewVector = this.getViewVector(1.0F);
                Vec3 targetVector = this.position().add(viewVector.x * distanceTraveled, viewVector.y * distanceTraveled, viewVector.z * distanceTraveled);
                aabb = new AABB(targetVector, targetVector).inflate(this.beamWidth);
            }
            this.damageEntities(entities);
        }
    }

    public void damageEntities(Set<LivingEntity> entities){
    }

    public Predicate<LivingEntity> canHitEntity(LivingEntity living){
        return living1 -> living1 != living && living.hasLineOfSight(living1) && living1.isAlive()
                && !MobUtil.areAllies(living, living1);
    }

    public void setItemBase(boolean itemBase){
        this.itemBase = itemBase;
    }

    public void updatePositionAndRotation() {
        LivingEntity owner = this.getOwner();
        if (owner != null) {
            Vec3 vec1 = owner.position();
            vec1 = vec1.add(this.getOffsetVector(owner));
            this.setPos(vec1.x, vec1.y, vec1.z);
            this.setYRot(boundDegrees(this.owner.getYRot()));
            this.setXRot(boundDegrees(this.owner.getXRot()));
            this.yRotO = boundDegrees(this.owner.yRotO);
            this.xRotO = boundDegrees(this.owner.xRotO);
        }
    }

    private float boundDegrees(float v) {
        return (v % 360 + 360) % 360;
    }

    private Vec3 getOffsetVector(LivingEntity living) {
        Vec3 viewVector = this.getViewVector(1.0F);
        return new Vec3(viewVector.x, living.getEyeHeight() * 0.4D, viewVector.z);
    }

    public float getBeamWidth() {
        return this.beamWidth;
    }

    public final Vec3 getWorldPosition(float p_242282_1_) {
        double d0 = Mth.lerp(p_242282_1_, this.xo, this.getX());
        double d1 = Mth.lerp(p_242282_1_, this.yo, this.getY());
        double d2 = Mth.lerp(p_242282_1_, this.zo, this.getZ());
        return new Vec3(d0, d1, d2);
    }

    public HitResult beamTraceResult(double distance, float ticks, boolean passesWater) {
        Vec3 vector3d = this.getWorldPosition(ticks);
        Vec3 vector3d1 = this.getViewVector(ticks);
        Vec3 vector3d2 = vector3d.add(vector3d1.x * distance, vector3d1.y * distance, vector3d1.z * distance);
        return level.clip(new ClipContext(vector3d, vector3d2, ClipContext.Block.COLLIDER, passesWater ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, this));
    }

    public double beamTraceDistance(double distance, float ticks, boolean passesWater) {
        HitResult rayTraceResult = beamTraceResult(distance, ticks, passesWater);
        double distanceToDestination = MAX_RAYTRACE_DISTANCE;
        if (rayTraceResult instanceof BlockHitResult) {
            BlockPos collision = ((BlockHitResult) rayTraceResult).getBlockPos();
            Vec3 destination = new Vec3(collision.getX(), collision.getY(), collision.getZ());
            distanceToDestination = this.position().distanceTo(destination);
        }
        return distanceToDestination;
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null) {
            if (this.level instanceof ServerLevel serverLevel) {
                Entity entity = serverLevel.getEntity(this.ownerUUID);
                if (entity instanceof LivingEntity) {
                    this.owner = (LivingEntity) entity;
                }
            } else if (this.level.isClientSide) {
                this.owner = this.level.getPlayerByUUID(this.ownerUUID);
            }
        }
        return this.owner;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.hasUUID("Owner")) {
            this.ownerUUID = pCompound.getUUID("Owner");
        }
        if (pCompound.contains("ItemBase")){
            this.itemBase = pCompound.getBoolean("ItemBase");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        if (this.ownerUUID != null) {
            pCompound.putUUID("Owner", this.ownerUUID);
        }
        pCompound.putBoolean("ItemBase", this.itemBase);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        if (this.ownerUUID != null) {
            buffer.writeUUID(this.ownerUUID);
        }
        buffer.writeBoolean(this.itemBase);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        this.ownerUUID = additionalData.readUUID();
        this.itemBase = additionalData.readBoolean();
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
