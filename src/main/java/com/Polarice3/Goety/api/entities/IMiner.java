package com.Polarice3.Goety.api.entities;

import com.Polarice3.Goety.api.entities.ally.illager.ILooter;
import net.minecraft.nbt.CompoundTag;

public interface IMiner extends ILooter {

    default int getMineTimes() {
        return 0;
    }

    default void setMineTimes(int time) {
    }

    default boolean isMining(){
        return false;
    }

    default void setMining(boolean mining){
    }

    default void readMinerData(CompoundTag pCompound) {
        if (pCompound.contains("MineTimes")){
            this.setMineTimes(pCompound.getInt("EnchantCool"));
        }
        if (pCompound.contains("Mining")) {
            this.setMining(pCompound.getBoolean("Mining"));
        }
    }

    default void addMinerData(CompoundTag pCompound) {
        pCompound.putInt("MineTimes", this.getMineTimes());
        pCompound.putBoolean("Mining", this.isMining());
    }
}
