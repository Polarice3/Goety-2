package com.Polarice3.Goety.common.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SaveBlockEntity extends BlockEntity {
    public BlockState oldBlock;
    public BlockEntity oldBlockEntity;

    public SaveBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    public void recreateBlockEntity(CompoundTag tag) {
        if (this.level != null) {
            if (this.oldBlockEntity != null && this.level.getBlockEntity(this.getBlockPos()) != null) {
                this.level.setBlockAndUpdate(this.getBlockPos(), this.oldBlock);
                if (this.level.getBlockEntity(this.getBlockPos()) != null){
                    BlockEntity blockEntity = this.level.getBlockEntity(this.getBlockPos());
                    if (blockEntity != null) {
                        blockEntity.load(tag);
                    }
                }
            }
        }
        this.setChanged();
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.writeNetwork(super.getUpdateTag());
    }

    public void load(CompoundTag nbt) {
        this.readNetwork(nbt);
        super.load(nbt);
    }

    public void saveAdditional(CompoundTag compound) {
        this.writeNetwork(compound);
        super.saveAdditional(compound);
    }

    public void readNetwork(CompoundTag tag) {
        if (tag.contains("BlockState")) {
            HolderGetter<Block> holdergetter = this.level != null ? this.level.holderLookup(Registries.BLOCK) : BuiltInRegistries.BLOCK.asLookup();
            this.oldBlock = NbtUtils.readBlockState(holdergetter, tag.getCompound("BlockState"));
        }
        if (tag.contains("BlockEntity")){
            this.recreateBlockEntity(tag.getCompound("BlockEntity"));
        }
    }

    public CompoundTag writeNetwork(CompoundTag tag) {
        if (this.oldBlock != null) {
            tag.put("BlockState", NbtUtils.writeBlockState(this.oldBlock));
        }
        if (this.oldBlockEntity != null){
            tag.put("BlockEntity", this.oldBlockEntity.saveWithFullMetadata());
        }
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
