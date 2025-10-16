package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.IPlantable;

public class ChorusStalkBlock extends BushBlock implements BonemealableBlock, IForgeShearable {
   protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

   public ChorusStalkBlock() {
      super(Properties.of()
              .mapColor(MapColor.COLOR_PURPLE)
              .replaceable()
              .noCollission()
              .instabreak()
              .lightLevel(l -> 4)
              .emissiveRendering((i, d, k) -> true)
              .sound(SoundType.STEM)
              .offsetType(BlockBehaviour.OffsetType.XYZ)
              .pushReaction(PushReaction.DESTROY));
   }

   public VoxelShape getShape(BlockState p_52419_, BlockGetter p_52420_, BlockPos p_52421_, CollisionContext p_52422_) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(BlockState state, BlockGetter world, BlockPos pos) {
      return state.is(ModTags.Blocks.CHORUS_GROW) || state.isSolidRender(world, pos);
   }

   public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
      return state.is(ModTags.Blocks.CHORUS_GROW) || state.isSolidRender(world, pos);
   }

   public boolean isValidBonemealTarget(LevelReader p_255692_, BlockPos p_57326_, BlockState p_57327_, boolean p_57328_) {
      return true;
   }

   public boolean isBonemealSuccess(Level p_222583_, RandomSource p_222584_, BlockPos p_222585_, BlockState p_222586_) {
      return true;
   }

   public void performBonemeal(ServerLevel p_222578_, RandomSource p_222579_, BlockPos p_222580_, BlockState p_222581_) {
      DoublePlantBlock doubleplantblock = (DoublePlantBlock)ModBlocks.LARGE_CHORUS_STALK.get();
      if (doubleplantblock.defaultBlockState().canSurvive(p_222578_, p_222580_) && p_222578_.isEmptyBlock(p_222580_.above())) {
         DoublePlantBlock.placeAt(p_222578_, doubleplantblock.defaultBlockState(), p_222580_, 2);
      }
   }
}
