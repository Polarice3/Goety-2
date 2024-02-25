package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.common.blocks.PedestalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PedestalBlockEntity extends RitualBlockEntity {
    public long lastChangeTime;
    public LazyOptional<ItemStackHandler> itemStackHandler = LazyOptional.of(
            () -> new ItemStackHandler(1) {
                @Override
                public int getSlotLimit(int slot) {
                    return 1;
                }

                @Override
                protected void onContentsChanged(int slot) {
                    assert PedestalBlockEntity.this.level != null;
                    if (!PedestalBlockEntity.this.level.isClientSide) {
                        PedestalBlockEntity.this.lastChangeTime = PedestalBlockEntity.this.level
                                .getGameTime();
                        boolean flag = !this.stacks.get(0).isEmpty();
                        PedestalBlockEntity.this.level.setBlockAndUpdate(PedestalBlockEntity.this.getBlockPos(),
                                PedestalBlockEntity.this.getBlockState().setValue(PedestalBlock.OCCUPIED, flag));
                        PedestalBlockEntity.this.markNetworkDirty();
                    }
                }
            });
    protected boolean initialized = false;

    public PedestalBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.PEDESTAL.get(), blockPos, blockState);
    }

    public PedestalBlockEntity(BlockEntityType<?> blockEntity, BlockPos blockPos, BlockState blockState){
        super(blockEntity, blockPos, blockState);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.itemStackHandler.cast();
        }
        return super.getCapability(cap, direction);
    }

    @Override
    public void readNetwork(CompoundTag compound) {
        this.itemStackHandler.ifPresent((handler) -> handler.deserializeNBT(compound.getCompound("inventory")));
        this.lastChangeTime = compound.getLong("lastChangeTime");
    }

    @Override
    public CompoundTag writeNetwork(CompoundTag compound) {
        this.itemStackHandler.ifPresent(handler -> compound.put("inventory", handler.serializeNBT()));
        compound.putLong("lastChangeTime", this.lastChangeTime);
        return compound;
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.itemStackHandler.invalidate();
    }
}
/*
 * MIT License
 *
 * Copyright 2020 klikli-dev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */