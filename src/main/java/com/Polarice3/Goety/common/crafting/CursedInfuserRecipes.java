package com.Polarice3.Goety.common.crafting;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CursedInfuserRecipes extends ModCookingRecipe {
    public boolean grim;

    public CursedInfuserRecipes(ResourceLocation pId, String pGroup, Ingredient pIngredient, ItemStack pResult, float pExperience, int pCookingTime, boolean grim) {
        super(ModRecipeSerializer.CURSED_INFUSER.get(), pId, pGroup, pIngredient, pResult, pExperience, pCookingTime);
        this.grim = grim;
    }

    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.CURSED_INFUSER.get());
    }

    public boolean isGrim() {
        return this.grim;
    }

    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializer.CURSED_INFUSER_RECIPES.get();
    }
}
