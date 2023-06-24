package com.Polarice3.Goety.common.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class BrewingRecipe implements Recipe<Container> {
    public static BrewingRecipe.Serializer SERIALIZER = new BrewingRecipe.Serializer();
    private final ResourceLocation id;
    public final Ingredient input;
    public final MobEffect output;
    public final int soulCost;
    public final int capacityExtra;
    public final int duration;

    public BrewingRecipe(ResourceLocation location, Ingredient ingredient, MobEffect mobEffect, int soulCost, int capacityExtra, int duration) {
        this.id = location;
        this.input = ingredient;
        this.output = mobEffect;
        this.soulCost = soulCost;
        this.capacityExtra = capacityExtra;
        this.duration = duration;
    }

    @Override
    public boolean matches(Container p_44002_, Level p_44003_) {
        return false;
    }

    @Override
    public ItemStack assemble(Container p_44001_) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return false;
    }

    public Ingredient getInput(){
        return this.input;
    }

    public int getSoulCost(){
        return this.soulCost;
    }

    public int getCapacityExtra(){
        return this.capacityExtra;
    }

    public int getDuration(){
        return this.duration;
    }

    public MobEffect getOutput(){
        return this.output;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeSerializer.BREWING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<BrewingRecipe> {
        @Override
        public BrewingRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new BrewingRecipe(id, Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient")),
                    ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(GsonHelper.getAsString(json, "effect"))),
                    GsonHelper.getAsInt(json, "soulCost"),
                    GsonHelper.getAsInt(json, "capacityExtra"),
                    GsonHelper.getAsInt(json, "duration"));
        }

        @Override
        public BrewingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            return new BrewingRecipe(id, Ingredient.fromNetwork(buf), ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(buf.readUtf())), buf.readInt(), buf.readInt(), buf.readInt());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, BrewingRecipe recipe) {
            recipe.input.toNetwork(buf);
            buf.writeUtf(ForgeRegistries.MOB_EFFECTS.getKey(recipe.output).toString());
            buf.writeInt(recipe.soulCost);
            buf.writeInt(recipe.capacityExtra);
            buf.writeInt(recipe.duration);
        }
    }
}
