package com.Polarice3.Goety.common.effects.brew;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;

import javax.annotation.Nullable;

public class FertilityBrewEffect extends BrewEffect {
    public FertilityBrewEffect() {
        super("fertility", MobEffectCategory.BENEFICIAL, 0x515151);
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
            if (pTarget instanceof Animal animal){
                if (animal.getAge() > 0){
                    animal.setAge(0);
                }
            }
        }
    }
}
