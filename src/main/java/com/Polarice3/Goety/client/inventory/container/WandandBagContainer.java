package com.Polarice3.Goety.client.inventory.container;

import com.Polarice3.Goety.common.items.handler.FocusBagItemHandler;
import com.Polarice3.Goety.common.items.handler.SoulUsingItemHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class WandandBagContainer extends AbstractContainerMenu {
    private final ItemStack stack;

    public static WandandBagContainer createContainerClientSide(int id, Inventory inventory, FriendlyByteBuf buffer) {
        return new WandandBagContainer(id, new SoulUsingItemHandler(ItemStack.EMPTY), new FocusBagItemHandler(ItemStack.EMPTY), ItemStack.EMPTY);
    }

    public WandandBagContainer(int id, SoulUsingItemHandler soulUsingItemHandler, FocusBagItemHandler bagItemHandler, ItemStack stack) {
        super(ModContainerType.WAND_AND_BAG.get(), id);
        this.stack = stack;
        this.addSlot(new SlotItemHandler(soulUsingItemHandler, 0, 80, 35));

        for (int i = 0; i < 5; i++) {
            addSlot(new SlotItemHandler(bagItemHandler, i + 1, 62 - 18 + i * 18, 104));
        }
        for (int i = 0; i < 5; i++) {
            addSlot(new SlotItemHandler(bagItemHandler,6 + i, 62 - 18 + i * 18, 122));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return (player.getMainHandItem() == stack || player.getOffhandItem() == stack) && !stack.isEmpty();
    }

    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex == 0) {
                if (!this.moveItemStackTo(itemstack1, 0, this.slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

}
