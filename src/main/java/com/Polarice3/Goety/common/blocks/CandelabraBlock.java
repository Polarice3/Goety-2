package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ItemHelper;
import com.google.common.collect.ImmutableList;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.ToIntFunction;

public class CandelabraBlock extends Block implements SimpleWaterloggedBlock {
    protected static final VoxelShape SHAPE_BASE = Block.box(4.0D, 0.0D, 4.0D,
            12.0D, 2.0D, 12.0D);
    protected static final VoxelShape SHAPE_POLE_LOWER = Block.box(7.0D, 2.0D, 7.0D,
            9.0D, 16.0D, 9.0D);
    protected static final VoxelShape SHAPE_POLE_UPPER = Block.box(7.0D, 0.0D, 7.0D,
            9.0D, 2.0D, 9.0D);
    protected static final VoxelShape SHAPE_UPPER_BASE = Block.box(2.0D, 2.0D, 2.0D,
            14.0D, 4.0D, 14.0D);
    protected static final VoxelShape SHAPE_CANDLE_1 = Block.box(12.0D, 4.0D, 2.0D,
            14.0D, 10.0D, 4.0D);
    protected static final VoxelShape SHAPE_CANDLE_2 = Block.box(2.0D, 4.0D, 2.0D,
            4.0D, 10.0D, 4.0D);
    protected static final VoxelShape SHAPE_CANDLE_3 = Block.box(12.0D, 4.0D, 12.0D,
            14.0D, 10.0D, 14.0D);
    protected static final VoxelShape SHAPE_CANDLE_4 = Block.box(2.0D, 4.0D, 12.0D,
            4.0D, 10.0D, 14.0D);
    protected static final VoxelShape SHAPE_CANDLE_5 = Block.box(7.0D, 4.0D, 7.0D,
            9.0D, 12.0D, 9.0D);
    public static final VoxelShape SHAPE_LOWER = Shapes.or(SHAPE_BASE, SHAPE_POLE_LOWER);
    public static final VoxelShape SHAPE_CANDLES = Shapes.or(SHAPE_CANDLE_1, SHAPE_CANDLE_2, SHAPE_CANDLE_3, SHAPE_CANDLE_4, SHAPE_CANDLE_5);
    public static final VoxelShape SHAPE_UPPER = Shapes.or(SHAPE_POLE_UPPER, SHAPE_UPPER_BASE, SHAPE_CANDLES);
    public List<Vec3> offset = ImmutableList.of(new Vec3(0.5D, 1.0D, 0.5D), new Vec3(3 / 16.0D, 14 / 16.0D, 3 / 16.0D), new Vec3(13 / 16.0D, 14 / 16.0D, 3 / 16.0D), new Vec3(13 / 16.0D, 14 / 16.0D, 13 / 16.0D), new Vec3(3 / 16.0D, 14 / 16.0D, 13 / 16.0D));
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public CandelabraBlock(Properties properties, int lightLevel) {
        super(properties
                .lightLevel(litBlockEmission(lightLevel))
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE).setValue(LIT, Boolean.TRUE).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    private static ToIntFunction<BlockState> litBlockEmission(int lightLevel) {
        return (state) -> state.getValue(BlockStateProperties.LIT) && state.getValue(HALF) == DoubleBlockHalf.UPPER ? lightLevel : 0;
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
        return p_152846_.hasProperty(LIT) && p_152846_.hasProperty(WATERLOGGED) && p_152846_.hasProperty(HALF) && !p_152846_.getValue(LIT) && !p_152846_.getValue(WATERLOGGED) && p_152846_.getValue(HALF) == DoubleBlockHalf.UPPER;
    }

    public RenderShape getRenderShape(BlockState p_222219_) {
        return RenderShape.MODEL;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        LevelAccessor iworld = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        boolean flag = iworld.getFluidState(blockpos).getType() == Fluids.WATER;
        if (blockpos.getY() < iworld.getMaxBuildHeight() - 1 && iworld.getBlockState(blockpos.above()).canBeReplaced(pContext)) {
            return this.defaultBlockState().setValue(WATERLOGGED, flag).setValue(HALF, DoubleBlockHalf.LOWER);
        } else {
            return null;
        }
    }

    public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos blockPos) {
        BlockPos blockpos = blockPos.below();
        BlockState blockstate = level.getBlockState(blockpos);
        return blockState.getValue(HALF) == DoubleBlockHalf.LOWER ? super.canSurvive(blockState, level, blockPos) : blockstate.is(this);
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        DoubleBlockHalf doubleblockhalf = pState.getValue(HALF);
        if (pFacing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (pFacing == Direction.UP)) {
            return pFacingState.is(this) && pFacingState.getValue(HALF) != doubleblockhalf ? pState.setValue(LIT, pFacingState.getValue(LIT)) : Blocks.AIR.defaultBlockState();
        } else {
            return doubleblockhalf == DoubleBlockHalf.LOWER && pFacing == Direction.DOWN && !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
        }
    }

    public boolean placeLiquid(LevelAccessor p_152805_, BlockPos p_152806_, BlockState p_152807_, FluidState p_152808_) {
        if (!p_152807_.getValue(WATERLOGGED) && p_152808_.getType() == Fluids.WATER) {
            BlockState blockstate = p_152807_.setValue(WATERLOGGED, Boolean.valueOf(true));
            if (p_152807_.getValue(LIT) && p_152807_.getValue(HALF) == DoubleBlockHalf.UPPER) {
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

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(WATERLOGGED, LIT, HALF);
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (pState.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return SHAPE_LOWER;
        } else if (pState.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return SHAPE_UPPER;
        }
        return Shapes.block();
    }

    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    protected Iterable<Vec3> getParticleOffsets() {
        return this.offset;
    }

    public void animateTick(BlockState p_220697_, Level p_220698_, BlockPos p_220699_, RandomSource p_220700_) {
        if (p_220697_.getValue(LIT) && p_220697_.getValue(HALF) == DoubleBlockHalf.UPPER) {
            this.getParticleOffsets().forEach((p_220695_) -> {
                this.addParticlesAndSound(p_220698_, p_220695_.add(p_220699_.getX(), p_220699_.getY(), p_220699_.getZ()), p_220700_);
            });
        }
    }

    public void addParticlesAndSound(Level p_220688_, Vec3 p_220689_, RandomSource p_220690_) {
        float f = p_220690_.nextFloat();
        if (f < 0.3F) {
            if (f < 0.17F) {
                p_220688_.playLocalSound(p_220689_.x, p_220689_.y, p_220689_.z, SoundEvents.CANDLE_AMBIENT, SoundSource.BLOCKS, 1.0F + p_220690_.nextFloat(), p_220690_.nextFloat() * 0.7F + 0.3F, false);
            }
        }

        p_220688_.addParticle(ModParticleTypes.SMALL_FIRE.get(), p_220689_.x, p_220689_.y, p_220689_.z, 0.0D, 0.0D, 0.0D);
        p_220688_.addParticle(ModParticleTypes.SMALL_FIRE_REVERSED.get(), p_220689_.x, p_220689_.y, p_220689_.z, 0.0D, 0.0D, 0.0D);
    }
}
