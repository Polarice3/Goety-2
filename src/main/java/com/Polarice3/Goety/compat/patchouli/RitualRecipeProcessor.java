package com.Polarice3.Goety.compat.patchouli;

import com.Polarice3.Goety.common.crafting.RitualRecipe;
import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class RitualRecipeProcessor implements IComponentProcessor {

    protected RitualRecipe recipe;
    protected ItemStack pedestal;

    @Override
    public void setup(IVariableProvider iVariableProvider) {
        String recipeId = iVariableProvider.get("recipe").asString();
        this.recipe = (RitualRecipe) Minecraft.getInstance().level.getRecipeManager()
                .byKey(new ResourceLocation(recipeId)).orElse(null);
        this.pedestal = new ItemStack(ModItems.PEDESTAL_DUMMY.get());
    }

    @Override
    public IVariable process(String key) {
        if (this.recipe == null)
            return IVariable.empty();

        if (key.startsWith("activation_item")) {
            return IVariable.from(this.recipe.getActivationItem().getItems());
        }

        if (key.startsWith("craftType")) {
            if (this.recipe.getCraftType() != null) {
                return IVariable.wrap(I18n.get("jei.goety.craftType." + I18n.get(recipe.getCraftType())));
            }
        }

        if (key.startsWith("ingredient")) {
            int index = Integer.parseInt(key.substring("ingredient".length())) - 1;
            if (index >= this.recipe.getIngredients().size())
                return IVariable.empty();


            Ingredient ingredient = this.recipe.getIngredients().get(index);
            return IVariable.from(ingredient.getItems());
        }

        if (key.startsWith("pedestal")) {
            int index = Integer.parseInt(key.substring("pedestal".length())) - 1;
            if (index >= this.recipe.getIngredients().size())
                return IVariable.empty();

            return IVariable.from(this.pedestal);
        }

        if (key.equals("output")) {
            if (this.recipe.getResultItem().getItem() != ModItems.JEI_DUMMY_NONE.get()) {
                return IVariable.from(this.recipe.getResultItem());
            } else {
                return IVariable.from(new ItemStack(ModItems.JEI_DUMMY_NONE.get()));
            }
        }

        if (key.equals("entity_to_summon")) {
            if (this.recipe.getEntityToSummon() != null) {
                return IVariable.wrap(I18n.get("jei.goety.summon", I18n.get(this.recipe.getEntityToSummon().getDescriptionId())));
            }
        }

        if (key.equals("entity_to_sacrifice")) {
            if (this.recipe.requiresSacrifice()) {
                return IVariable.wrap(I18n.get("jei.goety.sacrifice", I18n.get(this.recipe.getEntityToSacrificeDisplayName())));
            }
        }

        if (key.startsWith("soulCost")) {
            return IVariable.wrap(I18n.get("jei.goety.soulCost", this.recipe.getSoulCost()));
        }

        if (key.startsWith("duration")) {
            return IVariable.wrap(I18n.get("jei.goety.duration", this.recipe.getDuration()));
        }

        return IVariable.empty();
    }
}
