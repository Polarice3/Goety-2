package com.Polarice3.Goety.api.blocks.entities;

import net.minecraft.world.entity.player.Player;

public interface IOwnedBlock {

    default boolean screenView(){
        return true;
    }

    Player getPlayer();
}
