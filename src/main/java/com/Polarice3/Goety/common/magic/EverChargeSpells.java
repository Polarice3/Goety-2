package com.Polarice3.Goety.common.magic;

import com.Polarice3.Goety.api.magic.IEverChargeSpell;

public abstract class EverChargeSpells extends ChargingSpells implements IEverChargeSpell {
    @Override
    public int Cooldown() {
        return 0;
    }
}
