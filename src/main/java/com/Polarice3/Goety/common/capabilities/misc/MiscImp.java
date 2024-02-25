package com.Polarice3.Goety.common.capabilities.misc;

public class MiscImp implements IMisc{
    private int freeze = 0;

    @Override
    public int freezeLevel() {
        return this.freeze;
    }

    @Override
    public void setFreezeLevel(int level) {
        this.freeze = level;
    }
}
