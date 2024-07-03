package com.Polarice3.Goety.common.effects.brew.block;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

public class FreezeBlockEffect extends BrewEffect {
    public FreezeBlockEffect(int soulCost) {
        super("freeze", soulCost, MobEffectCategory.NEUTRAL, 0x92b9fe);
    }

    @Override
    public boolean canLinger() {
        return true;
    }

    @Override
    public void applyBlockEffect(Level pLevel, BlockPos pPos, LivingEntity pSource, int pAmplifier, int pAreaOfEffect) {
        for (BlockPos blockPos : this.getSpherePos(pPos, pAreaOfEffect + 2)){
            if (pLevel.getFluidState(blockPos).isSourceOfType(Fluids.WATER)){
                if (pAmplifier < 1) {
                    pLevel.setBlockAndUpdate(blockPos, Blocks.ICE.defaultBlockState());
                } else if (pAmplifier < 2){
                    pLevel.setBlockAndUpdate(blockPos, Blocks.PACKED_ICE.defaultBlockState());
                } else {
                    if (pLevel.random.nextFloat() <= 0.1 + (pAmplifier / 10.0F)){
                        pLevel.setBlockAndUpdate(blockPos, Blocks.BLUE_ICE.defaultBlockState());
                    } else {
                        pLevel.setBlockAndUpdate(blockPos, Blocks.PACKED_ICE.defaultBlockState());
                    }
                }
            }
            for (LivingEntity livingEntity : pLevel.getEntitiesOfClass(LivingEntity.class, new AABB(blockPos))){
                this.applyEntityEffect(livingEntity, pSource, pAmplifier);
            }
        }
    }

    @Override
    public void applyEntityEffect(LivingEntity pTarget, @Nullable Entity pSource, int pAmplifier){
        if (pTarget.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)){
            pTarget.hurt(DamageSource.FREEZE, pAmplifier + 5.0F);
        }
    }
}
