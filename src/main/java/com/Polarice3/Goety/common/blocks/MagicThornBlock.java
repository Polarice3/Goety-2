package com.Polarice3.Goety.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;

public class MagicThornBlock extends Block implements IPlantable, SimpleWaterloggedBlock {
   protected static final VoxelShape COLLISION_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);
   protected static final VoxelShape OUTLINE_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
   public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

   public MagicThornBlock() {
      super(BlockBehaviour.Properties.of(Material.WOOD)
              .strength(2.0F)
              .randomTicks()
              .noOcclusion()
              .sound(SoundType.WOOL));
      this.registerDefaultState(this.stateDefinition.any().setValue(PERSISTENT, Boolean.FALSE).setValue(WATERLOGGED, Boolean.FALSE));
   }

   public VoxelShape getVisualShape(BlockState p_48735_, BlockGetter p_48736_, BlockPos p_48737_, CollisionContext p_48738_) {
      return Shapes.empty();
   }

   @Override
   public boolean isRandomlyTicking(BlockState p_49921_) {
      return !p_49921_.getValue(PERSISTENT);
   }

   public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
      if (!pLevel.isAreaLoaded(pPos, 1)) {
         return;
      }
      pLevel.destroyBlock(pPos, false);
   }

   public void tick(BlockState p_220908_, ServerLevel p_220909_, BlockPos p_220910_, RandomSource p_220911_) {
      if (!p_220909_.isAreaLoaded(p_220910_, 1)) {
         return;
      }
      if (!p_220908_.canSurvive(p_220909_, p_220910_)) {
         p_220909_.destroyBlock(p_220910_, true);
      }

   }

   public VoxelShape getCollisionShape(BlockState p_51176_, BlockGetter p_51177_, BlockPos p_51178_, CollisionContext p_51179_) {
      return COLLISION_SHAPE;
   }

   public VoxelShape getShape(BlockState p_51171_, BlockGetter p_51172_, BlockPos p_51173_, CollisionContext p_51174_) {
      return OUTLINE_SHAPE;
   }

   public BlockState updateShape(BlockState p_51157_, Direction p_51158_, BlockState p_51159_, LevelAccessor p_51160_, BlockPos p_51161_, BlockPos p_51162_) {
      if (!p_51157_.canSurvive(p_51160_, p_51161_)) {
         p_51160_.scheduleTick(p_51161_, this, 1);
      }
      if (p_51157_.getValue(WATERLOGGED)) {
         p_51160_.scheduleTick(p_51161_, Fluids.WATER, Fluids.WATER.getTickDelay(p_51160_));
      }

      return super.updateShape(p_51157_, p_51158_, p_51159_, p_51160_, p_51161_, p_51162_);
   }

   public boolean canSurvive(BlockState p_51153_, LevelReader p_51154_, BlockPos p_51155_) {
      for(Direction direction : Direction.Plane.HORIZONTAL) {
         if (p_51154_.getFluidState(p_51155_.relative(direction)).is(FluidTags.LAVA)) {
            return false;
         }
      }
      return (p_51154_.getBlockState(p_51155_.below()).isSolidRender(p_51154_, p_51155_.below()) || p_51154_.getBlockState(p_51155_.below()).is(this)) && !p_51154_.getBlockState(p_51155_.above()).getMaterial().isLiquid();
   }

   public void entityInside(BlockState p_51148_, Level p_51149_, BlockPos p_51150_, Entity p_51151_) {
      if (p_51151_ instanceof LivingEntity) {
         p_51151_.hurt(DamageSource.SWEET_BERRY_BUSH, 1.0F);
      }
   }

   public FluidState getFluidState(BlockState p_221384_) {
      return p_221384_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_221384_);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_54447_) {
      p_54447_.add(PERSISTENT, WATERLOGGED);
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_54424_) {
      FluidState fluidstate = p_54424_.getLevel().getFluidState(p_54424_.getClickedPos());
      return this.defaultBlockState().setValue(PERSISTENT, Boolean.valueOf(true)).setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
   }

   public boolean isPathfindable(BlockState p_51143_, BlockGetter p_51144_, BlockPos p_51145_, PathComputationType p_51146_) {
      return false;
   }

   public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, net.minecraftforge.common.IPlantable plantable) {
      return state.getBlock() instanceof MagicThornBlock;
   }

   @Override
   public BlockState getPlant(BlockGetter level, BlockPos pos) {
      BlockState state = level.getBlockState(pos);
      if (state.getBlock() != this) return defaultBlockState();
      return state;
   }
}
