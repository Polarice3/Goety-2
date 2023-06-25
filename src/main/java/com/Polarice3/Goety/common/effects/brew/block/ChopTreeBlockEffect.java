package com.Polarice3.Goety.common.effects.brew.block;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class ChopTreeBlockEffect extends BrewEffect {
    public ChopTreeBlockEffect() {
        super("chop_tree", MobEffectCategory.NEUTRAL, 0x6a5227);
    }

    @Override
    public void applyBlockEffect(Level pLevel, BlockPos pPos, LivingEntity pSource, int pAmplifier, int pAreaOfEffect) {
        for (BlockPos blockPos : this.getSpherePos(pPos, pAreaOfEffect + 2 + pAmplifier)) {
            if (pLevel.getBlockState(blockPos).is(BlockTags.LOGS)){
                pLevel.destroyBlock(blockPos, true, pSource);
            }
        }
    }
}
