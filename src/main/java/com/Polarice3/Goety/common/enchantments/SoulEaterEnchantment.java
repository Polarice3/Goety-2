package com.Polarice3.Goety.common.enchantments;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.items.magic.MagicFocus;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class SoulEaterEnchantment extends Enchantment {
    public SoulEaterEnchantment(Rarity rarityIn, EnchantmentCategory enchantmentType, EquipmentSlot... slots) {
        super(rarityIn, enchantmentType, slots);
    }

    public int getMinCost(int enchantmentLevel) {
        return 5 + (enchantmentLevel - 1) * 9;
    }

    public int getMaxCost(int enchantmentLevel) {
        return this.getMinCost(enchantmentLevel) + 15;
    }

    public int getMaxLevel() {
        return SpellConfig.MaxSoulEaterLevel.get();
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return super.canApplyAtEnchantingTable(stack)
                && !(stack.getItem() instanceof Wearable) && !(stack.getItem() instanceof FishingRodItem);
    }
}
