package com.Polarice3.Goety.api.magic;

public interface IEverChargeSpell extends IChargingSpell{
    default int Cooldown() {
        return 0;
    }
}
