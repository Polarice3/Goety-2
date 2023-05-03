package com.Polarice3.Goety.common.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class ModShapelessRecipe implements Recipe<CraftingContainer> {
    private final ResourceLocation id;
    final String group;
    final ItemStack result;
    final NonNullList<Ingredient> ingredients;
    private final boolean isSimple;

    public ModShapelessRecipe(ResourceLocation p_44246_, String p_44247_, ItemStack p_44248_, NonNullList<Ingredient> p_44249_) {
        this.id = p_44246_;
        this.group = p_44247_;
        this.result = p_44248_;
        this.ingredients = p_44249_;
        this.isSimple = p_44249_.stream().allMatch(Ingredient::isSimple);
    }

    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializer.MODDED_SHAPELESS.get();
    }

    public String getGroup() {
        return this.group;
    }

    public ItemStack getResultItem() {
        return this.result;
    }

    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public boolean matches(CraftingContainer p_44262_, Level p_44263_) {
        StackedContents stackedcontents = new StackedContents();
        java.util.List<ItemStack> inputs = new java.util.ArrayList<>();
        int i = 0;

        for(int j = 0; j < p_44262_.getContainerSize(); ++j) {
            ItemStack itemstack = p_44262_.getItem(j);
            if (!itemstack.isEmpty()) {
                ++i;
                if (isSimple)
                    stackedcontents.accountStack(itemstack, 1);
                else inputs.add(itemstack);
            }
        }

        return i == this.ingredients.size() && (isSimple ? stackedcontents.canCraft(this, (IntList)null) : net.minecraftforge.common.util.RecipeMatcher.findMatches(inputs,  this.ingredients) != null);
    }

    public ItemStack assemble(CraftingContainer p_44260_) {
        return this.result.copy();
    }

    public boolean canCraftInDimensions(int p_44252_, int p_44253_) {
        return p_44252_ * p_44253_ >= this.ingredients.size();
    }

    public static class Serializer implements RecipeSerializer<ModShapelessRecipe> {
        private static final ResourceLocation NAME = new ResourceLocation("minecraft", "crafting_shapeless");
        public ModShapelessRecipe fromJson(ResourceLocation p_44290_, JsonObject p_44291_) {
            String s = GsonHelper.getAsString(p_44291_, "group", "");
            NonNullList<Ingredient> nonnulllist = itemsFromJson(GsonHelper.getAsJsonArray(p_44291_, "ingredients"));
            if (nonnulllist.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            } else if (nonnulllist.size() > 3 * 3) {
                throw new JsonParseException("Too many ingredients for shapeless recipe. The maximum is " + (3 * 3));
            } else {
                ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(p_44291_, "result"));
                return new ModShapelessRecipe(p_44290_, s, itemstack, nonnulllist);
            }
        }

        private static NonNullList<Ingredient> itemsFromJson(JsonArray p_44276_) {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();

            for(int i = 0; i < p_44276_.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(p_44276_.get(i));
                if (true || !ingredient.isEmpty()) { // FORGE: Skip checking if an ingredient is empty during shapeless recipe deserialization to prevent complex ingredients from caching tags too early. Can not be done using a config value due to sync issues.
                    nonnulllist.add(ingredient);
                }
            }

            return nonnulllist;
        }

        public ModShapelessRecipe fromNetwork(ResourceLocation p_44293_, FriendlyByteBuf p_44294_) {
            String s = p_44294_.readUtf();
            int i = p_44294_.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

            for(int j = 0; j < nonnulllist.size(); ++j) {
                nonnulllist.set(j, Ingredient.fromNetwork(p_44294_));
            }

            ItemStack itemstack = p_44294_.readItem();
            return new ModShapelessRecipe(p_44293_, s, itemstack, nonnulllist);
        }

        public void toNetwork(FriendlyByteBuf p_44281_, ModShapelessRecipe p_44282_) {
            p_44281_.writeUtf(p_44282_.group);
            p_44281_.writeVarInt(p_44282_.ingredients.size());

            for(Ingredient ingredient : p_44282_.ingredients) {
                ingredient.toNetwork(p_44281_);
            }

            p_44281_.writeItem(p_44282_.result);
        }
    }
}
