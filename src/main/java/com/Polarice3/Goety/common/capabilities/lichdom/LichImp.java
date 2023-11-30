package com.Polarice3.Goety.common.capabilities.lichdom;

public class LichImp implements ILichdom{

    private boolean lichdom;
    private boolean lichMode;

    @Override
    public boolean getLichdom() {
        return this.lichdom;
    }

    @Override
    public void setLichdom(boolean lichdom) {
        this.lichdom = lichdom;
    }

    @Override
    public boolean isLichMode(){
        return this.lichMode;
    }

    @Override
    public void setLichMode(boolean lichMode){
        this.lichMode = lichMode;
    }

}
