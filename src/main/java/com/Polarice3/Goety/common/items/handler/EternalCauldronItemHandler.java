package com.Polarice3.Goety.common.items.handler;

import com.Polarice3.Goety.common.items.brew.BrewItem;
import com.Polarice3.Goety.common.items.brew.ThrowableBrewItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class EternalCauldronItemHandler extends ItemStackHandler {

    public EternalCauldronItemHandler() {
    }

    public ItemStack extractItem() {
        return extractItem(0, 1, false);
    }

    public ItemStack insertItem(ItemStack insert) {
        return insertItem(0, insert, false);
    }

    public ItemStack getSlot() {
        return getStackInSlot(0);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        Item item = stack.getItem();
        return (item instanceof PotionItem && !(item instanceof ThrowablePotionItem)) || (item instanceof BrewItem && !(item instanceof ThrowableBrewItem));
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    public static EternalCauldronItemHandler get(ItemStack stack) {
        IItemHandler handler = stack.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .orElseThrow(() -> new IllegalArgumentException("ItemStack is missing item capability"));
        return (EternalCauldronItemHandler) handler;
    }
}
