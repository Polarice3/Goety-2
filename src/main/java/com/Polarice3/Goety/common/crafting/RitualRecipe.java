package com.Polarice3.Goety.common.crafting;

import com.Polarice3.Goety.common.ritual.ModRituals;
import com.Polarice3.Goety.common.ritual.Ritual;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class RitualRecipe extends ModShapelessRecipe {
    public static Serializer SERIALIZER = new Serializer();

    private final ResourceLocation ritualType;
    private final Ritual ritual;
    private final String craftType;
    private final int soulCost;
    private final Ingredient activationItem;
    private final TagKey<EntityType<?>> entityToSacrifice;
    private final TagKey<EntityType<?>> entityToConvert;
    private final EntityType<?> entityToSummon;
    private final EntityType<?> entityToConvertInto;
    private final int duration;
    private final int summonLife;
    private final float durationPerIngredient;
    private final String entityToSacrificeDisplayName;
    private final String entityToConvertDisplayName;
    private final String research;

    public RitualRecipe(ResourceLocation id, String group, String pCraftType, ResourceLocation ritualType,
                        ItemStack result, EntityType<?> entityToSummon, EntityType<?> entityToConvertInto, Ingredient activationItem, NonNullList<Ingredient> input, int duration, int summonLife, int pSoulCost,
                        TagKey<EntityType<?>> entityToSacrifice, String entityToSacrificeDisplayName,
                        TagKey<EntityType<?>> entityToConvert, String entityToConvertDisplayName, String research) {
        super(id, group, CraftingBookCategory.MISC, result, input);
        this.craftType = pCraftType;
        this.soulCost = pSoulCost;
        this.entityToSummon = entityToSummon;
        this.entityToConvertInto = entityToConvertInto;
        this.ritualType = ritualType;
        this.ritual = ModRituals.REGISTRY.get().getValue(this.ritualType).create(this);
        this.activationItem = activationItem;
        this.duration = duration;
        this.summonLife = summonLife;
        this.durationPerIngredient = this.duration / (float) (this.getIngredients().size() + 1);
        this.entityToSacrifice = entityToSacrifice;
        this.entityToSacrificeDisplayName = entityToSacrificeDisplayName;
        this.entityToConvert = entityToConvert;
        this.entityToConvertDisplayName = entityToConvertDisplayName;
        this.research = research;
    }

    public String getCraftType() {
        return this.craftType;
    }

    public int getSoulCost() {
        return this.soulCost;
    }

    public Ingredient getActivationItem() {
        return this.activationItem;
    }

    public int getDuration() {
        return this.duration;
    }

    public float getDurationPerIngredient() {
        return this.durationPerIngredient;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public boolean matches(Level world, BlockPos darkAltarPos, Player player, ItemStack activationItem) {
        return this.ritual.identify(world, darkAltarPos, player, activationItem);
    }

    @Override
    public boolean matches(@Nonnull CraftingContainer inventory, @Nonnull Level world) {
        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer inventoryCrafting, RegistryAccess pAccess) {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeSerializer.RITUAL_TYPE.get();
    }

    public TagKey<EntityType<?>> getEntityToSacrifice() {
        return this.entityToSacrifice;
    }

    public boolean requiresSacrifice() {
        return this.entityToSacrifice != null;
    }

    public EntityType<?> getEntityToSummon() {
        return this.entityToSummon;
    }

    public EntityType<?> getEntityToConvertInto() {
        return this.entityToConvertInto;
    }

    public TagKey<EntityType<?>> getEntityToConvert() {
        return this.entityToConvert;
    }

    public boolean isConversion(){
        return this.entityToConvert != null && this.entityToConvertInto != null;
    }

    public ResourceLocation getRitualType() {
        return this.ritualType;
    }

    public Ritual getRitual() {
        return this.ritual;
    }

    public String getEntityToSacrificeDisplayName() {
        return this.entityToSacrificeDisplayName;
    }

    public String getEntityToConvertDisplayName() {
        return this.entityToConvertDisplayName;
    }

    public String getResearch(){
        return this.research;
    }

    public int getSummonLife() {
        return this.summonLife;
    }

    public static class Serializer implements RecipeSerializer<RitualRecipe> {
        private static final ModShapelessRecipe.Serializer serializer = new ModShapelessRecipe.Serializer();

        @Override
        public RitualRecipe fromJson(ResourceLocation recipeId, JsonObject json) {

            String group = GsonHelper.getAsString(json, "group", "");
            String craftType = GsonHelper.getAsString(json, "craftType", "");
            NonNullList<Ingredient> ingredients = itemsFromJson(GsonHelper.getAsJsonArray(json, "ingredients"));
            if (ingredients.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            }
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

            ResourceLocation ritualType = new ResourceLocation(json.get("ritual_type").getAsString());

            EntityType<?> entityToSummon = null;
            if (json.has("entity_to_summon")) {
                entityToSummon = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(GsonHelper.getAsString(json, "entity_to_summon")));
            }

            JsonElement activationItemElement =
                    GsonHelper.isArrayNode(json, "activation_item") ? GsonHelper.getAsJsonArray(json,
                            "activation_item") : GsonHelper.getAsJsonObject(json, "activation_item");
            Ingredient activationItem = Ingredient.fromJson(activationItemElement);

            int duration = GsonHelper.getAsInt(json, "duration", 30);
            int summonLife = GsonHelper.getAsInt(json, "summonLife", -1);
            int soulCost = GsonHelper.getAsInt(json, "soulCost", 0);

            TagKey<EntityType<?>> entityToSacrifice = null;
            String entityToSacrificeDisplayName = "";
            if (json.has("entity_to_sacrifice")) {
                var tagRL = new ResourceLocation(GsonHelper.getAsString(json.getAsJsonObject("entity_to_sacrifice"), "tag"));
                entityToSacrifice = TagKey.create(Registries.ENTITY_TYPE, tagRL);

                entityToSacrificeDisplayName = json.getAsJsonObject("entity_to_sacrifice").get("display_name").getAsString();
            }

            EntityType<?> entityToConvertInto = null;
            TagKey<EntityType<?>> entityToConvert = null;
            String entityToConvertDisplayName = "";
            String research = "";
            if (json.has("entity_to_convert")){
                var tagRL = new ResourceLocation(GsonHelper.getAsString(json.getAsJsonObject("entity_to_convert"), "tag"));
                entityToConvert = TagKey.create(Registries.ENTITY_TYPE, tagRL);

                entityToConvertDisplayName = json.getAsJsonObject("entity_to_convert").get("display_name").getAsString();
            }
            if (json.has("entity_to_convert_into")) {
                entityToConvertInto = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(GsonHelper.getAsString(json, "entity_to_convert_into")));
            }
            if (json.has("research")){
                research = GsonHelper.getAsString(json, "research", "");
            }
            return new RitualRecipe(recipeId, group, craftType, ritualType,
                    result, entityToSummon, entityToConvertInto, activationItem, ingredients, duration,
                    summonLife, soulCost, entityToSacrifice, entityToSacrificeDisplayName,
                    entityToConvert, entityToConvertDisplayName, research);
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
        public RitualRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            ModShapelessRecipe recipe = serializer.fromNetwork(recipeId, buffer);
            String craftType = buffer.readUtf(32767);

            ResourceLocation ritualType = buffer.readResourceLocation();

            EntityType<?> entityToSummon = null;
            if (buffer.readBoolean()) {
                entityToSummon = buffer.readRegistryId();
            }

            int duration = buffer.readVarInt();
            int summonLife = buffer.readVarInt();
            int soulCost = buffer.readVarInt();

            Ingredient activationItem = Ingredient.fromNetwork(buffer);

            TagKey<EntityType<?>> entityToSacrifice = null;
            String entityToSacrificeDisplayName = "";
            if (buffer.readBoolean()) {
                var tagRL = buffer.readResourceLocation();
                entityToSacrifice = TagKey.create(Registries.ENTITY_TYPE, tagRL);
                entityToSacrificeDisplayName = buffer.readUtf();
            }

            EntityType<?> entityToConvertInto = null;
            TagKey<EntityType<?>> entityToConvert = null;
            String entityToConvertDisplayName = "";
            if (buffer.readBoolean()) {
                var tagRL = buffer.readResourceLocation();
                entityToConvert = TagKey.create(Registries.ENTITY_TYPE, tagRL);
                entityToConvertDisplayName = buffer.readUtf();
            }

            if (buffer.readBoolean()){
                entityToConvertInto = buffer.readRegistryId();
            }
            String research = "";
            if (buffer.readBoolean()){
                research = buffer.readUtf(32767);
            }

            assert recipe != null;
            return new RitualRecipe(recipe.getId(), recipe.getGroup(), craftType, ritualType, recipe.getResultItem(null), entityToSummon, entityToConvertInto,
                    activationItem, recipe.getIngredients(), duration, summonLife, soulCost, entityToSacrifice, entityToSacrificeDisplayName, entityToConvert, entityToConvertDisplayName, research);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, RitualRecipe recipe) {
            serializer.toNetwork(buffer, recipe);
            buffer.writeUtf(recipe.craftType);

            buffer.writeResourceLocation(recipe.ritualType);

            buffer.writeBoolean(recipe.entityToSummon != null);
            if (recipe.entityToSummon != null) {
                buffer.writeRegistryId(ForgeRegistries.ENTITY_TYPES, recipe.entityToSummon);
            }

            buffer.writeVarInt(recipe.duration);
            buffer.writeVarInt(recipe.summonLife);
            buffer.writeVarInt(recipe.soulCost);
            recipe.activationItem.toNetwork(buffer);
            buffer.writeBoolean(recipe.entityToSacrifice != null);
            if (recipe.entityToSacrifice != null) {
                buffer.writeResourceLocation(recipe.entityToSacrifice.location());
                buffer.writeUtf(recipe.entityToSacrificeDisplayName);
            }
            buffer.writeBoolean(recipe.entityToConvert != null);
            if (recipe.entityToConvert != null){
                buffer.writeResourceLocation(recipe.entityToConvert.location());
                buffer.writeUtf(recipe.entityToConvertDisplayName);
            }
            buffer.writeBoolean(recipe.entityToConvertInto != null);
            if (recipe.entityToConvertInto != null) {
                buffer.writeRegistryId(ForgeRegistries.ENTITY_TYPES, recipe.entityToConvertInto);
            }
            buffer.writeBoolean(recipe.research != null);
            if (recipe.research != null) {
                buffer.writeUtf(recipe.research);
            }
        }
    }
}
/*
 * MIT License
 *
 * Copyright 2020 klikli-dev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */