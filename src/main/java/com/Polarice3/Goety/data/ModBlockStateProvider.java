package com.Polarice3.Goety.data;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraftforge.client.model.generators.BlockStateProvider;
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

        buttonBlockWithItem((ButtonBlock) ModBlocks.HAUNTED_BUTTON.get(), Goety.location("block/haunted_planks"));
        buttonBlockWithItem((ButtonBlock) ModBlocks.ROTTEN_BUTTON.get(), Goety.location("block/rotten_planks"));
        buttonBlockWithItem((ButtonBlock) ModBlocks.WINDSWEPT_BUTTON.get(), Goety.location("block/windswept_planks"));
        buttonBlockWithItem((ButtonBlock) ModBlocks.STEEP_BUTTON.get(), Goety.location("block/steep_planks"));

        doorBlockWithRenderType((DoorBlock) ModBlocks.HAUNTED_DOOR.get(), Goety.location("block/haunted_door_bottom"), Goety.location("block/haunted_door_top"), "cutout");
        doorBlockWithRenderType((DoorBlock) ModBlocks.ROTTEN_DOOR.get(), Goety.location("block/rotten_door_bottom"), Goety.location("block/rotten_door_top"), "cutout");
        doorBlockWithRenderType((DoorBlock) ModBlocks.WINDSWEPT_DOOR.get(), Goety.location("block/windswept_door_bottom"), Goety.location("block/windswept_door_top"), "cutout");

        fenceGateWithItem((FenceGateBlock) ModBlocks.HAUNTED_FENCE_GATE.get(), Goety.location("block/haunted_planks"));
        fenceGateWithItem((FenceGateBlock) ModBlocks.ROTTEN_FENCE_GATE.get(), Goety.location("block/rotten_planks"));
        fenceGateWithItem((FenceGateBlock) ModBlocks.WINDSWEPT_FENCE_GATE.get(), Goety.location("block/windswept_planks"));
        fenceGateWithItem((FenceGateBlock) ModBlocks.STEEP_FENCE_GATE.get(), Goety.location("block/steep_planks"));

        fenceBlockWithItem((FenceBlock) ModBlocks.HAUNTED_FENCE.get(), Goety.location("block/haunted_planks"));
        fenceBlockWithItem((FenceBlock) ModBlocks.ROTTEN_FENCE.get(), Goety.location("block/rotten_planks"));
        fenceBlockWithItem((FenceBlock) ModBlocks.WINDSWEPT_FENCE.get(), Goety.location("block/windswept_planks"));
        fenceBlockWithItem((FenceBlock) ModBlocks.STEEP_FENCE.get(), Goety.location("block/steep_planks"));

        logBlockWithItem((RotatedPillarBlock) ModBlocks.HAUNTED_LOG.get());
        logBlockWithItem((RotatedPillarBlock) ModBlocks.ROTTEN_LOG.get());
        logBlockWithItem((RotatedPillarBlock) ModBlocks.WINDSWEPT_LOG.get());

        logBlockWithItem((RotatedPillarBlock) ModBlocks.STRIPPED_HAUNTED_LOG.get());
        logBlockWithItem((RotatedPillarBlock) ModBlocks.STRIPPED_ROTTEN_LOG.get());
        logBlockWithItem((RotatedPillarBlock) ModBlocks.STRIPPED_WINDSWEPT_LOG.get());

        columnBlockWithItem((RotatedPillarBlock) ModBlocks.HAUNTED_WOOD.get(), Goety.location("block/haunted_log"));
        columnBlockWithItem((RotatedPillarBlock) ModBlocks.ROTTEN_WOOD.get(), Goety.location("block/rotten_log"));
        columnBlockWithItem((RotatedPillarBlock) ModBlocks.WINDSWEPT_WOOD.get(), Goety.location("block/windswept_log"));
        columnBlockWithItem((RotatedPillarBlock) ModBlocks.STEEP_WOOD.get(), Goety.location("block/steep_log"));

        columnBlockWithItem((RotatedPillarBlock) ModBlocks.STRIPPED_HAUNTED_WOOD.get(), Goety.location("block/stripped_haunted_log"));
        columnBlockWithItem((RotatedPillarBlock) ModBlocks.STRIPPED_ROTTEN_WOOD.get(), Goety.location("block/stripped_rotten_log"));
        columnBlockWithItem((RotatedPillarBlock) ModBlocks.STRIPPED_WINDSWEPT_WOOD.get(), Goety.location("block/stripped_windswept_log"));

        pressurePlateWithItem((PressurePlateBlock) ModBlocks.HAUNTED_PRESSURE_PLATE.get(), Goety.location("block/haunted_planks"));
        pressurePlateWithItem((PressurePlateBlock) ModBlocks.ROTTEN_PRESSURE_PLATE.get(), Goety.location("block/rotten_planks"));
        pressurePlateWithItem((PressurePlateBlock) ModBlocks.WINDSWEPT_PRESSURE_PLATE.get(), Goety.location("block/windswept_planks"));
        pressurePlateWithItem((PressurePlateBlock) ModBlocks.STEEP_PRESSURE_PLATE.get(), Goety.location("block/steep_planks"));

        slabBlockWithItem((SlabBlock) ModBlocks.HAUNTED_SLAB.get(), Goety.location("block/haunted_planks"));
        slabBlockWithItem((SlabBlock) ModBlocks.ROTTEN_SLAB.get(), Goety.location("block/rotten_planks"));
        slabBlockWithItem((SlabBlock) ModBlocks.WINDSWEPT_SLAB.get(), Goety.location("block/windswept_planks"));
        slabBlockWithItem((SlabBlock) ModBlocks.COMPACTED_WINDSWEPT_SLAB.get(), Goety.location("block/compacted_windswept_planks"));
        slabBlockWithItem((SlabBlock) ModBlocks.STEEP_SLAB.get(), Goety.location("block/steep_planks"));
        slabBlockWithItem((SlabBlock) ModBlocks.STEEP_WOOD_SLAB.get(), Goety.location("block/steep_wood"), Goety.location("block/steep_log"));

        slabBlockWithItem((SlabBlock) ModBlocks.CRACKED_MARBLE_SLAB.get(), Goety.location("block/cracked_marble"));
        slabBlockWithItem((SlabBlock) ModBlocks.SMOOTH_MARBLE_SLAB.get(), Goety.location("block/marble"));
        slabBlockWithItem((SlabBlock) ModBlocks.SLATE_MARBLE_SLAB.get(), Goety.location("block/slate_marble"));

        slabBlockWithItem((SlabBlock) ModBlocks.HIGHROCK_SLAB.get(), Goety.location("block/highrock"));
        slabBlockWithItem((SlabBlock) ModBlocks.POLISHED_HIGHROCK_SLAB.get(), Goety.location("block/polished_highrock"));
        slabBlockWithItem((SlabBlock) ModBlocks.HIGHROCK_BRICK_SLAB.get(), Goety.location("block/highrock_bricks"));

        slabBlockWithItem((SlabBlock) ModBlocks.SNOW_BRICK_SLAB.get(), Goety.location("block/snow_bricks"));

        stairsBlockWithItem((StairBlock) ModBlocks.HAUNTED_STAIRS.get(), Goety.location("block/haunted_planks"));
        stairsBlockWithItem((StairBlock) ModBlocks.ROTTEN_STAIRS.get(), Goety.location("block/rotten_planks"));
        stairsBlockWithItem((StairBlock) ModBlocks.WINDSWEPT_STAIRS.get(), Goety.location("block/windswept_planks"));
        stairsBlockWithItem((StairBlock) ModBlocks.STEEP_STAIRS.get(), Goety.location("block/steep_planks"));

        stairsBlockWithItem((StairBlock) ModBlocks.MARBLE_STAIRS_BLOCK.get(), Goety.location("block/marble"));
        stairsBlockWithItem((StairBlock) ModBlocks.SLATE_MARBLE_STAIRS_BLOCK.get(), Goety.location("block/slate_marble"));

        stairsBlockWithItem((StairBlock) ModBlocks.HIGHROCK_STAIRS.get(), Goety.location("block/highrock"));
        stairsBlockWithItem((StairBlock) ModBlocks.POLISHED_HIGHROCK_STAIRS.get(), Goety.location("block/polished_highrock"));
        stairsBlockWithItem((StairBlock) ModBlocks.HIGHROCK_BRICK_STAIRS.get(), Goety.location("block/highrock_bricks"));

        stairsBlockWithItem((StairBlock) ModBlocks.SNOW_BRICK_STAIRS_BLOCK.get(), Goety.location("block/snow_bricks"));

        trapdoorBlock((TrapDoorBlock) ModBlocks.HAUNTED_TRAPDOOR.get(), Goety.location("block/haunted_trapdoor"), true);
        trapdoorBlockWithRenderType((TrapDoorBlock) ModBlocks.ROTTEN_TRAPDOOR.get(), Goety.location("block/rotten_trapdoor"), true, "cutout");
        trapdoorBlockWithRenderType((TrapDoorBlock) ModBlocks.WINDSWEPT_TRAPDOOR.get(), Goety.location("block/windswept_trapdoor"), false, "cutout");

        wallBlockWithItem((WallBlock) ModBlocks.HIGHROCK_WALL_BLOCK.get(), Goety.location("block/highrock"));
        wallBlockWithItem((WallBlock) ModBlocks.POLISHED_HIGHROCK_WALL_BLOCK.get(), Goety.location("block/polished_highrock"));
        wallBlockWithItem((WallBlock) ModBlocks.HIGHROCK_BRICK_WALL_BLOCK.get(), Goety.location("block/highrock_bricks"));

        wallBlockWithItem((WallBlock) ModBlocks.INDENTED_GOLD_WALL_BLOCK.get(), Goety.location("block/indented_gold"));

        wallBlockWithItem((WallBlock) ModBlocks.SNOW_BRICK_WALL_BLOCK.get(), Goety.location("block/snow_bricks"));

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

    public void slabBlock(SlabBlock block, ResourceLocation texture) {
        slabBlock(block, texture, texture, texture, texture);
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
