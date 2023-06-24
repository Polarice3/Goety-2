package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.properties.ModStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class WitchPoleBlock extends Block implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = ModStateProperties.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    protected static final VoxelShape POLE = Block.box(6.0D, 0.0D, 6.0D,
            10.0D, 16.0D, 10.0D);
    protected static final VoxelShape TOP_POLE = Block.box(6.0D, 0.0D, 6.0D,
            10.0D, 16.0D, 10.0D);
    protected static final VoxelShape WEST_AABB = Shapes.or(
            TOP_POLE,
            Block.box(10.0D, 0.0D, 5.0D,
                    14.0D, 6.0D, 11.0D));
    protected static final VoxelShape EAST_AABB = Shapes.or(
            TOP_POLE,
            Block.box(2.0D, 0.0D, 5.0D,
                    6.0D, 6.0D, 11.0D));
    protected static final VoxelShape NORTH_AABB = Shapes.or(
            TOP_POLE,
            Block.box(5.0D, 0.0D, 10.0D,
                    11.0D, 6.0D, 14.0D));
    protected static final VoxelShape SOUTH_AABB = Shapes.or(
            TOP_POLE,
            Block.box(5.0D, 0.0D, 2.0D,
                    11.0D, 6.0D, 6.0D));

    public WitchPoleBlock() {
        super(Properties.of(Material.WOOD)
                .strength(2.5F)
                .requiresCorrectToolForDrops()
                .sound(SoundType.WOOD)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.FALSE).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return POLE;
    }

    public VoxelShape getShape(BlockState p_51787_, BlockGetter p_51788_, BlockPos p_51789_, CollisionContext p_51790_) {
        if (p_51787_.getValue(HALF) == DoubleBlockHalf.UPPER) {
            switch ((Direction) p_51787_.getValue(FACING)) {
                case SOUTH:
                    return SOUTH_AABB;
                case NORTH:
                default:
                    return NORTH_AABB;
                case WEST:
                    return WEST_AABB;
                case EAST:
                    return EAST_AABB;
            }
        } else {
            return POLE;
        }
    }

    public void setPlacedBy(Level p_52749_, BlockPos p_52750_, BlockState p_52751_, LivingEntity p_52752_, ItemStack p_52753_) {
        p_52749_.setBlock(p_52750_.above(), p_52751_.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext p_51750_) {
        BlockState blockstate = this.defaultBlockState();
        LevelReader levelreader = p_51750_.getLevel();
        BlockPos blockpos = p_51750_.getClickedPos();
        FluidState fluidstate = levelreader.getFluidState(blockpos);
        boolean flag = fluidstate.getType() == Fluids.WATER;

        for(Direction direction : p_51750_.getNearestLookingDirections()) {
            if (direction.getAxis() != Direction.Axis.Y) {
                blockstate = blockstate.setValue(FACING, direction).setValue(HALF, DoubleBlockHalf.LOWER);
                if (blockstate.canSurvive(levelreader, blockpos)) {
                    return blockstate.setValue(WATERLOGGED, flag);
                }
            }
        }
        return null;
    }

    public boolean canSurvive(BlockState p_52783_, LevelReader p_52784_, BlockPos p_52785_) {
        BlockPos blockpos = p_52785_.below();
        BlockState blockstate = p_52784_.getBlockState(blockpos);
        return p_52783_.getValue(HALF) == DoubleBlockHalf.LOWER ? blockstate.isFaceSturdy(p_52784_, blockpos, Direction.UP) : blockstate.is(this);
    }

    public PushReaction getPistonPushReaction(BlockState p_52814_) {
        return PushReaction.DESTROY;
    }

    public BlockState rotate(BlockState p_54125_, Rotation p_54126_) {
        return p_54125_.setValue(FACING, p_54126_.rotate(p_54125_.getValue(FACING)));
    }

    public BlockState mirror(BlockState p_54122_, Mirror p_54123_) {
        return p_54122_.rotate(p_54123_.getRotation(p_54122_.getValue(FACING)));
    }

    public BlockState updateShape(BlockState p_51771_, Direction p_51772_, BlockState p_51773_, LevelAccessor p_51774_, BlockPos p_51775_, BlockPos p_51776_) {
        DoubleBlockHalf doubleblockhalf = p_51771_.getValue(HALF);
        if (p_51772_.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (p_51772_ == Direction.UP)) {
            return p_51773_.is(this) && p_51773_.getValue(HALF) != doubleblockhalf ? p_51771_.setValue(FACING, p_51773_.getValue(FACING)) : Blocks.AIR.defaultBlockState();
        } else {
            return doubleblockhalf == DoubleBlockHalf.LOWER && p_51772_ == Direction.DOWN && p_51772_ == p_51771_.getValue(FACING) && !p_51771_.canSurvive(p_51774_, p_51775_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_51771_, p_51772_, p_51773_, p_51774_, p_51775_, p_51776_);
        }
    }

    public long getSeed(BlockState p_52793_, BlockPos p_52794_) {
        return Mth.getSeed(p_52794_.getX(), p_52794_.below(p_52793_.getValue(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), p_52794_.getZ());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_51778_) {
        p_51778_.add(FACING, WATERLOGGED, HALF);
    }

    public boolean isPathfindable(BlockState p_51762_, BlockGetter p_51763_, BlockPos p_51764_, PathComputationType p_51765_) {
        return false;
    }
}
