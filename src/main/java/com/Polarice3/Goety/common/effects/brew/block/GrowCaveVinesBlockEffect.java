package com.Polarice3.Goety.common.effects.brew.block;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class GrowCaveVinesBlockEffect extends BrewEffect {
    public GrowCaveVinesBlockEffect(int soulCost) {
        super("grow_cave_vines", soulCost, MobEffectCategory.NEUTRAL, 0x6c8031);
    }

    @Override
    public void applyBlockEffect(Level pLevel, BlockPos pPos, LivingEntity pSource, int pAmplifier, int pAreaOfEffect) {
        BlockPos blockPos0 = pPos.below();
        if (pLevel.getBlockState(blockPos0).isAir()) {
            if (Blocks.CAVE_VINES.defaultBlockState().canSurvive(pLevel, blockPos0) || Blocks.CAVE_VINES_PLANT.defaultBlockState().canSurvive(pLevel, blockPos0)) {
                pLevel.setBlockAndUpdate(blockPos0, Blocks.CAVE_VINES_PLANT.defaultBlockState());
                int j = 3 + pLevel.getRandom().nextInt(pAmplifier + 1);
                for (int i = 0; i > -j; --i) {
                    BlockPos blockPos = blockPos0.offset(0, i, 0);
                    if (pLevel.getBlockState(blockPos).isAir()) {
                        if (pLevel.getBlockState(blockPos.above()).is(Blocks.CAVE_VINES) || pLevel.getBlockState(blockPos.above()).is(Blocks.CAVE_VINES_PLANT)) {
                            pLevel.setBlockAndUpdate(blockPos, Blocks.CAVE_VINES.defaultBlockState());
                        }
                    }
                }
            }
        }
    }
}
