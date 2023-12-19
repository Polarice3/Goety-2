package com.Polarice3.Goety.api.magic;

public interface IChargingSpell extends ISpell {
    int Cooldown();

    default int CastDuration() {
        return 72000;
    }

    default int SpellCooldown() {
        return 0;
    }
}
