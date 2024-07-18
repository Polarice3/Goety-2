package com.Polarice3.Goety.common.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class CursedInfuserRecipeSerializer<T extends CursedInfuserRecipes> implements RecipeSerializer<T>{
    private final int defaultCookingTime;
    private final IFactory<T> factory;

    public CursedInfuserRecipeSerializer(IFactory<T> pFactory, int pDefaultCookingTime) {
        this.defaultCookingTime = pDefaultCookingTime;
        this.factory = pFactory;
    }

    public T fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
        String s = GsonHelper.getAsString(pJson, "group", "");
        JsonElement jsonelement = GsonHelper.isArrayNode(pJson, "ingredient") ? GsonHelper.getAsJsonArray(pJson, "ingredient") : GsonHelper.getAsJsonObject(pJson, "ingredient");
        Ingredient ingredient = Ingredient.fromJson(jsonelement);
        boolean grim = false;
        if (pJson.has("grim")){
            grim = GsonHelper.getAsBoolean(pJson, "grim");
        }
        if (!pJson.has("result")) {
            throw new com.google.gson.JsonSyntaxException("Missing result, expected to find a string or object");
        }
        ItemStack itemstack;
        if (pJson.get("result").isJsonObject()) {
            itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pJson, "result"));
        } else {
            String s1 = GsonHelper.getAsString(pJson, "result");
            ResourceLocation resourcelocation = new ResourceLocation(s1);
            itemstack = new ItemStack(Registry.ITEM.getOptional(resourcelocation).orElseThrow(() -> {
                return new IllegalStateException("Item: " + s1 + " does not exist");
            }));
        }
        int i = GsonHelper.getAsInt(pJson, "cookingTime", this.defaultCookingTime);
        return this.factory.create(pRecipeId, s, ingredient, itemstack, 0.0F, i, grim);
    }

    public T fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
        String s = pBuffer.readUtf(32767);
        Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
        ItemStack itemstack = pBuffer.readItem();
        int i = pBuffer.readVarInt();
        boolean grim = pBuffer.readBoolean();
        return this.factory.create(pRecipeId, s, ingredient, itemstack, 0.0F, i, grim);
    }

    public void toNetwork(FriendlyByteBuf pBuffer, T pRecipe) {
        pBuffer.writeUtf(pRecipe.group);
        pRecipe.ingredient.toNetwork(pBuffer);
        pBuffer.writeItem(pRecipe.result);
        pBuffer.writeVarInt(pRecipe.cookingTime);
        pBuffer.writeBoolean(pRecipe.grim);
    }

    interface IFactory<T extends CursedInfuserRecipes> {
        T create(ResourceLocation p_create_1_, String p_create_2_, Ingredient p_create_3_, ItemStack p_create_4_, float p_create_5_, int p_create_6_, boolean p_create_7_);
    }
}
