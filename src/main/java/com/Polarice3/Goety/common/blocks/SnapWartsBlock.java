package com.Polarice3.Goety.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class SnapWartsBlock extends Block implements BonemealableBlock {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_2;
   public static final DirectionProperty FACING = BlockStateProperties.FACING;
   protected static final VoxelShape[] EAST_AABB = new VoxelShape[]{Block.box(14.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D), Block.box(13.0D, 5.0D, 5.0D, 16.0D, 11.0D, 11.0D), Block.box(12.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D)};
   protected static final VoxelShape[] WEST_AABB = new VoxelShape[]{Block.box(0.0D, 6.0D, 6.0D, 2.0D, 10.0D, 10.0D), Block.box(0.0D, 5.0D, 5.0D, 3.0D, 11.0D, 11.0D), Block.box(0.0D, 4.0D, 4.0D, 4.0D, 12.0D, 12.0D)};
   protected static final VoxelShape[] NORTH_AABB = new VoxelShape[]{Block.box(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 2.0D), Block.box(5.0D, 5.0D, 0.0D, 11.0D, 11.0D, 3.0D), Block.box(4.0D, 4.0D, 0, 12.0D, 12.0D, 4.0D)};
   protected static final VoxelShape[] SOUTH_AABB = new VoxelShape[]{Block.box(6.0D, 6.0D, 14.0D, 10.0D, 10.0D, 16.0D), Block.box(5.0D, 5.0D, 13.0D, 11.0D, 11.0D, 16.0D), Block.box(4.0D, 4.0D, 12.0D, 12.0D, 12.0D, 16.0D)};
   protected static final VoxelShape[] UP_AABB = new VoxelShape[]{Block.box(6.0D, 14.0D, 6.0D, 10.0D, 16.0D, 10.0D), Block.box(5.0D, 13.0D, 5.0D, 11.0D, 16.0D, 11.0D), Block.box(4.0D, 12.0D, 4.0D, 12.0D, 16.0D, 12.0D)};
   protected static final VoxelShape[] DOWN_AABB = new VoxelShape[]{Block.box(6.0D, 0.0D, 6.0D, 10.0D, 2.0D, 10.0D), Block.box(5.0D, 0.0D, 5.0D, 11.0D, 3.0D, 11.0D), Block.box(4.0D, 0.0D, 4.0D, 12.0D, 4.0D, 12.0D)};

   public SnapWartsBlock() {
      super(BlockBehaviour.Properties.of(Material.PLANT)
              .randomTicks()
              .strength(0.2F, 3.0F)
              .sound(SoundType.FUNGUS)
              .noOcclusion());
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(AGE, 0));
   }

   public boolean isRandomlyTicking(BlockState p_51780_) {
      return p_51780_.getValue(AGE) < 2;
   }

   public void randomTick(BlockState p_221000_, ServerLevel p_221001_, BlockPos p_221002_, RandomSource p_221003_) {
      int i = p_221000_.getValue(AGE);
      if (i < 2 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(p_221001_, p_221002_, p_221000_, p_221001_.random.nextInt(5) == 0)) {
         p_221001_.setBlock(p_221002_, p_221000_.setValue(AGE, i + 1), 2);
         net.minecraftforge.common.ForgeHooks.onCropsGrowPost(p_221001_, p_221002_, p_221000_);
      }

   }

   public boolean canSurvive(BlockState p_51767_, LevelReader p_51768_, BlockPos p_51769_) {
      BlockState blockstate = p_51768_.getBlockState(p_51769_.relative(p_51767_.getValue(FACING)));
      return blockstate.is(BlockTags.CRIMSON_STEMS) || blockstate.is(BlockTags.WARPED_STEMS);
   }

   public VoxelShape getShape(BlockState p_51787_, BlockGetter p_51788_, BlockPos p_51789_, CollisionContext p_51790_) {
      int i = p_51787_.getValue(AGE);
      switch ((Direction)p_51787_.getValue(FACING)) {
         case SOUTH:
            return SOUTH_AABB[i];
         case NORTH:
         default:
            return NORTH_AABB[i];
         case WEST:
            return WEST_AABB[i];
         case EAST:
            return EAST_AABB[i];
         case UP:
            return UP_AABB[i];
         case DOWN:
            return DOWN_AABB[i];
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_51750_) {
      BlockState blockstate = this.defaultBlockState();
      LevelReader levelreader = p_51750_.getLevel();
      BlockPos blockpos = p_51750_.getClickedPos();

      for(Direction direction : p_51750_.getNearestLookingDirections()) {
         blockstate = blockstate.setValue(FACING, direction);
         if (blockstate.canSurvive(levelreader, blockpos)) {
            return blockstate;
         }
      }

      return null;
   }

   public BlockState rotate(BlockState p_54125_, Rotation p_54126_) {
      return p_54125_.setValue(FACING, p_54126_.rotate(p_54125_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_54122_, Mirror p_54123_) {
      return p_54122_.rotate(p_54123_.getRotation(p_54122_.getValue(FACING)));
   }

   public BlockState updateShape(BlockState p_51771_, Direction p_51772_, BlockState p_51773_, LevelAccessor p_51774_, BlockPos p_51775_, BlockPos p_51776_) {
      return p_51772_ == p_51771_.getValue(FACING) && !p_51771_.canSurvive(p_51774_, p_51775_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_51771_, p_51772_, p_51773_, p_51774_, p_51775_, p_51776_);
   }

   public boolean isValidBonemealTarget(BlockGetter p_51752_, BlockPos p_51753_, BlockState p_51754_, boolean p_51755_) {
      return p_51754_.getValue(AGE) < 2;
   }

   public boolean isBonemealSuccess(Level p_220995_, RandomSource p_220996_, BlockPos p_220997_, BlockState p_220998_) {
      return true;
   }

   public void performBonemeal(ServerLevel p_220990_, RandomSource p_220991_, BlockPos p_220992_, BlockState p_220993_) {
      p_220990_.setBlock(p_220992_, p_220993_.setValue(AGE, Integer.valueOf(p_220993_.getValue(AGE) + 1)), 2);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_51778_) {
      p_51778_.add(FACING, AGE);
   }

   public boolean isPathfindable(BlockState p_51762_, BlockGetter p_51763_, BlockPos p_51764_, PathComputationType p_51765_) {
      return false;
   }
}
