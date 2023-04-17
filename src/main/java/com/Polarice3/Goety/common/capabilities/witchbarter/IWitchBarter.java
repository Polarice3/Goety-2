package com.Polarice3.Goety.common.capabilities.witchbarter;

import net.minecraft.world.entity.LivingEntity;

public interface IWitchBarter {
    int getTimer();
    void setTimer(int timer);
    LivingEntity getTrader();
    void setTrader(LivingEntity trader);
}
