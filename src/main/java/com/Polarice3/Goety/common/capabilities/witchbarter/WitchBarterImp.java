package com.Polarice3.Goety.common.capabilities.witchbarter;

import net.minecraft.world.entity.LivingEntity;

public class WitchBarterImp implements IWitchBarter {

    private int timer;
    private LivingEntity trader;

    @Override
    public int getTimer() {
        return timer;
    }

    @Override
    public void setTimer(int timer) {
        this.timer = timer;
    }

    @Override
    public LivingEntity getTrader() {
        return trader;
    }

    @Override
    public void setTrader(LivingEntity trader) {
        this.trader = trader;
    }
}
