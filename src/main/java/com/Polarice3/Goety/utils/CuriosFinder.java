package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.curios.RingItem;
import com.Polarice3.Goety.compat.curios.CuriosLoaded;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.function.Predicate;

public class CuriosFinder {

    public static ItemStack findCurio(LivingEntity livingEntity, Predicate<ItemStack> filter){
        ItemStack foundStack = ItemStack.EMPTY;
        if (CuriosLoaded.CURIOS.isLoaded()) {
            foundStack = CuriosApi.getCuriosHelper().findEquippedCurio(filter, livingEntity).map(
                    ImmutableTriple::getRight).orElse(ItemStack.EMPTY);
        }

        return foundStack;
    }

    public static boolean hasCurio(LivingEntity livingEntity, Predicate<ItemStack> filter){
        return !findCurio(livingEntity, filter).isEmpty();
    }

    public static ItemStack findCurio(LivingEntity livingEntity, Item item){
        ItemStack foundStack = ItemStack.EMPTY;
        if (CuriosLoaded.CURIOS.isLoaded()) {
            foundStack = CuriosApi.getCuriosHelper().findEquippedCurio(item, livingEntity).map(
                    ImmutableTriple::getRight).orElse(ItemStack.EMPTY);
        }

        return foundStack;
    }

    public static boolean hasDarkRobe(LivingEntity livingEntity){
        return hasCurio(livingEntity, (itemStack -> itemStack.getItem() == ModItems.DARK_ROBE.get()));
    }

    public static boolean hasDarkHat(LivingEntity livingEntity){
        return hasCurio(livingEntity, (itemStack -> itemStack.getItem() == ModItems.DARK_HAT.get()));
    }

    public static boolean hasIllusionRobe(LivingEntity livingEntity){
        return hasCurio(livingEntity, (itemStack -> itemStack.getItem() == ModItems.ILLUSION_ROBE.get()));
    }

    private static boolean isRing(ItemStack itemStack) {
        return itemStack.getItem() instanceof RingItem;
    }

    public static ItemStack findRing(Player playerEntity){
        ItemStack foundStack = ItemStack.EMPTY;
        if (CuriosLoaded.CURIOS.isLoaded()) {
            foundStack = CuriosApi.getCuriosHelper().findEquippedCurio(CuriosFinder::isRing, playerEntity).map(
                    ImmutableTriple::getRight).orElse(ItemStack.EMPTY);
        } else {
            for (int i = 0; i <= playerEntity.getInventory().getContainerSize(); i++) {
                ItemStack itemStack = playerEntity.getInventory().getItem(i);
                if (!itemStack.isEmpty() && isRing(itemStack)) {
                    foundStack = itemStack;
                    break;
                }
            }
        }

        return foundStack;
    }
}
