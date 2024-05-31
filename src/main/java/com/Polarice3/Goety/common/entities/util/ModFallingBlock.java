package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.common.entities.ModEntityType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.Optional;

/**
 * Code based of @BobMowzies EntityFallingBlock: <a href="https://github.com/BobMowzie/MowziesMobs/blob/1.16.5/src/main/java/com/bobmowzie/mowziesmobs/server/entity/effects/EntityFallingBlock.java">...</a>
 */
public class ModFallingBlock extends Entity {
    public static float GRAVITY = 0.1F;
    private static final EntityDataAccessor<Optional<BlockState>> BLOCK_STATE = SynchedEntityData.defineId(ModFallingBlock.class, EntityDataSerializers.OPTIONAL_BLOCK_STATE);
    private static final EntityDataAccessor<Integer> DURATION = SynchedEntityData.defineId(ModFallingBlock.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TICKS_EXISTED = SynchedEntityData.defineId(ModFallingBlock.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> MODE = SynchedEntityData.defineId(ModFallingBlock.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> POP_UP_LEVEL = SynchedEntityData.defineId(ModFallingBlock.class, EntityDataSerializers.FLOAT);
    public double prevMotionX, prevMotionY, prevMotionZ;
    public float animY = 0;
    public float prevAnimY = 0;

    public ModFallingBlock(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.setBlock(Blocks.DIRT.defaultBlockState());
        this.setDuration(70);
    }

    public ModFallingBlock(Level worldIn, int duration, BlockState blockState) {
        super(ModEntityType.FALLING_BLOCK.get(), worldIn);
        this.setBlock(blockState);
        this.setDuration(duration);
    }

    public ModFallingBlock(Level worldIn, BlockState blockState, float popUp) {
        super(ModEntityType.FALLING_BLOCK.get(), worldIn);
        this.setBlock(blockState);
        this.setMode(FallingBlockMode.POPUP_ANIM);
        this.setPopUp(popUp);
    }

    public ModFallingBlock(Level worldIn, Vec3 vec3, BlockState blockState, float popUp) {
        this(worldIn, blockState, popUp);
        this.setPos(vec3);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(BLOCK_STATE, Optional.of(Blocks.DIRT.defaultBlockState()));
        this.entityData.define(DURATION, 70);
        this.entityData.define(TICKS_EXISTED, 0);
        this.entityData.define(MODE, FallingBlockMode.MOBILE.toString());
        this.entityData.define(POP_UP_LEVEL, 1.0F);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        Tag blockStateCompound = compound.get("BlockState");
        if (blockStateCompound != null) {
            BlockState blockState = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), compound.getCompound("BlockState"));
            this.setBlock(blockState);
        }
        this.setDuration(compound.getInt("duration"));
        this.tickCount = compound.getInt("tickCount");
        this.entityData.set(MODE, compound.getString("mode"));
        this.setPopUp(compound.getFloat("popUp"));

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        BlockState blockState = getBlock();
        if (blockState != null) {
            compound.put("BlockState", NbtUtils.writeBlockState(blockState));
        }
        compound.putInt("duration", this.getDuration());
        compound.putInt("tickCount", this.tickCount);
        compound.putString("mode", this.entityData.get(MODE));
        compound.putFloat("popUp", this.entityData.get(POP_UP_LEVEL));
    }
    @Override
    public void onAddedToWorld() {
        if (this.getDeltaMovement().x() > 0.0D || this.getDeltaMovement().z() > 0.0D) {
            this.setYRot((float) ((180.0F / Math.PI) * Math.atan2(getDeltaMovement().x(), getDeltaMovement().z())));
        }
        this.setXRot(getXRot() + random.nextFloat() * 360.0F);
        super.onAddedToWorld();
    }

    @Override
    public void tick() {
        if (this.getMode() == FallingBlockMode.POPUP_ANIM) {
            this.setDeltaMovement(0.0F, 0.0F, 0.0F);
            this.gameEvent(GameEvent.BLOCK_DETACH);
        }
        this.prevMotionX = this.getDeltaMovement().x;
        this.prevMotionY = this.getDeltaMovement().y;
        this.prevMotionZ = this.getDeltaMovement().z;
        super.tick();
        if (this.getMode() == FallingBlockMode.MOBILE) {
            this.setDeltaMovement(this.getDeltaMovement().subtract(0.0F, GRAVITY, 0.0F));
            if (this.onGround()) {
                this.setDeltaMovement(this.getDeltaMovement().scale(0.7F));
            } else {
                this.setXRot(this.getXRot() + 15.0F);
            }
            this.move(MoverType.SELF, this.getDeltaMovement());

            if (this.getTicksExisted() > this.getDuration()) {
                this.discard();
            }
        } else {
            this.prevAnimY = this.animY;
            this.animY += this.getPopUp();
            this.setPopUp(this.getPopUp() - GRAVITY);
            if (this.animY < -0.5F) {
                this.discard();
            }
        }
    }

    public BlockState getBlock() {
        Optional<BlockState> blockState = this.entityData.get(BLOCK_STATE);
        return blockState.orElse(null);
    }

    public void setBlock(BlockState block) {
        this.entityData.set(BLOCK_STATE, Optional.of(block));
    }

    public int getDuration() {
        return this.entityData.get(DURATION);
    }

    public void setDuration(int duration) {
        this.entityData.set(DURATION, duration);
    }

    public int getTicksExisted() {
        return this.entityData.get(TICKS_EXISTED);
    }

    public void setTicksExisted(int ticksExisted) {
        this.entityData.set(TICKS_EXISTED, ticksExisted);
    }

    public FallingBlockMode getMode() {
        String mode = this.entityData.get(MODE);
        if (mode.isEmpty()) {
            return FallingBlockMode.MOBILE;
        }
        return FallingBlockMode.valueOf(this.entityData.get(MODE));
    }

    private void setMode(FallingBlockMode mode) {
        this.entityData.set(MODE, mode.toString());
    }

    public float getPopUp() {
        return this.entityData.get(POP_UP_LEVEL);
    }

    private void setPopUp(float power) {
        this.entityData.set(POP_UP_LEVEL, power);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public enum FallingBlockMode {
        MOBILE,
        POPUP_ANIM
    }
}
