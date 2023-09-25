package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.handler.FocusBagItemHandler;
import com.Polarice3.Goety.common.items.magic.MagicFocus;
import com.Polarice3.Goety.common.items.magic.TotemOfSouls;
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
        for (int i = 0; i < playerEntity.getInventory().getContainerSize(); i++) {
            ItemStack itemStack = playerEntity.getInventory().getItem(i);
            if (!itemStack.isEmpty() && isFocusBag(itemStack)) {
                foundStack = itemStack;
                break;
            }
        }

        return foundStack;
    }

    public static ItemStack findFocusInBag(Player player){
        ItemStack foundStack = ItemStack.EMPTY;
        if (!findBag(player).isEmpty()){
            FocusBagItemHandler focusBagItemHandler = FocusBagItemHandler.get(findBag(player));
            for (int i = 1; i < focusBagItemHandler.getSlots(); ++i){
                ItemStack itemStack = focusBagItemHandler.getStackInSlot(i);
                if (itemStack.getItem() instanceof MagicFocus){
                    foundStack = itemStack;
                }
            }
        }
        return foundStack;
    }

    public static int getFocusBagTotal(Player player){
        int num = 0;
        if (!findBag(player).isEmpty()){
            FocusBagItemHandler focusBagItemHandler = FocusBagItemHandler.get(findBag(player));
            for (int i = 1; i < focusBagItemHandler.getSlots(); ++i){
                ItemStack itemStack = focusBagItemHandler.getStackInSlot(i);
                if (itemStack.getItem() instanceof MagicFocus){
                    ++num;
                }
            }
        }
        return num;
    }

    public static boolean hasEmptyBagSpace(Player player){
        return getFocusBagTotal(player) < 10;
    }

    public static boolean hasFocusInBag(Player player){
        return !findFocusInBag(player).isEmpty();
    }

    public static boolean canOpenWandCircle(Player player){
        return hasFocusInBag(player) || WandUtil.hasFocusInInv(player) || !WandUtil.findFocus(player).isEmpty();
    }

    private static boolean isTotem(ItemStack itemStack) {
        return itemStack.getItem() instanceof TotemOfSouls;
    }

    public static ItemStack FindTotem(Player playerEntity){
        ItemStack foundStack = ItemStack.EMPTY;
        if (CuriosLoaded.CURIOS.isLoaded()) {
            foundStack = CuriosApi.getCuriosHelper().findEquippedCurio(TotemFinder::isTotem, playerEntity).map(
                    ImmutableTriple::getRight).orElse(ItemStack.EMPTY);
        }

        for (int i = 0; i <= 9; i++) {
            ItemStack itemStack = playerEntity.getInventory().getItem(i);
            if (!itemStack.isEmpty() && isTotem(itemStack)) {
                foundStack = itemStack;
                break;
            }
        }
        return foundStack;
    }
}
