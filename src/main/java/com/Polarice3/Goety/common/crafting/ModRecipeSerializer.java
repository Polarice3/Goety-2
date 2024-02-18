package com.Polarice3.Goety.common.crafting;

import com.Polarice3.Goety.Goety;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializer {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(
            ForgeRegistries.RECIPE_TYPES, Goety.MOD_ID);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(
            ForgeRegistries.RECIPE_SERIALIZERS, Goety.MOD_ID);

    public static void init(){
        RECIPE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<RecipeType<CursedInfuserRecipes>> CURSED_INFUSER = register("cursed_infuser");

    public static final RegistryObject<RecipeSerializer<CursedInfuserRecipes>> CURSED_INFUSER_RECIPES = RECIPE_SERIALIZERS.register("cursed_infuser_recipes",
            () -> new CursedInfuserRecipeSerializer<>(CursedInfuserRecipes::new, 60));

    public static final RegistryObject<RecipeType<SoulAbsorberRecipes>> SOUL_ABSORBER = register("soul_absorber");

    public static final RegistryObject<RecipeSerializer<SoulAbsorberRecipes>> SOUL_ABSORBER_RECIPES = RECIPE_SERIALIZERS.register("soul_absorber_recipes",
            () -> new SoulAbsorberRecipeSerializer<>(SoulAbsorberRecipes::new, 25, 200));

    public static final RegistryObject<RecipeType<RitualRecipe>> RITUAL_TYPE = register("ritual");

    public static final RegistryObject<RecipeSerializer<RitualRecipe>> RITUAL = RECIPE_SERIALIZERS.register("ritual",
            () -> RitualRecipe.SERIALIZER);

    public static final RegistryObject<RecipeType<BrazierRecipe>> BRAZIER_TYPE = register("brazier");

    public static final RegistryObject<RecipeSerializer<BrazierRecipe>> BRAZIER = RECIPE_SERIALIZERS.register("brazier",
            () -> BrazierRecipe.SERIALIZER);

    public static final RegistryObject<RecipeType<BrewingRecipe>> BREWING_TYPE = register("brewing");

    public static final RegistryObject<RecipeSerializer<BrewingRecipe>> BREWING = RECIPE_SERIALIZERS.register("brewing",
            () -> BrewingRecipe.SERIALIZER);

    public static final RegistryObject<RecipeType<PulverizeRecipe>> PULVERIZE_TYPE = register("pulverize");

    public static final RegistryObject<RecipeSerializer<PulverizeRecipe>> PULVERIZE = RECIPE_SERIALIZERS.register("pulverize",
            () -> PulverizeRecipe.SERIALIZER);

    public static final RegistryObject<RecipeSerializer<ModShapelessRecipe>> MODDED_SHAPELESS = RECIPE_SERIALIZERS.register("crafting_shapeless",
            ModShapelessRecipe.Serializer::new);

    static <T extends Recipe<?>> RegistryObject<RecipeType<T>> register(final String id) {
        return RECIPE_TYPES.register(id, () -> new RecipeType<T>() {
            public String toString() {
                return id;
            }
        });
    }
}
