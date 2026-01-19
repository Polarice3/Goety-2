package com.Polarice3.Goety.common.items.handler;

import com.Polarice3.Goety.api.items.magic.IFocus;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class SoulUsingItemHandler extends ItemStackHandler {
    private final ItemStack itemStack;

    public SoulUsingItemHandler(ItemStack itemStack) {
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
        return stack.getItem() instanceof IFocus;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    public NonNullList<ItemStack> getContents() {
        return stacks;
    }

    @Override
    protected void onContentsChanged(int slot) {
        CompoundTag nbt = itemStack.getOrCreateTag();
        nbt.putBoolean("goety-dirty", !nbt.getBoolean("goety-dirty"));
    }

    public static SoulUsingItemHandler get(ItemStack stack) {
        IItemHandler handler = stack.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .orElseThrow(() -> new IllegalArgumentException("ItemStack is missing item capability"));
        return (SoulUsingItemHandler) handler;
    }
}
