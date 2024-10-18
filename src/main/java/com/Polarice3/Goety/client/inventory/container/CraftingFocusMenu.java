package com.Polarice3.Goety.client.inventory.container;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.MenuType;

public class CraftingFocusMenu extends CraftingMenu {
    public CraftingFocusMenu(int p_39353_, Inventory p_39354_, FriendlyByteBuf friendlyByteBuf) {
        super(p_39353_, p_39354_);
    }

    public CraftingFocusMenu(int p_39353_, Inventory p_39354_, ContainerLevelAccess p_39358_) {
        super(p_39353_, p_39354_, p_39358_);
    }

    public MenuType<?> getType() {
        return ModContainerType.CRAFTING_FOCUS.get();
    }

    public boolean stillValid(Player p_39368_) {
        return p_39368_.isAlive();
    }
}
