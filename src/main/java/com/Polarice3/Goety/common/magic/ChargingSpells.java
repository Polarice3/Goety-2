package com.Polarice3.Goety.common.magic;

public abstract class ChargingSpells extends Spells{

    public abstract int Cooldown();

    public int CastDuration() {
        return 72000;
    }

    @Override
    public int SpellCooldown() {
        return 0;
    }
}
