package com.Polarice3.Goety.data;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModCraftingProvider extends RecipeProvider {
    public ModCraftingProvider(DataGenerator p_248933_) {
        super(p_248933_);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(ModBlocks.CRYPT_PILLAR_BLOCK.get(), 2)
                .pattern("#")
                .pattern("#")
                .define('#', ModBlocks.CRYPT_STONE_POLISHED_BLOCK.get())
                .unlockedBy("has_item", has(ModBlocks.CRYPT_STONE_POLISHED_BLOCK.get()))
                .save(consumer, loc("crypt/crypt_pillar"));
        ShapedRecipeBuilder.shaped(ModBlocks.CRYPT_STONE_CHISELED_BLOCK.get())
                .pattern("#")
                .pattern("#")
                .define('#', ModBlocks.CRYPT_STONE_POLISHED_SLAB_BLOCK.get())
                .unlockedBy("has_item", has(ModBlocks.CRYPT_STONE_POLISHED_SLAB_BLOCK.get()))
                .save(consumer, loc("crypt/crypt_stone_chiseled"));
        ShapedRecipeBuilder.shaped(ModBlocks.CRYPT_PLINTH_BLOCK.get())
                .pattern("#")
                .pattern("#")
                .define('#', ModBlocks.CRYPT_STONE_SLAB_BLOCK.get())
                .unlockedBy("has_item", has(ModBlocks.CRYPT_STONE_SLAB_BLOCK.get()))
                .save(consumer, loc("crypt/crypt_plinth"));

        ShapedRecipeBuilder.shaped(ModBlocks.SILTSTONE_PILLAR_BLOCK.get())
                .pattern("#")
                .pattern("#")
                .define('#', ModBlocks.SILTSTONE_TILE_SLAB.get())
                .unlockedBy("has_item", has(ModBlocks.SILTSTONE_TILE_SLAB.get()))
                .save(consumer, loc("silt/siltstone_pillar"));
        ShapedRecipeBuilder.shaped(ModBlocks.CHISELED_SILTSTONE_BLOCK.get())
                .pattern("#")
                .pattern("#")
                .define('#', ModBlocks.SILTSTONE_PAVEMENT_SLAB.get())
                .unlockedBy("has_item", has(ModBlocks.SILTSTONE_PAVEMENT_SLAB.get()))
                .save(consumer, loc("silt/chiseled_siltstone"));
        ShapedRecipeBuilder.shaped(ModBlocks.CHISELED_SILTSTONE_BRICKS_BLOCK.get())
                .pattern("#")
                .pattern("#")
                .define('#', ModBlocks.SILTSTONE_BRICK_SLAB.get())
                .unlockedBy("has_item", has(ModBlocks.SILTSTONE_BRICK_SLAB.get()))
                .save(consumer, loc("silt/chiseled_siltstone_bricks"));

        smelting(consumer,"crypt/crypt_stone_polished", ModBlocks.CRYPT_STONE_BLOCK.get(), ModBlocks.CRYPT_STONE_POLISHED_BLOCK.get());

        twoByTwo(consumer, "crypt/crypt_bricks", ModBlocks.CRYPT_STONE_POLISHED_BLOCK, ModBlocks.CRYPT_BRICKS_BLOCK);
        twoByTwo(consumer, "crypt/crypt_tiles", ModBlocks.CRYPT_STONE_BLOCK, ModBlocks.CRYPT_TILES_BLOCK);

        twoByTwo(consumer, "silt/siltstone_bricks", ModBlocks.SILTSTONE_BLOCK, ModBlocks.SILTSTONE_BRICKS_BLOCK);
        twoByTwo(consumer, "silt/siltstone_tiles", ModBlocks.SILTSTONE_BRICKS_BLOCK, ModBlocks.SILTSTONE_TILES_BLOCK);
        twoByTwo(consumer, "silt/siltstone_pavement", ModBlocks.SILTSTONE_TILES_BLOCK, ModBlocks.SILTSTONE_PAVEMENT_BLOCK);

        slabBlock(consumer, "crypt/crypt_stone", ModBlocks.CRYPT_STONE_BLOCK, ModBlocks.CRYPT_STONE_SLAB_BLOCK);
        slabBlock(consumer, "crypt/crypt_stone_polished", ModBlocks.CRYPT_STONE_POLISHED_BLOCK, ModBlocks.CRYPT_STONE_POLISHED_SLAB_BLOCK);
        slabBlock(consumer, "crypt/crypt_bricks", ModBlocks.CRYPT_BRICKS_BLOCK, ModBlocks.CRYPT_BRICKS_SLAB_BLOCK);
        slabBlock(consumer, "crypt/crypt_tiles", ModBlocks.CRYPT_TILES_BLOCK, ModBlocks.CRYPT_TILES_SLAB_BLOCK);

        slabBlock(consumer, "silt/siltstone", ModBlocks.SILTSTONE_BLOCK, ModBlocks.SILTSTONE_SLAB);
        slabBlock(consumer, "silt/siltstone_bricks", ModBlocks.SILTSTONE_BRICKS_BLOCK, ModBlocks.SILTSTONE_BRICK_SLAB);
        slabBlock(consumer, "silt/siltstone_tiles", ModBlocks.SILTSTONE_TILES_BLOCK, ModBlocks.SILTSTONE_TILE_SLAB);
        slabBlock(consumer, "silt/siltstone_pavement", ModBlocks.SILTSTONE_PAVEMENT_BLOCK, ModBlocks.SILTSTONE_PAVEMENT_SLAB);

        stairsBlock(consumer, "crypt/crypt_stone", ModBlocks.CRYPT_STONE_BLOCK, ModBlocks.CRYPT_STONE_STAIRS_BLOCK);
        stairsBlock(consumer, "crypt/crypt_stone_polished", ModBlocks.CRYPT_STONE_POLISHED_BLOCK, ModBlocks.CRYPT_STONE_POLISHED_STAIRS_BLOCK);
        stairsBlock(consumer, "crypt/crypt_bricks", ModBlocks.CRYPT_BRICKS_BLOCK, ModBlocks.CRYPT_BRICKS_STAIRS_BLOCK);
        stairsBlock(consumer, "crypt/crypt_tiles", ModBlocks.CRYPT_TILES_BLOCK, ModBlocks.CRYPT_TILES_STAIRS_BLOCK);

        stairsBlock(consumer, "silt/siltstone", ModBlocks.SILTSTONE_BLOCK, ModBlocks.SILTSTONE_STAIRS);
        stairsBlock(consumer, "silt/siltstone_bricks", ModBlocks.SILTSTONE_BRICKS_BLOCK, ModBlocks.SILTSTONE_BRICK_STAIRS);
        stairsBlock(consumer, "silt/siltstone_tiles", ModBlocks.SILTSTONE_TILES_BLOCK, ModBlocks.SILTSTONE_TILE_STAIRS);
        stairsBlock(consumer, "silt/siltstone_pavement", ModBlocks.SILTSTONE_PAVEMENT_BLOCK, ModBlocks.SILTSTONE_PAVEMENT_STAIRS);

        wallBlock(consumer, "crypt/crypt_stone_polished", ModBlocks.CRYPT_STONE_POLISHED_BLOCK, ModBlocks.CRYPT_STONE_POLISHED_WALL_BLOCK);
        wallBlock(consumer, "crypt/crypt_bricks", ModBlocks.CRYPT_BRICKS_BLOCK, ModBlocks.CRYPT_BRICKS_WALL_BLOCK);
        wallBlock(consumer, "crypt/crypt_tiles", ModBlocks.CRYPT_TILES_BLOCK, ModBlocks.CRYPT_TILES_WALL_BLOCK);

        wallBlock(consumer, "silt/siltstone", ModBlocks.SILTSTONE_BLOCK, ModBlocks.SILTSTONE_WALL_BLOCK);
        wallBlock(consumer, "silt/siltstone_bricks", ModBlocks.SILTSTONE_BRICKS_BLOCK, ModBlocks.SILTSTONE_BRICK_WALL_BLOCK);
        wallBlock(consumer, "silt/siltstone_tiles", ModBlocks.SILTSTONE_TILES_BLOCK, ModBlocks.SILTSTONE_TILE_WALL_BLOCK);
        wallBlock(consumer, "silt/siltstone_pavement", ModBlocks.SILTSTONE_PAVEMENT_BLOCK, ModBlocks.SILTSTONE_PAVEMENT_WALL_BLOCK);

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

        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_BLOCK, ModBlocks.SILTSTONE_SLAB, 2);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_BLOCK, ModBlocks.SILTSTONE_BRICK_SLAB, 2);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_BLOCK, ModBlocks.SILTSTONE_TILE_SLAB, 2);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_BLOCK, ModBlocks.SILTSTONE_PAVEMENT_SLAB, 2);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_BRICKS_BLOCK, ModBlocks.SILTSTONE_BRICK_SLAB, 2);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_BRICKS_BLOCK, ModBlocks.SILTSTONE_TILE_SLAB, 2);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_BRICKS_BLOCK, ModBlocks.SILTSTONE_PAVEMENT_SLAB, 2);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_TILES_BLOCK, ModBlocks.SILTSTONE_TILE_SLAB, 2);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_TILES_BLOCK, ModBlocks.SILTSTONE_PAVEMENT_SLAB, 2);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_PAVEMENT_BLOCK, ModBlocks.SILTSTONE_PAVEMENT_SLAB, 2);

        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_BLOCK, ModBlocks.SILTSTONE_STAIRS);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_BLOCK, ModBlocks.SILTSTONE_BRICK_STAIRS);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_BLOCK, ModBlocks.SILTSTONE_TILE_STAIRS);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_BLOCK, ModBlocks.SILTSTONE_PAVEMENT_STAIRS);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_BRICKS_BLOCK, ModBlocks.SILTSTONE_BRICK_STAIRS);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_BRICKS_BLOCK, ModBlocks.SILTSTONE_TILE_STAIRS);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_BRICKS_BLOCK, ModBlocks.SILTSTONE_PAVEMENT_STAIRS);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_TILES_BLOCK, ModBlocks.SILTSTONE_TILE_STAIRS);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_TILES_BLOCK, ModBlocks.SILTSTONE_PAVEMENT_STAIRS);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_PAVEMENT_BLOCK, ModBlocks.SILTSTONE_PAVEMENT_STAIRS);

        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_BLOCK, ModBlocks.SILTSTONE_WALL_BLOCK);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_BLOCK, ModBlocks.SILTSTONE_BRICK_WALL_BLOCK);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_BLOCK, ModBlocks.SILTSTONE_TILE_WALL_BLOCK);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_BLOCK, ModBlocks.SILTSTONE_PAVEMENT_WALL_BLOCK);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_BRICKS_BLOCK, ModBlocks.SILTSTONE_BRICK_WALL_BLOCK);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_BRICKS_BLOCK, ModBlocks.SILTSTONE_TILE_WALL_BLOCK);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_BRICKS_BLOCK, ModBlocks.SILTSTONE_PAVEMENT_WALL_BLOCK);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_TILES_BLOCK, ModBlocks.SILTSTONE_TILE_WALL_BLOCK);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_TILES_BLOCK, ModBlocks.SILTSTONE_PAVEMENT_WALL_BLOCK);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_PAVEMENT_BLOCK, ModBlocks.SILTSTONE_PAVEMENT_WALL_BLOCK);

        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_BLOCK, ModBlocks.CHISELED_SILTSTONE_BLOCK);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_BRICKS_BLOCK, ModBlocks.CHISELED_SILTSTONE_BLOCK);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_TILES_BLOCK, ModBlocks.CHISELED_SILTSTONE_BLOCK);

        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_BLOCK, ModBlocks.CHISELED_SILTSTONE_BRICKS_BLOCK);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_BRICKS_BLOCK, ModBlocks.CHISELED_SILTSTONE_BRICKS_BLOCK);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_TILES_BLOCK, ModBlocks.CHISELED_SILTSTONE_BRICKS_BLOCK);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_PAVEMENT_BLOCK, ModBlocks.CHISELED_SILTSTONE_BRICKS_BLOCK);

        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_BLOCK, ModBlocks.SILTSTONE_PILLAR_BLOCK);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_BRICKS_BLOCK, ModBlocks.SILTSTONE_PILLAR_BLOCK);
        stoneCutting(consumer, "silt", ModBlocks.SILTSTONE_TILES_BLOCK, ModBlocks.SILTSTONE_PILLAR_BLOCK);
    }

    protected final void twoByTwo(Consumer<FinishedRecipe> consumer, String name, Supplier<? extends Block> input, Supplier<? extends Block> output) {
        twoByTwo(consumer, name, input, output, 4);
    }

    protected final void twoByTwo(Consumer<FinishedRecipe> consumer, String name, Supplier<? extends Block> input, Supplier<? extends Block> output, int amount) {
        ShapedRecipeBuilder.shaped(output.get(), amount)
                .pattern("##")
                .pattern("##")
                .define('#', input.get())
                .unlockedBy("has_item", has(input.get()))
                .save(consumer, loc(name));
    }

    protected final void stairsBlock(Consumer<FinishedRecipe> consumer, String name, Supplier<? extends Block> input, Supplier<? extends Block> output) {
        ShapedRecipeBuilder.shaped(output.get(), 8)
                .pattern("#  ")
                .pattern("## ")
                .pattern("###")
                .define('#', input.get())
                .unlockedBy("has_item", has(input.get()))
                .save(consumer, loc(name + "_stairs"));
    }

    protected final void slabBlock(Consumer<FinishedRecipe> consumer, String name, Supplier<? extends Block> input, Supplier<? extends Block> output) {
        ShapedRecipeBuilder.shaped(output.get(), 6)
                .pattern("###")
                .define('#', input.get())
                .unlockedBy("has_item", has(input.get()))
                .save(consumer, loc(name + "_slab"));
    }

    protected final void wallBlock(Consumer<FinishedRecipe> consumer, String name, Supplier<? extends Block> input, Supplier<? extends Block> output) {
        ShapedRecipeBuilder.shaped(output.get(), 6)
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
                .stonecutting(Ingredient.of(input.get()), output.get(), amount)
                .unlockedBy("has_item", has(input.get()))
                .save(consumer, loc(folder + "/" + getConversionRecipeName(input.get(), output.get()) + "_stonecutting"));
    }

    public final void smelting(Consumer<FinishedRecipe> consumer, String name, ItemLike input, ItemLike output){
        SimpleCookingRecipeBuilder
                .smelting(Ingredient.of(input), output, 0.35F, 200)
                .unlockedBy("has_item", has(input))
                .save(consumer, loc(name + "_smelting"));
    }
}
