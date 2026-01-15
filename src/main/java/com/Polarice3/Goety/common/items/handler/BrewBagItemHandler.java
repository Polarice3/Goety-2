package com.Polarice3.Goety.common.items.handler;

import com.Polarice3.Goety.common.items.brew.ThrowableBrewItem;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class BrewBagItemHandler extends ItemStackHandler {
    private final ItemStack itemStack;

    public BrewBagItemHandler(ItemStack itemStack) {
        super(11);
        this.itemStack = itemStack;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return stack.getItem() instanceof ThrowableBrewItem;
    }

    public NonNullList<ItemStack> getContents() {
        return stacks;
    }

    @Override
    protected void onContentsChanged(int slot) {
        CompoundTag nbt = itemStack.getOrCreateTag();
        nbt.putBoolean("goety-dirty", !nbt.getBoolean("goety-dirty"));
    }

    public static BrewBagItemHandler get(ItemStack stack) {
        IItemHandler handler = stack.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .orElseThrow(() -> new IllegalArgumentException("ItemStack is missing item capability"));
        return (BrewBagItemHandler) handler;
    }
}
