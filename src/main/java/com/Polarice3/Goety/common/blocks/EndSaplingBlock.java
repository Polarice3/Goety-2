package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.init.ModTags;
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

    protected boolean mayPlaceOn(BlockState state, BlockGetter world, BlockPos pos) {
        return state.is(ModTags.Blocks.CHORUS_SAPLING_GROW);
    }

    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
        return state.is(ModTags.Blocks.CHORUS_SAPLING_GROW);
    }
}
