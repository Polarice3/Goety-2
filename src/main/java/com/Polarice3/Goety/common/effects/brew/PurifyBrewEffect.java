package com.Polarice3.Goety.common.effects.brew;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class PurifyBrewEffect extends BrewEffect{
    public boolean removeDebuff;

    public PurifyBrewEffect(String effectID, int soulCost, int cap, MobEffectCategory mobEffectCategory, int color, boolean removeDebuff) {
        super(effectID, soulCost, cap, mobEffectCategory, color);
        this.removeDebuff = removeDebuff;
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
        if (!pTarget.level.isClientSide) {
            if (this.removeDebuff) {
                pTarget.getActiveEffects().removeIf(mobEffectInstance -> !mobEffectInstance.getEffect().isBeneficial() && !mobEffectInstance.getEffect().getCurativeItems().isEmpty());
            } else {
                pTarget.getActiveEffects().removeIf(mobEffectInstance -> mobEffectInstance.getEffect().isBeneficial() && !mobEffectInstance.getEffect().getCurativeItems().isEmpty());
            }
        }
    }
}
