package com.Polarice3.Goety.api.blocks;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Map;

public interface IEnchantedBlock {
    Object2IntMap<Enchantment> getEnchantments();

    default void loadEnchants(CompoundTag p_222787_){
        ListTag enchants = p_222787_.getList("enchantments", Tag.TAG_COMPOUND);
        Map<Enchantment, Integer> map = EnchantmentHelper.deserializeEnchantments(enchants);
        getEnchantments().clear();
        getEnchantments().putAll(map);
    }

    default void saveEnchants(CompoundTag p_222789_, Item item) {
        ItemStack stack = new ItemStack(item);
        EnchantmentHelper.setEnchantments(getEnchantments(), stack);
        p_222789_.put("enchantments", stack.getEnchantmentTags());
    }

}
