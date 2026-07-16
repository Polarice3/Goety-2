package com.Polarice3.Goety.common.enchantments;

import net.minecraft.world.entity.EquipmentSlot;

public class BroomEnchantment extends FocusEnchantments {
    public int maxLevel = 1;

    public BroomEnchantment(Rarity pRarity, int maxLevel, EquipmentSlot... pApplicableSlots) {
        super(pRarity, pApplicableSlots);
        this.maxLevel = maxLevel;
    }

    @Override
    public int getMaxLevel() {
        return this.maxLevel;
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
}
