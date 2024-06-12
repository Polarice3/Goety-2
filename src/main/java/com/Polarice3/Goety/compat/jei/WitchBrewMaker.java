package com.Polarice3.Goety.compat.jei;

import com.Polarice3.Goety.common.crafting.BrewingRecipe;
import com.Polarice3.Goety.common.crafting.ModRecipeSerializer;
import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.Polarice3.Goety.common.effects.brew.BrewEffectInstance;
import com.Polarice3.Goety.common.effects.brew.BrewEffects;
import com.Polarice3.Goety.common.effects.brew.PotionBrewEffect;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.BrewUtils;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

public class WitchBrewMaker {
    private WitchBrewMaker() {
    }

    public static List<WitchBrewJeiRecipe> getRecipes(RecipeManager recipeManager, IVanillaRecipeFactory vanillaRecipeFactory, IIngredientManager ingredientManager) {
        return getBrewRecipes(recipeManager, vanillaRecipeFactory, ingredientManager);
    }

    private static List<WitchBrewJeiRecipe> getBrewRecipes(RecipeManager recipeManager, IVanillaRecipeFactory vanillaRecipeFactory, IIngredientManager ingredientManager) {
        List<WitchBrewJeiRecipe> recipes = new ArrayList<>();
        for (ItemStack itemStack : ingredientManager.getAllItemStacks()){
            List<MobEffectInstance> effects = new ArrayList<>();
            List<BrewEffectInstance> blockEffects = new ArrayList<>();
            BrewingRecipe brewingRecipe = recipeManager.getAllRecipesFor(ModRecipeSerializer.BREWING_TYPE.get()).stream().filter(recipe -> recipe.input.test(itemStack)).findFirst().orElse(null);
            ItemStack brew = new ItemStack(ModItems.BREW.get());
            int capacity = 0;
            int soulCost = 0;
            if (brewingRecipe != null){
                effects.add(new MobEffectInstance(brewingRecipe.output, brewingRecipe.duration));
                capacity = brewingRecipe.getCapacityExtra();
                soulCost = brewingRecipe.getSoulCost();
            } else if (BrewEffects.INSTANCE.getEffectFromCatalyst(itemStack.getItem()) != null){
                BrewEffect brewEffect = BrewEffects.INSTANCE.getEffectFromCatalyst(itemStack.getItem());
                if (brewEffect != null){
                    if (brewEffect instanceof PotionBrewEffect potionBrewEffect){
                        effects.add(new MobEffectInstance(potionBrewEffect.mobEffect, potionBrewEffect.duration));
                    } else {
                        blockEffects.add(new BrewEffectInstance(brewEffect, 1));
                    }
                    capacity = brewEffect.getCapacityExtra();
                    soulCost = brewEffect.getSoulCost();
                }
            }
            if (!effects.isEmpty() || !blockEffects.isEmpty()) {
                BrewUtils.setCustomEffects(brew, effects, blockEffects);
                brew.getOrCreateTag().putInt("CustomPotionColor", BrewUtils.getColor(effects, blockEffects));
                WitchBrewJeiRecipe recipe = new WitchBrewJeiRecipe(itemStack, brew, capacity, soulCost);
                if (!recipes.contains(recipe)) {
                    recipes.add(recipe);
                }
            }
        }
        return recipes;
    }
}
