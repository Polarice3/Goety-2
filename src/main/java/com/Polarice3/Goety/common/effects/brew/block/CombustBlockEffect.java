package com.Polarice3.Goety.common.effects.brew.block;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.Polarice3.Goety.utils.BlockFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;

import javax.annotation.Nullable;

public class CombustBlockEffect extends BrewEffect {
    public CombustBlockEffect(int soulCost, int cap) {
        super("combust", soulCost, cap, MobEffectCategory.HARMFUL, 0xffd800);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    @Override
    public boolean canLinger() {
        return true;
    }

    @Override
    public void applyBlockEffect(Level pLevel, BlockPos pPos, LivingEntity pSource, int pAmplifier, int pAreaOfEffect) {
        for (BlockPos blockPos : this.getSpherePos(pPos, pAreaOfEffect + 2)){
            if (BlockFinder.canBeReplaced(pLevel, blockPos)
                    && pLevel.getFluidState(blockPos).isEmpty()
                    && pLevel.getBlockState(blockPos.below()).isSolidRender(pLevel, blockPos.below())) {
                pLevel.setBlockAndUpdate(blockPos, BaseFireBlock.getState(pLevel, blockPos));
            }
        }
    }

    public void applyEntityEffect(LivingEntity pTarget, @Nullable Entity pSource, @Nullable Entity pIndirectSource, int pAmplifier){
        pTarget.setSecondsOnFire(5 * (pAmplifier + 1));
    }
}
