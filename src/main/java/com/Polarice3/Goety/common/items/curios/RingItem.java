package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class RingItem extends SingleStackItem {

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
    {
        if (stack.getItem() == ModItems.RING_OF_WANT.get()) {
            return enchantment == ModEnchantments.WANTING.get();
        }
        return false;
    }
}
