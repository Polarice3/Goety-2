package com.Polarice3.Goety.common.magic;

import net.minecraft.sounds.SoundEvent;

public abstract class EverChargeSpells extends ChargingSpells{
    @Override
    public int Cooldown() {
        return 0;
    }

    public SoundEvent loopSound(){
        return null;
    }
}
