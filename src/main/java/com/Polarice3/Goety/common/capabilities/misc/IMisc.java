package com.Polarice3.Goety.common.capabilities.misc;

public interface IMisc {
    int freezeLevel();
    void setFreezeLevel(int level);
    int shieldsLeft();
    void breakShield();
    void increaseShields();
    void setShields(int amount);
    int shieldTime();
    void setShieldTime(int time);
    void decreaseShieldTime();
    int shieldCool();
    void setShieldCool(int cool);
    void decreaseShieldCool();
}
