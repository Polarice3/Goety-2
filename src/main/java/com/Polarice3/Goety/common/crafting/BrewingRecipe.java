package com.Polarice3.Goety.common.crafting;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class BrewingRecipe implements Recipe<Container> {
    public static Serializer SERIALIZER = new Serializer();
    private final ResourceLocation id;
    public final Ingredient input;
    private final TagKey<EntityType<?>> entityTypeTag;
    private final EntityType<?> entityType;
    public final MobEffect output;
    public final int soulCost;
    public final int capacityExtra;
    public final int duration;

    public BrewingRecipe(ResourceLocation location,
                         Ingredient ingredient,
                         @Nullable TagKey<EntityType<?>> entityTypeTag,
                         @Nullable EntityType<?> entityType, MobEffect mobEffect, int soulCost, int capacityExtra, int duration) {
        this.id = location;
        this.input = ingredient;
        this.entityTypeTag = entityTypeTag;
        this.entityType = entityType;
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
    public ItemStack assemble(Container p_44001_, RegistryAccess p_267052_) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return false;
    }

    public Ingredient getInput(){
        return this.input;
    }

    @Nullable
    public TagKey<EntityType<?>> getEntityTypeTag() {
        return entityTypeTag;
    }

    @Nullable
    public EntityType<?> getEntityType(){
        return this.entityType;
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
    public ItemStack getResultItem(RegistryAccess p_267052_) {
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
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
            EntityType<?> entityType = null;
            TagKey<EntityType<?>> entityTag = null;
            JsonObject data2 = json.getAsJsonObject("entity");
            if (data2 != null) {
                if (data2.has("entity_type")) {
                    ResourceLocation resourceLocation = new ResourceLocation(data2.getAsJsonPrimitive("entity_type").getAsString());
                    entityType = ForgeRegistries.ENTITY_TYPES.getValue(resourceLocation);
                } else if (data2.has("tag")) {
                    ResourceLocation resourceLocation = new ResourceLocation(data2.getAsJsonPrimitive("tag").getAsString());
                    entityTag = TagKey.create(ForgeRegistries.ENTITY_TYPES.getRegistryKey(), resourceLocation);
                }
            }
            return new BrewingRecipe(id,
                    ingredient,
                    entityTag,
                    entityType,
                    ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(GsonHelper.getAsString(json, "effect"))),
                    GsonHelper.getAsInt(json, "soulCost"),
                    GsonHelper.getAsInt(json, "capacityExtra"),
                    GsonHelper.getAsInt(json, "duration"));
        }

        @Override
        public BrewingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            TagKey<EntityType<?>> entityTag = null;
            EntityType<?> entityType = null;

            if (buf.readBoolean()) {
                var tagRL = buf.readResourceLocation();
                entityTag = TagKey.create(Registries.ENTITY_TYPE, tagRL);
            }

            if (buf.readBoolean()){
                entityType = buf.readRegistryId();
            }
            return new BrewingRecipe(id,
                    Ingredient.fromNetwork(buf),
                    entityTag,
                    entityType,
                    ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(buf.readUtf())),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, BrewingRecipe recipe) {
            recipe.input.toNetwork(buf);
            buf.writeBoolean(recipe.entityTypeTag != null);
            if (recipe.entityTypeTag != null) {
                buf.writeResourceLocation(recipe.entityTypeTag.location());
            }
            buf.writeBoolean(recipe.entityType != null);
            if (recipe.entityType != null) {
                buf.writeRegistryId(ForgeRegistries.ENTITY_TYPES, recipe.entityType);
            }
            buf.writeUtf(ForgeRegistries.MOB_EFFECTS.getKey(recipe.output).toString());
            buf.writeInt(recipe.soulCost);
            buf.writeInt(recipe.capacityExtra);
            buf.writeInt(recipe.duration);
        }
    }
}
