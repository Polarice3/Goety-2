package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.api.blocks.ISeat;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Map;

public class RoyalThroneBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock, ISeat {
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final VoxelShape SHAPE_LEG_1 = Block.box(16.0D, 0.0D, 0.0D,
            18.0D, 5.0D, 2.0D);
    public static final VoxelShape SHAPE_LEG_2 = Block.box(-1.0D, 0.0D, 0.0D,
            1.0D, 5.0D, 2.0D);
    public static final VoxelShape SHAPE_LEG_3 = Block.box(16.0D, 0.0D, 14.0D,
            18.0D, 5.0D, 16.0D);
    public static final VoxelShape SHAPE_LEG_4 = Block.box(-1.0D, 0.0D, 14.0D,
            1.0D, 5.0D, 16.0D);
    public static final VoxelShape SHAPE_LEFT_ARM = Block.box(17.0D, 5.0D, -1.0D,
            19.0D, 16.0D, 17.0D);
    public static final VoxelShape SHAPE_SIT = Block.box(0.0D, 5.0D, -1.0D,
            17.0D, 8.0D, 17.0D);
    public static final VoxelShape SHAPE_RIGHT_ARM = Block.box(-2.0D, 5.0D, -1.0D,
            0.0D, 16.0D, 17.0D);
    public static final VoxelShape SHAPE_BACK = Block.box(0.0D, 8.0D, 15.0D,
            17.0D, 16.0D, 17.0D);
    public static final VoxelShape SHAPE_TOP_LEFT_ARM = Block.box(17.0D, 0.0D, -1.0D,
            19.0D, 1.0D, 17.0D);
    public static final VoxelShape SHAPE_TOP_RIGHT_ARM = Block.box(-2.0D, 0.0D, -1.0D,
            0.0D, 1.0D, 17.0D);
    public static final VoxelShape SHAPE_TOP_BACK = Block.box(0.0D, 0.0D, 15.0D,
            17.0D, 15.0D, 17.0D);
    public static final VoxelShape SHAPE_BOTTOM = Shapes.or(SHAPE_LEG_1, SHAPE_LEG_2, SHAPE_LEG_3, SHAPE_LEG_4, SHAPE_LEFT_ARM, SHAPE_SIT, SHAPE_RIGHT_ARM, SHAPE_BACK);
    public static final VoxelShape SHAPE_TOP = Shapes.or(SHAPE_TOP_LEFT_ARM, SHAPE_TOP_RIGHT_ARM, SHAPE_TOP_BACK);
    private static final Map<Direction, VoxelShape> LOWER_AABB = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, SHAPE_BOTTOM, Direction.SOUTH, MathHelper.rotateVoxelShape(SHAPE_BOTTOM, Direction.SOUTH), Direction.WEST, MathHelper.rotateVoxelShape(SHAPE_BOTTOM, Direction.WEST), Direction.EAST, MathHelper.rotateVoxelShape(SHAPE_BOTTOM, Direction.EAST)));
    private static final Map<Direction, VoxelShape> TOP_AABB = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, SHAPE_TOP, Direction.SOUTH, MathHelper.rotateVoxelShape(SHAPE_TOP, Direction.SOUTH), Direction.WEST, MathHelper.rotateVoxelShape(SHAPE_TOP, Direction.WEST), Direction.EAST, MathHelper.rotateVoxelShape(SHAPE_TOP, Direction.EAST)));

    public RoyalThroneBlock(Properties p_54120_) {
        super(p_54120_);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE).setValue(HALF, DoubleBlockHalf.LOWER).setValue(FACING, Direction.NORTH));
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

    @Override
    public BlockPathTypes getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, @Nullable Mob entity) {
        return BlockPathTypes.RAIL;
    }

    @Override
    public Vec3 seatOffset(BlockPos pos) {
        return ISeat.super.seatOffset(pos).add(0, 0.1D, 0);
    }

    @Override
    public boolean hasLookAngle() {
        return true;
    }

    @Override
    public float seatLookAngle(Level world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.hasProperty(FACING)) {
            return blockState.getValue(FACING).toYRot();
        }
        return 0.0F;
    }

    @Override
    public InteractionResult use(BlockState p_60503_, Level p_60504_, BlockPos p_60505_, Player p_60506_, InteractionHand p_60507_, BlockHitResult p_60508_) {
        if (p_60503_.getValue(HALF) == DoubleBlockHalf.UPPER) {
            p_60505_ = p_60505_.below();
        }
        return this.sitDown(p_60503_, p_60504_, p_60505_, p_60506_, p_60507_, p_60508_);
    }

    public VoxelShape getShape(BlockState p_58152_, BlockGetter p_58153_, BlockPos p_58154_, CollisionContext p_58155_) {
        return getShape(p_58152_);
    }

    public static VoxelShape getShape(BlockState p_58157_) {
        if (p_58157_.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return TOP_AABB.get(p_58157_.getValue(FACING));
        }
        return LOWER_AABB.get(p_58157_.getValue(FACING));
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext p_51750_) {
        BlockState blockstate = this.defaultBlockState();
        LevelReader levelreader = p_51750_.getLevel();
        BlockPos blockpos = p_51750_.getClickedPos();
        FluidState fluidstate = levelreader.getFluidState(blockpos);
        boolean flag = fluidstate.getType() == Fluids.WATER;

        for(Direction direction : p_51750_.getNearestLookingDirections()) {
            if (direction.getAxis().isHorizontal()) {
                Direction direction1 = direction.getOpposite();
                blockstate = blockstate.setValue(FACING, direction1).setValue(HALF, DoubleBlockHalf.LOWER);
                if (blockstate.canSurvive(levelreader, blockpos) && blockpos.getY() < levelreader.getMaxBuildHeight() - 1 && levelreader.getBlockState(blockpos.above()).canBeReplaced(p_51750_)) {
                    return blockstate.setValue(WATERLOGGED, flag);
                }
            }
        }
        return null;
    }

    public boolean placeLiquid(LevelAccessor pLevel, BlockPos pPos, BlockState pState, FluidState pFluidState) {
        if (!pState.getValue(BlockStateProperties.WATERLOGGED) && pFluidState.getType() == Fluids.WATER) {
            pLevel.setBlock(pPos, pState.setValue(WATERLOGGED, Boolean.TRUE), 3);
            pLevel.scheduleTick(pPos, pFluidState.getType(), pFluidState.getType().getTickDelay(pLevel));
            return true;
        } else {
            return false;
        }
    }

    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    public boolean canSurvive(BlockState p_52783_, LevelReader p_52784_, BlockPos p_52785_) {
        BlockPos blockpos = p_52785_.below();
        BlockState blockstate = p_52784_.getBlockState(blockpos);
        return p_52783_.getValue(HALF) == DoubleBlockHalf.LOWER ? blockstate.isFaceSturdy(p_52784_, blockpos, Direction.UP) : blockstate.is(this);
    }

    public BlockState updateShape(BlockState p_51771_, Direction p_51772_, BlockState p_51773_, LevelAccessor p_51774_, BlockPos p_51775_, BlockPos p_51776_) {
        DoubleBlockHalf doubleblockhalf = p_51771_.getValue(HALF);
        if (p_51772_.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (p_51772_ == Direction.UP)) {
            return p_51773_.is(this) && p_51773_.getValue(HALF) != doubleblockhalf ? p_51771_.setValue(FACING, p_51773_.getValue(FACING)) : Blocks.AIR.defaultBlockState();
        } else {
            return doubleblockhalf == DoubleBlockHalf.LOWER && p_51772_ == Direction.DOWN && p_51772_ == p_51771_.getValue(FACING) && !p_51771_.canSurvive(p_51774_, p_51775_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_51771_, p_51772_, p_51773_, p_51774_, p_51775_, p_51776_);
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_51778_) {
        p_51778_.add(FACING, WATERLOGGED, HALF);
    }

    public boolean isPathfindable(BlockState p_51762_, BlockGetter p_51763_, BlockPos p_51764_, PathComputationType p_51765_) {
        return false;
    }
}
