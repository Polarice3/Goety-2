package com.Polarice3.Goety.common.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.List;

public class CauldronRecipe implements Recipe<Container> {
    public static Serializer SERIALIZER = new Serializer();
    public final ResourceLocation id;
    public final ItemStack result;
    public final NonNullList<Ingredient> ingredients;
    public final Ingredient takeWith;
    public final int levelLeft;
    public final int soulCost;
    public final int color;

    public CauldronRecipe(ResourceLocation p_44246_, ItemStack p_44248_, NonNullList<Ingredient> p_44249_, Ingredient takeWith, int levelLeft, int soulCost, int color) {
        this.id = p_44246_;
        this.result = p_44248_;
        this.ingredients = p_44249_;
        this.takeWith = takeWith;
        this.levelLeft = Mth.clamp(levelLeft, 1, 3);
        this.soulCost = soulCost;
        this.color = color;
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
    public ItemStack assemble(Container p_44001_, RegistryAccess pAccess) {
        return this.result.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pAccess) {
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
        return ModRecipeSerializer.CAULDRON_TYPE.get();
    }

    public Ingredient getTakeWith() {
        return this.takeWith;
    }

    public int getLevelLeft() {
        return this.levelLeft;
    }

    public int getSoulCost() {
        return this.soulCost;
    }

    public int getColor() {
        return this.color;
    }

    public boolean couldMatch(Container container, Level level) {
        List<Ingredient> remainingIngredients = Lists.newArrayList(this.ingredients);

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemStack = container.getItem(i);
            if (itemStack.isEmpty()) {
                continue;
            }

            int index = -1;
            for (int j = 0; j < remainingIngredients.size(); j++) {
                if (remainingIngredients.get(j).test(itemStack)) {
                    index = j;
                    break;
                }
            }
            if (index == -1) {
                return false;
            }
            remainingIngredients.remove(index);
        }
        return true;
    }

    public static class Serializer implements RecipeSerializer<CauldronRecipe> {
        @Override
        public CauldronRecipe fromJson(ResourceLocation recipeId, JsonObject json) {

            NonNullList<Ingredient> ingredients = itemsFromJson(GsonHelper.getAsJsonArray(json, "ingredients"));
            if (ingredients.isEmpty()) {
                throw new JsonParseException("No ingredients for Cauldron recipe");
            }
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

            JsonElement jsonelement = GsonHelper.isArrayNode(json, "take_with") ? GsonHelper.getAsJsonArray(json, "take_with") : GsonHelper.getAsJsonObject(json, "take_with");
            Ingredient takeWith = Ingredient.fromJson(jsonelement);
            if (takeWith.isEmpty()) {
                takeWith = Ingredient.EMPTY;
            }

            int levelLeft = GsonHelper.getAsInt(json, "levelLeft", 3);

            int soulCost = GsonHelper.getAsInt(json, "soulCost", 0);

            int color = GsonHelper.getAsInt(json, "color", 0x3F76E4);

            return new CauldronRecipe(recipeId, result, ingredients, takeWith, levelLeft, soulCost, color);
        }

        public static NonNullList<Ingredient> itemsFromJson(JsonArray pIngredientArray) {
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
        public CauldronRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int i = buffer.readVarInt();
            NonNullList<Ingredient> ingredients1 = NonNullList.withSize(i, Ingredient.EMPTY);

            for(int j = 0; j < ingredients1.size(); ++j) {
                ingredients1.set(j, Ingredient.fromNetwork(buffer));
            }

            ItemStack result = buffer.readItem();

            Ingredient takeWith = Ingredient.fromNetwork(buffer);

            int levelLeft = buffer.readVarInt();

            int soulCost = buffer.readVarInt();

            int color = buffer.readVarInt();

            return new CauldronRecipe(recipeId, result, ingredients1, takeWith, levelLeft, soulCost, color);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CauldronRecipe recipe) {
            buffer.writeVarInt(recipe.ingredients.size());

            for(Ingredient ingredient : recipe.ingredients) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItem(recipe.result);
            recipe.takeWith.toNetwork(buffer);
            buffer.writeVarInt(recipe.levelLeft);
            buffer.writeVarInt(recipe.soulCost);
            buffer.writeVarInt(recipe.color);
        }
    }
}
