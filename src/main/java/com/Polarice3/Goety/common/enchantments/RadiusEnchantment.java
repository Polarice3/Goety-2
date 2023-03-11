package com.Polarice3.Goety.common.enchantments;

import com.Polarice3.Goety.SpellConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

public class RadiusEnchantment extends FocusEnchantments {
    public RadiusEnchantment(Enchantment.Rarity pRarity, EquipmentSlot... pApplicableSlots) {
        super(pRarity, pApplicableSlots);
    }

    public int getMinCost(int pEnchantmentLevel) {
        return 12 + (pEnchantmentLevel - 1) * 20;
    }

    public int getMaxCost(int pEnchantmentLevel) {
        return this.getMinCost(pEnchantmentLevel) + 25;
    }

    public int getMaxLevel() {
        return SpellConfig.MaxRadiusLevel.get();
    }
}
