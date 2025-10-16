package com.Polarice3.Goety.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TallEndGrassBlock extends EndPlantBlock {
   protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);

   public TallEndGrassBlock() {
      super(Properties.of()
              .mapColor(MapColor.SAND)
              .replaceable()
              .noCollission()
              .instabreak()
              .lightLevel(l -> 4)
              .sound(SoundType.GRASS)
              .offsetType(BlockBehaviour.OffsetType.XYZ)
              .pushReaction(PushReaction.DESTROY));
   }

   public VoxelShape getShape(BlockState p_52419_, BlockGetter p_52420_, BlockPos p_52421_, CollisionContext p_52422_) {
      return SHAPE;
   }
}
