package com.Polarice3.Goety.compat.jei;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.crafting.BrazierRecipe;
import com.Polarice3.Goety.common.crafting.BrewingRecipe;
import com.Polarice3.Goety.common.crafting.CursedInfuserRecipes;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import mezz.jei.api.recipe.RecipeType;

public class JeiRecipeTypes {
    public static final RecipeType<CursedInfuserRecipes> CURSED_INFUSER =
            RecipeType.create(Goety.MOD_ID, "cursed_infuser", CursedInfuserRecipes.class);

    public static final RecipeType<RitualRecipe> RITUAL =
            RecipeType.create(Goety.MOD_ID, "ritual", RitualRecipe.class);

    public static final RecipeType<BrazierRecipe> BRAZIER =
            RecipeType.create(Goety.MOD_ID, "brazier", BrazierRecipe.class);

    public static final RecipeType<BrewingRecipe> BREWING =
            RecipeType.create(Goety.MOD_ID, "brewing", BrewingRecipe.class);
}
