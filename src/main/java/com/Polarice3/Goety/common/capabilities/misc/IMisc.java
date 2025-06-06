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
    int ambientSoundTime();
    void setAmbientSoundTime(int soundTime);
    void increaseAmbientSoundTime();
    String customSpinTexture();
    void setCustomSpinTexture(String texture);
    int getClientTargetID();
    void setClientTargetID(int id);
    int getMobTargetID();
    void setMobTargetID(int id);
    int getNoHealTime();
    void setNoHealTime(int seconds);
    int getShakeTime();
    void setShakeTime(int ticks);
}
