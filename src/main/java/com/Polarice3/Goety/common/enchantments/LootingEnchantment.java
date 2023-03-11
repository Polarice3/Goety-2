package com.Polarice3.Goety.common.enchantments;

import com.Polarice3.Goety.SpellConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.LootBonusEnchantment;

public class LootingEnchantment extends LootBonusEnchantment {
    public LootingEnchantment(Rarity p_i46726_1_, EnchantmentCategory p_i46726_2_, EquipmentSlot... p_i46726_3_) {
        super(p_i46726_1_, p_i46726_2_, p_i46726_3_);
    }

    public int getMaxLevel() {
        return SpellConfig.MaxWantingLevel.get();
    }

    public boolean isAllowedOnBooks() {
        return false;
    }

}
