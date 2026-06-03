package com.Polarice3.Goety.common.enchantments;

import net.minecraft.world.entity.EquipmentSlot;

public class RoyaltyEnchantment extends FocusEnchantments {
    public RoyaltyEnchantment(Rarity pRarity, EquipmentSlot... pApplicableSlots) {
        super(pRarity, pApplicableSlots);
    }

    public int getMinCost(int p_45102_) {
        return p_45102_ * 25;
    }

    public int getMaxCost(int p_45105_) {
        return this.getMinCost(p_45105_) + 50;
    }

    public boolean isTradeable() {
        return false;
    }

    public boolean isDiscoverable() {
        return false;
    }
}
