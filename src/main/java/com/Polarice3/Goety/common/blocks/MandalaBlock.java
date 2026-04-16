package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.MandalaBlockEntity;
import com.Polarice3.Goety.common.blocks.properties.ModStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MandalaBlock extends Block implements SimpleWaterloggedBlock, EntityBlock {
    private static final VoxelShape SHAPE_FLOOR = Block.box(0, 0, 0, 16, 1, 16);
    private static final VoxelShape SHAPE_CEILING = Block.box(0, 15, 0, 16, 16, 16);
    private static final VoxelShape SHAPE_WALL_N = Block.box(0, 0, 15, 16, 16, 16);
    private static final VoxelShape SHAPE_WALL_S = Block.box(0, 0, 0, 16, 16, 1);
    private static final VoxelShape SHAPE_WALL_E = Block.box(0, 0, 0, 1, 16, 16);
    private static final VoxelShape SHAPE_WALL_W = Block.box(15, 0, 0, 16, 16, 16);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final IntegerProperty PART = ModStateProperties.PART;

    public MandalaBlock() {
        super(ModBlocks.OminousStoneProperties()
                .noCollission()
                .noOcclusion());
        registerDefaultState(stateDefinition.any()
                .setValue(PART, 0)
                .setValue(FACING, Direction.UP)
                .setValue(WATERLOGGED, false));
    }

    @Override
    public boolean isPossibleToRespawnInThis(BlockState blockState) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART, FACING, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return switch (state.getValue(FACING)) {
            case NORTH -> SHAPE_WALL_N;
            case SOUTH -> SHAPE_WALL_S;
            case EAST -> SHAPE_WALL_E;
            case WEST -> SHAPE_WALL_W;
            case DOWN -> SHAPE_CEILING;
            default -> SHAPE_FLOOR;
        };
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
        if (state.getValue(PART) != 0) {
            return;
        }

        Direction facing = state.getValue(FACING);

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                int partIndex = row * 4 + col;
                if (partIndex == 0) {
                    continue;
                }

                BlockPos partPos = this.getPartPos(pos, facing, col, row);
                level.setBlock(partPos, this.defaultBlockState().setValue(PART, partIndex).setValue(FACING, facing), 3);
            }
        }
    }

    private BlockPos getPartPos(BlockPos anchor, Direction facing, int col, int row) {
        Direction colDir;
        Direction rowDir;

        switch (facing) {
            case UP, DOWN -> {
                colDir = Direction.WEST;
                rowDir = Direction.SOUTH;
            }
            case NORTH -> {
                colDir = Direction.WEST;
                rowDir = Direction.UP;
            }
            case SOUTH -> {
                colDir = Direction.EAST;
                rowDir = Direction.UP;
            }
            case EAST -> {
                colDir = Direction.NORTH;
                rowDir = Direction.UP;
            }
            case WEST -> {
                colDir = Direction.SOUTH;
                rowDir = Direction.UP;
            }
            default -> {
                colDir = Direction.EAST;
                rowDir = Direction.SOUTH;
            }
        }

        return anchor.relative(colDir, col).relative(rowDir, row);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (newState.is(this)) {
            return;
        }

        BlockPos anchor = getAnchorPos(state, pos);
        Direction facing = state.getValue(FACING);

        for (int i = 0; i < 16; i++) {
            int col = i % 4, row = i / 4;
            BlockPos partPos = this.getPartPos(anchor, facing, col, row);
            if (!partPos.equals(pos) && level.getBlockState(partPos).is(this)) {
                level.removeBlock(partPos, false);
            }
        }

        super.onRemove(state, level, pos, newState, moving);
    }

    private BlockPos getAnchorPos(BlockState state, BlockPos pos) {
        int part = state.getValue(PART);
        int col = part % 4, row = part / 4;
        Direction facing = state.getValue(FACING);
        return this.getPartPos(pos, facing, -col, -row);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction clickedFace = ctx.getClickedFace();
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();

        for (int i = 1; i < 16; i++) {
            int col = i % 4, row = i / 4;
            BlockPos partPos = this.getPartPos(pos, clickedFace, col, row);
            if (!level.getBlockState(partPos).canBeReplaced(ctx)) {
                return null;
            }
        }

        return this.defaultBlockState().setValue(FACING, clickedFace).setValue(PART, 0);
    }

    public @NotNull BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public @NotNull BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor world, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(PART) == 0) {
            return new MandalaBlockEntity(pos, state);
        }
        return null;
    }
}
