package com.Polarice3.Goety.common.effects.brew.block;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.Polarice3.Goety.common.entities.ally.Leapleaf;
import com.Polarice3.Goety.common.entities.ally.Whisperer;
import com.Polarice3.Goety.common.entities.neutral.AbstractVine;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

public class GrowBlockEffect extends BrewEffect {
    public GrowBlockEffect(int soulCost) {
        super("growth", soulCost, MobEffectCategory.BENEFICIAL, 0x009611);
    }

    @Override
    public void applyBlockEffect(Level pLevel, BlockPos pPos, LivingEntity pSource, int pAmplifier, int pAreaOfEffect) {
        for (BlockPos blockPos : this.getCubePos(pPos, pAreaOfEffect + 1)){
            if (pLevel.getBlockState(blockPos).getBlock() instanceof BonemealableBlock bonemealableBlock
                    && bonemealableBlock.isValidBonemealTarget(pLevel, blockPos, pLevel.getBlockState(blockPos), pLevel.isClientSide)) {
                if (pLevel instanceof ServerLevel serverLevel) {
                    bonemealableBlock.performBonemeal(serverLevel, serverLevel.random, blockPos, pLevel.getBlockState(blockPos));
                }
            }
            for (LivingEntity livingEntity : pLevel.getEntitiesOfClass(LivingEntity.class, new AABB(blockPos))){
                this.applyEntityEffect(livingEntity, pSource, pAmplifier);
            }
        }
    }

    @Override
    public void applyEntityEffect(LivingEntity pTarget, @Nullable Entity pSource, int pAmplifier){
        if (pTarget instanceof AbstractVine || pTarget instanceof Whisperer || pTarget instanceof Leapleaf){
            pTarget.heal(5.0F);
            if (pTarget.level instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 7; ++i) {
                    double d0 = pTarget.getRandom().nextGaussian() * 0.02D;
                    double d1 = pTarget.getRandom().nextGaussian() * 0.02D;
                    double d2 = pTarget.getRandom().nextGaussian() * 0.02D;
                    serverLevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), pTarget.getRandomX(1.0D), pTarget.getRandomY() + 0.5D, pTarget.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                }
            }
        }
    }
}
