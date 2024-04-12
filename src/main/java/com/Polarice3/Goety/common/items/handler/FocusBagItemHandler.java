package com.Polarice3.Goety.common.items.handler;

import com.Polarice3.Goety.api.items.magic.IFocus;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class FocusBagItemHandler extends ItemStackHandler {
    private final ItemStack itemStack;
    private final int size;
    private int slot;

    public FocusBagItemHandler(ItemStack itemStack, int size) {
        super(size);
        this.size = size;
        this.itemStack = itemStack;
    }

    public ItemStack extractItem() {
        return extractItem(slot, 1, false);
    }

    public ItemStack insertItem(ItemStack insert) {
        return insertItem(slot, insert, false);
    }

    public ItemStack getSlot() {
        return getStackInSlot(slot);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return stack.getItem() instanceof IFocus;
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.size;
    }

    public NonNullList<ItemStack> getContents(){
        return stacks;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putInt("slot", slot);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++)
        {
            CompoundTag itemTags = tagList.getCompound(i);
            if (nbt.contains("slot")) {
                slot = nbt.getInt("slot");
                stacks.set(slot, ItemStack.of(itemTags));
            }
        }
        onLoad();

    }

    @Override
    protected void onContentsChanged(int slot) {
        CompoundTag nbt = itemStack.getOrCreateTag();
        nbt.putBoolean("goety-dirty", !nbt.getBoolean("goety-dirty"));
    }

    public static FocusBagItemHandler get(ItemStack stack) {
        IItemHandler handler = stack.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .orElseThrow(() -> new IllegalArgumentException("ItemStack is missing item capability"));
        return (FocusBagItemHandler) handler;
    }
}
