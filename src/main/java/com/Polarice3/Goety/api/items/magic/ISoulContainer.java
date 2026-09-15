package com.Polarice3.Goety.api.items.magic;

import com.Polarice3.Goety.config.MainConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public interface ISoulContainer {
    String SOULS_AMOUNT = "Souls";
    String MAX_SOUL_AMOUNT = "Max Souls";
    int MAX_SOULS = MainConfig.MaxSouls.get();

    default boolean hasMaxAmount() {
        return false;
    }

    default int getMaxSouls() {
        return 0;
    }

    default void setTagTick(ItemStack stack){
        if (stack.getTag() == null){
            CompoundTag compound = stack.getOrCreateTag();
            compound.putInt(SOULS_AMOUNT, 0);
            if (this.hasMaxAmount()) {
                compound.putInt(MAX_SOUL_AMOUNT, this.getMaxSouls());
            }
        }
        if (this.hasMaxAmount()) {
            if (!stack.getTag().contains(MAX_SOUL_AMOUNT)) {
                CompoundTag compound = stack.getOrCreateTag();
                compound.putInt(MAX_SOUL_AMOUNT, this.getMaxSouls());
            }
            if (stack.getTag().getInt(SOULS_AMOUNT) > stack.getTag().getInt(MAX_SOUL_AMOUNT)) {
                stack.getTag().putInt(SOULS_AMOUNT, stack.getTag().getInt(MAX_SOUL_AMOUNT));
            }
        }
        if (stack.getTag().getInt(SOULS_AMOUNT) < 0){
            stack.getTag().putInt(SOULS_AMOUNT, 0);
        }
    }

    static boolean isFull(ItemStack itemStack) {
        if (itemStack.getTag() == null){
            return false;
        }
        if (itemStack.getItem() instanceof ISoulContainer soulContainer && !soulContainer.hasMaxAmount()) {
            return false;
        }
        int soulCount = itemStack.getTag().getInt(SOULS_AMOUNT);
        int MaxSouls = itemStack.getTag().getInt(MAX_SOUL_AMOUNT);
        return soulCount == MaxSouls;
    }

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

    static int maximumSouls(ItemStack itemStack){
        if (itemStack.getItem() instanceof ISoulContainer soulContainer && soulContainer.hasMaxAmount()) {
            if (itemStack.getTag() != null){
                return itemStack.getTag().getInt(MAX_SOUL_AMOUNT);
            }
        }
        return 0;
    }

    static void setMaxSoulAmount(ItemStack itemStack, int souls){
        if (itemStack.getItem() instanceof ISoulContainer soulContainer && soulContainer.hasMaxAmount()) {
            itemStack.getOrCreateTag().putInt(MAX_SOUL_AMOUNT, souls);
        }
    }

    static void increaseSouls(ItemStack itemStack, int souls) {
        if (!(itemStack.getItem() instanceof ISoulContainer soulContainer) || itemStack.getTag() == null) {
            return;
        }
        int soulCount = itemStack.getTag().getInt(SOULS_AMOUNT);
        if (!isFull(itemStack) || !soulContainer.hasMaxAmount()) {
            int finalCount = soulCount + souls;
            if (soulContainer.hasMaxAmount()) {
                finalCount = Math.min(soulCount + souls, maximumSouls(itemStack));
            }
            itemStack.getOrCreateTag().putInt(SOULS_AMOUNT, finalCount);
        }
    }
}
