package com.Polarice3.Goety.common.effects.brew.block;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

public class SnowBlockEffect extends BrewEffect {
    public SnowBlockEffect() {
        super("snow", MobEffectCategory.NEUTRAL, 0xffffff);
    }

    @Override
    public boolean canLinger() {
        return true;
    }

    @Override
    public void applyBlockEffect(Level pLevel, BlockPos pPos, LivingEntity pSource, int pAmplifier, int pAreaOfEffect) {
        for (BlockPos blockPos : this.getSpherePos(pPos, pAreaOfEffect + 2)){
            if (pLevel.getBlockState(blockPos).isAir() && pLevel.getBlockState(blockPos.below()).isSolidRender(pLevel, blockPos.below())){
                pLevel.setBlockAndUpdate(blockPos, Blocks.SNOW.defaultBlockState());
            } else if (pLevel.getBlockState(blockPos).is(Blocks.SNOW) && pLevel.random.nextFloat() <= 0.1 + (pAmplifier / 10.0F)){
                pLevel.setBlockAndUpdate(blockPos, Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, Math.min(pLevel.getBlockState(blockPos).getValue(SnowLayerBlock.LAYERS) + 1, 8)));
            }
            for (LivingEntity livingEntity : pLevel.getEntitiesOfClass(LivingEntity.class, new AABB(blockPos))){
                this.applyEntityEffect(livingEntity, pSource, pAmplifier);
            }
        }
    }

    @Override
    public void applyEntityEffect(LivingEntity pTarget, @Nullable Entity pSource, int pAmplifier){
        if (pTarget.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)){
            pTarget.hurt(pTarget.damageSources().freeze(), pAmplifier + 1.0F);
        }
    }
}
