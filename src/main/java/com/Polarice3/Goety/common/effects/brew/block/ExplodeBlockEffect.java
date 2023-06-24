package com.Polarice3.Goety.common.effects.brew.block;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

public class ExplodeBlockEffect extends BrewEffect {
    public ExplodeBlockEffect(int soulCost, int capacityExtra) {
        super("explode", soulCost, capacityExtra, MobEffectCategory.HARMFUL, 0xdb2f1a);
    }

    @Override
    public void applyBlockEffect(Level pLevel, BlockPos pPos, LivingEntity pSource, int pAmplifier, int pAreaOfEffect) {
        pLevel.explode(pSource, pPos.getX(), pPos.getY() + 1, pPos.getZ(), pAreaOfEffect + (pAmplifier / 2.0F) + 1, Explosion.BlockInteraction.DESTROY);
    }
}
