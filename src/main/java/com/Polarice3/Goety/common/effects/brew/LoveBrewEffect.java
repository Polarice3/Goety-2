package com.Polarice3.Goety.common.effects.brew;

import com.Polarice3.Goety.common.entities.ally.AnimalSummon;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class LoveBrewEffect extends BrewEffect {
    public LoveBrewEffect(int soulCost) {
        super("love", soulCost, MobEffectCategory.BENEFICIAL, 0xff0659);
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
                Player player = pIndirectSource instanceof Player player1 ? player1 : null;
                animal.setInLove(player);
            }
            if (pTarget instanceof AnimalSummon animal){
                Player player = pIndirectSource instanceof Player player1 ? player1 : null;
                animal.setInLove(player);
            }
        }
    }
}
