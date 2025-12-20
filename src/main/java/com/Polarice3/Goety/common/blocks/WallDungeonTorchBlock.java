package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Map;

public class WallDungeonTorchBlock extends DungeonTorchBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   private static final Map<Direction, VoxelShape> AABBS = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.box(5.5D, 3.0D, 11.0D, 10.5D, 13.0D, 16.0D), Direction.SOUTH, Block.box(5.5D, 3.0D, 0.0D, 10.5D, 13.0D, 5.0D), Direction.WEST, Block.box(11.0D, 3.0D, 5.5D, 16.0D, 13.0D, 10.5D), Direction.EAST, Block.box(0.0D, 3.0D, 5.5D, 5.0D, 13.0D, 10.5D)));

   public WallDungeonTorchBlock(Properties p_58123_) {
      super(p_58123_);
      this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
   }

   public String getDescriptionId() {
      return this.asItem().getDescriptionId();
   }

   public VoxelShape getShape(BlockState p_58152_, BlockGetter p_58153_, BlockPos p_58154_, CollisionContext p_58155_) {
      return getShape(p_58152_);
   }

   public static VoxelShape getShape(BlockState p_58157_) {
      return AABBS.get(p_58157_.getValue(FACING));
   }

   public boolean canSurvive(BlockState p_58133_, LevelReader p_58134_, BlockPos p_58135_) {
      Direction direction = p_58133_.getValue(FACING);
      BlockPos blockpos = p_58135_.relative(direction.getOpposite());
      BlockState blockstate = p_58134_.getBlockState(blockpos);
      return blockstate.isFaceSturdy(p_58134_, blockpos, direction);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_58126_) {
      BlockState blockstate = this.defaultBlockState();
      LevelReader levelreader = p_58126_.getLevel();
      BlockPos blockpos = p_58126_.getClickedPos();
      Direction[] adirection = p_58126_.getNearestLookingDirections();

      for(Direction direction : adirection) {
         if (direction.getAxis().isHorizontal()) {
            Direction direction1 = direction.getOpposite();
            blockstate = blockstate.setValue(FACING, direction1);
            if (blockstate.canSurvive(levelreader, blockpos)) {
               return blockstate;
            }
         }
      }

      return null;
   }

   public BlockState updateShape(BlockState p_58143_, Direction p_58144_, BlockState p_58145_, LevelAccessor p_58146_, BlockPos p_58147_, BlockPos p_58148_) {
      return p_58144_.getOpposite() == p_58143_.getValue(FACING) && !p_58143_.canSurvive(p_58146_, p_58147_) ? Blocks.AIR.defaultBlockState() : p_58143_;
   }

   public void animateTick(BlockState p_222660_, Level p_222661_, BlockPos p_222662_, RandomSource p_222663_) {
      if (p_222660_.getValue(LIT)) {
         double d0 = (double) p_222662_.getX() + 0.5D;
         double d1 = (double) p_222662_.getY() + 1.25D;
         double d2 = (double) p_222662_.getZ() + 0.5D;
         p_222661_.addParticle(ModParticleTypes.BIG_FIRE.get(), d0, d1, d2, 0.0D, 0.0D, 0.0D);
         p_222661_.addParticle(ModParticleTypes.BIG_FIRE_DROP.get(), d0, d1, d2, 0.0D, 0.0D, 0.0D);
      }
   }

   public BlockState rotate(BlockState p_58140_, Rotation p_58141_) {
      return p_58140_.setValue(FACING, p_58141_.rotate(p_58140_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_58137_, Mirror p_58138_) {
      return p_58137_.rotate(p_58138_.getRotation(p_58137_.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_58150_) {
      super.createBlockStateDefinition(p_58150_);
      p_58150_.add(FACING);
   }
}