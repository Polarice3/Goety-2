package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.utils.MathHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class RoyalThroneBlock extends ThroneBlock {
    public static final VoxelShape SHAPE_LEG_1 = Block.box(15.5D, 0.0D, 0.0D,
            17.5D, 5.0D, 2.0D);
    public static final VoxelShape SHAPE_LEG_2 = Block.box(-1.5D, 0.0D, 0.0D,
            0.5D, 5.0D, 2.0D);
    public static final VoxelShape SHAPE_LEG_3 = Block.box(15.5D, 0.0D, 14.0D,
            17.5D, 5.0D, 16.0D);
    public static final VoxelShape SHAPE_LEG_4 = Block.box(-1.5D, 0.0D, 14.0D,
            0.5D, 5.0D, 16.0D);
    public static final VoxelShape SHAPE_LEFT_ARM = Block.box(16.5D, 5.0D, -1.0D,
            18.5D, 16.0D, 17.0D);
    public static final VoxelShape SHAPE_SIT = Block.box(-0.5D, 5.0D, -1.0D,
            16.5D, 8.0D, 17.0D);
    public static final VoxelShape SHAPE_RIGHT_ARM = Block.box(-2.5D, 5.0D, -1.0D,
            -0.5D, 16.0D, 17.0D);
    public static final VoxelShape SHAPE_BACK = Block.box(-0.5D, 8.0D, 15.0D,
            16.5D, 16.0D, 17.0D);
    public static final VoxelShape SHAPE_TOP_LEFT_ARM = Block.box(16.5D, 0.0D, -1.0D,
            18.5D, 1.0D, 17.0D);
    public static final VoxelShape SHAPE_TOP_RIGHT_ARM = Block.box(-2.5D, 0.0D, -1.0D,
            -0.5D, 1.0D, 17.0D);
    public static final VoxelShape SHAPE_TOP_BACK = Block.box(-0.5D, 0.0D, 15.0D,
            16.5D, 15.0D, 17.0D);
    public static final VoxelShape SHAPE_BOTTOM = Shapes.or(SHAPE_LEG_1, SHAPE_LEG_2, SHAPE_LEG_3, SHAPE_LEG_4, SHAPE_LEFT_ARM, SHAPE_SIT, SHAPE_RIGHT_ARM, SHAPE_BACK);
    public static final VoxelShape SHAPE_TOP = Shapes.or(SHAPE_TOP_LEFT_ARM, SHAPE_TOP_RIGHT_ARM, SHAPE_TOP_BACK);
    private static final Map<Direction, VoxelShape> LOWER_AABB = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, SHAPE_BOTTOM, Direction.SOUTH, MathHelper.rotateVoxelShape(SHAPE_BOTTOM, Direction.SOUTH), Direction.WEST, MathHelper.rotateVoxelShape(SHAPE_BOTTOM, Direction.WEST), Direction.EAST, MathHelper.rotateVoxelShape(SHAPE_BOTTOM, Direction.EAST)));
    private static final Map<Direction, VoxelShape> TOP_AABB = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, SHAPE_TOP, Direction.SOUTH, MathHelper.rotateVoxelShape(SHAPE_TOP, Direction.SOUTH), Direction.WEST, MathHelper.rotateVoxelShape(SHAPE_TOP, Direction.WEST), Direction.EAST, MathHelper.rotateVoxelShape(SHAPE_TOP, Direction.EAST)));

    public RoyalThroneBlock(Properties p_54120_) {
        super(p_54120_);
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
