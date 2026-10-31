package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.common.blocks.SealerBlock;
import com.Polarice3.Goety.common.items.WaystoneItem;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

//Backported from Goety 3, made by Vivideru: https://github.com/Vivideru/Goety-3/blob/main/src/main/java/com/Vivideru/Goety/common/blocks/entities/SealerBlockEntity.java
public class SealerBlockEntity extends BlockEntity {
    public static final int RANGE = 32;
    private static final int CHECK_INTERVAL = 20;
    private ItemStack waystone = ItemStack.EMPTY;
    private boolean playerPlaced;
    private boolean sealing;
    @Nullable
    private BlockPos sealedPos;
    @Nullable
    private Vec3i relativeTarget;
    private int tickCount;
    private int soulTimer;

    public SealerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SEALER.get(), pos, state);
    }

    public ItemStack getWaystone() {
        return this.waystone;
    }

    public void setWaystone(ItemStack stack) {
        this.waystone = stack;
        this.sync();
        if (this.level instanceof ServerLevel serverLevel) {
            this.refresh(serverLevel);
        }
    }

    public void setPlayerPlaced(boolean playerPlaced) {
        this.playerPlaced = playerPlaced;
        this.setChanged();
    }

    public boolean isPlayerPlaced() {
        return this.playerPlaced;
    }

    @Nullable
    public BlockPos getSealedPos() {
        return this.sealedPos;
    }

    public boolean isSealing() {
        return this.sealing;
    }

    private void sync() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void tick() {
        if (!(this.level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (++this.tickCount % CHECK_INTERVAL == 0) {
            this.refresh(serverLevel);
        }
    }

    private void refresh(ServerLevel level) {
        boolean wasSealing = this.sealing;
        BlockPos wasSealed = this.sealedPos;
        BlockPos target = this.target(level);
        boolean powered = level.hasNeighborSignal(this.worldPosition);
        boolean active = target != null && powered && this.hasSoulEnergy(level);
        if (active) {
            if (this.sealedPos != null && !this.sealedPos.equals(target)) {
                SealerHooks.unseal(level, this.sealedPos, this.worldPosition);
            }
            SealerHooks.seal(level, target, this.worldPosition);
            this.sealedPos = target;
            this.sealing = true;
        } else {
            this.soulTimer = 0;
            if (this.sealing && this.sealedPos != null) {
                SealerHooks.unseal(level, this.sealedPos, this.worldPosition);
            }
            this.sealing = false;
            this.sealedPos = null;
        }
        boolean changed = this.sealing != wasSealing || (this.sealedPos == null ? wasSealed != null : !this.sealedPos.equals(wasSealed));
        BlockState state = this.getBlockState();
        if (state.hasProperty(SealerBlock.LIT) && state.getValue(SealerBlock.LIT) != active) {
            level.setBlock(this.worldPosition, state.setValue(SealerBlock.LIT, active), 3);
        }
        if (changed) {
            this.sync();
        } else {
            this.setChanged();
        }
    }

    public Direction facing() {
        BlockState state = this.getBlockState();
        return state.hasProperty(SealerBlock.FACING) ? state.getValue(SealerBlock.FACING) : Direction.NORTH;
    }

    public Vec3i toRelative(BlockPos target) {
        Direction forward = this.facing();
        Direction right = forward.getClockWise();
        BlockPos delta = target.subtract(this.worldPosition);
        int f = delta.getX() * forward.getStepX() + delta.getZ() * forward.getStepZ();
        int r = delta.getX() * right.getStepX() + delta.getZ() * right.getStepZ();
        return new Vec3i(f, delta.getY(), r);
    }

    public BlockPos fromRelative(Vec3i relative) {
        Direction forward = this.facing();
        Direction right = forward.getClockWise();
        return this.worldPosition.offset(forward.getStepX() * relative.getX() + right.getStepX() * relative.getZ(), relative.getY(), forward.getStepZ() * relative.getX() + right.getStepZ() * relative.getZ());
    }

    @Nullable
    public Vec3i getRelativeTarget() {
        return this.relativeTarget;
    }

    public void setRelativeTarget(@Nullable Vec3i relativeTarget) {
        this.relativeTarget = relativeTarget;
        this.setChanged();
    }

    @Nullable
    private BlockPos target(ServerLevel level) {
        BlockPos pos;
        if (this.relativeTarget != null) {
            pos = this.fromRelative(this.relativeTarget);
        } else {
            if (this.waystone.isEmpty()) {
                return null;
            }
            GlobalPos globalPos = WaystoneItem.getPosition(this.waystone);
            if (globalPos == null || globalPos.dimension() != level.dimension()) {
                return null;
            }
            pos = globalPos.pos();
        }
        if (pos.distSqr(this.worldPosition) > (double) RANGE * RANGE || !level.isLoaded(pos)) {
            return null;
        }
        return SealerHooks.isSealable(level, pos) ? pos : null;
    }

    private boolean hasSoulEnergy(ServerLevel level) {
        if (!this.playerPlaced) {
            return true;
        }
        if (level.getBlockEntity(this.worldPosition.below()) instanceof CursedCageBlockEntity cage) {
            this.soulTimer += CHECK_INTERVAL;
            int seconds = MathHelper.secondsToTicks(MainConfig.SealerIntervalCost.get());
            if (this.soulTimer >= seconds) {
                int decrease = MainConfig.SealerCost.get();
                if (cage.getSouls() < decrease) {
                    return false;
                }
                cage.decreaseSouls(decrease);
                this.soulTimer = 0;
            }
            return cage.getSouls() > 0;
        }
        return false;
    }

    public void dropContents() {
        if (this.level instanceof ServerLevel serverLevel) {
            if (this.sealing && this.sealedPos != null) {
                SealerHooks.unseal(serverLevel, this.sealedPos, this.worldPosition);
                this.sealing = false;
            }
            if (!this.waystone.isEmpty()) {
                Containers.dropItemStack(serverLevel, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), this.waystone);
                this.waystone = ItemStack.EMPTY;
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!this.waystone.isEmpty()) {
            tag.put("Waystone", this.waystone.save(new CompoundTag()));
        }
        tag.putBoolean("PlayerPlaced", this.playerPlaced);
        tag.putBoolean("Sealing", this.sealing);
        if (this.sealedPos != null) {
            tag.putLong("SealedPos", this.sealedPos.asLong());
        }
        tag.putInt("SoulTimer", this.soulTimer);
        if (this.relativeTarget != null) {
            tag.putIntArray("RelativeTarget", new int[]{this.relativeTarget.getX(), this.relativeTarget.getY(), this.relativeTarget.getZ()});
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.mirrorOnClient(this.sealedPos, this.sealing);
    }

    private void mirrorOnClient(@Nullable BlockPos target, boolean active) {
        if (this.level != null && this.level.isClientSide) {
            SealerHooks.clientMirror(this.level, this.worldPosition, target, active);
        }
    }

    @Override
    public void setRemoved() {
        this.mirrorOnClient(this.sealedPos, false);
        super.setRemoved();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        BlockPos oldTarget = this.sealedPos;
        boolean wasSealing = this.sealing;
        this.waystone = tag.contains("Waystone") ? ItemStack.of(tag.getCompound("Waystone")) : ItemStack.EMPTY;
        this.playerPlaced = tag.getBoolean("PlayerPlaced");
        this.sealing = tag.getBoolean("Sealing");
        this.sealedPos = tag.contains("SealedPos") ? BlockPos.of(tag.getLong("SealedPos")) : null;
        this.soulTimer = tag.getInt("SoulTimer");
        int[] relative = tag.getIntArray("RelativeTarget");
        this.relativeTarget = relative.length == 3 ? new Vec3i(relative[0], relative[1], relative[2]) : null;
        if (this.level != null && this.level.isClientSide) {
            if (wasSealing && oldTarget != null && (!this.sealing || !oldTarget.equals(this.sealedPos))) {
                this.mirrorOnClient(oldTarget, false);
            }
            this.mirrorOnClient(this.sealedPos, this.sealing);
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(this.getBlockPos()).inflate(SealerBlockEntity.RANGE + 1);
    }
}
