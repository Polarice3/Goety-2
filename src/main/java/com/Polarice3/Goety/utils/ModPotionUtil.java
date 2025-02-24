package com.Polarice3.Goety.utils;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;

public class ModPotionUtil extends BrewingRecipe {

    private final ItemStack inputStack;

    public ModPotionUtil(ItemStack inputStack, Ingredient ingredient, ItemStack output) {
        super(Ingredient.of(inputStack), ingredient, output);
        this.inputStack = inputStack;
    }

    @Override
    public boolean isInput(ItemStack stack) {
        return super.isInput(stack) && PotionUtils.getPotion(stack) == PotionUtils.getPotion(inputStack);
    }

    public static ItemStack setPotion(Potion pPotion) {
        return PotionUtils.setPotion(new ItemStack(Items.POTION), pPotion);
    }

    public static ItemStack setSplashPotion(Potion pPotion) {
        return PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), pPotion);
    }

    public static ItemStack setLingeringPotion(Potion pPotion) {
        return PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), pPotion);
    }
}
