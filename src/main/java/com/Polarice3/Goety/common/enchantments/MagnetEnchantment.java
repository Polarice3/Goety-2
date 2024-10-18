package com.Polarice3.Goety.common.enchantments;

import net.minecraft.world.entity.EquipmentSlot;

public class MagnetEnchantment extends FocusEnchantments {
    public MagnetEnchantment(Rarity pRarity, EquipmentSlot... pApplicableSlots) {
        super(pRarity, pApplicableSlots);
    }

    public int getMinCost(int pEnchantmentLevel) {
        return pEnchantmentLevel * 25;
    }

    public int getMaxCost(int pEnchantmentLevel) {
        return this.getMinCost(pEnchantmentLevel) + 50;
    }

    public int getMaxLevel() {
        return 1;
    }
}
