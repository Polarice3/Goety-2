package com.Polarice3.Goety.api.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public interface INamelessDyeable {
    String TAG_COLOR = "color";
    String TAG_DISPLAY = "display";

    default boolean canDye() {
        return true;
    }

    default boolean hasCustomColor(ItemStack p_41114_) {
        CompoundTag compoundtag = p_41114_.getTagElement(TAG_DISPLAY);
        return compoundtag != null && compoundtag.contains(TAG_COLOR, 99);
    }

    default int getColor(ItemStack p_41122_) {
        CompoundTag compoundtag = p_41122_.getTagElement(TAG_DISPLAY);
        return compoundtag != null && compoundtag.contains(TAG_COLOR, 99) ? compoundtag.getInt(TAG_COLOR) : -1;
    }

    default void clearColor(ItemStack p_41124_) {
        CompoundTag compoundtag = p_41124_.getTagElement(TAG_DISPLAY);
        if (compoundtag != null && compoundtag.contains(TAG_COLOR)) {
            compoundtag.remove(TAG_COLOR);
        }

    }

    default void setColor(ItemStack p_41116_, int p_41117_) {
        p_41116_.getOrCreateTagElement(TAG_DISPLAY).putInt(TAG_COLOR, p_41117_);
    }
}
