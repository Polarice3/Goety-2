package com.Polarice3.Goety.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.Nonnull;

public class PurpurEndRodBlock extends Block {
    public static final BooleanProperty CONNECTED_NORTH = BooleanProperty.create("connected_north");
    public static final BooleanProperty CONNECTED_SOUTH = BooleanProperty.create("connected_south");
    public static final BooleanProperty CONNECTED_WEST = BooleanProperty.create("connected_west");
    public static final BooleanProperty CONNECTED_EAST = BooleanProperty.create("connected_east");

    public PurpurEndRodBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(stateDefinition.any().setValue(CONNECTED_EAST, Boolean.FALSE).setValue(CONNECTED_NORTH, Boolean.FALSE).setValue(CONNECTED_SOUTH, Boolean.FALSE).setValue(CONNECTED_WEST, Boolean.FALSE));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return this.defaultBlockState()
                .setValue(CONNECTED_EAST, this.isSideConnectable(world, pos, Direction.EAST))
                .setValue(CONNECTED_NORTH, this.isSideConnectable(world, pos, Direction.NORTH))
                .setValue(CONNECTED_SOUTH, this.isSideConnectable(world, pos, Direction.SOUTH))
                .setValue(CONNECTED_WEST, this.isSideConnectable(world, pos, Direction.WEST));
    }

    @Nonnull
    @Override
    public BlockState updateShape(BlockState stateIn, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull LevelAccessor world, @Nonnull BlockPos pos, @Nonnull BlockPos facingPos) {
        return stateIn.setValue(CONNECTED_EAST, this.isSideConnectable(world, pos, Direction.EAST))
                .setValue(CONNECTED_NORTH, this.isSideConnectable(world, pos, Direction.NORTH))
                .setValue(CONNECTED_SOUTH, this.isSideConnectable(world, pos, Direction.SOUTH))
                .setValue(CONNECTED_WEST, this.isSideConnectable(world, pos, Direction.WEST));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CONNECTED_NORTH, CONNECTED_SOUTH, CONNECTED_WEST, CONNECTED_EAST);
    }

    private boolean isNotConnected(BlockState state) {
        return !state.getValue(CONNECTED_WEST) && !state.getValue(CONNECTED_EAST) && !state.getValue(CONNECTED_NORTH) && !state.getValue(CONNECTED_SOUTH);
    }

    private boolean isSideConnectable(BlockGetter world, BlockPos pos, Direction side) {
        if (side.getAxis().isHorizontal()) {
            final BlockState stateConnection = world.getBlockState(pos.relative(side));
            if (stateConnection != null) {
                return stateConnection.getBlock() == this;
            }
        }
        return false;
    }
}
