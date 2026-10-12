package com.Polarice3.Goety.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class EndStandingLanternBlock extends TallEndLanternBlock {
    private static final VoxelShape LOWER = Block.box(3.0D, 0.0D, 3.0D, 14.0D, 16.0D, 13.0D);
    private static final VoxelShape UPPER = Block.box(3.0D, 0.0D, 3.0D, 14.0D, 11.0D, 13.0D);
    private static final List<Emitter> EMITTERS = List.of(new Emitter(new Vec3(0.5D, 2.1D, 0.5D), Kind.GLOW_ORB));

    public EndStandingLanternBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape lowerShape() {
        return LOWER;
    }

    @Override
    protected VoxelShape upperShape() {
        return UPPER;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            BlockPos below = pos.below();
            return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
        }
        return super.canSurvive(state, level, pos);
    }

    @Override
    public List<Emitter> getEmitters(BlockState state) {
        return EMITTERS;
    }
}
