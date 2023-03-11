package com.Polarice3.Goety.common.enchantments;

import com.Polarice3.Goety.common.items.magic.MagicFocus;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public abstract class FocusEnchantments extends Enchantment {
    public FocusEnchantments(Enchantment.Rarity pRarity, EquipmentSlot... pApplicableSlots) {
        super(pRarity, ModEnchantments.FOCUS, pApplicableSlots);
    }

    public boolean isAllowedOnBooks() {
        return false;
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return super.canApplyAtEnchantingTable(stack) && stack.getItem() instanceof MagicFocus;
    }
}
