package com.Polarice3.Goety.api.items.curios;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IActivatable {

    default void activate(Level level, Player player, ItemStack itemStack){
    }
}
