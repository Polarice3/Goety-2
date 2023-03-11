package com.Polarice3.Goety.common.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nullable;

public class SoulAbsorberRecipeSerializer <T extends SoulAbsorberRecipes>  implements RecipeSerializer<T> {
    private final int defaultSoulIncrease;
    private final int defaultCookingTime;
    private final IFactory<T> factory;

    public SoulAbsorberRecipeSerializer(IFactory<T> pFactory, int pDefaultSoulIncrease, int pDefaultCookingTime) {
        this.defaultSoulIncrease = pDefaultSoulIncrease;
        this.defaultCookingTime = pDefaultCookingTime;
        this.factory = pFactory;
    }

    @Override
    public T fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
        JsonElement jsonelement = GsonHelper.isArrayNode(pJson, "ingredient") ? GsonHelper.getAsJsonArray(pJson, "ingredient") : GsonHelper.getAsJsonObject(pJson, "ingredient");
        Ingredient ingredient = Ingredient.fromJson(jsonelement);
        int s = GsonHelper.getAsInt(pJson, "soulIncrease", this.defaultSoulIncrease);
        int i = GsonHelper.getAsInt(pJson, "cookingtime", this.defaultCookingTime);
        return this.factory.create(pRecipeId, ingredient, s, i);
    }

    @Nullable
    @Override
    public T fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
        Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
        int s = pBuffer.readVarInt();
        int i = pBuffer.readVarInt();
        return this.factory.create(pRecipeId, ingredient, i, s);
    }

    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, T pRecipe) {
        pRecipe.ingredient.toNetwork(pBuffer);
        pBuffer.writeVarInt(pRecipe.soulIncrease);
        pBuffer.writeVarInt(pRecipe.cookingTime);
    }

    interface IFactory<T extends SoulAbsorberRecipes> {
        T create(ResourceLocation p_create_1_, Ingredient p_create_3_, int p_create_4_, int p_create_5_);
    }
}
