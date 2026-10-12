package com.Polarice3.Goety.common.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class EndFloatingLanternBlock extends TallEndLanternBlock {
    private static final VoxelShape LOWER = Block.box(2.5D, 1.5D, 3.0D, 13.5D, 16.0D, 13.0D);
    private static final VoxelShape UPPER = Block.box(2.5D, 0.0D, 3.0D, 13.5D, 16.0D, 13.0D);
    private static final List<Emitter> EMITTERS = List.of(
            new Emitter(new Vec3(8.5D / 16.0D, 37.96D / 16.0D, 8.61D / 16.0D), Kind.GLOW_ORB));

    public EndFloatingLanternBlock(Properties properties) {
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
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public List<Emitter> getEmitters(BlockState state) {
        return EMITTERS;
    }
}
