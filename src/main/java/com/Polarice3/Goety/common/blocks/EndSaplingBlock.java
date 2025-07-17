package com.Polarice3.Goety.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;

public class EndSaplingBlock extends SaplingBlock {
    public EndSaplingBlock(AbstractTreeGrower p_55978_, Properties p_55979_) {
        super(p_55978_, p_55979_);
    }

    protected boolean mayPlaceOn(BlockState p_51042_, BlockGetter p_51043_, BlockPos p_51044_) {
        return p_51042_.is(ModBlocks.END_SOIL.get());
    }

    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
        return state.is(ModBlocks.END_SOIL.get());
    }
}
