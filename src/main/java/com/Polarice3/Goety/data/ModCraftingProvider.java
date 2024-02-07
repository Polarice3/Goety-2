package com.Polarice3.Goety.data;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModCraftingProvider extends RecipeProvider {
    public ModCraftingProvider(PackOutput p_248933_) {
        super(p_248933_);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CRYPT_PILLAR_BLOCK.get(), 2)
                .pattern("#")
                .pattern("#")
                .define('#', ModBlocks.CRYPT_STONE_POLISHED_BLOCK.get())
                .unlockedBy("has_item", has(ModBlocks.CRYPT_STONE_POLISHED_BLOCK.get()))
                .save(consumer, loc("crypt/crypt_pillar"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CRYPT_STONE_CHISELED_BLOCK.get())
                .pattern("#")
                .pattern("#")
                .define('#', ModBlocks.CRYPT_STONE_POLISHED_SLAB_BLOCK.get())
                .unlockedBy("has_item", has(ModBlocks.CRYPT_STONE_POLISHED_SLAB_BLOCK.get()))
                .save(consumer, loc("crypt/crypt_stone_chiseled"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CRYPT_PLINTH_BLOCK.get())
                .pattern("#")
                .pattern("#")
                .define('#', ModBlocks.CRYPT_STONE_SLAB_BLOCK.get())
                .unlockedBy("has_item", has(ModBlocks.CRYPT_STONE_SLAB_BLOCK.get()))
                .save(consumer, loc("crypt/crypt_plinth"));

        smelting(consumer,"crypt/crypt_stone_polished", ModBlocks.CRYPT_STONE_BLOCK.get(), ModBlocks.CRYPT_STONE_POLISHED_BLOCK.get());

        twoByTwo(consumer, "crypt/crypt_bricks", ModBlocks.CRYPT_STONE_POLISHED_BLOCK, ModBlocks.CRYPT_BRICKS_BLOCK);
        twoByTwo(consumer, "crypt/crypt_tiles", ModBlocks.CRYPT_STONE_BLOCK, ModBlocks.CRYPT_TILES_BLOCK);

        slabBlock(consumer, "crypt/crypt_stone", ModBlocks.CRYPT_STONE_BLOCK, ModBlocks.CRYPT_STONE_SLAB_BLOCK);
        slabBlock(consumer, "crypt/crypt_stone_polished", ModBlocks.CRYPT_STONE_POLISHED_BLOCK, ModBlocks.CRYPT_STONE_POLISHED_SLAB_BLOCK);
        slabBlock(consumer, "crypt/crypt_bricks", ModBlocks.CRYPT_BRICKS_BLOCK, ModBlocks.CRYPT_BRICKS_SLAB_BLOCK);
        slabBlock(consumer, "crypt/crypt_tiles", ModBlocks.CRYPT_TILES_BLOCK, ModBlocks.CRYPT_TILES_SLAB_BLOCK);

        stairsBlock(consumer, "crypt/crypt_stone", ModBlocks.CRYPT_STONE_BLOCK, ModBlocks.CRYPT_STONE_STAIRS_BLOCK);
        stairsBlock(consumer, "crypt/crypt_stone_polished", ModBlocks.CRYPT_STONE_POLISHED_BLOCK, ModBlocks.CRYPT_STONE_POLISHED_STAIRS_BLOCK);
        stairsBlock(consumer, "crypt/crypt_bricks", ModBlocks.CRYPT_BRICKS_BLOCK, ModBlocks.CRYPT_BRICKS_STAIRS_BLOCK);
        stairsBlock(consumer, "crypt/crypt_tiles", ModBlocks.CRYPT_TILES_BLOCK, ModBlocks.CRYPT_TILES_STAIRS_BLOCK);

        wallBlock(consumer, "crypt/crypt_stone_polished", ModBlocks.CRYPT_STONE_POLISHED_BLOCK, ModBlocks.CRYPT_STONE_POLISHED_WALL_BLOCK);
        wallBlock(consumer, "crypt/crypt_bricks", ModBlocks.CRYPT_BRICKS_BLOCK, ModBlocks.CRYPT_BRICKS_WALL_BLOCK);
        wallBlock(consumer, "crypt/crypt_tiles", ModBlocks.CRYPT_TILES_BLOCK, ModBlocks.CRYPT_TILES_WALL_BLOCK);

        stoneCutting(consumer, "crypt", ModBlocks.CRYPT_STONE_BLOCK, ModBlocks.CRYPT_STONE_SLAB_BLOCK, 2);
        stoneCutting(consumer, "crypt", ModBlocks.CRYPT_STONE_BLOCK, ModBlocks.CRYPT_TILES_SLAB_BLOCK, 2);
        stoneCutting(consumer, "crypt", ModBlocks.CRYPT_STONE_POLISHED_BLOCK, ModBlocks.CRYPT_STONE_POLISHED_SLAB_BLOCK, 2);
        stoneCutting(consumer, "crypt", ModBlocks.CRYPT_STONE_POLISHED_BLOCK, ModBlocks.CRYPT_BRICKS_SLAB_BLOCK, 2);
        stoneCutting(consumer, "crypt", ModBlocks.CRYPT_BRICKS_BLOCK, ModBlocks.CRYPT_BRICKS_SLAB_BLOCK, 2);
        stoneCutting(consumer, "crypt", ModBlocks.CRYPT_TILES_BLOCK, ModBlocks.CRYPT_TILES_SLAB_BLOCK, 2);

        stoneCutting(consumer, "crypt", ModBlocks.CRYPT_STONE_BLOCK, ModBlocks.CRYPT_STONE_STAIRS_BLOCK);
        stoneCutting(consumer, "crypt", ModBlocks.CRYPT_STONE_BLOCK, ModBlocks.CRYPT_TILES_STAIRS_BLOCK);
        stoneCutting(consumer, "crypt", ModBlocks.CRYPT_STONE_POLISHED_BLOCK, ModBlocks.CRYPT_STONE_POLISHED_STAIRS_BLOCK);
        stoneCutting(consumer, "crypt", ModBlocks.CRYPT_STONE_POLISHED_BLOCK, ModBlocks.CRYPT_BRICKS_STAIRS_BLOCK);
        stoneCutting(consumer, "crypt", ModBlocks.CRYPT_BRICKS_BLOCK, ModBlocks.CRYPT_BRICKS_STAIRS_BLOCK);
        stoneCutting(consumer, "crypt", ModBlocks.CRYPT_TILES_BLOCK, ModBlocks.CRYPT_TILES_STAIRS_BLOCK);

        stoneCutting(consumer, "crypt", ModBlocks.CRYPT_STONE_BLOCK, ModBlocks.CRYPT_TILES_WALL_BLOCK);
        stoneCutting(consumer, "crypt", ModBlocks.CRYPT_STONE_POLISHED_BLOCK, ModBlocks.CRYPT_STONE_POLISHED_WALL_BLOCK);
        stoneCutting(consumer, "crypt", ModBlocks.CRYPT_STONE_POLISHED_BLOCK, ModBlocks.CRYPT_BRICKS_WALL_BLOCK);
        stoneCutting(consumer, "crypt", ModBlocks.CRYPT_BRICKS_BLOCK, ModBlocks.CRYPT_BRICKS_WALL_BLOCK);
        stoneCutting(consumer, "crypt", ModBlocks.CRYPT_TILES_BLOCK, ModBlocks.CRYPT_TILES_WALL_BLOCK);

        stoneCutting(consumer, "crypt", ModBlocks.CRYPT_STONE_BLOCK, ModBlocks.CRYPT_TILES_BLOCK);
        stoneCutting(consumer, "crypt", ModBlocks.CRYPT_STONE_BLOCK, ModBlocks.CRYPT_PLINTH_BLOCK);

        stoneCutting(consumer, "crypt", ModBlocks.CRYPT_STONE_POLISHED_BLOCK, ModBlocks.CRYPT_BRICKS_BLOCK);
        stoneCutting(consumer, "crypt", ModBlocks.CRYPT_STONE_POLISHED_BLOCK, ModBlocks.CRYPT_PILLAR_BLOCK);
        stoneCutting(consumer, "crypt", ModBlocks.CRYPT_STONE_POLISHED_BLOCK, ModBlocks.CRYPT_STONE_CHISELED_BLOCK);
    }

    protected final void twoByTwo(Consumer<FinishedRecipe> consumer, String name, Supplier<? extends Block> input, Supplier<? extends Block> output) {
        twoByTwo(consumer, name, input, output, 4);
    }

    protected final void twoByTwo(Consumer<FinishedRecipe> consumer, String name, Supplier<? extends Block> input, Supplier<? extends Block> output, int amount) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output.get(), amount)
                .pattern("##")
                .pattern("##")
                .define('#', input.get())
                .unlockedBy("has_item", has(input.get()))
                .save(consumer, loc(name));
    }

    protected final void stairsBlock(Consumer<FinishedRecipe> consumer, String name, Supplier<? extends Block> input, Supplier<? extends Block> output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output.get(), 8)
                .pattern("#  ")
                .pattern("## ")
                .pattern("###")
                .define('#', input.get())
                .unlockedBy("has_item", has(input.get()))
                .save(consumer, loc(name + "_stairs"));
    }

    protected final void slabBlock(Consumer<FinishedRecipe> consumer, String name, Supplier<? extends Block> input, Supplier<? extends Block> output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output.get(), 6)
                .pattern("###")
                .define('#', input.get())
                .unlockedBy("has_item", has(input.get()))
                .save(consumer, loc(name + "_slab"));
    }

    protected final void wallBlock(Consumer<FinishedRecipe> consumer, String name, Supplier<? extends Block> input, Supplier<? extends Block> output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output.get(), 6)
                .pattern("###")
                .pattern("###")
                .define('#', input.get())
                .unlockedBy("has_item", has(input.get()))
                .save(consumer, loc(name + "_wall"));
    }

    protected final ResourceLocation loc(String name) {
        return Goety.location(name);
    }

    public final void stoneCutting(Consumer<FinishedRecipe> consumer, String folder, Supplier<? extends Block> input, Supplier<? extends Block> output) {
        stoneCutting(consumer, folder, input, output, 1);
    }

    public final void stoneCutting(Consumer<FinishedRecipe> consumer, String folder, Supplier<? extends Block> input, Supplier<? extends Block> output, int amount) {
        SingleItemRecipeBuilder
                .stonecutting(Ingredient.of(input.get()), RecipeCategory.BUILDING_BLOCKS, output.get(), amount)
                .unlockedBy("has_item", has(input.get()))
                .save(consumer, loc(folder + "/" + getConversionRecipeName(input.get(), output.get()) + "_stonecutting"));
    }

    public final void smelting(Consumer<FinishedRecipe> consumer, String name, ItemLike input, ItemLike output){
        SimpleCookingRecipeBuilder
                .smelting(Ingredient.of(input), RecipeCategory.BUILDING_BLOCKS, output, 0.35F, 200)
                .unlockedBy("has_item", has(input))
                .save(consumer, loc(name + "_smelting"));
    }
}
