package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.OminousStatueBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class OminousStatueBlock extends StatueBlock {

    public OminousStatueBlock() {
        super(ModBlocks.OminousStoneProperties()
                .noOcclusion());
    }

    public BlockEntity newBlockEntity(BlockPos p_151996_, BlockState p_151997_) {
        if (p_151997_.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return new OminousStatueBlockEntity(p_151996_, p_151997_);
        } else {
            return null;
        }
    }
}
