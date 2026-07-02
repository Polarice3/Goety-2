package com.Polarice3.Goety.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.Nonnull;

public class DBSPlanksBlock extends Block {
    public static final BooleanProperty BOTTOM = BlockStateProperties.BOTTOM;

    public DBSPlanksBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(BOTTOM, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return this.defaultBlockState().setValue(BOTTOM, !world.getBlockState(pos.above()).isAir());
    }

    @Nonnull
    @Override
    public BlockState updateShape(BlockState stateIn, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull LevelAccessor world, @Nonnull BlockPos pos, @Nonnull BlockPos facingPos) {
        return stateIn.setValue(BOTTOM, !world.getBlockState(pos.above()).isAir());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(BOTTOM);
    }
}
