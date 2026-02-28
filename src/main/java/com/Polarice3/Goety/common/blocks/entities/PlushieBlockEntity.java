package com.Polarice3.Goety.common.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PlushieBlockEntity extends BlockEntity {
    private int animationTickCount;
    private boolean isAnimating;

    public PlushieBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.PLUSHIE.get(), p_155229_, p_155230_);
    }

    public static void animation(PlushieBlockEntity p_261594_) {
        if (p_261594_.animationTickCount > 5) {
            p_261594_.isAnimating = false;
        }
        if (p_261594_.isAnimating) {
            ++p_261594_.animationTickCount;
        } else if (p_261594_.animationTickCount > 0) {
            p_261594_.animationTickCount = 0;
        }
    }

    public static void startAnimating(PlushieBlockEntity p_261594_) {
        p_261594_.isAnimating = true;
    }

    public float getAnimation() {
        return this.animationTickCount;
    }
}
