package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class StairCarpetBlock extends WoolCarpetBlock {
    public static final DirectionProperty STAIR_FACING = DirectionProperty.create("stair_facing", Direction.Plane.HORIZONTAL);
    public static final BooleanProperty ON_STAIRS = BooleanProperty.create("on_stairs");
    private static final VoxelShape FLAT = Block.box(0, 0, 0, 16, 1, 16);
    private static final VoxelShape NORTH_DRAPED = Shapes.or(Block.box(0, 0, 7, 16, 1, 16), Block.box(0, -8, 7, 16, 1, 8), Block.box(0, -8, -1, 16, -7, 8), Block.box(0, -16, -1, 16, -7, 0));
    private static final Map<Direction, VoxelShape> DRAPED = Map.of(
            Direction.NORTH, NORTH_DRAPED,
            Direction.SOUTH, MathHelper.rotateVoxelShape(NORTH_DRAPED, Direction.SOUTH),
            Direction.WEST,  MathHelper.rotateVoxelShape(NORTH_DRAPED, Direction.WEST),
            Direction.EAST,  MathHelper.rotateVoxelShape(NORTH_DRAPED, Direction.EAST));

    public StairCarpetBlock(DyeColor p_58291_, Properties p_58292_) {
        super(p_58291_, p_58292_);
        this.registerDefaultState(this.stateDefinition.any().setValue(ON_STAIRS, false).setValue(STAIR_FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        if (!state.getValue(ON_STAIRS)) {
            return FLAT;
        }
        return DRAPED.getOrDefault(state.getValue(STAIR_FACING), FLAT);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ON_STAIRS, STAIR_FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return conform(this.defaultBlockState(), ctx.getLevel(), ctx.getClickedPos());
    }

    public static BlockState conform(BlockState state, LevelAccessor level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        if (below.getBlock() instanceof StairBlock && below.getValue(StairBlock.HALF) == Half.BOTTOM && below.getValue(StairBlock.SHAPE) == StairsShape.STRAIGHT) {
            return state.setValue(ON_STAIRS, true).setValue(STAIR_FACING, below.getValue(StairBlock.FACING).getOpposite());
        }
        return state.setValue(ON_STAIRS, false);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN) {
            if (!state.canSurvive(level, pos)) {
                return Blocks.AIR.defaultBlockState();
            }
            return conform(state, level, pos);
        }
        return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }
}
