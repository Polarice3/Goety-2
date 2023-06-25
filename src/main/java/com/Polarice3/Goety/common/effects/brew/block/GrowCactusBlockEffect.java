package com.Polarice3.Goety.common.effects.brew.block;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class GrowCactusBlockEffect extends BrewEffect {
    public GrowCactusBlockEffect(int soulCost) {
        super("grow_cactus", soulCost, MobEffectCategory.NEUTRAL, 0x649832);
    }

    @Override
    public void applyBlockEffect(Level pLevel, BlockPos pPos, LivingEntity pSource, int pAmplifier, int pAreaOfEffect) {
        if (Blocks.CACTUS.defaultBlockState().canSurvive(pLevel, pPos.above())) {
            for (int i = 0; i < pAmplifier + 3; ++i) {
                BlockPos blockPos = pPos.offset(0, i + 1, 0);
                if (pLevel.getBlockState(blockPos).isAir()) {
                    pLevel.setBlockAndUpdate(blockPos, Blocks.CACTUS.defaultBlockState());
                }
            }
        }
    }
}
