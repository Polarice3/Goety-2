package com.Polarice3.Goety.api.magic;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IChargingSpell extends ISpell {
    int Cooldown();

    default int Cooldown(LivingEntity caster, ItemStack staff, int shots){
        return this.Cooldown();
    }

    default boolean everCharge(){
        return false;
    }

    default int defaultCastDuration() {
        return 72000;
    }

    default int defaultCastUp(){
        return 0;
    }

    default int castUp(LivingEntity caster, ItemStack staff){
        if (ReduceCastTime(caster)){
            return defaultCastUp() / 2;
        } else {
            return defaultCastUp();
        }
    }

    default int defaultSpellCooldown() {
        return 0;
    }

    default int shotsNumber(){
        return 0;
    }

    default int shotsNumber(LivingEntity caster, ItemStack staff){
        return this.shotsNumber();
    }
}
