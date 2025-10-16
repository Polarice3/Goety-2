package com.Polarice3.Goety.common.blocks;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EndGrowthVinesPlantBlock extends GrowingPlantBodyBlock {
   public static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

   public EndGrowthVinesPlantBlock(Properties p_154873_) {
      super(p_154873_, Direction.UP, SHAPE, false);
   }

   protected GrowingPlantHeadBlock getHeadBlock() {
      return (GrowingPlantHeadBlock) ModBlocks.END_GROWTH_VINES.get();
   }
}