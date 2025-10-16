package com.Polarice3.Goety.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EndGrassBlock extends EndPlantBlock implements BonemealableBlock {
   protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 11.0D, 14.0D);
   protected static final VoxelShape SMALL = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);

   public EndGrassBlock() {
      super(Properties.of()
              .mapColor(MapColor.SAND)
              .replaceable()
              .noCollission()
              .instabreak()
              .sound(SoundType.GRASS)
              .offsetType(BlockBehaviour.OffsetType.XYZ)
              .pushReaction(PushReaction.DESTROY));
   }

   public VoxelShape getShape(BlockState p_52419_, BlockGetter p_52420_, BlockPos p_52421_, CollisionContext p_52422_) {
      if (p_52419_.is(ModBlocks.END_GRASS_SPROUT.get())) {
         return SMALL;
      }
      return SHAPE;
   }

   public boolean isValidBonemealTarget(LevelReader p_255692_, BlockPos p_57326_, BlockState p_57327_, boolean p_57328_) {
      return true;
   }

   public boolean isBonemealSuccess(Level p_222583_, RandomSource p_222584_, BlockPos p_222585_, BlockState p_222586_) {
      return true;
   }

   public void performBonemeal(ServerLevel p_222578_, RandomSource p_222579_, BlockPos p_222580_, BlockState p_222581_) {
      Block block = ModBlocks.TALL_END_GRASS.get();
      if (p_222581_.is(ModBlocks.END_GRASS_SPROUT.get())) {
         block = ModBlocks.END_GRASS.get();
      }
      if (block.defaultBlockState().canSurvive(p_222578_, p_222580_)) {
         p_222578_.setBlock(p_222580_, block.defaultBlockState(), 2);
      }
   }
}
