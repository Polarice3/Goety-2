package com.Polarice3.Goety.common.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.List;

public class BrazierRecipe implements Recipe<Container> {
    public static BrazierRecipe.Serializer SERIALIZER = new BrazierRecipe.Serializer();
    private final ResourceLocation id;
    private final ItemStack result;
    private final NonNullList<Ingredient> ingredients;
    private final int soulCost;

    public BrazierRecipe(ResourceLocation p_44246_, ItemStack p_44248_, NonNullList<Ingredient> p_44249_, int soulCost) {
        this.id = p_44246_;
        this.result = p_44248_;
        this.ingredients = p_44249_;
        this.soulCost = soulCost;
    }

    /**
     * Based on Runic Altar Recipe code by @Vazkii
     */
    @Override
    public boolean matches(Container container, Level p_44003_) {
        List<Ingredient> missingIngredients = Lists.newArrayList(this.ingredients);

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemStack = container.getItem(i);
            if (itemStack.isEmpty()) {
                break;
            }

            int index = -1;

            for (int j = 0; j < missingIngredients.size(); j++) {
                Ingredient ingredient = missingIngredients.get(j);
                if (ingredient.test(itemStack)) {
                    index = j;
                    break;
                }
            }

            if (index == -1) {
                return false;
            } else {
                missingIngredients.remove(index);
            }
        }

        return missingIngredients.isEmpty();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public ItemStack assemble(Container p_44001_) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return false;
    }

    @Override
    public ItemStack getResultItem() {
        return this.result;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeSerializer.BRAZIER_TYPE.get();
    }

    public int getSoulCost() {
        return this.soulCost;
    }

    public static class Serializer implements RecipeSerializer<BrazierRecipe> {
        @Override
        public BrazierRecipe fromJson(ResourceLocation recipeId, JsonObject json) {

            NonNullList<Ingredient> ingredients = itemsFromJson(GsonHelper.getAsJsonArray(json, "ingredients"));
            if (ingredients.isEmpty()) {
                throw new JsonParseException("No ingredients for Brazier recipe");
            }
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

            int soulCost = GsonHelper.getAsInt(json, "soulCost", 0);

            return new BrazierRecipe(recipeId, result, ingredients, soulCost);
        }

        private static NonNullList<Ingredient> itemsFromJson(JsonArray pIngredientArray) {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();

            for(int i = 0; i < pIngredientArray.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(pIngredientArray.get(i));
                if (!ingredient.isEmpty()) {
                    nonnulllist.add(ingredient);
                }
            }

            return nonnulllist;
        }

        @Override
        public BrazierRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int i = buffer.readVarInt();
            NonNullList<Ingredient> ingredients1 = NonNullList.withSize(i, Ingredient.EMPTY);

            for(int j = 0; j < ingredients1.size(); ++j) {
                ingredients1.set(j, Ingredient.fromNetwork(buffer));
            }

            ItemStack result = buffer.readItem();

            int soulCost = buffer.readVarInt();

            return new BrazierRecipe(recipeId, result, ingredients1, soulCost);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, BrazierRecipe recipe) {
            buffer.writeVarInt(recipe.ingredients.size());

            for(Ingredient ingredient : recipe.ingredients) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItem(recipe.result);
            buffer.writeVarInt(recipe.soulCost);
        }
    }
}
