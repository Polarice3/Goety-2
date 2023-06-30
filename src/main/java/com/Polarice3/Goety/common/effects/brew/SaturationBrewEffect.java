package com.Polarice3.Goety.common.effects.brew;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class SaturationBrewEffect extends BrewEffect {
    public SaturationBrewEffect(int soulCost) {
        super("saturation", soulCost, MobEffectCategory.BENEFICIAL, 16262179);
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
        if (pTarget instanceof Player player && !pTarget.hasEffect(MobEffects.SATURATION)){
            if (!player.level.isClientSide){
                player.getFoodData().eat((pAmplifier * 2) + 1, 1.0F);
            }
        }
    }
}
