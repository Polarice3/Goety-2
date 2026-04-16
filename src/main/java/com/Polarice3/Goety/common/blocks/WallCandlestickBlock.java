package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.MathHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.ToIntFunction;

public class WallCandlestickBlock extends Block implements SimpleWaterloggedBlock {
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty LIT = BlockStateProperties.LIT;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public int lightLevel = 0;
   protected static final VoxelShape NORTH_LOWER_AABB = Shapes.or(
           Block.box(4.0D, 4.0D, 14.0D,
                   12.0D, 12.0D, 16.0D),
           Block.box(6.0D, 6.0D, 12.0D,
                   10.0D, 10.0D, 14.0D),
           Block.box(7.0D, 7.0D, 7.0D,
                   9.0D, 9.0D, 12.0D),
           Block.box(7.0D, 9.0D, 7.0D,
                   9.0D, 13.0D, 9.0D),
           Block.box(6.0D, 13.0D, 6.0D,
                   10.0D, 14.0D, 10.0D),
           Block.box(7.0D, 14.0D, 7.0D,
                   9.0D, 16.0D, 9.0D));
   private static final Map<Direction, VoxelShape> LOWER_AABB = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, NORTH_LOWER_AABB, Direction.SOUTH, MathHelper.rotateVoxelShape(NORTH_LOWER_AABB, Direction.SOUTH), Direction.WEST, MathHelper.rotateVoxelShape(NORTH_LOWER_AABB, Direction.WEST), Direction.EAST, MathHelper.rotateVoxelShape(NORTH_LOWER_AABB, Direction.EAST)));
   protected static final VoxelShape TOP_AABB = Block.box(7.0D, 0.0D, 7.0D, 9.0D, 6.0D, 9.0D);

   public WallCandlestickBlock(Properties p_49795_, int lightLevel) {
      super(p_49795_
              .lightLevel(litBlockEmission(lightLevel))
              .noOcclusion());
      this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE).setValue(LIT, Boolean.TRUE).setValue(FACING, Direction.NORTH).setValue(HALF, DoubleBlockHalf.LOWER));
   }

   private static ToIntFunction<BlockState> litBlockEmission(int lightLevel) {
      return (state) -> state.getValue(BlockStateProperties.LIT) ? lightLevel : 0;
   }

   public InteractionResult use(BlockState p_152822_, Level p_152823_, BlockPos p_152824_, Player p_152825_, InteractionHand p_152826_, BlockHitResult p_152827_) {
      ItemStack itemStack = p_152825_.getItemInHand(p_152826_);
      if (p_152822_.getValue(HALF) == DoubleBlockHalf.UPPER) {
         if (p_152825_.getAbilities().mayBuild && itemStack.isEmpty() && p_152822_.getValue(LIT)) {
            extinguish(p_152825_, p_152822_, p_152823_, p_152824_);
            return InteractionResult.sidedSuccess(p_152823_.isClientSide);
         } else if ((itemStack.getItem() instanceof FlintAndSteelItem || itemStack.getItem() instanceof FireChargeItem) && !p_152822_.getValue(LIT)) {
            if (itemStack.getItem() instanceof FlintAndSteelItem) {
               p_152823_.playSound(p_152825_, p_152824_, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, p_152823_.getRandom().nextFloat() * 0.4F + 0.8F);
               ItemHelper.hurtAndBreak(itemStack, 1, p_152825_);
            } else if (itemStack.getItem() instanceof FireChargeItem) {
               p_152823_.playSound(null, p_152824_, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, (p_152823_.getRandom().nextFloat() - p_152823_.getRandom().nextFloat()) * 0.2F + 1.0F);
               itemStack.shrink(1);
            }
            p_152823_.setBlockAndUpdate(p_152824_, p_152822_.setValue(LIT, Boolean.TRUE));
            p_152823_.gameEvent(p_152825_, GameEvent.BLOCK_CHANGE, p_152824_);
            return InteractionResult.sidedSuccess(p_152823_.isClientSide);
         }
      }
      return InteractionResult.PASS;
   }

   public void onProjectileHit(Level p_151905_, BlockState p_151906_, BlockHitResult p_151907_, Projectile p_151908_) {
      if (!p_151905_.isClientSide && p_151908_.isOnFire() && canLight(p_151906_)) {
         setLit(p_151905_, p_151906_, p_151907_.getBlockPos(), true);
      }
   }

   public static void extinguish(@Nullable Player p_151900_, BlockState p_151901_, LevelAccessor p_151902_, BlockPos p_151903_) {
      setLit(p_151902_, p_151901_, p_151903_, false);
      p_151902_.playSound((Player)null, p_151903_, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
      p_151902_.gameEvent(p_151900_, GameEvent.BLOCK_CHANGE, p_151903_);
   }

   private static void setLit(LevelAccessor p_151919_, BlockState p_151920_, BlockPos p_151921_, boolean p_151922_) {
      p_151919_.setBlock(p_151921_, p_151920_.setValue(LIT, p_151922_), 11);
   }

   public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
      pLevel.setBlock(pPos.above(), pState.setValue(HALF, DoubleBlockHalf.UPPER).setValue(WATERLOGGED, pLevel.getFluidState(pPos.above()).getType() == Fluids.WATER), 3);
   }

   public void playerWillDestroy(Level p_52755_, BlockPos p_52756_, BlockState p_52757_, Player p_52758_) {
      if (!p_52755_.isClientSide && p_52758_.isCreative()) {
         BlockFinder.preventCreativeDropFromBottomPart(p_52755_, p_52756_, p_52757_, p_52758_);
      }

      super.playerWillDestroy(p_52755_, p_52756_, p_52757_, p_52758_);
   }

   public static boolean canLight(BlockState p_152846_) {
      return p_152846_.hasProperty(LIT) && p_152846_.hasProperty(WATERLOGGED) && !p_152846_.getValue(LIT) && !p_152846_.getValue(WATERLOGGED);
   }

   public String getDescriptionId() {
      return this.asItem().getDescriptionId();
   }

   public VoxelShape getShape(BlockState p_58152_, BlockGetter p_58153_, BlockPos p_58154_, CollisionContext p_58155_) {
      return getShape(p_58152_);
   }

   public static VoxelShape getShape(BlockState p_58157_) {
      if (p_58157_.getValue(HALF) == DoubleBlockHalf.UPPER) {
         return TOP_AABB;
      }
      return LOWER_AABB.get(p_58157_.getValue(FACING));
   }

   public boolean canSurvive(BlockState p_58133_, LevelReader p_58134_, BlockPos p_58135_) {
      Direction direction = p_58133_.getValue(FACING);
      BlockPos blockpos = p_58135_.relative(direction.getOpposite());
      BlockState blockstate = p_58134_.getBlockState(blockpos);
      BlockPos below = p_58135_.below();
      BlockState blockBelow = p_58134_.getBlockState(below);
      if (p_58133_.getValue(HALF) == DoubleBlockHalf.UPPER) {
         return blockBelow.is(this);
      } else {
         return blockstate.isFaceSturdy(p_58134_, blockpos, direction);
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_58126_) {
      BlockState blockstate = this.defaultBlockState();
      LevelReader levelreader = p_58126_.getLevel();
      BlockPos blockpos = p_58126_.getClickedPos();
      Direction[] adirection = p_58126_.getNearestLookingDirections();
      boolean flag = levelreader.getFluidState(blockpos).getType() == Fluids.WATER;

      for(Direction direction : adirection) {
         if (direction.getAxis().isHorizontal()) {
            Direction direction1 = direction.getOpposite();
            blockstate = blockstate.setValue(FACING, direction1);
            if (blockstate.canSurvive(levelreader, blockpos) && blockpos.getY() < levelreader.getMaxBuildHeight() - 1 && levelreader.getBlockState(blockpos.above()).canBeReplaced(p_58126_)) {
               return blockstate.setValue(WATERLOGGED, flag);
            }
         }
      }

      return null;
   }

   public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
      DoubleBlockHalf doubleblockhalf = pState.getValue(HALF);
      if (pFacing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (pFacing == Direction.UP)) {
         return pFacingState.is(this) && pFacingState.getValue(HALF) != doubleblockhalf ? pState.setValue(LIT, pFacingState.getValue(LIT)) : Blocks.AIR.defaultBlockState();
      } else {
         return doubleblockhalf == DoubleBlockHalf.LOWER && pFacing == Direction.DOWN && !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
      }
   }

   public void animateTick(BlockState p_220697_, Level p_220698_, BlockPos p_220699_, RandomSource p_220700_) {
      if (p_220697_.getValue(LIT) && p_220697_.getValue(HALF) == DoubleBlockHalf.UPPER) {
         float f = p_220700_.nextFloat();
         if (f < 0.3F) {
            if (f < 0.17F) {
               p_220698_.playLocalSound(p_220699_.getX() + 0.5D, p_220699_.getY() + 0.5D, p_220699_.getZ() + 0.5D, SoundEvents.CANDLE_AMBIENT, SoundSource.BLOCKS, 1.0F + p_220700_.nextFloat(), p_220700_.nextFloat() * 0.7F + 0.3F, false);
            }
         }

         p_220698_.addParticle(ModParticleTypes.SMALL_FIRE.get(), p_220699_.getX() + 0.5D, p_220699_.getY() + (9 / 16.0D), p_220699_.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
         p_220698_.addParticle(ModParticleTypes.SMALL_FIRE_DROP.get(), p_220699_.getX() + 0.5D, p_220699_.getY() + (9 / 16.0D), p_220699_.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
      }
   }

   public BlockState rotate(BlockState p_58140_, Rotation p_58141_) {
      return p_58140_.setValue(FACING, p_58141_.rotate(p_58140_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_58137_, Mirror p_58138_) {
      return p_58137_.rotate(p_58138_.getRotation(p_58137_.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_58150_) {
      p_58150_.add(FACING, LIT, WATERLOGGED, HALF);
   }

   public boolean placeLiquid(LevelAccessor p_152805_, BlockPos p_152806_, BlockState p_152807_, FluidState p_152808_) {
      if (!p_152807_.getValue(WATERLOGGED) && p_152808_.getType() == Fluids.WATER) {
         BlockState blockstate = p_152807_.setValue(WATERLOGGED, Boolean.TRUE);
         if (p_152807_.getValue(LIT)) {
            extinguish((Player)null, blockstate, p_152805_, p_152806_);
         } else {
            p_152805_.setBlock(p_152806_, blockstate, 3);
         }

         p_152805_.scheduleTick(p_152806_, p_152808_.getType(), p_152808_.getType().getTickDelay(p_152805_));
         return true;
      } else {
         return false;
      }
   }

   public FluidState getFluidState(BlockState pState) {
      return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
   }
}