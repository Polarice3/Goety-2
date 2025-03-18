package com.Polarice3.Goety.common.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CryptChestBlockEntity extends ChestBlockEntity {

    public CryptChestBlockEntity(BlockPos p_155328_, BlockState p_155329_) {
        super(ModBlockEntities.CRYPT_CHEST.get(), p_155328_, p_155329_);
    }
}
