package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.ModSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class ModStandSignBlock extends StandingSignBlock {
    public ModStandSignBlock(Properties properties, WoodType type) {
        super(properties, type);
    }

    public BlockEntity newBlockEntity(BlockPos p_154556_, BlockState p_154557_) {
        return new ModSignBlockEntity(p_154556_, p_154557_);
    }
}
