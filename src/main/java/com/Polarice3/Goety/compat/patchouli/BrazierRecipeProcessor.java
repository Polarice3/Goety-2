package com.Polarice3.Goety.compat.patchouli;

import com.Polarice3.Goety.common.crafting.BrazierRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class BrazierRecipeProcessor implements IComponentProcessor {
    protected BrazierRecipe recipe;

    @Override
    public void setup(IVariableProvider iVariableProvider) {
        String recipeId = iVariableProvider.get("recipe").asString();
        this.recipe = (BrazierRecipe) Minecraft.getInstance().level.getRecipeManager()
                .byKey(new ResourceLocation(recipeId)).orElse(null);
    }

    @Override
    public IVariable process(String key) {
        if (this.recipe == null)
            return IVariable.empty();

        if (key.startsWith("ingredient")) {
            int index = Integer.parseInt(key.substring("ingredient".length())) - 1;
            if (index >= this.recipe.getIngredients().size())
                return IVariable.empty();


            Ingredient ingredient = this.recipe.getIngredients().get(index);
            return IVariable.from(ingredient.getItems());
        }

        if (key.equals("output")) {
            return IVariable.from(this.recipe.getResultItem());
        }

        if (key.startsWith("soulCost")) {
            return IVariable.wrap(I18n.get("jei.goety.single.soulcost", this.recipe.getSoulCost()));
        }

        return IVariable.empty();
    }
}
