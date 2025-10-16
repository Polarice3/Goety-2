package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.IPlantable;

public class LargeChorusFernBlock extends DoublePlantBlock implements IForgeShearable {

   public LargeChorusFernBlock() {
      super(Properties.of()
              .mapColor(MapColor.COLOR_YELLOW)
              .replaceable()
              .noCollission()
              .instabreak()
              .sound(SoundType.GRASS)
              .offsetType(BlockBehaviour.OffsetType.XYZ)
              .pushReaction(PushReaction.DESTROY));
   }

   protected boolean mayPlaceOn(BlockState state, BlockGetter world, BlockPos pos) {
      return state.is(ModTags.Blocks.END_PLANTABLES) || state.isSolidRender(world, pos);
   }

   public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
      return state.is(ModTags.Blocks.END_PLANTABLES) || state.isSolidRender(world, pos);
   }
}
