package com.Polarice3.Goety.common.effects.brew.block;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

public class DroughtBlockEffect extends BrewEffect {
    public DroughtBlockEffect() {
        super("drought", MobEffectCategory.NEUTRAL, 0x9d8f39);
    }

    @Override
    public boolean canLinger() {
        return true;
    }

    @Override
    public void applyBlockEffect(Level pLevel, BlockPos pPos, LivingEntity pSource, int pAmplifier, int pAreaOfEffect) {
        for (BlockPos blockPos : this.getSpherePos(pPos, pAreaOfEffect + 2)){
            if (pLevel.getFluidState(blockPos).isSourceOfType(Fluids.WATER)){
                pLevel.setBlockAndUpdate(blockPos, Blocks.CAVE_AIR.defaultBlockState());
            }
            for (LivingEntity livingEntity : pLevel.getEntitiesOfClass(LivingEntity.class, new AABB(blockPos))){
                this.applyEntityEffect(livingEntity, pSource, pAmplifier);
            }
        }
    }

    @Override
    public void applyEntityEffect(LivingEntity pTarget, @Nullable Entity pSource, int pAmplifier){
        if (pTarget.getMobType() == MobType.WATER){
            pTarget.hurt(DamageSource.DRY_OUT, pAmplifier + 5.0F);
        }
    }
}
