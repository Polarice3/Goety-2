package com.Polarice3.Goety.common.capabilities.misc;

public class MiscImp implements IMisc{
    private int freeze = 0;
    private int shields = 0;
    private int shieldTime = 0;
    private int shieldCool = 0;
    private int ambientSoundTime = 0;

    @Override
    public int freezeLevel() {
        return this.freeze;
    }

    @Override
    public void setFreezeLevel(int level) {
        this.freeze = level;
    }

    @Override
    public int shieldsLeft() {
        return this.shields;
    }

    @Override
    public void breakShield() {
        --this.shields;
    }

    @Override
    public void increaseShields() {
        ++this.shields;
    }

    @Override
    public void setShields(int amount) {
        this.shields = amount;
    }

    @Override
    public int shieldTime(){
        return this.shieldTime;
    }

    @Override
    public void setShieldTime(int time) {
        this.shieldTime = time;
    }

    @Override
    public void decreaseShieldTime() {
        --this.shieldTime;
    }

    @Override
    public int shieldCool() {
        return this.shieldCool;
    }

    @Override
    public void setShieldCool(int cool) {
        this.shieldCool = cool;
    }

    @Override
    public void decreaseShieldCool() {
        --this.shieldCool;
    }

    @Override
    public int ambientSoundTime() {
        return this.ambientSoundTime;
    }

    @Override
    public void setAmbientSoundTime(int soundTime) {
        this.ambientSoundTime = soundTime;
    }
}
