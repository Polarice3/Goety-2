package com.Polarice3.Goety.common.effects.brew.block;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;

public class PruningBlockEffect extends BrewEffect {
    public PruningBlockEffect() {
        super("pruning", MobEffectCategory.NEUTRAL, 0x3a4c26);
    }

    @Override
    public void applyBlockEffect(Level pLevel, BlockPos pPos, LivingEntity pSource, int pAmplifier, int pAreaOfEffect) {
        for (BlockPos blockPos : this.getCubePos(pPos, pAreaOfEffect + 2)) {
            if (pLevel.getBlockState(blockPos).getBlock() instanceof LeavesBlock || pLevel.getBlockState(blockPos).is(BlockTags.LEAVES)){
                pLevel.destroyBlock(blockPos, true, pSource);
            }
        }
    }
}
