package com.Polarice3.Goety.common.blocks;

import com.google.common.collect.Maps;
import net.minecraft.core.Registry;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.stream.Stream;

public class ModBlockFamilies {
    private static final Map<Block, BlockFamily> MAP = Maps.newHashMap();
    public static final BlockFamily SHADE_STONES = familyBuilder(ModBlocks.SHADE_STONE_BLOCK.get())
            .slab(ModBlocks.SHADE_STONE_SLAB_BLOCK.get())
            .stairs(ModBlocks.SHADE_STONE_STAIRS_BLOCK.get())
            .chiseled(ModBlocks.SHADE_STONE_CHISELED_BLOCK.get()).getFamily();

    public static final BlockFamily SHADE_STONE_BRICKS = familyBuilder(ModBlocks.SHADE_STONE_BRICK_BLOCK.get())
            .slab(ModBlocks.SHADE_STONE_BRICK_SLAB_BLOCK.get())
            .stairs(ModBlocks.SHADE_STONE_BRICK_STAIRS_BLOCK.get())
            .wall(ModBlocks.SHADE_STONE_BRICK_WALL_BLOCK.get()).getFamily();

    public static final BlockFamily SHADE_BRICKS = familyBuilder(ModBlocks.SHADE_BRICK_BLOCK.get())
            .slab(ModBlocks.SHADE_BRICK_SLAB_BLOCK.get())
            .stairs(ModBlocks.SHADE_BRICK_STAIRS_BLOCK.get())
            .wall(ModBlocks.SHADE_BRICK_WALL_BLOCK.get()).getFamily();

    public static final BlockFamily SHADE_TILES = familyBuilder(ModBlocks.SHADE_TILES_BLOCK.get())
            .slab(ModBlocks.SHADE_TILES_SLAB_BLOCK.get())
            .stairs(ModBlocks.SHADE_TILES_STAIRS_BLOCK.get()).getFamily();

    public static final BlockFamily HAUNTED_PLANKS = familyBuilder(ModBlocks.HAUNTED_PLANKS.get())
            .button(ModBlocks.HAUNTED_BUTTON.get())
            .fence(ModBlocks.HAUNTED_FENCE.get())
            .fenceGate(ModBlocks.HAUNTED_FENCE_GATE.get())
            .pressurePlate(ModBlocks.HAUNTED_PRESSURE_PLATE.get())
            .sign(ModBlocks.HAUNTED_SIGN.get(), ModBlocks.HAUNTED_WALL_SIGN.get())
            .slab(ModBlocks.HAUNTED_SLAB.get())
            .stairs(ModBlocks.HAUNTED_STAIRS.get())
            .door(ModBlocks.HAUNTED_DOOR.get())
            .trapdoor(ModBlocks.HAUNTED_TRAPDOOR.get()).getFamily();

    private static BlockFamily.Builder familyBuilder(Block p_175936_) {
        BlockFamily.Builder blockfamily$builder = new BlockFamily.Builder(p_175936_);
        BlockFamily blockfamily = MAP.put(p_175936_, blockfamily$builder.getFamily());
        if (blockfamily != null) {
            throw new IllegalStateException("Duplicate family definition for " + Registry.BLOCK.getKey(p_175936_));
        } else {
            return blockfamily$builder;
        }
    }

    public static Stream<BlockFamily> getAllFamilies() {
        return MAP.values().stream();
    }
}
