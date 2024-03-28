package com.Polarice3.Goety.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CorpseBlossomBlock extends Block implements SimpleWaterloggedBlock {
    private static final VoxelShape TOP_SHAPE = Block.box(2.0D, 13.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    private static final VoxelShape BOTTOM_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D);
    public static final DirectionProperty VERTICAL_FACING = DirectionProperty.create("facing", Direction.Plane.VERTICAL);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public CorpseBlossomBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(VERTICAL_FACING, Direction.DOWN).setValue(WATERLOGGED, Boolean.FALSE));
    }

    public boolean canSurvive(BlockState p_154709_, LevelReader p_154710_, BlockPos p_154711_) {
        Direction direction = Direction.UP;
        BlockPos blockPos = p_154711_.above();
        if (p_154709_.getValue(VERTICAL_FACING) == Direction.DOWN){
            direction = Direction.DOWN;
            blockPos = p_154711_.below();
        }
        return Block.canSupportCenter(p_154710_, blockPos, direction);
    }

    public BlockState updateShape(BlockState p_154713_, Direction p_154714_, BlockState p_154715_, LevelAccessor p_154716_, BlockPos p_154717_, BlockPos p_154718_) {
        if (p_154713_.getValue(WATERLOGGED)) {
            p_154716_.scheduleTick(p_154717_, Fluids.WATER, Fluids.WATER.getTickDelay(p_154716_));
        }
        return p_154714_.getAxis() == Direction.Axis.Y && !this.canSurvive(p_154713_, p_154716_, p_154717_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_154713_, p_154714_, p_154715_, p_154716_, p_154717_, p_154718_);
    }

    public VoxelShape getShape(BlockState p_154699_, BlockGetter p_154700_, BlockPos p_154701_, CollisionContext p_154702_) {
        if (p_154699_.getValue(VERTICAL_FACING) == Direction.DOWN){
            return BOTTOM_SHAPE;
        } else {
            return TOP_SHAPE;
        }
    }

    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
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

    public BlockState getStateForPlacement(BlockPlaceContext p_53087_) {
        Direction direction = p_53087_.getClickedFace();
        LevelAccessor iworld = p_53087_.getLevel();
        BlockPos blockpos = p_53087_.getClickedPos();
        boolean flag = iworld.getFluidState(blockpos).getType() == Fluids.WATER;
        return direction == Direction.UP ? this.defaultBlockState().setValue(VERTICAL_FACING, Direction.DOWN).setValue(WATERLOGGED, flag) : this.defaultBlockState().setValue(VERTICAL_FACING, Direction.UP).setValue(WATERLOGGED, flag);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_54663_) {
        p_54663_.add(VERTICAL_FACING, WATERLOGGED);
    }

}
