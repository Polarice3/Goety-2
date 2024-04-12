package com.Polarice3.Goety.common.enchantments;

import com.Polarice3.Goety.SpellConfig;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.common.Tags;

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
                || stack.getItem() instanceof AxeItem
                || (stack.getItem() instanceof TieredItem
                && !(stack.getItem() instanceof DiggerItem))
                || stack.getItem() instanceof TridentItem
                || stack.getItem() instanceof ProjectileWeaponItem
                || stack.is(ItemTags.SWORDS)
                || stack.is(ItemTags.AXES)
                || stack.is(Tags.Items.TOOLS_TRIDENTS)
                || stack.is(Tags.Items.TOOLS_BOWS)
                || stack.is(Tags.Items.TOOLS_CROSSBOWS);
    }
}
