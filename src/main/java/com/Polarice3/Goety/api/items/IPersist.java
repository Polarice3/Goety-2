package com.Polarice3.Goety.api.items;

import net.minecraft.world.item.ItemStack;

public interface IPersist {

    default boolean isNotBroken(ItemStack stack) {
        return stack.getDamageValue() < stack.getMaxDamage() - 1;
    }

    default boolean isBroken(ItemStack stack) {
        return !this.isNotBroken(stack);
    }

}
