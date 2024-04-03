package com.Polarice3.Goety.api.magic;

public interface IChargingSpell extends ISpell {
    int Cooldown();

    default boolean everCharge(){
        return false;
    }

    default int defaultCastDuration() {
        return 72000;
    }

    default int defaultCastUp(){
        return 0;
    }

    default int defaultSpellCooldown() {
        return 0;
    }

    default int shotsNumber(){
        return 0;
    }
}
