package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.utils.MathHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Map;

public class StoneThroneBlock extends ThroneBlock {
    public static final VoxelShape SHAPE_LEFT_ARM = Block.box(14.0D, 0.0D, -1.0D,
            18.0D, 16.0D, 13.0D);
    public static final VoxelShape SHAPE_SIT = Block.box(2.0D, 0.0D, 2.0D,
            14.0D, 11.0D, 13.0D);
    public static final VoxelShape SHAPE_RIGHT_ARM = Block.box(-2.0D, 0.0D, -1.0D,
            2.0D, 16.0D, 13.0D);
    public static final VoxelShape SHAPE_BACK = Block.box(-2.0D, 0.0D, 13.0D,
            18.0D, 16.0D, 16.0D);
    public static final VoxelShape SHAPE_BOTTOM = Shapes.or(SHAPE_LEFT_ARM, SHAPE_SIT, SHAPE_RIGHT_ARM, SHAPE_BACK);
    private static final Map<Direction, VoxelShape> LOWER_AABB = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, SHAPE_BOTTOM, Direction.SOUTH, MathHelper.rotateVoxelShape(SHAPE_BOTTOM, Direction.SOUTH), Direction.WEST, MathHelper.rotateVoxelShape(SHAPE_BOTTOM, Direction.WEST), Direction.EAST, MathHelper.rotateVoxelShape(SHAPE_BOTTOM, Direction.EAST)));
    private static final Map<Direction, VoxelShape> TOP_AABB = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, SHAPE_BACK, Direction.SOUTH, MathHelper.rotateVoxelShape(SHAPE_BACK, Direction.SOUTH), Direction.WEST, MathHelper.rotateVoxelShape(SHAPE_BACK, Direction.WEST), Direction.EAST, MathHelper.rotateVoxelShape(SHAPE_BACK, Direction.EAST)));

    public StoneThroneBlock(Properties p_54120_) {
        super(p_54120_);
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, @Nullable Mob entity) {
        return BlockPathTypes.RAIL;
    }

    @Override
    public Vec3 seatOffset(BlockPos pos) {
        return super.seatOffset(pos).add(0, 0.1D, 0);
    }

    public VoxelShape getShape(BlockState p_58152_, BlockGetter p_58153_, BlockPos p_58154_, CollisionContext p_58155_) {
        return getShape(p_58152_);
    }

    public static VoxelShape getShape(BlockState p_58157_) {
        if (p_58157_.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return TOP_AABB.get(p_58157_.getValue(FACING));
        }
        return LOWER_AABB.get(p_58157_.getValue(FACING));
    }
}
