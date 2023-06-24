package com.Polarice3.Goety.common.effects.brew.block;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

public class ExtinguishBlockEffect extends BrewEffect {
    public ExtinguishBlockEffect() {
        super("extinguish", MobEffectCategory.BENEFICIAL, 0xfff5c6);
    }

    @Override
    public void applyBlockEffect(Level pLevel, BlockPos pPos, LivingEntity pSource, int pAmplifier, int pAreaOfEffect) {
        for (BlockPos blockPos : this.getSpherePos(pPos, pAreaOfEffect + 2)){
            if (pLevel.getBlockState(blockPos).is(BlockTags.FIRE)){
                pLevel.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                pLevel.levelEvent((Player)null, 1009, blockPos, 0);
            }
            for (LivingEntity livingEntity : pLevel.getEntitiesOfClass(LivingEntity.class, new AABB(blockPos))){
                this.applyEntityEffect(livingEntity, pSource, pSource, pAmplifier);
            }
        }
    }

    @Override
    public void applyEntityEffect(LivingEntity pTarget, @Nullable Entity source, @Nullable Entity pIndirectSource, int pAmplifier){
        if (pTarget instanceof Blaze){
            DamageSource source1 = source != null ? DamageSource.indirectMagic(source, pIndirectSource) : DamageSource.MAGIC;
            pTarget.hurt(source1, pAmplifier + 2.0F);
        }
    }
}
