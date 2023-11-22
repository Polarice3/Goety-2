package com.Polarice3.Goety.common.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItem;

import javax.annotation.Nonnull;

public class RepeatCraftItem extends Item implements IForgeItem {
    public RepeatCraftItem(Item.Properties properties){
        super(properties);
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        return itemStack.copy();
    }
}
