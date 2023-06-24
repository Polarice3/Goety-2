package com.Polarice3.Goety.common.effects.brew;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nullable;

public class WebbedBrewEffect extends BrewEffect {
    public WebbedBrewEffect(int soulCost, int cap) {
        super("webbed", soulCost, cap, MobEffectCategory.HARMFUL, 0xc4ced2);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    @Override
    public boolean canLinger() {
        return true;
    }

    public void applyEntityEffect(LivingEntity pTarget, @Nullable Entity pSource, @Nullable Entity pIndirectSource, int pAmplifier){
        Level level = pTarget.level;
        level.setBlockAndUpdate(pTarget.blockPosition(), Blocks.COBWEB.defaultBlockState());
        for (BlockPos blockPos : this.getSpherePos(pTarget.blockPosition(), pAmplifier + 1)){
            if (level.getBlockState(blockPos).isAir()){
                if (level.random.nextFloat() <= 0.25F + (pAmplifier / 10.0F)) {
                    level.setBlockAndUpdate(blockPos, Blocks.COBWEB.defaultBlockState());
                }
            }
        }
    }
}
