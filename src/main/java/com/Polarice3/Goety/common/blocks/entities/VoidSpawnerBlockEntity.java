package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.VoidSpawnerBlock;
import com.Polarice3.Goety.common.blocks.entities.void_spawner.VoidSpawner;
import com.Polarice3.Goety.common.blocks.entities.void_spawner.VoidSpawnerState;
import com.Polarice3.Goety.common.blocks.properties.ModStateProperties;
import com.Polarice3.Goety.utils.PlayerDetector;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class VoidSpawnerBlockEntity extends BlockEntity implements VoidSpawner.StateAccessor{
    private VoidSpawner voidSpawner;

    public VoidSpawnerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.VOID_SPAWNER.get(), blockPos, blockState);
        this.voidSpawner = new VoidSpawner(this, PlayerDetector.NO_CREATIVE_PLAYERS, PlayerDetector.EntitySelector.SELECT_FROM_LEVEL);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        this.voidSpawner
                .codec()
                .parse(NbtOps.INSTANCE, compoundTag)
                .resultOrPartial(Goety.LOGGER::error)
                .ifPresent(voidSpawner -> this.voidSpawner = voidSpawner);
        if (this.level != null) {
            this.markUpdated();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        this.voidSpawner
                .codec()
                .encodeStart(NbtOps.INSTANCE, this.voidSpawner)
                .get()
                .ifLeft(tag -> compoundTag.merge((CompoundTag)tag))
                .ifRight(param0x -> Goety.LOGGER.warn("Failed to encode VoidSpawner {}", param0x.message()));
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.voidSpawner.getData().getUpdateTag(this.getBlockState().getValue(VoidSpawnerBlock.STATE));
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    public void setEntityId(EntityType<?> entityType, RandomSource randomSource) {
        this.voidSpawner.getData().setEntityId(this.voidSpawner, randomSource, entityType);
        this.setChanged();
    }

    public VoidSpawner getVoidSpawner() {
        return this.voidSpawner;
    }

    @Override
    public VoidSpawnerState getState() {
        return !this.getBlockState().hasProperty(ModStateProperties.VOID_SPAWNER_STATE)
                ? VoidSpawnerState.INACTIVE
                : this.getBlockState().getValue(ModStateProperties.VOID_SPAWNER_STATE);
    }

    @Override
    public void setState(Level level, VoidSpawnerState spawnerState) {
        this.setChanged();
        level.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(ModStateProperties.VOID_SPAWNER_STATE, spawnerState));
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (pkt.getTag() != null) {
            this.load(pkt.getTag());
        }
        super.onDataPacket(net, pkt);
    }

    @Override
    public void markUpdated() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }

    }
}
