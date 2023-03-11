package com.Polarice3.Goety.common.capabilities.lichdom;

public class LichImp implements ILichdom{

    private boolean lichdom;

    @Override
    public boolean getLichdom() {
        return this.lichdom;
    }

    @Override
    public void setLichdom(boolean lichdom) {
        this.lichdom = lichdom;
    }

}
