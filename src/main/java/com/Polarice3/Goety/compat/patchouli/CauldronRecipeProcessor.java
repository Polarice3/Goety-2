package com.Polarice3.Goety.compat.patchouli;

import com.Polarice3.Goety.common.crafting.CauldronRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class CauldronRecipeProcessor implements IComponentProcessor {
    protected CauldronRecipe recipe;

    @Override
    public void setup(Level level, IVariableProvider iVariableProvider) {
        String recipeId = iVariableProvider.get("recipe").asString();
        this.recipe = (CauldronRecipe) Minecraft.getInstance().level.getRecipeManager()
                .byKey(new ResourceLocation(recipeId)).orElse(null);
    }

    @Override
    public IVariable process(Level level, String key) {
        if (this.recipe == null)
            return IVariable.empty();

        if (key.startsWith("ingredient")) {
            int index = Integer.parseInt(key.substring("ingredient".length())) - 1;
            if (index >= this.recipe.getIngredients().size()) {
                return IVariable.empty();
            }


            Ingredient ingredient = this.recipe.getIngredients().get(index);
            return IVariable.from(ingredient.getItems());
        }

        if (key.equals("take_with")) {
            Ingredient ingredient = this.recipe.getTakeWith();
            if (ingredient != null) {
                return IVariable.from(ingredient.getItems());
            }
        }

        if (key.equals("output")) {
            return IVariable.from(this.recipe.getResultItem(level.registryAccess()));
        }

        if (key.startsWith("soulCost")) {
            return IVariable.wrap(I18n.get("jei.goety.single.soulcost", this.recipe.getSoulCost()));
        }

        return IVariable.empty();
    }
}
