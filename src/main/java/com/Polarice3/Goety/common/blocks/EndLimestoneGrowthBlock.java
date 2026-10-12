package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EndLimestoneGrowthBlock extends GrowingPlantBodyBlock {
   public static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

   public EndLimestoneGrowthBlock(Properties p_154873_) {
      super(p_154873_, Direction.UP, SHAPE, false);
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

   protected GrowingPlantHeadBlock getHeadBlock() {
      return (GrowingPlantHeadBlock) ModBlocks.END_LIMESTONE_GROWTH.get();
   }
}