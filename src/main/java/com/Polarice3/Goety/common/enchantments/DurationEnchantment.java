package com.Polarice3.Goety.common.enchantments;

import com.Polarice3.Goety.SpellConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

public class DurationEnchantment extends FocusEnchantments{
    public DurationEnchantment(Enchantment.Rarity pRarity, EquipmentSlot... pApplicableSlots) {
        super(pRarity, pApplicableSlots);
    }

    public int getMinCost(int pEnchantmentLevel) {
        return 20;
    }

    public int getMaxCost(int pEnchantmentLevel) {
        return 50;
    }

    public int getMaxLevel() {
        return SpellConfig.MaxDurationLevel.get();
    }
}
