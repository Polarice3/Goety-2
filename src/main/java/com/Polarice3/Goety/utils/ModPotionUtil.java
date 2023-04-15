package com.Polarice3.Goety.utils;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;

public class ModPotionUtil {

    public static ItemStack setPotion(Potion pPotion) {
        ItemStack stack = new ItemStack(Items.POTION);
        return PotionUtils.setPotion(stack, pPotion);
    }

    public static ItemStack setSplashPotion(Potion pPotion) {
        ItemStack stack = new ItemStack(Items.SPLASH_POTION);
        return PotionUtils.setPotion(stack, pPotion);
    }

    public static ItemStack setLingeringPotion(Potion pPotion) {
        ItemStack stack = new ItemStack(Items.LINGERING_POTION);
        return PotionUtils.setPotion(stack, pPotion);
    }
}
