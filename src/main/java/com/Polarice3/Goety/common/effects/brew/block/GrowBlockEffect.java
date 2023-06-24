package com.Polarice3.Goety.common.effects.brew.block;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;

public class GrowBlockEffect extends BrewEffect {
    public GrowBlockEffect(int soulCost) {
        super("growth", soulCost, MobEffectCategory.BENEFICIAL, 0x009611);
    }

    @Override
    public void applyBlockEffect(Level pLevel, BlockPos pPos, LivingEntity pSource, int pAmplifier, int pAreaOfEffect) {
        if (pLevel instanceof ServerLevel serverLevel) {
            for (BlockPos blockPos : this.getCubePos(pPos, pAreaOfEffect + 1)){
                if (pLevel.getBlockState(blockPos).getBlock() instanceof BonemealableBlock bonemealableBlock) {
                    bonemealableBlock.performBonemeal(serverLevel, serverLevel.random, blockPos, pLevel.getBlockState(blockPos));
                }
            }
        }
    }
}
