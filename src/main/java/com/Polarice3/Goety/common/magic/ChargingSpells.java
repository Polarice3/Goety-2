package com.Polarice3.Goety.common.magic;

import com.Polarice3.Goety.api.magic.IChargingSpell;

public abstract class ChargingSpells extends Spells implements IChargingSpell {

    public abstract int Cooldown();

    public int CastDuration() {
        return 72000;
    }

    @Override
    public int SpellCooldown() {
        return 0;
    }
}
