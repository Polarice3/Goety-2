package com.Polarice3.Goety.common.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CauldronSusStewRecipe extends CauldronRecipe {
    public static CauldronSusStewRecipe.Serializer SERIALIZER = new CauldronSusStewRecipe.Serializer();

    public CauldronSusStewRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, Ingredient takeWith, int levelLeft, int soulCost, int color) {
        super(id, new ItemStack(Items.SUSPICIOUS_STEW), ingredients, takeWith, levelLeft, soulCost, color);
    }

    private static List<FlowerBlock> flowerCache = null;

    public static List<FlowerBlock> getFlowers() {
        if (flowerCache == null) {
            flowerCache = ForgeRegistries.BLOCKS.getValues().stream()
                    .filter(b -> b instanceof FlowerBlock)
                    .map(b -> (FlowerBlock) b)
                    .toList();
        }
        return flowerCache;
    }

    public static void invalidateFlowerCache() {
        flowerCache = null;
    }

    @Nullable
    private static FlowerBlock findFlower(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return null;
        }
        return blockItem.getBlock() instanceof FlowerBlock flower ? flower : null;
    }

    @Nullable
    private static FlowerBlock findFlowerIn(Container container) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            FlowerBlock flower = findFlower(container.getItem(i));
            if (flower != null) return flower;
        }
        return null;
    }

    private static Container withoutFlowers(Container container) {
        SimpleContainer filtered = new SimpleContainer(container.getContainerSize());
        int slot = 0;
        boolean flowerSkipped = false;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!flowerSkipped && findFlower(stack) != null) {
                flowerSkipped = true; // consume exactly one flower
                continue;
            }
            filtered.setItem(slot++, stack);
        }
        return filtered;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return findFlowerIn(container) != null && super.matches(withoutFlowers(container), level);
    }

    @Override
    public boolean couldMatch(Container container, Level level) {
        return super.couldMatch(withoutFlowers(container), level);
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess access) {
        ItemStack stew = new ItemStack(Items.SUSPICIOUS_STEW);
        FlowerBlock flower = findFlowerIn(container);
        if (flower != null) {
            SuspiciousStewItem.saveMobEffect(stew, flower.getSuspiciousEffect(), flower.getEffectDuration());
        }
        return stew;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> display = NonNullList.create();
        display.addAll(this.ingredients);
        ItemStack[] flowerStacks = getFlowers().stream()
                .map(ItemStack::new)
                .toArray(ItemStack[]::new);
        if (flowerStacks.length > 0) {
            display.add(Ingredient.of(flowerStacks));
        }
        return display;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer implements RecipeSerializer<CauldronSusStewRecipe> {
        @Override
        public CauldronSusStewRecipe fromJson(ResourceLocation id, JsonObject json) {
            NonNullList<Ingredient> ingredients = CauldronRecipe.Serializer.itemsFromJson(GsonHelper.getAsJsonArray(json, "ingredients"));
            JsonElement el = GsonHelper.isArrayNode(json, "take_with")
                    ? GsonHelper.getAsJsonArray(json, "take_with")
                    : GsonHelper.getAsJsonObject(json, "take_with");
            Ingredient takeWith = Ingredient.fromJson(el);
            int levelLeft = GsonHelper.getAsInt(json, "levelLeft", 3);
            int soulCost = GsonHelper.getAsInt(json, "soulCost", 0);
            int color = GsonHelper.getAsInt(json, "color", 0xC28340);
            return new CauldronSusStewRecipe(id, ingredients, takeWith, levelLeft, soulCost, color);
        }

        @Override
        public CauldronSusStewRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int i = buffer.readVarInt();
            NonNullList<Ingredient> ingredients1 = NonNullList.withSize(i, Ingredient.EMPTY);

            for(int j = 0; j < ingredients1.size(); ++j) {
                ingredients1.set(j, Ingredient.fromNetwork(buffer));
            }

            Ingredient takeWith = Ingredient.fromNetwork(buffer);

            int levelLeft = buffer.readVarInt();

            int soulCost = buffer.readVarInt();

            int color = buffer.readVarInt();

            return new CauldronSusStewRecipe(recipeId, ingredients1, takeWith, levelLeft, soulCost, color);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CauldronSusStewRecipe recipe) {
            buffer.writeVarInt(recipe.ingredients.size());

            for(Ingredient ingredient : recipe.ingredients) {
                ingredient.toNetwork(buffer);
            }

            recipe.takeWith.toNetwork(buffer);
            buffer.writeVarInt(recipe.levelLeft);
            buffer.writeVarInt(recipe.soulCost);
            buffer.writeVarInt(recipe.color);
        }
    }
}
