package com.Polarice3.Goety.common.effects.brew;

import com.Polarice3.Goety.config.BrewConfig;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import javax.annotation.Nullable;

public class AgingBrewEffect extends BrewEffect {
    public AgingBrewEffect() {
        super("aging", BrewConfig.AgingCost.get(), MobEffectCategory.NEUTRAL, 0x515151);
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
            if (pTarget instanceof Mob mob){
                mob.setBaby(false);
            }
        }
    }
}
