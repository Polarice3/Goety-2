package com.Polarice3.Goety.compat.jei;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.crafting.*;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.ritual.RitualTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class GoetyJeiPlugin implements IModPlugin {
    public static IJeiHelpers jeiHelper;

    public void registerCategories(IRecipeCategoryRegistration registration) {
        jeiHelper = registration.getJeiHelpers();
        registration.addRecipeCategories(new CursedInfuserCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new ModRitualCategory(registration.getJeiHelpers().getGuiHelper(), ""));
        registration.addRecipeCategories(new ModRitualCategory(registration.getJeiHelpers().getGuiHelper(), RitualTypes.ANIMATION));
        registration.addRecipeCategories(new ModRitualCategory(registration.getJeiHelpers().getGuiHelper(), RitualTypes.NECROTURGY));
        registration.addRecipeCategories(new ModRitualCategory(registration.getJeiHelpers().getGuiHelper(), RitualTypes.FORGE));
        registration.addRecipeCategories(new ModRitualCategory(registration.getJeiHelpers().getGuiHelper(), RitualTypes.GEOTURGY));
        registration.addRecipeCategories(new ModRitualCategory(registration.getJeiHelpers().getGuiHelper(), RitualTypes.MAGIC));
        registration.addRecipeCategories(new ModRitualCategory(registration.getJeiHelpers().getGuiHelper(), RitualTypes.SABBATH));
        registration.addRecipeCategories(new ModRitualCategory(registration.getJeiHelpers().getGuiHelper(), RitualTypes.ADEPT_NETHER));
        registration.addRecipeCategories(new ModRitualCategory(registration.getJeiHelpers().getGuiHelper(), RitualTypes.EXPERT_NETHER));
        registration.addRecipeCategories(new ModRitualCategory(registration.getJeiHelpers().getGuiHelper(), RitualTypes.END));
        registration.addRecipeCategories(new ModRitualCategory(registration.getJeiHelpers().getGuiHelper(), RitualTypes.FROST));
        registration.addRecipeCategories(new ModRitualCategory(registration.getJeiHelpers().getGuiHelper(), RitualTypes.SKY));
        registration.addRecipeCategories(new ModRitualCategory(registration.getJeiHelpers().getGuiHelper(), RitualTypes.STORM));
        registration.addRecipeCategories(new ModRitualCategory(registration.getJeiHelpers().getGuiHelper(), RitualTypes.DEEP));
        registration.addRecipeCategories(new ModBrazierCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new PulverizeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new WitchBrewCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CURSED_INFUSER.get()), JeiRecipeTypes.CURSED_INFUSER);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.GRIM_INFUSER.get()), JeiRecipeTypes.CURSED_INFUSER);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.DARK_ALTAR.get()), JeiRecipeTypes.RITUAL);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.PEDESTAL.get()), JeiRecipeTypes.RITUAL);
        registration.addRecipeCatalyst(new ItemStack(ModItems.ANIMATION_CORE.get()), JeiRecipeTypes.getRitual(RitualTypes.ANIMATION));
        registration.addRecipeCatalyst(new ItemStack(Blocks.SCULK), JeiRecipeTypes.getRitual(RitualTypes.NECROTURGY));
        registration.addRecipeCatalyst(new ItemStack(Blocks.ANVIL), JeiRecipeTypes.getRitual(RitualTypes.FORGE));
        registration.addRecipeCatalyst(new ItemStack(Blocks.CHIPPED_ANVIL), JeiRecipeTypes.getRitual(RitualTypes.FORGE));
        registration.addRecipeCatalyst(new ItemStack(Blocks.DAMAGED_ANVIL), JeiRecipeTypes.getRitual(RitualTypes.FORGE));
        registration.addRecipeCatalyst(new ItemStack(Blocks.AMETHYST_BLOCK), JeiRecipeTypes.getRitual(RitualTypes.GEOTURGY));
        registration.addRecipeCatalyst(new ItemStack(Blocks.ENCHANTING_TABLE), JeiRecipeTypes.getRitual(RitualTypes.MAGIC));
        registration.addRecipeCatalyst(new ItemStack(Blocks.CRYING_OBSIDIAN), JeiRecipeTypes.getRitual(RitualTypes.SABBATH));
        registration.addRecipeCatalyst(new ItemStack(Blocks.BLACKSTONE), JeiRecipeTypes.getRitual(RitualTypes.ADEPT_NETHER));
        registration.addRecipeCatalyst(new ItemStack(Blocks.NETHER_BRICKS), JeiRecipeTypes.getRitual(RitualTypes.EXPERT_NETHER));
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.VOID_BLOCK.get()), JeiRecipeTypes.getRitual(RitualTypes.END));
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.FREEZING_LAMP.get()), JeiRecipeTypes.getRitual(RitualTypes.FROST));
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MARBLE_BLOCK.get()), JeiRecipeTypes.getRitual(RitualTypes.SKY));
        registration.addRecipeCatalyst(new ItemStack(Blocks.LIGHTNING_ROD), JeiRecipeTypes.getRitual(RitualTypes.STORM));
        registration.addRecipeCatalyst(new ItemStack(Blocks.PRISMARINE_BRICKS), JeiRecipeTypes.getRitual(RitualTypes.DEEP));
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.NECRO_BRAZIER.get()), JeiRecipeTypes.BRAZIER);
        registration.addRecipeCatalyst(new ItemStack(ModItems.PULVERIZE_FOCUS.get()), JeiRecipeTypes.PULVERIZE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.BREWING_CAULDRON.get()), JeiRecipeTypes.BREWING);
    }

    public void registerRecipes(IRecipeRegistration registration) {
        ClientLevel world = Objects.requireNonNull(Minecraft.getInstance().level);
        RecipeManager recipeManager = world.getRecipeManager();
        IIngredientManager ingredientManager = registration.getIngredientManager();
        IVanillaRecipeFactory vanillaRecipeFactory = registration.getVanillaRecipeFactory();
        List<CursedInfuserRecipes> cursedRecipes = recipeManager.getAllRecipesFor(ModRecipeSerializer.CURSED_INFUSER.get());
        registration.addRecipes(JeiRecipeTypes.CURSED_INFUSER, cursedRecipes);
        List<RitualRecipe> ritualRecipes = recipeManager.getAllRecipesFor(ModRecipeSerializer.RITUAL_TYPE.get());
        registration.addRecipes(JeiRecipeTypes.RITUAL, ritualRecipes);
        this.registerRitualType(registration, recipeManager, RitualTypes.ANIMATION);
        this.registerRitualType(registration, recipeManager, RitualTypes.NECROTURGY);
        this.registerRitualType(registration, recipeManager, RitualTypes.FORGE);
        this.registerRitualType(registration, recipeManager, RitualTypes.GEOTURGY);
        this.registerRitualType(registration, recipeManager, RitualTypes.MAGIC);
        this.registerRitualType(registration, recipeManager, RitualTypes.SABBATH);
        this.registerRitualType(registration, recipeManager, RitualTypes.ADEPT_NETHER);
        this.registerRitualType(registration, recipeManager, RitualTypes.EXPERT_NETHER);
        this.registerRitualType(registration, recipeManager, RitualTypes.END);
        this.registerRitualType(registration, recipeManager, RitualTypes.FROST);
        this.registerRitualType(registration, recipeManager, RitualTypes.SKY);
        this.registerRitualType(registration, recipeManager, RitualTypes.STORM);
        this.registerRitualType(registration, recipeManager, RitualTypes.DEEP);
        List<BrazierRecipe> brazierRecipes = recipeManager.getAllRecipesFor(ModRecipeSerializer.BRAZIER_TYPE.get());
        registration.addRecipes(JeiRecipeTypes.BRAZIER, brazierRecipes);
        List<PulverizeRecipe> pulverizeRecipes = recipeManager.getAllRecipesFor(ModRecipeSerializer.PULVERIZE_TYPE.get());
        registration.addRecipes(JeiRecipeTypes.PULVERIZE, pulverizeRecipes);
        registration.addRecipes(JeiRecipeTypes.BREWING, WitchBrewMaker.getRecipes(recipeManager, vanillaRecipeFactory, ingredientManager));
    }

    public void registerRitualType(IRecipeRegistration registration, RecipeManager recipeManager, String type){
        this.registerRitualType(registration, recipeManager, type, type);
    }

    public void registerRitualType(IRecipeRegistration registration, RecipeManager recipeManager, String type, String type2){
        registration.addRecipes(JeiRecipeTypes.getRitual(type), this.ritualTypeRecipe(recipeManager, type2));
    }

    public List<RitualRecipe> ritualTypeRecipe(RecipeManager recipeManager, String type){
        return recipeManager.getAllRecipesFor(ModRecipeSerializer.RITUAL_TYPE.get()).stream().filter(ritualRecipe -> ritualRecipe.getCraftType().contains(type)).toList();
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Goety.MOD_ID, "jei_plugin");
    }
}
