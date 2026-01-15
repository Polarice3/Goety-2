package com.Polarice3.Goety.common.items.handler;

import com.Polarice3.Goety.common.items.brew.BrewItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class WitchStaffItemHandler extends ItemStackHandler {
    private final ItemStack itemStack;

    public WitchStaffItemHandler(ItemStack itemStack) {
        this.itemStack = itemStack;
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
        return stack.getItem() instanceof BrewItem;
    }

    @Override
    protected void onContentsChanged(int slot) {
        CompoundTag nbt = itemStack.getOrCreateTag();
        nbt.putBoolean("goety-dirty", !nbt.getBoolean("goety-dirty"));
    }

    public static WitchStaffItemHandler get(ItemStack stack) {
        IItemHandler handler = stack.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .orElseThrow(() -> new IllegalArgumentException("ItemStack is missing item capability"));
        return (WitchStaffItemHandler) handler;
    }
}
