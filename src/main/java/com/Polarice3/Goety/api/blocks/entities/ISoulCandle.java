package com.Polarice3.Goety.api.blocks.entities;

import net.minecraft.core.BlockPos;

public interface ISoulCandle {

    default void drainSouls(int amount, BlockPos blockPos){
    }

    default int getSouls(){
        return 0;
    }

    default boolean checkCage() {
        return false;
    }
}
