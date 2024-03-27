package com.Polarice3.Goety.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CorpseBlossomBlock extends Block {
    private static final VoxelShape TOP_SHAPE = Block.box(2.0D, 13.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    private static final VoxelShape BOTTOM_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D);
    public static final DirectionProperty VERTICAL_FACING = DirectionProperty.create("facing", Direction.Plane.VERTICAL);

    public CorpseBlossomBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(VERTICAL_FACING, Direction.DOWN));
    }

    public boolean canSurvive(BlockState p_154709_, LevelReader p_154710_, BlockPos p_154711_) {
        Direction direction = Direction.UP;
        BlockPos blockPos = p_154711_.above();
        if (p_154709_.getValue(VERTICAL_FACING) == Direction.DOWN){
            direction = Direction.DOWN;
            blockPos = p_154711_.below();
        }
        return Block.canSupportCenter(p_154710_, blockPos, direction) && !p_154710_.isWaterAt(p_154711_);
    }

    public BlockState updateShape(BlockState p_154713_, Direction p_154714_, BlockState p_154715_, LevelAccessor p_154716_, BlockPos p_154717_, BlockPos p_154718_) {
        return p_154714_.getAxis() == Direction.Axis.Y && !this.canSurvive(p_154713_, p_154716_, p_154717_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_154713_, p_154714_, p_154715_, p_154716_, p_154717_, p_154718_);
    }

    public VoxelShape getShape(BlockState p_154699_, BlockGetter p_154700_, BlockPos p_154701_, CollisionContext p_154702_) {
        if (p_154699_.getValue(VERTICAL_FACING) == Direction.DOWN){
            return BOTTOM_SHAPE;
        } else {
            return TOP_SHAPE;
        }
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_53087_) {
        Direction direction = p_53087_.getClickedFace();
        return direction == Direction.UP ? this.defaultBlockState().setValue(VERTICAL_FACING, Direction.DOWN) : this.defaultBlockState().setValue(VERTICAL_FACING, Direction.UP);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_54663_) {
        p_54663_.add(VERTICAL_FACING);
    }

}
