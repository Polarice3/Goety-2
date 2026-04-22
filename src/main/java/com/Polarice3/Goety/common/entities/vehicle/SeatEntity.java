package com.Polarice3.Goety.common.entities.vehicle;

import com.Polarice3.Goety.api.blocks.ISeat;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.entities.ModEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

/**
 * Based on @Creators-of-Create codes: <a href="https://github.com/Creators-of-Create/Create/blob/mc1.20.1/dev/src/main/java/com/simibubi/create/content/contraptions/actors/seat/SeatEntity.java">...</a>;
 */
public class SeatEntity extends Entity implements IEntityAdditionalSpawnData {
    private static final EntityDataAccessor<Float> LOOK_ANGLE = SynchedEntityData.defineId(SeatEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> HAS_LOOK = SynchedEntityData.defineId(SeatEntity.class, EntityDataSerializers.BOOLEAN);

    public SeatEntity(EntityType<?> p_i48580_1_, Level p_i48580_2_) {
        super(p_i48580_1_, p_i48580_2_);
        this.noPhysics = true;
    }

    public SeatEntity(Level world, BlockPos pos) {
        this(ModEntityType.SEAT.get(), world);
        this.setPos(pos.getX(), pos.getY(), pos.getZ());
    }

    public SeatEntity(Level world, Vec3 pos) {
        this(ModEntityType.SEAT.get(), world);
        this.setPos(pos.x(), pos.y(), pos.z());
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        AABB bb = getBoundingBox();
        Vec3 diff = new Vec3(x, y, z).subtract(bb.getCenter());
        this.setBoundingBox(bb.move(diff));
    }

    @Override
    protected void positionRider(Entity pEntity, Entity.MoveFunction pCallback) {
        if (!this.hasPassenger(pEntity)) {
            return;
        }
        double d0 = this.getY() + this.getPassengersRidingOffset() + pEntity.getMyRidingOffset();
        pCallback.accept(pEntity, this.getX(), d0 + getCustomEntitySeatOffset(pEntity), this.getZ());
    }

    public static double getCustomEntitySeatOffset(Entity entity) {
        if (entity instanceof Slime) {
            return 0.25f;
        }
        if (entity instanceof Parrot || entity instanceof Wolf) {
            return 1 / 16f;
        }
        if (entity instanceof Cat || entity instanceof Skeleton || entity instanceof Creeper) {
            return 1 / 8f;
        }
        if (entity instanceof Frog) {
            return 1 / 8f + 1 / 64f;
        }
        return 0;
    }

    @Override
    public void setDeltaMovement(Vec3 p_213317_1_) {}

    @Override
    public void tick() {
        if (this.level.isClientSide) {
            return;
        }
        if (this.getFirstPassenger() instanceof Mob mob) {
            boolean flag = false;
            if (mob instanceof IServant servant) {
                if (servant.isCommanded()) {
                    mob.stopRiding();
                }
                flag = servant.isStaying() || servant.isCommanded();
            }
            if (!flag) {
                if (mob.getTarget() != null) {
                    mob.stopRiding();
                }
            }
            if (this.hasCustomLook()) {
                mob.setYRot(this.getCustomLook());
                mob.setYBodyRot(this.getCustomLook());
            }
        }
        boolean blockPresent = this.level.getBlockState(blockPosition()).getBlock() instanceof ISeat;
        if (this.isVehicle() && blockPresent) {
            return;
        }
        this.discard();
    }

    @Override
    protected boolean canRide(Entity entity) {
        return !(entity instanceof FakePlayer);
    }

    @Override
    protected void removePassenger(Entity entity) {
        super.removePassenger(entity);
        if (entity instanceof TamableAnimal ta) {
            ta.setInSittingPose(false);
        }
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity pLivingEntity) {
        return super.getDismountLocationForPassenger(pLivingEntity).add(0, 0.5f, 0);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(LOOK_ANGLE, 0.0F);
        this.entityData.define(HAS_LOOK, false);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("CustomLookAngle")) {
            this.setCustomLook(compound.getFloat("CustomLookAngle"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.hasCustomLook()) {
            compound.putFloat("CustomLookAngle", this.getCustomLook());
        }
    }

    public float getCustomLook() {
        return this.entityData.get(LOOK_ANGLE);
    }

    public void setCustomLook(float angle) {
        this.entityData.set(LOOK_ANGLE, angle);
        this.entityData.set(HAS_LOOK, true);
    }

    public boolean hasCustomLook() {
        return this.entityData.get(HAS_LOOK);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
    }
}
