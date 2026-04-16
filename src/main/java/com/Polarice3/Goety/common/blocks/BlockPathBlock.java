package com.Polarice3.Goety.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockPathBlock extends Block {
   protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);;
   public BlockState convertTo;

   public BlockPathBlock(Properties p_153129_, BlockState convertTo) {
      super(p_153129_);
      this.convertTo = convertTo;
   }

   public boolean useShapeForLightOcclusion(BlockState p_153159_) {
      return true;
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_153131_) {
      if (this.convertTo == null) {
         return super.getStateForPlacement(p_153131_);
      }
      return !this.defaultBlockState().canSurvive(p_153131_.getLevel(), p_153131_.getClickedPos()) ? Block.pushEntitiesUp(this.defaultBlockState(), this.convertTo, p_153131_.getLevel(), p_153131_.getClickedPos()) : super.getStateForPlacement(p_153131_);
   }

   public BlockState updateShape(BlockState p_153152_, Direction p_153153_, BlockState p_153154_, LevelAccessor p_153155_, BlockPos p_153156_, BlockPos p_153157_) {
      if (p_153153_ == Direction.UP && !p_153152_.canSurvive(p_153155_, p_153156_)) {
         p_153155_.scheduleTick(p_153156_, this, 1);
      }

      return super.updateShape(p_153152_, p_153153_, p_153154_, p_153155_, p_153156_, p_153157_);
   }

   public void tick(BlockState p_221070_, ServerLevel p_221071_, BlockPos p_221072_, RandomSource p_221073_) {
      if (this.convertTo != null) {
         BlockState blockstate = pushEntitiesUp(p_221070_, this.convertTo, p_221071_, p_221072_);
         p_221071_.setBlockAndUpdate(p_221072_, blockstate);
         p_221071_.gameEvent(GameEvent.BLOCK_CHANGE, p_221072_, GameEvent.Context.of(null, blockstate));
      }
   }

   public boolean canSurvive(BlockState p_153148_, LevelReader p_153149_, BlockPos p_153150_) {
      BlockState blockstate = p_153149_.getBlockState(p_153150_.above());
      return !blockstate.isSolidRender(p_153149_, p_153150_.above()) || blockstate.getBlock() instanceof FenceGateBlock;
   }

   public VoxelShape getShape(BlockState p_153143_, BlockGetter p_153144_, BlockPos p_153145_, CollisionContext p_153146_) {
      return SHAPE;
   }

   public boolean isPathfindable(BlockState p_153138_, BlockGetter p_153139_, BlockPos p_153140_, PathComputationType p_153141_) {
      return false;
   }
}