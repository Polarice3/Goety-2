package com.Polarice3.Goety.common.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Map;

public class PineLanternBlock extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    protected static final VoxelShape NORTH_AABB = Shapes.or(
            Block.box(3.5D, 2.0D, 13.0D,
                    12.5D, 16.0D, 16.0D),
            Block.box(3.5D, 6.0D, 10.0D,
                    12.5D, 16.0D, 13.0D),
            Block.box(3.5D, 10.0D, -2.0D,
                    12.5D, 16.0D, 10.0D),
            Block.box(5.5D, -7.0D, -0.5D,
                    10.5D, 1.0D, 4.5D));
    protected static final VoxelShape SOUTH_AABB = Shapes.or(
            Block.box(3.5D, 2.0D, 0.0D,
                    12.5D, 16.0D, 3.0D),
            Block.box(3.5D, 6.0D, 3.0D,
                    12.5D, 16.0D, 6.0D),
            Block.box(3.5D, 10.0D, 6.0D,
                    12.5D, 16.0D, 18.0D),
            Block.box(5.5D, -7.0D, 11.5D,
                    10.5D, 1.0D, 16.5D));
    protected static final VoxelShape WEST_AABB = Shapes.or(
            Block.box(13.0D, 2.0D, 3.5D,
                    16.0D, 16.0D, 12.5D),
            Block.box(10.0D, 6.0D, 3.5D,
                    13.0D, 16.0D, 12.5D),
            Block.box(-2.0D, 10.0D, 3.5D,
                    10.0D, 16.0D, 12.5D),
            Block.box(-0.5D, -7.0D, 5.5D,
                    4.5D, 1.0D, 10.5D));
    protected static final VoxelShape EAST_AABB = Shapes.or(
            Block.box(0.0D, 2.0D, 3.5D,
                    3.0D, 16.0D, 12.5D),
            Block.box(3.0D, 6.0D, 3.5D,
                    6.0D, 16.0D, 12.5D),
            Block.box(6.0D, 10.0D, 3.5D,
                    18.0D, 16.0D, 12.5D),
            Block.box(11.5D, -7.0D, 5.5D,
                    16.5D, 1.0D, 10.5D));
    private static final Map<Direction, VoxelShape> AABBS = Maps.newEnumMap(ImmutableMap.of(
            Direction.SOUTH, SOUTH_AABB,
            Direction.NORTH, NORTH_AABB,
            Direction.EAST, EAST_AABB,
            Direction.WEST, WEST_AABB));

    public PineLanternBlock() {
        super(Properties.of()
                .mapColor(MapColor.COLOR_BROWN)
                .instrument(NoteBlockInstrument.BASS)
                .sound(SoundType.WOOD)
                .ignitedByLava()
                .instabreak()
                .lightLevel((p_50886_) -> 14));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public VoxelShape getShape(BlockState p_58152_, BlockGetter p_58153_, BlockPos p_58154_, CollisionContext p_58155_) {
        return getShape(p_58152_);
    }

    public static VoxelShape getShape(BlockState p_58157_) {
        return AABBS.get(p_58157_.getValue(FACING));
    }

    public boolean canSurvive(BlockState p_58133_, LevelReader p_58134_, BlockPos p_58135_) {
        Direction direction = p_58133_.getValue(FACING);
        BlockPos blockpos = p_58135_.relative(direction.getOpposite());
        BlockState blockstate = p_58134_.getBlockState(blockpos);
        return blockstate.isFaceSturdy(p_58134_, blockpos, direction);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext p_58126_) {
        BlockState blockstate = this.defaultBlockState();
        LevelReader levelreader = p_58126_.getLevel();
        BlockPos blockpos = p_58126_.getClickedPos();
        Direction[] adirection = p_58126_.getNearestLookingDirections();

        for(Direction direction : adirection) {
            if (direction.getAxis().isHorizontal()) {
                Direction direction1 = direction.getOpposite();
                blockstate = blockstate.setValue(FACING, direction1);
                if (blockstate.canSurvive(levelreader, blockpos)) {
                    return blockstate;
                }
            }
        }

        return null;
    }

    public BlockState updateShape(BlockState p_58143_, Direction p_58144_, BlockState p_58145_, LevelAccessor p_58146_, BlockPos p_58147_, BlockPos p_58148_) {
        return p_58144_.getOpposite() == p_58143_.getValue(FACING) && !p_58143_.canSurvive(p_58146_, p_58147_) ? Blocks.AIR.defaultBlockState() : p_58143_;
    }

    public BlockState rotate(BlockState p_58140_, Rotation p_58141_) {
        return p_58140_.setValue(FACING, p_58141_.rotate(p_58140_.getValue(FACING)));
    }

    public BlockState mirror(BlockState p_58137_, Mirror p_58138_) {
        return p_58137_.rotate(p_58138_.getRotation(p_58137_.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_58150_) {
        p_58150_.add(FACING);
    }
}
