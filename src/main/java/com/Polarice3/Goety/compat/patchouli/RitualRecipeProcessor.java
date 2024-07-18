package com.Polarice3.Goety.compat.patchouli;

import com.Polarice3.Goety.common.crafting.RitualRecipe;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.ritual.EnchantItemRitual;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.ArrayList;
import java.util.List;

public class RitualRecipeProcessor implements IComponentProcessor {

    protected RitualRecipe recipe;
    protected ItemStack pedestal;

    @Override
    public void setup(Level level, IVariableProvider iVariableProvider) {
        String recipeId = iVariableProvider.get("recipe").asString();
        this.recipe = (RitualRecipe) Minecraft.getInstance().level.getRecipeManager()
                .byKey(new ResourceLocation(recipeId)).orElse(null);
        this.pedestal = new ItemStack(ModItems.PEDESTAL_DUMMY.get());
    }

    @Override
    public IVariable process(Level level, String key) {
        if (this.recipe == null) {
            return IVariable.empty();
        }

        if (key.startsWith("activation_item")) {
            if (this.recipe.getRitual() instanceof EnchantItemRitual){
                return IVariable.from(Ingredient.of(Items.BOOK));
            } else {
                return IVariable.from(this.recipe.getActivationItem().getItems());
            }
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

        if (key.startsWith("enchantment")) {
            if (this.recipe.getEnchantment() != null) {
                return IVariable.wrap(I18n.get("jei.goety.enchantment", I18n.get(this.recipe.getEnchantment().getDescriptionId())));
            }
        }

        if (key.equals("output")) {
            if (this.recipe.getRitual() instanceof EnchantItemRitual && this.recipe.getEnchantment() != null){
                List<ItemStack> results = new ArrayList<>();
                for (int i = 1; i <= recipe.getEnchantment().getMaxLevel(); ++i){
                    EnchantmentInstance enchantmentInstance = new EnchantmentInstance(recipe.getEnchantment(), i);
                    results.add(EnchantedBookItem.createForEnchantment(enchantmentInstance));
                }
                List<IVariable> variables = new ArrayList<>();
                for (ItemStack itemStack : results){
                    variables.add(IVariable.from(itemStack));
                }
                return IVariable.wrapList(variables);
            } else if (this.recipe.getResultItem(level.registryAccess()).getItem() != ModItems.JEI_DUMMY_NONE.get()) {
                return IVariable.from(this.recipe.getResultItem(level.registryAccess()));
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

        if (key.equals("entity_to_convert")) {
            if (this.recipe.getEntityToConvert() != null) {
                return IVariable.wrap(I18n.get("jei.goety.convert", I18n.get(this.recipe.getEntityToConvertDisplayName())));
            }
        }

        if (key.equals("entity_to_convert_into")) {
            if (this.recipe.getEntityToConvertInto() != null) {
                return IVariable.wrap(I18n.get("jei.goety.convertInto", I18n.get(this.recipe.getEntityToConvertInto().getDescriptionId())));
            }
        }

        if (key.equals("xp_levels")) {
            if (this.recipe.getEnchantment() != null) {
                return IVariable.wrap(I18n.get("jei.goety.xp", this.recipe.getXPLevelCost()));
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
