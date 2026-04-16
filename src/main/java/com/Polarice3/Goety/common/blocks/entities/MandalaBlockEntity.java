package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.common.blocks.MandalaBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MandalaBlockEntity extends BlockEntity {

    public MandalaBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MANDALA.get(), pos, state);
    }

    public boolean isAnchor() {
        return this.getBlockState().getValue(MandalaBlock.PART) == 0;
    }
}
