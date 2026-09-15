package com.Polarice3.Goety.api.items.magic;

import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.TotemFinder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ITotem extends ISoulContainer {

    @Override
    default boolean hasMaxAmount() {
        return true;
    }

    default void setTagTick(ItemStack stack){
        ISoulContainer.super.setTagTick(stack);
    }

    static boolean isFull(ItemStack itemStack) {
        return ISoulContainer.isFull(itemStack);
    }

    static boolean isEmpty(ItemStack itemStack) {
        return ISoulContainer.isEmpty(itemStack);
    }

    static boolean UndyingEffect(Player player){
        ItemStack itemStack = TotemFinder.FindTotem(player);
        if (!itemStack.isEmpty()) {
            if (itemStack.getTag() != null) {
                if (MainConfig.TotemUndying.get()) {
                    return itemStack.getTag().getInt(SOULS_AMOUNT) == MAX_SOULS;
                }
            }
        }
        return false;
    }

    static int currentSouls(ItemStack itemStack){
        return ISoulContainer.currentSouls(itemStack);
    }

    static int maximumSouls(ItemStack itemStack){
        return ISoulContainer.maximumSouls(itemStack);
    }

    static void setSoulsAmount(ItemStack itemStack, int souls){
        ISoulContainer.setSoulsAmount(itemStack, souls);
    }

    @Deprecated(forRemoval = true)
    static void setSoulsamount(ItemStack itemStack, int souls){
        setSoulsAmount(itemStack, souls);
    }

    static void setMaxSoulAmount(ItemStack itemStack, int souls){
        ISoulContainer.setMaxSoulAmount(itemStack, souls);
    }

    static void increaseSouls(ItemStack itemStack, int souls) {
        ISoulContainer.increaseSouls(itemStack, souls);
    }

    static void decreaseSouls(ItemStack itemStack, int souls) {
        ISoulContainer.decreaseSouls(itemStack, souls);
    }
}
