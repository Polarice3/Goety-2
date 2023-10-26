package com.Polarice3.Goety.common.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class SoulAbsorberRecipes implements Recipe<Container> {
    protected final ResourceLocation id;
    protected final Ingredient ingredient;
    protected final int soulIncrease;
    protected final int cookingTime;

    public SoulAbsorberRecipes(ResourceLocation pId, Ingredient pIngredient, int pSouls, int pCookingTime) {
        this.id = pId;
        this.ingredient = pIngredient;
        this.soulIncrease = pSouls;
        this.cookingTime = pCookingTime;
    }

    public boolean matches(Container pInv, Level pLevel) {
        return this.ingredient.test(pInv.getItem(0));
    }

    @Override
    public ItemStack assemble(Container pInv, RegistryAccess pAccess) {
        return ItemStack.EMPTY;
    }

    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.add(this.ingredient);
        return nonnulllist;
    }

    public int getCookingTime() {
        return this.cookingTime;
    }

    public int getSoulIncrease(){
        return this.soulIncrease;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pAccess) {
        return ItemStack.EMPTY;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializer.SOUL_ABSORBER_RECIPES.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeSerializer.SOUL_ABSORBER.get();
    }
}
