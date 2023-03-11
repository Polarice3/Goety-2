package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.compat.curios.CuriosLoaded;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosApi;

public class TotemFinder {

    private static boolean isFocusBag(ItemStack itemStack) {
        return itemStack.getItem() == ModItems.FOCUS_BAG.get();
    }
    public static ItemStack findBag(Player playerEntity) {
        ItemStack foundStack = ItemStack.EMPTY;
        if (CuriosLoaded.CURIOS.isLoaded()) {
            foundStack = CuriosApi.getCuriosHelper().findEquippedCurio(TotemFinder::isFocusBag, playerEntity).map(
                    ImmutableTriple::getRight).orElse(ItemStack.EMPTY);
        }
        for (int i = 0; i <= playerEntity.getInventory().getContainerSize(); i++) {
            ItemStack itemStack = playerEntity.getInventory().getItem(i);
            if (!itemStack.isEmpty() && isMatchingItem(itemStack)) {
                foundStack = itemStack;
                break;
            }
        }

        return foundStack;
    }

    private static boolean isMatchingItem(ItemStack itemStack) {
        return itemStack.getItem() == ModItems.TOTEM_OF_SOULS.get();
    }

    public static ItemStack FindTotem(Player playerEntity){
        ItemStack foundStack = ItemStack.EMPTY;
        if (CuriosLoaded.CURIOS.isLoaded()) {
            foundStack = CuriosApi.getCuriosHelper().findEquippedCurio(TotemFinder::isMatchingItem, playerEntity).map(
                    ImmutableTriple::getRight).orElse(ItemStack.EMPTY);
        }

        for (int i = 0; i <= 9; i++) {
            ItemStack itemStack = playerEntity.getInventory().getItem(i);
            if (!itemStack.isEmpty() && isMatchingItem(itemStack)) {
                foundStack = itemStack;
                break;
            }
        }
        return foundStack;
    }
}
