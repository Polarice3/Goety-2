package com.Polarice3.Goety.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SiennaGrassBlock extends SiennaPlantBlock implements BonemealableBlock {
   protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 11.0D, 14.0D);

   public SiennaGrassBlock() {
      super(Properties.of()
              .mapColor(MapColor.COLOR_RED)
              .replaceable()
              .noCollission()
              .instabreak()
              .sound(SoundType.GRASS)
              .ignitedByLava()
              .offsetType(OffsetType.XYZ)
              .pushReaction(PushReaction.DESTROY));
   }

   public VoxelShape getShape(BlockState p_52419_, BlockGetter p_52420_, BlockPos p_52421_, CollisionContext p_52422_) {
      return SHAPE;
   }

   public boolean isValidBonemealTarget(LevelReader p_255692_, BlockPos p_57326_, BlockState p_57327_, boolean p_57328_) {
      return true;
   }

   public boolean isBonemealSuccess(Level p_222583_, RandomSource p_222584_, BlockPos p_222585_, BlockState p_222586_) {
      return true;
   }

   public void performBonemeal(ServerLevel p_222578_, RandomSource p_222579_, BlockPos p_222580_, BlockState p_222581_) {
      DoublePlantBlock doubleplantblock = null;
      if (p_222581_.is(ModBlocks.SIENNA_FERN.get())) {
         doubleplantblock = (DoublePlantBlock) ModBlocks.LARGE_SIENNA_FERN.get();
      } else if (p_222581_.is(ModBlocks.SIENNA_GRASS.get())) {
         doubleplantblock = (DoublePlantBlock) ModBlocks.TALL_SIENNA_GRASS.get();
      }
      if (doubleplantblock != null) {
         if (doubleplantblock.defaultBlockState().canSurvive(p_222578_, p_222580_) && p_222578_.isEmptyBlock(p_222580_.above())) {
            DoublePlantBlock.placeAt(p_222578_, doubleplantblock.defaultBlockState(), p_222580_, 3);
         }
      }
   }
}
