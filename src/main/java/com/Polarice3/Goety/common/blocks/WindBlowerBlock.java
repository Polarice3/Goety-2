package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.WindBlowerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;

public class WindBlowerBlock extends DirectionalBlock implements EntityBlock {
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public WindBlowerBlock() {
        super(ModBlocks.ShadeStoneProperties());
        this.registerDefaultState(this.stateDefinition.any().setValue(POWER, 0).setValue(POWERED, false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWER, POWERED);
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getNearestLookingDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, direction).setValue(POWER, 0).setValue(POWERED, false);
    }

    public void onPlace(BlockState p_55630_, Level p_55631_, BlockPos p_55632_, BlockState p_55633_, boolean p_55634_) {
        if (!p_55633_.is(p_55630_.getBlock()) && !p_55631_.isClientSide) {
            int signal = p_55631_.getBestNeighborSignal(p_55632_);
            if (signal != p_55630_.getValue(POWER)) {
                p_55631_.setBlock(p_55632_, p_55630_.setValue(POWER, signal), 4);
            }
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        int signal = world.getBestNeighborSignal(pos);
        if (signal != state.getValue(POWER)) {
            world.setBlock(pos, state.setValue(POWER, signal), 4);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new WindBlowerBlockEntity(p_153215_, p_153216_);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return (world, pos, state, blockEntity) -> {
            if (blockEntity instanceof WindBlowerBlockEntity blockEntity1)
                blockEntity1.tick();
        };
    }
}
