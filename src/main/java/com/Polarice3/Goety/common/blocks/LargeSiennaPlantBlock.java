package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.IPlantable;

public class LargeSiennaPlantBlock extends DoublePlantBlock implements IForgeShearable {
   protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);

   public LargeSiennaPlantBlock() {
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

   protected boolean mayPlaceOn(BlockState state, BlockGetter world, BlockPos pos) {
      return state.is(ModTags.Blocks.RED_MOSS_PLANTABLES) || state.isSolidRender(world, pos);
   }

   public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
      return state.is(ModTags.Blocks.RED_MOSS_PLANTABLES) || state.isSolidRender(world, pos);
   }
}
