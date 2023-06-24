package com.Polarice3.Goety.common.effects.brew.block;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SweetBerryBushBlock;

public class SweetBerriedEffect extends BrewEffect {
    public SweetBerriedEffect() {
        super("sweet_thorns", MobEffectCategory.HARMFUL, 0x286240);
    }

    @Override
    public void applyBlockEffect(Level pLevel, BlockPos pPos, LivingEntity pSource, int pAmplifier, int pAreaOfEffect) {
        for (BlockPos blockPos : this.getSpherePos(pPos, pAreaOfEffect + 2)){
            if (pLevel.getBlockState(blockPos).isAir()
                    && Blocks.SWEET_BERRY_BUSH.defaultBlockState().canSurvive(pLevel, blockPos.below())
                    && pLevel.getBlockState(blockPos.below()).isSolidRender(pLevel, blockPos.below())) {
                pLevel.setBlockAndUpdate(blockPos, Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, pAmplifier));
            }
        }
    }
}
