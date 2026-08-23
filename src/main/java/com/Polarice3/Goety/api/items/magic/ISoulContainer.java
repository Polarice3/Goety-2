package com.Polarice3.Goety.api.items.magic;

import net.minecraft.world.item.ItemStack;

public interface ISoulContainer {
    String SOULS_AMOUNT = "Souls";

    static boolean isEmpty(ItemStack itemStack) {
        if (itemStack.getTag() == null){
            return true;
        }
        int soulCount = itemStack.getTag().getInt(SOULS_AMOUNT);
        return soulCount == 0;
    }

    static int currentSouls(ItemStack itemStack){
        if (itemStack.getTag() != null){
            return itemStack.getTag().getInt(SOULS_AMOUNT);
        } else {
            return 0;
        }
    }

    static void setSoulsAmount(ItemStack itemStack, int souls){
        if (!(itemStack.getItem() instanceof ISoulContainer)) {
            return;
        }
        itemStack.getOrCreateTag().putInt(SOULS_AMOUNT, souls);
    }

    static void decreaseSouls(ItemStack itemStack, int souls) {
        if (!(itemStack.getItem() instanceof ISoulContainer) || itemStack.getTag() == null) {
            return;
        }
        int soulCount = itemStack.getTag().getInt(SOULS_AMOUNT);
        if (!isEmpty(itemStack)) {
            int finalCount = Math.max(soulCount - souls, 0);
            itemStack.getOrCreateTag().putInt(SOULS_AMOUNT, finalCount);
        }
    }
}
