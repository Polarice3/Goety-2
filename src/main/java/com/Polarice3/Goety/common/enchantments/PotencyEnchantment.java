package com.Polarice3.Goety.common.enchantments;

import com.Polarice3.Goety.config.SpellConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

public class PotencyEnchantment extends FocusEnchantments {
    public PotencyEnchantment(Rarity pRarity, EquipmentSlot... pApplicableSlots) {
        super(pRarity, pApplicableSlots);
    }

    public int getMinCost(int pEnchantmentLevel) {
        return 15 + (pEnchantmentLevel - 1) * 9;
    }

    public int getMaxCost(int pEnchantmentLevel) {
        return super.getMinCost(pEnchantmentLevel) + 50;
    }

    public int getMaxLevel() {
        return SpellConfig.MaxPotencyLevel.get();
    }

    public boolean checkCompatibility(Enchantment pEnch) {
        return super.checkCompatibility(pEnch) && pEnch != ModEnchantments.ABSORB.get();
    }

}
