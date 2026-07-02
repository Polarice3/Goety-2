package com.Polarice3.Goety.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class HenbaneBlock extends CropBlock {
   public static final int MAX_AGE = 3;
   public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
   private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
           Block.box(6.0D, 0.0D, 6.0D, 10.0D, 4.0D, 10.0D),
           Block.box(4.0D, 0.0D, 4.0D, 12.0D, 12.0D, 12.0D),
           Block.box(2.0D, 0.0D, 2.0D, 14.0D, 15.0D, 14.0D),
           Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};

   public HenbaneBlock() {
      super(Properties.of()
              .mapColor(MapColor.PLANT)
              .noCollission()
              .randomTicks()
              .instabreak()
              .sound(SoundType.CROP)
              .pushReaction(PushReaction.DESTROY));
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
   }

   protected @NotNull IntegerProperty getAgeProperty() {
      return AGE;
   }

   public int getMaxAge() {
      return MAX_AGE;
   }

   protected @NotNull ItemLike getBaseSeedId() {
      return ModBlocks.HENBANE_SEEDS.get();
   }

   public void randomTick(@NotNull BlockState p_220778_, @NotNull ServerLevel p_220779_, @NotNull BlockPos p_220780_, RandomSource p_220781_) {
      if (p_220781_.nextInt(3) != 0) {
         super.randomTick(p_220778_, p_220779_, p_220780_, p_220781_);
      }
   }

   protected int getBonemealAgeIncrease(@NotNull Level p_49663_) {
      return super.getBonemealAgeIncrease(p_49663_) / 3;
   }

   public @NotNull VoxelShape getShape(@NotNull BlockState p_57291_, @NotNull BlockGetter p_57292_, @NotNull BlockPos p_57293_, @NotNull CollisionContext p_57294_) {
      return SHAPE_BY_AGE[this.getAge(p_57291_)];
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_57282_) {
      p_57282_.add(AGE);
   }
}