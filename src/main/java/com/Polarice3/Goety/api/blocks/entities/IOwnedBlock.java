package com.Polarice3.Goety.api.blocks.entities;

import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.UUID;

public interface IOwnedBlock {

    default boolean screenView(){
        return true;
    }

    @Nullable
    default UUID getOwnerUUID() {
        return null;
    }

    default void setOwnerUUID(@Nullable UUID p_184754_1_) {
    }

    default int getOwnerId() {
        return -1;
    }

    default void setOwnerId(int p_184754_1_) {
    }

    Player getPlayer();
}
