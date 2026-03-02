package com.Polarice3.Goety.common.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PlushieBlockEntity extends BlockEntity {
    public static final int MAX_ANIMATION_TICKS = 12;
    private int animationTickCount, oAnimationTickCount;
    private boolean isAnimating;

    public PlushieBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PLUSHIE.get(), pos, state);
    }

    public static void animation(PlushieBlockEntity blockEntity) {
        blockEntity.oAnimationTickCount = blockEntity.animationTickCount;
        if (blockEntity.animationTickCount > MAX_ANIMATION_TICKS) {
            blockEntity.isAnimating = false;
        }
        if (blockEntity.isAnimating) {
            ++blockEntity.animationTickCount;
        } else if (blockEntity.animationTickCount > 0) {
            blockEntity.oAnimationTickCount = 0;
            blockEntity.animationTickCount = 0;
        }
    }

    public static void startAnimating(PlushieBlockEntity blockEntity) {
        blockEntity.isAnimating = true;
    }

    public float getAnimation(float partialTick) {
        return Mth.lerp(partialTick, this.oAnimationTickCount, this.animationTickCount);
    }
}
