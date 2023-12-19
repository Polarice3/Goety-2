package com.Polarice3.Goety.api.magic;

import net.minecraft.sounds.SoundEvent;

public interface IEverChargeSpell extends IChargingSpell{
    default int Cooldown() {
        return 0;
    }

    SoundEvent loopSound();
}
