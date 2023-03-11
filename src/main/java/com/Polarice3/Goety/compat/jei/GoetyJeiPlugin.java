package com.Polarice3.Goety.compat.jei;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.crafting.CursedInfuserRecipes;
import com.Polarice3.Goety.common.crafting.ModRecipeSerializer;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class GoetyJeiPlugin implements IModPlugin {
    public static IJeiHelpers jeiHelper;

    public void registerCategories(IRecipeCategoryRegistration registration) {
        jeiHelper = registration.getJeiHelpers();
        registration.addRecipeCategories(new CursedInfuserCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new ModRitualCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CURSED_INFUSER.get()), JeiRecipeTypes.CURSED_INFUSER);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.DARK_ALTAR.get()), JeiRecipeTypes.RITUAL);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.PEDESTAL.get()), JeiRecipeTypes.RITUAL);
    }

    public void registerRecipes(IRecipeRegistration registration) {
        ClientLevel world = Objects.requireNonNull(Minecraft.getInstance().level);
        RecipeManager recipeManager = world.getRecipeManager();
        List<CursedInfuserRecipes> cursedRecipes = recipeManager.getAllRecipesFor(ModRecipeSerializer.CURSED_INFUSER.get());
        registration.addRecipes(JeiRecipeTypes.CURSED_INFUSER, cursedRecipes);
        List<RitualRecipe> ritualRecipes = recipeManager.getAllRecipesFor(ModRecipeSerializer.RITUAL_TYPE.get());
        registration.addRecipes(JeiRecipeTypes.RITUAL, ritualRecipes);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Goety.MOD_ID, "jei_plugin");
    }
}
