package com.Polarice3.Goety.common.enchantments;

import com.Polarice3.Goety.SpellConfig;
import net.minecraft.world.entity.EquipmentSlot;

public class VelocityEnchantment extends FocusEnchantments{
    public VelocityEnchantment(Rarity pRarity, EquipmentSlot... pApplicableSlots) {
        super(pRarity, pApplicableSlots);
    }

    public int getMinCost(int pEnchantmentLevel) {
        return 15 + (pEnchantmentLevel - 1) * 9;
    }

    public int getMaxCost(int pEnchantmentLevel) {
        return super.getMinCost(pEnchantmentLevel) + 50;
    }

    public int getMaxLevel() {
        return SpellConfig.MaxVelocityLevel.get();
    }
}
