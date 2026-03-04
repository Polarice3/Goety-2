package com.Polarice3.Goety.data;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.*;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput gen, ExistingFileHelper exFileHelper) {
        super(gen, Goety.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        builtinEntity(ModBlocks.HAUNTED_HANGING_SIGN.get(), "goety:block/stripped_haunted_log");
        builtinEntity(ModBlocks.HAUNTED_WALL_HANGING_SIGN.get(), "goety:block/stripped_haunted_log");
        builtinEntity(ModBlocks.ROTTEN_HANGING_SIGN.get(), "goety:block/stripped_rotten_log");
        builtinEntity(ModBlocks.ROTTEN_WALL_HANGING_SIGN.get(), "goety:block/stripped_rotten_log");
        builtinEntity(ModBlocks.WINDSWEPT_HANGING_SIGN.get(), "goety:block/stripped_windswept_log");
        builtinEntity(ModBlocks.WINDSWEPT_WALL_HANGING_SIGN.get(), "goety:block/stripped_windswept_log");
        builtinEntity(ModBlocks.PINE_HANGING_SIGN.get(), "goety:block/stripped_pine_log");
        builtinEntity(ModBlocks.PINE_WALL_HANGING_SIGN.get(), "goety:block/stripped_pine_log");
        builtinEntity(ModBlocks.CHORUS_HANGING_SIGN.get(), "goety:block/chorus_log");
        builtinEntity(ModBlocks.CHORUS_WALL_HANGING_SIGN.get(), "goety:block/chorus_log");
        builtinEntity(ModBlocks.CORRUPT_CHORUS_HANGING_SIGN.get(), "goety:block/corrupt_chorus_log");
        builtinEntity(ModBlocks.CORRUPT_CHORUS_WALL_HANGING_SIGN.get(), "goety:block/corrupt_chorus_log");

        plushie(ModBlocks.PLUSHIE.get());
        plushie(ModBlocks.PLUSHIE_1.get());
        plushie(ModBlocks.PLUSHIE_2.get());
        plushie(ModBlocks.PLUSHIE_3.get());
        plushie(ModBlocks.PLUSHIE_4.get());
        plushie(ModBlocks.PLUSHIE_5.get());

        simpleBlockWithItem(ModBlocks.DIAMOND_MOLD_BLOCK.get());

        simpleBlockWithItem(ModBlocks.CRAGROCKS_BLOCK.get());
        simpleBlockWithItem(ModBlocks.CRAG_TILES_BLOCK.get());
        simpleBlockWithItem(ModBlocks.CRAG_BRICKS_BLOCK.get());
        simpleBlockWithItem(ModBlocks.SNOWY_CRAG_BRICKS_BLOCK.get());
        simpleBlockWithItem(ModBlocks.SILT_STUDDED_CRAG_TILES_BLOCK.get());

        simpleBlockWithItem(ModBlocks.SMOOTH_SILTSTONE_BLOCK.get());
        simpleBlockWithItem(ModBlocks.COBBLED_SILTSTONE_BLOCK.get());
        simpleBlockWithItem(ModBlocks.SNOWY_COBBLED_SILTSTONE_BLOCK.get());
        simpleBlockWithItem(ModBlocks.SILTSTONE_BRICKS_BLOCK.get());
        simpleBlockWithItem(ModBlocks.SNOWY_SILTSTONE_BRICKS_SLIGHT_BLOCK.get());
        simpleBlockWithItem(ModBlocks.SNOWY_SILTSTONE_BRICKS_BLOCK.get());
        simpleBlockWithItem(ModBlocks.SILTSTONE_TILES_BLOCK.get());
        simpleBlockWithItem(ModBlocks.SILTSTONE_PAVEMENT_BLOCK.get());
        simpleBlockWithItem(ModBlocks.CHISELED_SILTSTONE_BLOCK.get());
        simpleBlockWithItem(ModBlocks.CHISELED_SILTSTONE_BRICKS_BLOCK.get());

        simpleBlockWithItem(ModBlocks.END_BASALT_BRICKS.get());
        simpleBlockWithItem(ModBlocks.SOILED_END_BASALT_BRICKS.get());
        simpleBlockWithItem(ModBlocks.SMOOTH_END_BASALT_BRICKS.get());

        simpleBlockWithItem(ModBlocks.END_ROCK_CHISELED.get());
        simpleBlockWithItem(ModBlocks.SOILED_END_ROCK_CHISELED.get());

        simpleBlockWithItem(ModBlocks.END_DIRT.get());
        simpleBlockWithItem(ModBlocks.SOILED_END_DIRT.get());

        simpleBlockWithItem(ModBlocks.BIG_END_STONE_BRICKS_BLOCK.get());
        simpleBlockWithItem(ModBlocks.MESSY_END_STONE_BRICKS_BLOCK.get());
        simpleBlockWithItem(ModBlocks.CHISELED_END_STONE_BLOCK.get());
        simpleBlockWithItem(ModBlocks.CHISELED_END_STONE_BRICKS_BLOCK.get());
        simpleBlockWithItem(ModBlocks.SOILED_END_STONE_BRICKS_SLIGHT_BLOCK.get());
        simpleBlockWithItem(ModBlocks.SOILED_END_STONE_BRICKS_BLOCK.get());
        simpleBlockWithItem(ModBlocks.SOILED_END_STONE_BRICKS_HEAVY_BLOCK.get());
        simpleBlockWithItem(ModBlocks.GRASSY_END_STONE_BRICKS_BLOCK.get());
        simpleBlockWithItem(ModBlocks.GRASSY_END_STONE_BRICKS_HEAVY_BLOCK.get());
        simpleBlockWithItem(ModBlocks.END_STONE_TILES_BLOCK.get());
        simpleBlockWithItem(ModBlocks.SOILED_END_STONE_TILES_BLOCK.get());
        simpleBlockWithItem(ModBlocks.GRASSY_END_STONE_TILES_BLOCK.get());
        simpleBlockWithItem(ModBlocks.SMOOTH_END_STONE_BLOCK.get());
        simpleBlockWithItem(ModBlocks.COBBLED_END_STONE_BLOCK.get());
        simpleBlockWithItem(ModBlocks.GRASSY_COBBLED_END_STONE_BLOCK.get());
        simpleBlockWithItem(ModBlocks.END_ROD_BLOCK.get());
        simpleBlockWithItem(ModBlocks.END_LAMP_BLOCK.get());

        simpleBlockWithItem(ModBlocks.SOILED_PURPUR_BLOCK.get());
        simpleBlockWithItem(ModBlocks.GRASSY_PURPUR_BLOCK.get());

        crossBlockWithItem(ModBlocks.CHORUS_SPROUT.get());
        crossBlockWithItem(ModBlocks.CHORUS_STALK.get());

        crossBlockWithItem(ModBlocks.END_GRASS_SPROUT.get());
        crossBlockWithItem(ModBlocks.END_GRASS.get());

        crossBlockWithItem(ModBlocks.CHORUS_TALL_GRASS.get());
        crossBlockWithItem(ModBlocks.CHORUS_FERN_SPROUT.get());

        buttonBlockWithItem((ButtonBlock) ModBlocks.HAUNTED_BUTTON.get(), Goety.location("block/haunted_planks"));
        buttonBlockWithItem((ButtonBlock) ModBlocks.ROTTEN_BUTTON.get(), Goety.location("block/rotten_planks"));
        buttonBlockWithItem((ButtonBlock) ModBlocks.WINDSWEPT_BUTTON.get(), Goety.location("block/windswept_planks"));
        buttonBlockWithItem((ButtonBlock) ModBlocks.PINE_BUTTON.get(), Goety.location("block/pine_planks"));
        buttonBlockWithItem((ButtonBlock) ModBlocks.STEEP_BUTTON.get(), Goety.location("block/steep_planks"));
        buttonBlockWithItem((ButtonBlock) ModBlocks.CHORUS_BUTTON.get(), Goety.location("block/chorus_planks"));
        buttonBlockWithItem((ButtonBlock) ModBlocks.CORRUPT_CHORUS_BUTTON.get(), Goety.location("block/corrupt_chorus_planks"));

        doorBlockWithRenderType((DoorBlock) ModBlocks.HAUNTED_DOOR.get(), Goety.location("block/haunted_door_bottom"), Goety.location("block/haunted_door_top"), "cutout");
        doorBlockWithRenderType((DoorBlock) ModBlocks.ROTTEN_DOOR.get(), Goety.location("block/rotten_door_bottom"), Goety.location("block/rotten_door_top"), "cutout");
        doorBlockWithRenderType((DoorBlock) ModBlocks.WINDSWEPT_DOOR.get(), Goety.location("block/windswept_door_bottom"), Goety.location("block/windswept_door_top"), "cutout");
        doorBlockWithRenderType((DoorBlock) ModBlocks.PINE_DOOR.get(), Goety.location("block/pine_door_bottom"), Goety.location("block/pine_door_top"), "cutout");
        doorBlockWithRenderType((DoorBlock) ModBlocks.CHORUS_DOOR.get(), Goety.location("block/chorus_door_bottom"), Goety.location("block/chorus_door_top"), "cutout");
        doorBlockWithRenderType((DoorBlock) ModBlocks.CORRUPT_CHORUS_DOOR.get(), Goety.location("block/corrupt_chorus_door_bottom"), Goety.location("block/corrupt_chorus_door_top"), "cutout");

        fenceGateWithItem((FenceGateBlock) ModBlocks.HAUNTED_FENCE_GATE.get(), Goety.location("block/haunted_planks"));
        fenceGateWithItem((FenceGateBlock) ModBlocks.ROTTEN_FENCE_GATE.get(), Goety.location("block/rotten_planks"));
        fenceGateWithItem((FenceGateBlock) ModBlocks.WINDSWEPT_FENCE_GATE.get(), Goety.location("block/windswept_planks"));
        fenceGateWithItem((FenceGateBlock) ModBlocks.PINE_FENCE_GATE.get(), Goety.location("block/pine_planks"));
        fenceGateWithItem((FenceGateBlock) ModBlocks.STEEP_FENCE_GATE.get(), Goety.location("block/steep_planks"));
        fenceGateWithItem((FenceGateBlock) ModBlocks.CHORUS_FENCE_GATE.get(), Goety.location("block/chorus_planks"));
        fenceGateWithItem((FenceGateBlock) ModBlocks.CORRUPT_CHORUS_FENCE_GATE.get(), Goety.location("block/corrupt_chorus_planks"));

        fenceBlockWithItem((FenceBlock) ModBlocks.HAUNTED_FENCE.get(), Goety.location("block/haunted_planks"));
        fenceBlockWithItem((FenceBlock) ModBlocks.ROTTEN_FENCE.get(), Goety.location("block/rotten_planks"));
        fenceBlockWithItem((FenceBlock) ModBlocks.WINDSWEPT_FENCE.get(), Goety.location("block/windswept_planks"));
        fenceBlockWithItem((FenceBlock) ModBlocks.PINE_FENCE.get(), Goety.location("block/pine_planks"));
        fenceBlockWithItem((FenceBlock) ModBlocks.STEEP_FENCE.get(), Goety.location("block/steep_planks"));
        fenceBlockWithItem((FenceBlock) ModBlocks.CHORUS_FENCE.get(), Goety.location("block/chorus_planks"));
        fenceBlockWithItem((FenceBlock) ModBlocks.CORRUPT_CHORUS_FENCE.get(), Goety.location("block/corrupt_chorus_planks"));
        fenceBlockWithItem((FenceBlock) ModBlocks.CRAGROCKS_FENCE.get(), Goety.location("block/cragrocks"));

        fenceBlockWithItem((FenceBlock) ModBlocks.END_ROCK_BRICK_FENCE.get(), Goety.location("block/end_rock_bricks"));
        fenceBlockWithItem((FenceBlock) ModBlocks.SMOOTH_END_STONE_FENCE.get(), Goety.location("block/smooth_end_stone"));

        sideBottomTopBlock(ModBlocks.TOP_SOILED_END_BASALT.get(), Goety.location("block/end_soil"), Goety.location("block/top_soiled_end_basalt"), Goety.location("block/end_basalt_top"));
        sideBottomTopBlock(ModBlocks.BOTTOM_SOILED_END_BASALT.get(), Goety.location("block/end_basalt_top"), Goety.location("block/bottom_soiled_end_basalt"), Goety.location("block/end_soil"));
        sideBottomTopBlock(ModBlocks.END_ROCK_SLATE.get(), Goety.location("block/end_rock_top"), Goety.location("block/end_rock_slate"), Goety.location("block/end_stone_slate_top"));
        sideBottomTopBlock(ModBlocks.END_STONE_SLATE_ROCK.get(), Goety.location("block/end_stone_slate_top"), Goety.location("block/end_stone_slate_rock"), Goety.location("block/end_rock_top"));

        logBlockWithItem((RotatedPillarBlock) ModBlocks.HAUNTED_LOG.get());
        logBlockWithItem((RotatedPillarBlock) ModBlocks.ROTTEN_LOG.get());
        logBlockWithItem((RotatedPillarBlock) ModBlocks.WINDSWEPT_LOG.get());
        logBlockWithItem((RotatedPillarBlock) ModBlocks.PINE_LOG.get());
        logBlockWithItem((RotatedPillarBlock) ModBlocks.CHORUS_LOG.get());
        logBlockWithItem((RotatedPillarBlock) ModBlocks.CORRUPT_CHORUS_LOG.get());

        logBlockWithItem((RotatedPillarBlock) ModBlocks.STRIPPED_HAUNTED_LOG.get());
        logBlockWithItem((RotatedPillarBlock) ModBlocks.STRIPPED_ROTTEN_LOG.get());
        logBlockWithItem((RotatedPillarBlock) ModBlocks.STRIPPED_WINDSWEPT_LOG.get());
        logBlockWithItem((RotatedPillarBlock) ModBlocks.STRIPPED_PINE_LOG.get());

        logBlockWithItem((RotatedPillarBlock) ModBlocks.END_BASALT.get());
        logBlockWithItem((RotatedPillarBlock) ModBlocks.END_STONE_SLATE_BLOCK.get());
        logBlockWithItem((RotatedPillarBlock) ModBlocks.END_STONE_PILLAR_BLOCK.get());
        logBlockWithItem((RotatedPillarBlock) ModBlocks.CHORUS_END_STONE_PILLAR_BLOCK.get());

        columnBlockWithItem((RotatedPillarBlock) ModBlocks.HAUNTED_WOOD.get(), Goety.location("block/haunted_log"));
        columnBlockWithItem((RotatedPillarBlock) ModBlocks.ROTTEN_WOOD.get(), Goety.location("block/rotten_log"));
        columnBlockWithItem((RotatedPillarBlock) ModBlocks.WINDSWEPT_WOOD.get(), Goety.location("block/windswept_log"));
        columnBlockWithItem((RotatedPillarBlock) ModBlocks.PINE_WOOD.get(), Goety.location("block/pine_log"));
        columnBlockWithItem((RotatedPillarBlock) ModBlocks.STEEP_WOOD.get(), Goety.location("block/steep_log"));
        columnBlockWithItem((RotatedPillarBlock) ModBlocks.CHORUS_WOOD.get(), Goety.location("block/chorus_log"));
        columnBlockWithItem((RotatedPillarBlock) ModBlocks.CORRUPT_CHORUS_WOOD.get(), Goety.location("block/corrupt_chorus_log"));

        columnBlockWithItem((RotatedPillarBlock) ModBlocks.STRIPPED_HAUNTED_WOOD.get(), Goety.location("block/stripped_haunted_log"));
        columnBlockWithItem((RotatedPillarBlock) ModBlocks.STRIPPED_ROTTEN_WOOD.get(), Goety.location("block/stripped_rotten_log"));
        columnBlockWithItem((RotatedPillarBlock) ModBlocks.STRIPPED_WINDSWEPT_WOOD.get(), Goety.location("block/stripped_windswept_log"));
        columnBlockWithItem((RotatedPillarBlock) ModBlocks.STRIPPED_PINE_WOOD.get(), Goety.location("block/stripped_pine_log"));

        columnBlockWithItem((RotatedPillarBlock) ModBlocks.END_ROCK_BRICKS.get(), Goety.location("block/end_rock_bricks"));
        columnBlockWithItem((RotatedPillarBlock) ModBlocks.SOILED_END_ROCK_BRICKS.get(), Goety.location("block/soiled_end_rock_bricks"));
        columnBlockWithItem((RotatedPillarBlock) ModBlocks.GRASSY_END_ROCK_BRICKS.get(), Goety.location("block/grassy_end_rock_bricks"));

        pressurePlateWithItem((PressurePlateBlock) ModBlocks.HAUNTED_PRESSURE_PLATE.get(), Goety.location("block/haunted_planks"));
        pressurePlateWithItem((PressurePlateBlock) ModBlocks.ROTTEN_PRESSURE_PLATE.get(), Goety.location("block/rotten_planks"));
        pressurePlateWithItem((PressurePlateBlock) ModBlocks.WINDSWEPT_PRESSURE_PLATE.get(), Goety.location("block/windswept_planks"));
        pressurePlateWithItem((PressurePlateBlock) ModBlocks.PINE_PRESSURE_PLATE.get(), Goety.location("block/pine_planks"));
        pressurePlateWithItem((PressurePlateBlock) ModBlocks.STEEP_PRESSURE_PLATE.get(), Goety.location("block/steep_planks"));
        pressurePlateWithItem((PressurePlateBlock) ModBlocks.CHORUS_PRESSURE_PLATE.get(), Goety.location("block/chorus_planks"));
        pressurePlateWithItem((PressurePlateBlock) ModBlocks.CORRUPT_CHORUS_PRESSURE_PLATE.get(), Goety.location("block/corrupt_chorus_planks"));

        slabBlockWithItem((SlabBlock) ModBlocks.HAUNTED_SLAB.get(), Goety.location("block/haunted_planks"));
        slabBlockWithItem((SlabBlock) ModBlocks.ROTTEN_SLAB.get(), Goety.location("block/rotten_planks"));
        slabBlockWithItem((SlabBlock) ModBlocks.WINDSWEPT_SLAB.get(), Goety.location("block/windswept_planks"));
        slabBlockWithItem((SlabBlock) ModBlocks.PINE_SLAB.get(), Goety.location("block/pine_planks"));
        slabBlockWithItem((SlabBlock) ModBlocks.COMPACTED_WINDSWEPT_SLAB.get(), Goety.location("block/compacted_windswept_planks"));
        slabBlockWithItem((SlabBlock) ModBlocks.COMPACTED_PINE_SLAB.get(), Goety.location("block/compacted_pine_planks"));
        slabBlockWithItem((SlabBlock) ModBlocks.STEEP_SLAB.get(), Goety.location("block/steep_planks"));
        slabBlockWithItem((SlabBlock) ModBlocks.STEEP_WOOD_SLAB.get(), Goety.location("block/steep_wood"), Goety.location("block/steep_log"));
        slabBlockWithItem((SlabBlock) ModBlocks.SKY_WOOD_SLAB.get(), Goety.location("block/sky_wood_planks"));
        slabBlockWithItem((SlabBlock) ModBlocks.CHORUS_SLAB.get(), Goety.location("block/chorus_planks"));
        slabBlockWithItem((SlabBlock) ModBlocks.CHORUS_WOOD_SLAB.get(), Goety.location("block/chorus_wood"), Goety.location("block/chorus_log"), Goety.location("block/chorus_log"));
        slabBlockWithItem((SlabBlock) ModBlocks.CORRUPT_CHORUS_SLAB.get(), Goety.location("block/corrupt_chorus_planks"));

        slabBlockWithItem((SlabBlock) ModBlocks.CRACKED_MARBLE_SLAB.get(), Goety.location("block/cracked_marble"));
        slabBlockWithItem((SlabBlock) ModBlocks.SMOOTH_MARBLE_SLAB.get(), Goety.location("block/marble"));
        slabBlockWithItem((SlabBlock) ModBlocks.SLATE_MARBLE_SLAB.get(), Goety.location("block/slate_marble"));

        slabBlockWithItem((SlabBlock) ModBlocks.CRAGROCKS_SLAB.get(), Goety.location("block/cragrocks"));
        slabBlockWithItem((SlabBlock) ModBlocks.CRAG_TILE_SLAB.get(), Goety.location("block/crag_tiles"));
        slabBlockWithItem((SlabBlock) ModBlocks.CRAG_BRICK_SLAB.get(), Goety.location("block/crag_bricks"));
        slabBlockWithItem((SlabBlock) ModBlocks.SNOWY_CRAG_BRICK_SLAB.get(), Goety.location("block/snowy_crag_bricks"));

        slabBlockWithItem((SlabBlock) ModBlocks.HIGHROCK_SLAB.get(), Goety.location("block/highrock"));
        slabBlockWithItem((SlabBlock) ModBlocks.POLISHED_HIGHROCK_SLAB.get(), Goety.location("block/polished_highrock"));
        slabBlockWithItem((SlabBlock) ModBlocks.HIGHROCK_BRICK_SLAB.get(), Goety.location("block/highrock_bricks"));

        slabBlockWithItem((SlabBlock) ModBlocks.SILTSTONE_SLAB.get(), Goety.location("block/siltstone"));
        slabBlockWithItem((SlabBlock) ModBlocks.SMOOTH_SILTSTONE_SLAB.get(), Goety.location("block/smooth_siltstone"));
        slabBlockWithItem((SlabBlock) ModBlocks.COBBLED_SILTSTONE_SLAB.get(), Goety.location("block/cobbled_siltstone"));
        slabBlockWithItem((SlabBlock) ModBlocks.SNOWY_COBBLED_SILTSTONE_SLAB.get(), Goety.location("block/snowy_cobbled_siltstone"));
        slabBlockWithItem((SlabBlock) ModBlocks.SILTSTONE_BRICK_SLAB.get(), Goety.location("block/siltstone_bricks"));
        slabBlockWithItem((SlabBlock) ModBlocks.SNOWY_SILTSTONE_BRICK_SLIGHT_SLAB.get(), Goety.location("block/snowy_siltstone_bricks_slight"));
        slabBlockWithItem((SlabBlock) ModBlocks.SNOWY_SILTSTONE_BRICK_SLAB.get(), Goety.location("block/snowy_siltstone_bricks"));
        slabBlockWithItem((SlabBlock) ModBlocks.SILTSTONE_TILE_SLAB.get(), Goety.location("block/siltstone_tiles"));
        slabBlockWithItem((SlabBlock) ModBlocks.SILTSTONE_PAVEMENT_SLAB.get(), Goety.location("block/siltstone_pavement"));

        slabBlockWithItem((SlabBlock) ModBlocks.SNOW_BRICK_SLAB.get(), Goety.location("block/snow_bricks"));

        slabBlockWithItem((SlabBlock) ModBlocks.END_MUD_SLAB.get(), Goety.location("block/end_mud"));

        slabBlockWithItem((SlabBlock) ModBlocks.END_DIRT_SLAB.get(), Goety.location("block/end_dirt"));

        slabBlockWithItem((SlabBlock) ModBlocks.END_BASALT_SLAB.get(), Goety.location("block/end_basalt"), Goety.location("block/end_basalt"), Goety.location("block/end_basalt_top"));

        slabBlockWithItem((SlabBlock) ModBlocks.END_BASALT_BRICK_SLAB.get(), Goety.location("block/end_basalt_bricks"));

        slabBlockWithItem((SlabBlock) ModBlocks.END_ROCK_SLAB.get(), Goety.location("block/end_rock"), Goety.location("block/end_rock"), Goety.location("block/end_rock_top"));
        slabBlockWithItem((SlabBlock) ModBlocks.END_ROCK_BRICK_SLAB.get(), Goety.location("block/end_rock_bricks"));

        slabBlockWithItem((SlabBlock) ModBlocks.END_STONE_SLAB.get(), new ResourceLocation("block/end_stone"));
        slabBlockWithItem((SlabBlock) ModBlocks.END_STONE_TILE_SLAB.get(), Goety.location("block/end_stone_tiles"));
        slabBlockWithItem((SlabBlock) ModBlocks.SMOOTH_END_STONE_SLAB.get(), Goety.location("block/smooth_end_stone"));
        slabBlockWithItem((SlabBlock) ModBlocks.COBBLED_END_STONE_SLAB.get(), Goety.location("block/cobbled_end_stone"));

        stairsBlockWithItem((StairBlock) ModBlocks.HAUNTED_STAIRS.get(), Goety.location("block/haunted_planks"));
        stairsBlockWithItem((StairBlock) ModBlocks.ROTTEN_STAIRS.get(), Goety.location("block/rotten_planks"));
        stairsBlockWithItem((StairBlock) ModBlocks.WINDSWEPT_STAIRS.get(), Goety.location("block/windswept_planks"));
        stairsBlockWithItem((StairBlock) ModBlocks.PINE_STAIRS.get(), Goety.location("block/pine_planks"));
        stairsBlockWithItem((StairBlock) ModBlocks.STEEP_STAIRS.get(), Goety.location("block/steep_planks"));
        stairsBlockWithItem((StairBlock) ModBlocks.SKY_WOOD_STAIRS.get(), Goety.location("block/sky_wood_planks"));
        stairsBlockWithItem((StairBlock) ModBlocks.CHORUS_STAIRS.get(), Goety.location("block/chorus_planks"));
        stairsBlockWithItem((StairBlock) ModBlocks.CHORUS_WOOD_STAIRS.get(), Goety.location("block/chorus_log"));
        stairsBlockWithItem((StairBlock) ModBlocks.CORRUPT_CHORUS_STAIRS.get(), Goety.location("block/corrupt_chorus_planks"));

        stairsBlockWithItem((StairBlock) ModBlocks.MARBLE_STAIRS_BLOCK.get(), Goety.location("block/marble"));
        stairsBlockWithItem((StairBlock) ModBlocks.SLATE_MARBLE_STAIRS_BLOCK.get(), Goety.location("block/slate_marble"));

        stairsBlockWithItem((StairBlock) ModBlocks.CRAGROCKS_STAIRS.get(), Goety.location("block/cragrocks"));
        stairsBlockWithItem((StairBlock) ModBlocks.CRAG_TILE_STAIRS.get(), Goety.location("block/crag_tiles"));
        stairsBlockWithItem((StairBlock) ModBlocks.CRAG_BRICK_STAIRS.get(), Goety.location("block/crag_bricks"));
        stairsBlockWithItem((StairBlock) ModBlocks.SNOWY_CRAG_BRICK_STAIRS.get(), Goety.location("block/snowy_crag_bricks"));

        stairsBlockWithItem((StairBlock) ModBlocks.HIGHROCK_STAIRS.get(), Goety.location("block/highrock"));
        stairsBlockWithItem((StairBlock) ModBlocks.POLISHED_HIGHROCK_STAIRS.get(), Goety.location("block/polished_highrock"));
        stairsBlockWithItem((StairBlock) ModBlocks.HIGHROCK_BRICK_STAIRS.get(), Goety.location("block/highrock_bricks"));

        stairsBlockWithItem((StairBlock) ModBlocks.SILTSTONE_STAIRS.get(), Goety.location("block/siltstone"));
        stairsBlockWithItem((StairBlock) ModBlocks.SMOOTH_SILTSTONE_STAIRS.get(), Goety.location("block/smooth_siltstone"));
        stairsBlockWithItem((StairBlock) ModBlocks.COBBLED_SILTSTONE_STAIRS.get(), Goety.location("block/cobbled_siltstone"));
        stairsBlockWithItem((StairBlock) ModBlocks.SNOWY_COBBLED_SILTSTONE_STAIRS.get(), Goety.location("block/snowy_cobbled_siltstone"));
        stairsBlockWithItem((StairBlock) ModBlocks.SILTSTONE_BRICK_STAIRS.get(), Goety.location("block/siltstone_bricks"));
        stairsBlockWithItem((StairBlock) ModBlocks.SNOWY_SILTSTONE_BRICK_SLIGHT_STAIRS.get(), Goety.location("block/snowy_siltstone_bricks_slight"));
        stairsBlockWithItem((StairBlock) ModBlocks.SNOWY_SILTSTONE_BRICK_STAIRS.get(), Goety.location("block/snowy_siltstone_bricks"));
        stairsBlockWithItem((StairBlock) ModBlocks.SILTSTONE_TILE_STAIRS.get(), Goety.location("block/siltstone_tiles"));
        stairsBlockWithItem((StairBlock) ModBlocks.SILTSTONE_PAVEMENT_STAIRS.get(), Goety.location("block/siltstone_pavement"));

        stairsBlockWithItem((StairBlock) ModBlocks.SNOW_BRICK_STAIRS_BLOCK.get(), Goety.location("block/snow_bricks"));

        stairsBlockWithItem((StairBlock) ModBlocks.END_BASALT_STAIRS.get(), Goety.location("block/end_basalt"));

        stairsBlockWithItem((StairBlock) ModBlocks.END_BASALT_BRICK_STAIRS.get(), Goety.location("block/end_basalt_bricks"));
        stairsBlockWithItem((StairBlock) ModBlocks.END_STONE_TILE_STAIRS_BLOCK.get(), Goety.location("block/end_stone_tiles"));
        stairsBlockWithItem((StairBlock) ModBlocks.SMOOTH_END_STONE_STAIRS_BLOCK.get(), Goety.location("block/smooth_end_stone"));
        stairsBlockWithItem((StairBlock) ModBlocks.COBBLED_END_STONE_STAIRS_BLOCK.get(), Goety.location("block/cobbled_end_stone"));

        trapdoorBlock((TrapDoorBlock) ModBlocks.HAUNTED_TRAPDOOR.get(), Goety.location("block/haunted_trapdoor"), true);
        trapdoorBlockWithRenderType((TrapDoorBlock) ModBlocks.ROTTEN_TRAPDOOR.get(), Goety.location("block/rotten_trapdoor"), true, "cutout");
        trapdoorBlockWithRenderType((TrapDoorBlock) ModBlocks.WINDSWEPT_TRAPDOOR.get(), Goety.location("block/windswept_trapdoor"), false, "cutout");
        trapdoorBlockWithRenderType((TrapDoorBlock) ModBlocks.PINE_TRAPDOOR.get(), Goety.location("block/pine_trapdoor"), false, "cutout");
        trapdoorBlockWithRenderType((TrapDoorBlock) ModBlocks.CHORUS_TRAPDOOR.get(), Goety.location("block/chorus_trapdoor"), false, "cutout");
        trapdoorBlockWithRenderType((TrapDoorBlock) ModBlocks.CORRUPT_CHORUS_TRAPDOOR.get(), Goety.location("block/corrupt_chorus_trapdoor"), false, "cutout");

        wallBlockWithItem((WallBlock) ModBlocks.CRAGROCKS_WALL_BLOCK.get(), Goety.location("block/cragrocks"));
        wallBlockWithItem((WallBlock) ModBlocks.CRAG_TILE_WALL_BLOCK.get(), Goety.location("block/crag_tiles"));
        wallBlockWithItem((WallBlock) ModBlocks.CRAG_BRICK_WALL_BLOCK.get(), Goety.location("block/crag_bricks"));
        wallBlockWithItem((WallBlock) ModBlocks.SNOWY_CRAG_BRICK_WALL_BLOCK.get(), Goety.location("block/snowy_crag_bricks"));

        wallBlockWithItem((WallBlock) ModBlocks.HIGHROCK_WALL_BLOCK.get(), Goety.location("block/highrock"));
        wallBlockWithItem((WallBlock) ModBlocks.POLISHED_HIGHROCK_WALL_BLOCK.get(), Goety.location("block/polished_highrock"));
        wallBlockWithItem((WallBlock) ModBlocks.HIGHROCK_BRICK_WALL_BLOCK.get(), Goety.location("block/highrock_bricks"));

        wallBlockWithItem((WallBlock) ModBlocks.SILTSTONE_WALL_BLOCK.get(), Goety.location("block/siltstone"));
        wallBlockWithItem((WallBlock) ModBlocks.SMOOTH_SILTSTONE_WALL_BLOCK.get(), Goety.location("block/smooth_siltstone"));
        wallBlockWithItem((WallBlock) ModBlocks.COBBLED_SILTSTONE_WALL_BLOCK.get(), Goety.location("block/cobbled_siltstone"));
        wallBlockWithItem((WallBlock) ModBlocks.SNOWY_COBBLED_SILTSTONE_WALL_BLOCK.get(), Goety.location("block/snowy_cobbled_siltstone"));
        wallBlockWithItem((WallBlock) ModBlocks.SILTSTONE_TILE_WALL_BLOCK.get(), Goety.location("block/siltstone_tiles"));
        wallBlockWithItem((WallBlock) ModBlocks.SILTSTONE_BRICK_WALL_BLOCK.get(), Goety.location("block/siltstone_bricks"));
        wallBlockWithItem((WallBlock) ModBlocks.SNOWY_SILTSTONE_BRICK_SLIGHT_WALL_BLOCK.get(), Goety.location("block/snowy_siltstone_bricks_slight"));
        wallBlockWithItem((WallBlock) ModBlocks.SNOWY_SILTSTONE_BRICK_WALL_BLOCK.get(), Goety.location("block/snowy_siltstone_bricks"));
        wallBlockWithItem((WallBlock) ModBlocks.SILTSTONE_PAVEMENT_WALL_BLOCK.get(), Goety.location("block/siltstone_pavement"));

        wallBlockWithItem((WallBlock) ModBlocks.INDENTED_GOLD_WALL_BLOCK.get(), Goety.location("block/indented_gold"));

        wallBlockWithItem((WallBlock) ModBlocks.SNOW_BRICK_WALL_BLOCK.get(), Goety.location("block/snow_bricks"));

        wallBlockWithItem((WallBlock) ModBlocks.END_ROCK_BRICK_WALL_BLOCK.get(), Goety.location("block/end_rock_bricks"));
        wallBlockWithItem((WallBlock) ModBlocks.SMOOTH_END_STONE_WALL_BLOCK.get(), Goety.location("block/smooth_end_stone"));
        wallBlockWithItem((WallBlock) ModBlocks.COBBLED_END_STONE_WALL_BLOCK.get(), Goety.location("block/cobbled_end_stone"));

        wallBlockWithItem((WallBlock) ModBlocks.PURPUR_WALL.get(), new ResourceLocation("block/purpur_block"));

        wallBlockWithItem((WallBlock) ModBlocks.WINDSWEPT_PLANK_WALL_BLOCK.get(), Goety.location("block/windswept_plank_wall"));
        wallBlockWithItem((WallBlock) ModBlocks.SNOWY_WINDSWEPT_PLANK_WALL_BLOCK.get(), Goety.location("block/snowy_windswept_plank_wall"));

        wallBlockWithItem((WallBlock) ModBlocks.STEEP_WALL_BLOCK.get(), Goety.location("block/steep_log"));
        wallBlockWithItem((WallBlock) ModBlocks.STUDDED_STEEP_WALL_BLOCK.get(), Goety.location("block/studded_steep_log"));
        wallBlockWithItem((WallBlock) ModBlocks.LINED_STEEP_WALL_BLOCK.get(), Goety.location("block/lined_steep_log"));
        wallBlockWithItem((WallBlock) ModBlocks.RIMMED_STEEP_WALL_BLOCK.get(), Goety.location("block/rimmed_steep_log"));
    }

    public void simpleBlockWithItem(Block block) {
        simpleBlock(block, cubeAll(block));
        simpleBlockItem(block, cubeAll(block));
    }

    public void slabBlockWithItem(SlabBlock block, ResourceLocation texture){
        slabBlock(block, texture);
        String baseName = key(block).toString();
        simpleBlockItem(block, models().slab(baseName, texture, texture, texture));
    }

    public void slabBlockWithItem(SlabBlock block, ResourceLocation doubleSlab, ResourceLocation texture){
        slabBlock(block, doubleSlab, texture);
        String baseName = key(block).toString();
        simpleBlockItem(block, models().slab(baseName, texture, texture, texture));
    }

    public void slabBlockWithItem(SlabBlock block, ResourceLocation doubleSlab, ResourceLocation side, ResourceLocation top){
        slabBlock(block, doubleSlab, side, top);
        String baseName = key(block).toString();
        simpleBlockItem(block, models().slab(baseName, side, top, top));
    }

    public void slabBlock(SlabBlock block, ResourceLocation texture) {
        slabBlock(block, texture, texture, texture, texture);
    }

    public void slabBlock(SlabBlock block, ResourceLocation doubleSlab, ResourceLocation side, ResourceLocation top) {
        slabBlock(block, doubleSlab, side, top, top);
    }

    public void stairsBlockWithItem(StairBlock block, ResourceLocation texture) {
        stairsBlock(block, texture, texture, texture);
        String baseName = key(block).toString();
        simpleBlockItem(block, models().stairs(baseName, texture, texture, texture));
    }

    public void buttonBlockWithItem(ButtonBlock block, ResourceLocation texture){
        buttonBlock(block, texture);
        buttonBlockInventory(block, texture);
        simpleBlockItem(block, buttonBlockInventory(block, texture));
    }

    public ModelFile buttonBlockInventory(ButtonBlock block, ResourceLocation texture){
        String baseName = key(block).toString();
        return models().buttonInventory(baseName + "_inventory", texture);
    }

    public void sideBottomTopBlock(Block block, ResourceLocation top, ResourceLocation side, ResourceLocation bottom){
        String baseName = key(block).toString();
        models().cube(baseName, bottom, top, side, side, side, side).texture("particle", side);
        simpleBlockWithItem(block, models().cube(baseName, bottom, top, side, side, side, side).texture("particle", side));
    }

    public void logBlockWithItem(RotatedPillarBlock block){
        logBlock(block);
        String baseName = key(block).toString();
        simpleBlockItem(block, models().cubeColumn(baseName, blockTexture(block), extend(blockTexture(block), "_top")));
    }

    public void columnBlockWithItem(RotatedPillarBlock block, ResourceLocation texture){
        columnBlock(block, texture);
        String baseName = key(block).toString();
        simpleBlockItem(block, models().cubeColumn(baseName, texture, texture));
    }

    public void columnBlock(RotatedPillarBlock block, ResourceLocation texture){
        axisBlock(block,
                models().cubeColumn(name(block), texture, texture),
                models().cubeColumn(name(block), texture, texture));
    }

    public void fenceGateWithItem(FenceGateBlock block, ResourceLocation texture){
        fenceGateBlock(block, texture);
        String baseName = key(block).toString();
        simpleBlockItem(block, models().fenceGate(baseName, texture));
    }

    public void fenceBlockWithItem(FenceBlock block, ResourceLocation texture){
        fenceBlock(block, texture);
        fenceInventory(block, texture);
        simpleBlockItem(block, fenceInventory(block, texture));
    }

    public ModelFile fenceInventory(FenceBlock block, ResourceLocation texture){
        String baseName = key(block).toString();
        return models().fenceInventory(baseName + "_inventory", texture);
    }

    public void fenceBlock(FenceBlock block, ResourceLocation texture) {
        String baseName = key(block).toString();
        fourWayBlock(block,
                models().fencePost(baseName + "_post", texture),
                models().fenceSide(baseName + "_side", texture));
        fenceInventory(block, texture);
    }

    public void pressurePlateWithItem(PressurePlateBlock block, ResourceLocation texture){
        pressurePlateBlock(block, texture);
        String baseName = key(block).toString();
        simpleBlockItem(block, models().pressurePlate(baseName, texture));
    }

    public void wallBlockWithItem(WallBlock block, ResourceLocation texture){
        wallBlock(block, texture);
        wallBlockInventory(block, texture);
        simpleBlockItem(block, wallBlockInventory(block, texture));
    }

    public void wallBlock(WallBlock block, ResourceLocation texture) {
        wallBlockInternal(block, key(block).toString(), texture);
    }

    public ModelFile wallBlockInventory(WallBlock block, ResourceLocation texture){
        String baseName = key(block).toString();
        return models().wallInventory(baseName + "_inventory", texture);
    }

    private void wallBlockInternal(WallBlock block, String baseName, ResourceLocation texture) {
        wallBlock(block, models().wallPost(baseName + "_post", texture),
                models().wallSide(baseName + "_side", texture),
                models().wallSideTall(baseName + "_side_tall", texture));
    }

    protected void builtinEntity(Block b, String particle) {
        simpleBlock(b, models().getBuilder(name(b))
                .parent(new ModelFile.UncheckedModelFile("builtin/entity"))
                .texture("particle", particle));
    }

    protected void plushie(Block b) {
        simpleBlock(b, models().getBuilder(name(b))
                .parent(new ModelFile.UncheckedModelFile("builtin/entity"))
                .texture("particle", "minecraft:block/white_wool"));
        itemModels().getBuilder(key(b).getPath())
                .parent(new ModelFile.UncheckedModelFile("builtin/entity"))
                .texture("particle", "minecraft:block/white_wool")
                .transforms()
                .transform(ItemDisplayContext.HEAD).rotation(180, 0, 180).translation(0, 30, 0).scale(3.0F, 3.0F, 3.0F).end()
                .transform(ItemDisplayContext.FIXED).rotation(0, 180, 0).translation(0, 4, 0).scale(1, 1, 1).end()
                .transform(ItemDisplayContext.GROUND).rotation(0, 0, 0).translation(0, 3, 0).scale(0.5F, 0.5F, 0.5F).end()
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).rotation(45, 45, 0).translation(0, 3, 0).scale(0.5F, 0.5F, 0.5F).end()
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(0, 180, 0).translation(0, 0, 0).scale(1, 1, 1).end();
    }

    public ModelFile cross(Block block) {
        return models().cross(name(block), blockTexture(block)).renderType("cutout");
    }

    public ItemModelBuilder generatedItem(Block block) {
        return itemModels().getBuilder(key(block).getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", blockTexture(block));
    }

    protected void crossBlockWithItem(Block block) {
        simpleBlock(block, cross(block));
        generatedItem(block);
    }

    private ResourceLocation extend(ResourceLocation rl, String suffix) {
        return new ResourceLocation(rl.getNamespace(), rl.getPath() + suffix);
    }

    private String name(Block block) {
        return key(block).getPath();
    }

    private ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

}
