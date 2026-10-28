package com.Polarice3.Goety.common.blocks;

import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class FancyCarpetBlock extends StairCarpetBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private final boolean corner;

    public FancyCarpetBlock(DyeColor p_58291_, Properties p_58292_) {
        this(p_58291_, p_58292_, false);
    }

    public FancyCarpetBlock(DyeColor color, Properties properties, boolean corner) {
        super(color, properties);
        this.corner = corner;
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return super.rotate(state, rotation).setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        Direction facing = state.getValue(FACING);
        Direction mirrored = this.corner ? mirror.mirror(facing.getCounterClockWise()) : mirror.mirror(facing);
        return super.mirror(state, mirror).setValue(FACING, mirrored);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return conform(this.defaultBlockState(), ctx.getLevel(), ctx.getClickedPos()).setValue(FACING, ctx.getHorizontalDirection());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }
}
