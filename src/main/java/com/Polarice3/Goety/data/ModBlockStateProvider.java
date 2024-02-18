package com.Polarice3.Goety.data;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.*;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, Goety.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        buttonBlock((ButtonBlock) ModBlocks.HAUNTED_BUTTON.get(), Goety.location("block/haunted_planks"));
        buttonBlock((ButtonBlock) ModBlocks.ROTTEN_BUTTON.get(), Goety.location("block/rotten_planks"));
        buttonBlock((ButtonBlock) ModBlocks.WINDSWEPT_BUTTON.get(), Goety.location("block/windswept_planks"));
        buttonBlock((ButtonBlock) ModBlocks.STEEP_BUTTON.get(), Goety.location("block/steep_planks"));

        doorBlockWithRenderType((DoorBlock) ModBlocks.HAUNTED_DOOR.get(), Goety.location("block/haunted_door_bottom"), Goety.location("block/haunted_door_top"), "cutout");
        doorBlockWithRenderType((DoorBlock) ModBlocks.ROTTEN_DOOR.get(), Goety.location("block/rotten_door_bottom"), Goety.location("block/rotten_door_top"), "cutout");
        doorBlockWithRenderType((DoorBlock) ModBlocks.WINDSWEPT_DOOR.get(), Goety.location("block/windswept_door_bottom"), Goety.location("block/windswept_door_top"), "cutout");

        fenceGateBlock((FenceGateBlock) ModBlocks.HAUNTED_FENCE_GATE.get(), Goety.location("block/haunted_planks"));
        fenceGateBlock((FenceGateBlock) ModBlocks.ROTTEN_FENCE_GATE.get(), Goety.location("block/rotten_planks"));
        fenceGateBlock((FenceGateBlock) ModBlocks.WINDSWEPT_FENCE_GATE.get(), Goety.location("block/windswept_planks"));
        fenceGateBlock((FenceGateBlock) ModBlocks.STEEP_FENCE_GATE.get(), Goety.location("block/steep_planks"));

        fenceBlock((FenceBlock) ModBlocks.HAUNTED_FENCE.get(), Goety.location("block/haunted_planks"));
        fenceBlock((FenceBlock) ModBlocks.ROTTEN_FENCE.get(), Goety.location("block/rotten_planks"));
        fenceBlock((FenceBlock) ModBlocks.WINDSWEPT_FENCE.get(), Goety.location("block/windswept_planks"));
        fenceBlock((FenceBlock) ModBlocks.STEEP_FENCE.get(), Goety.location("block/steep_planks"));

        logBlock((RotatedPillarBlock) ModBlocks.HAUNTED_LOG.get());
        logBlock((RotatedPillarBlock) ModBlocks.ROTTEN_LOG.get());
        logBlock((RotatedPillarBlock) ModBlocks.WINDSWEPT_LOG.get());

        logBlock((RotatedPillarBlock) ModBlocks.STRIPPED_HAUNTED_LOG.get());
        logBlock((RotatedPillarBlock) ModBlocks.STRIPPED_ROTTEN_LOG.get());
        logBlock((RotatedPillarBlock) ModBlocks.STRIPPED_WINDSWEPT_LOG.get());

        pressurePlateBlock((PressurePlateBlock) ModBlocks.HAUNTED_PRESSURE_PLATE.get(), Goety.location("block/haunted_planks"));
        pressurePlateBlock((PressurePlateBlock) ModBlocks.ROTTEN_PRESSURE_PLATE.get(), Goety.location("block/rotten_planks"));
        pressurePlateBlock((PressurePlateBlock) ModBlocks.WINDSWEPT_PRESSURE_PLATE.get(), Goety.location("block/windswept_planks"));
        pressurePlateBlock((PressurePlateBlock) ModBlocks.STEEP_PRESSURE_PLATE.get(), Goety.location("block/steep_planks"));

        slabBlock((SlabBlock) ModBlocks.HAUNTED_SLAB.get(), Goety.location("block/haunted_planks"), Goety.location("block/haunted_planks"));
        slabBlock((SlabBlock) ModBlocks.ROTTEN_SLAB.get(), Goety.location("block/rotten_planks"), Goety.location("block/rotten_planks"));
        slabBlock((SlabBlock) ModBlocks.WINDSWEPT_SLAB.get(), Goety.location("block/windswept_planks"), Goety.location("block/windswept_planks"));
        slabBlock((SlabBlock) ModBlocks.COMPACTED_WINDSWEPT_SLAB.get(), Goety.location("block/compacted_windswept_planks"), Goety.location("block/compacted_windswept_planks"), Goety.location("block/compacted_windswept_planks"), Goety.location("block/compacted_windswept_planks"));
        slabBlock((SlabBlock) ModBlocks.STEEP_SLAB.get(), Goety.location("block/steep_planks"), Goety.location("block/steep_planks"));
        slabBlock((SlabBlock) ModBlocks.STEEP_WOOD_SLAB.get(), Goety.location("block/steep_wood"), Goety.location("block/steep_log"));

        slabBlock((SlabBlock) ModBlocks.HIGHROCK_SLAB.get(), Goety.location("block/highrock"), Goety.location("block/highrock"));
        slabBlock((SlabBlock) ModBlocks.POLISHED_HIGHROCK_SLAB.get(), Goety.location("block/polished_highrock"), Goety.location("block/polished_highrock"));
        slabBlock((SlabBlock) ModBlocks.HIGHROCK_BRICK_SLAB.get(), Goety.location("block/highrock_bricks"), Goety.location("block/highrock_bricks"));

        stairsBlock((StairBlock) ModBlocks.HAUNTED_STAIRS.get(), Goety.location("block/haunted_planks"));
        stairsBlock((StairBlock) ModBlocks.ROTTEN_STAIRS.get(), Goety.location("block/rotten_planks"));
        stairsBlock((StairBlock) ModBlocks.WINDSWEPT_STAIRS.get(), Goety.location("block/windswept_planks"));
        stairsBlock((StairBlock) ModBlocks.STEEP_STAIRS.get(), Goety.location("block/steep_planks"));

        stairsBlock((StairBlock) ModBlocks.MARBLE_STAIRS_BLOCK.get(), Goety.location("block/marble"));
        stairsBlock((StairBlock) ModBlocks.SLATE_MARBLE_STAIRS_BLOCK.get(), Goety.location("block/slate_marble"));

        stairsBlock((StairBlock) ModBlocks.HIGHROCK_STAIRS.get(), Goety.location("block/highrock"));
        stairsBlock((StairBlock) ModBlocks.POLISHED_HIGHROCK_STAIRS.get(), Goety.location("block/polished_highrock"));
        stairsBlock((StairBlock) ModBlocks.HIGHROCK_BRICK_STAIRS.get(), Goety.location("block/highrock_bricks"));

        trapdoorBlock((TrapDoorBlock) ModBlocks.HAUNTED_TRAPDOOR.get(), Goety.location("block/haunted_trapdoor"), true);
        trapdoorBlockWithRenderType((TrapDoorBlock) ModBlocks.ROTTEN_TRAPDOOR.get(), Goety.location("block/rotten_trapdoor"), true, "cutout");
        trapdoorBlockWithRenderType((TrapDoorBlock) ModBlocks.WINDSWEPT_TRAPDOOR.get(), Goety.location("block/windswept_trapdoor"), false, "cutout");

        wallBlock((WallBlock) ModBlocks.HIGHROCK_WALL_BLOCK.get(), Goety.location("block/highrock"));
        wallBlock((WallBlock) ModBlocks.POLISHED_HIGHROCK_WALL_BLOCK.get(), Goety.location("block/polished_highrock"));
        wallBlock((WallBlock) ModBlocks.HIGHROCK_BRICK_WALL_BLOCK.get(), Goety.location("block/highrock_bricks"));

        wallBlock((WallBlock) ModBlocks.INDENTED_GOLD_WALL_BLOCK.get(), Goety.location("block/indented_gold"));

        wallBlock((WallBlock) ModBlocks.STEEP_WALL_BLOCK.get(), Goety.location("block/steep_log"));
        wallBlock((WallBlock) ModBlocks.STUDDED_STEEP_WALL_BLOCK.get(), Goety.location("block/studded_steep_log"));
        wallBlock((WallBlock) ModBlocks.LINED_STEEP_WALL_BLOCK.get(), Goety.location("block/lined_steep_log"));
        wallBlock((WallBlock) ModBlocks.RIMMED_STEEP_WALL_BLOCK.get(), Goety.location("block/rimmed_steep_log"));
    }
}
