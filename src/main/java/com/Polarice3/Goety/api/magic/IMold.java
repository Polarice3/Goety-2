package com.Polarice3.Goety.api.magic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IMold {
    default boolean spawnServant(Player player, ItemStack stack, Level level, BlockPos blockPos){
        return false;
    }
}
