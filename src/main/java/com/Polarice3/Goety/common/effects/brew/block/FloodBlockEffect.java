package com.Polarice3.Goety.common.effects.brew.block;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

public class FloodBlockEffect extends BrewEffect {
    public FloodBlockEffect(int soulCost, int cap) {
        super("flood", soulCost, cap, MobEffectCategory.NEUTRAL, 0x3F76E4);
    }

    @Override
    public void applyBlockEffect(Level pLevel, BlockPos pPos, LivingEntity pSource, int pAmplifier, int pAreaOfEffect) {
        for (BlockPos blockPos : this.getSpherePos(pPos, pAreaOfEffect + 2)){
            if (pLevel.getBlockState(blockPos).canBeReplaced(Fluids.WATER)
                    && (pLevel.getBlockState(blockPos.below()).isSolidRender(pLevel, blockPos.below())
                    || pLevel.getFluidState(blockPos.below()).is(Fluids.WATER))) {
                pLevel.setBlockAndUpdate(blockPos, Blocks.WATER.defaultBlockState());
            }
            for (LivingEntity livingEntity : pLevel.getEntitiesOfClass(LivingEntity.class, new AABB(blockPos))){
                this.applyEntityEffect(livingEntity, pSource, pAmplifier);
            }
        }
    }

    @Override
    public void applyEntityEffect(LivingEntity pTarget, @Nullable Entity pSource, int pAmplifier){
        if (pTarget.isSensitiveToWater()){
            pTarget.hurt(DamageSource.DROWN, pAmplifier + 5.0F);
        }
    }
}
