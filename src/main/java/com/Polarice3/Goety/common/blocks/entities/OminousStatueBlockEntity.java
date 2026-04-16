package com.Polarice3.Goety.common.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class OminousStatueBlockEntity extends BlockEntity {

    public OminousStatueBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.OMINOUS_STATUE.get(), p_155229_, p_155230_);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return BlockEntity.INFINITE_EXTENT_AABB;
    }
}
