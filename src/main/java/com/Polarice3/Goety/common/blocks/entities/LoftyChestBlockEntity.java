package com.Polarice3.Goety.common.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LoftyChestBlockEntity extends ChestBlockEntity {
    protected LoftyChestBlockEntity(BlockEntityType<?> p_155327_, BlockPos p_155328_, BlockState p_155329_) {
        super(p_155327_, p_155328_, p_155329_);
    }

    public LoftyChestBlockEntity(BlockPos p_155328_, BlockState p_155329_) {
        super(ModBlockEntities.LOFTY_CHEST.get(), p_155328_, p_155329_);
    }
}
