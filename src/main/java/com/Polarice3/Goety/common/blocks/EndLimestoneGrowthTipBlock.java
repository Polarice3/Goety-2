package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.NetherVines;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EndLimestoneGrowthTipBlock extends GrowingPlantHeadBlock {
    public static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 15.0D, 12.0D);

    public EndLimestoneGrowthTipBlock(Properties p_154864_) {
        super(p_154864_, Direction.UP, SHAPE, false, 0.1D);
    }

    public boolean canSurvive(BlockState p_53876_, LevelReader p_53877_, BlockPos p_53878_) {
        BlockPos blockpos = p_53878_.relative(this.growthDirection.getOpposite());
        BlockState blockstate = p_53877_.getBlockState(blockpos);
        if (!this.canAttachTo(blockstate)) {
            return false;
        } else {
            return blockstate.is(this.getHeadBlock()) || blockstate.is(this.getBodyBlock()) || (blockstate.isFaceSturdy(p_53877_, blockpos, this.growthDirection) && blockstate.is(ModTags.Blocks.END_LIMESTONE));
        }
    }

    public boolean isValidBonemealTarget(LevelReader p_255931_, BlockPos p_256046_, BlockState p_256550_, boolean p_256181_) {
        return false;
    }

    public boolean isBonemealSuccess(Level p_221343_, RandomSource p_221344_, BlockPos p_221345_, BlockState p_221346_) {
        return false;
    }

    public void performBonemeal(ServerLevel p_221337_, RandomSource p_221338_, BlockPos p_221339_, BlockState p_221340_) {
    }

    protected int getBlocksToGrowWhenBonemealed(RandomSource p_222649_) {
        return NetherVines.getBlocksToGrowWhenBonemealed(p_222649_);
    }

    protected Block getBodyBlock() {
        return ModBlocks.END_LIMESTONE_GROWTH_BASE.get();
    }

    protected boolean canGrowInto(BlockState p_154869_) {
        return NetherVines.isValidGrowthState(p_154869_);
    }
}
