package com.Polarice3.Goety.common.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

public class PulverizeRecipe implements Recipe<Container> {
    public static Serializer SERIALIZER = new Serializer();
    protected final ResourceLocation id;
    public final Ingredient ingredient;
    protected final ItemStack itemResult;
    protected final Block blockResult;

    public PulverizeRecipe(ResourceLocation pId, Ingredient pIngredient, ItemStack pResult, Block pBlock) {
        this.id = pId;
        this.ingredient = pIngredient;
        this.itemResult = pResult;
        this.blockResult = pBlock;
    }

    @Override
    public boolean matches(Container pInv, Level pLevel) {
        return this.ingredient.test(pInv.getItem(0));
    }

    @Override
    public ItemStack assemble(Container p_44001_, RegistryAccess p_267165_) {
        return this.itemResult.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return this.itemResult;
    }

    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.add(this.ingredient);
        return nonnulllist;
    }

    public Block getBlockResult(){
        return this.blockResult;
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
        return ModRecipeSerializer.PULVERIZE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<PulverizeRecipe>{

        public PulverizeRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
            JsonElement jsonelement = GsonHelper.isArrayNode(pJson, "ingredient") ? GsonHelper.getAsJsonArray(pJson, "ingredient") : GsonHelper.getAsJsonObject(pJson, "ingredient");
            Ingredient ingredient = Ingredient.fromJson(jsonelement);
            ItemStack itemstack = ItemStack.EMPTY;
            if (pJson.has("item_result")) {
                if (pJson.get("item_result").isJsonObject()) {
                    itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pJson, "item_result"));
                } else {
                    String s1 = GsonHelper.getAsString(pJson, "item_result");
                    ResourceLocation resourcelocation = new ResourceLocation(s1);
                    itemstack = new ItemStack(ForgeRegistries.ITEMS.getValue(resourcelocation));
                }
            }
            Block block = Blocks.CAVE_AIR;
            if (pJson.has("block_result")){
                String s1 = GsonHelper.getAsString(pJson, "block_result");
                ResourceLocation resourcelocation = new ResourceLocation(s1);
                block = ForgeRegistries.BLOCKS.getValue(resourcelocation);
            }

            return new PulverizeRecipe(pRecipeId, ingredient, itemstack, block);
        }

        public PulverizeRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
            ItemStack itemstack = pBuffer.readItem();
            Block block = ForgeRegistries.BLOCKS.getValue(pBuffer.readResourceLocation());
            return new PulverizeRecipe(pRecipeId, ingredient, itemstack, block);
        }

        public void toNetwork(FriendlyByteBuf pBuffer, PulverizeRecipe pRecipe) {
            pRecipe.ingredient.toNetwork(pBuffer);
            pBuffer.writeItem(pRecipe.itemResult);
            ResourceLocation resourceLocation = ForgeRegistries.BLOCKS.getKey(pRecipe.blockResult);
            if (resourceLocation != null) {
                pBuffer.writeResourceLocation(resourceLocation);
            }
        }
    }
}
