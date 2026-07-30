package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.GravestoneBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.ModBlockEntities;
import com.Polarice3.Goety.common.blocks.entities.OssuaryBlockEntity;
import com.Polarice3.Goety.common.blocks.properties.ModStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class OssuaryBlock extends TrainingBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = ModStateProperties.FACING;
    public static final VoxelShape SHAPE_BOTTOM = Block.box(0.0D, 0.0D, 0.0D,
            16.0D, 2.0D, 16.0D);
    public static final VoxelShape SHAPE_MID = Block.box(2.0D, 2.0D, 2.0D,
            14.0D, 9.0D, 14.0D);
    public static final VoxelShape SHAPE_TOP_FRONT = Block.box(0.0D, 8.0D, 0.0D,
            16.0D, 10.0D, 2.0D);
    public static final VoxelShape SHAPE_TOP_LEFT = Block.box(14.0D, 8.0D, 2.0D,
            16.0D, 10.0D, 14.0D);
    public static final VoxelShape SHAPE_TOP_BACK = Block.box(0.0D, 8.0D, 14.0D,
            16.0D, 10.0D, 16.0D);
    public static final VoxelShape SHAPE_TOP_RIGHT = Block.box(0.0D, 8.0D, 2.0D,
            2.0D, 10.0D, 14.0D);
    public static final VoxelShape SHAPE_SKULL = Block.box(4.0D, 8.0D, 4.0D,
            12.0D, 15.0D, 12.0D);
    public static final VoxelShape SHAPE = Shapes.or(SHAPE_BOTTOM, SHAPE_MID, SHAPE_TOP_FRONT, SHAPE_TOP_LEFT, SHAPE_TOP_BACK, SHAPE_TOP_RIGHT, SHAPE_SKULL);

    public OssuaryBlock() {
        super(ModBlocks.ShadeStoneProperties()
                .requiresCorrectToolForDrops()
                .noOcclusion()
                .dynamicShape()
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.FALSE).setValue(POWERED, Boolean.FALSE));
    }

    public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
        return false;
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return SHAPE;
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
                return blockstate.setValue(FACING, direction).setValue(WATERLOGGED, flag);
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

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(POWERED, FACING, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new OssuaryBlockEntity(p_153215_, p_153216_);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152755_, BlockState p_152756_, BlockEntityType<T> p_152757_) {
        return createTickerHelper(p_152757_, ModBlockEntities.SHADE_OSSUARY.get(), p_152755_.isClientSide ? GravestoneBlockEntity::clientTick : GravestoneBlockEntity::serverTick);
    }
}
