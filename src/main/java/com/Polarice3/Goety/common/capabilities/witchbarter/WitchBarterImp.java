package com.Polarice3.Goety.common.capabilities.witchbarter;

public class WitchBarterImp implements IWitchBarter {

    private int timer;
    private int traderID = -1;

    @Override
    public int getTimer() {
        return timer;
    }

    @Override
    public void setTimer(int timer) {
        this.timer = timer;
    }

    @Override
    public int getTraderID() {
        return this.traderID;
    }

    @Override
    public void setTraderID(int id) {
        this.traderID = id;
    }
}
