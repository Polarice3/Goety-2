package com.Polarice3.Goety.client.render.block;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.entities.ModChestBlockEntity;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.ChestType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Based on @TeamTwilight's TwilightChestRenderer: <a href="https://github.com/TeamTwilight/twilightforest/blob/1.20.x/src/main/java/twilightforest/client/renderer/tileentity/TwilightChestRenderer.java">...</a>
 */
public class ModChestRenderer<T extends ModChestBlockEntity> extends ChestRenderer<T> {
    public static final Map<Block, EnumMap<ChestType, Material>> MATERIALS;

    static {
        ImmutableMap.Builder<Block, EnumMap<ChestType, Material>> builder = ImmutableMap.builder();

        builder.put(ModBlocks.HAUNTED_CHEST.get(), chestMaterial("haunted"));
        builder.put(ModBlocks.TRAPPED_HAUNTED_CHEST.get(), trappedMaterial("haunted"));
        builder.put(ModBlocks.ROTTEN_CHEST.get(), chestMaterial("rotten"));
        builder.put(ModBlocks.TRAPPED_ROTTEN_CHEST.get(), trappedMaterial("rotten"));
        builder.put(ModBlocks.WINDSWEPT_CHEST.get(), chestMaterial("windswept"));
        builder.put(ModBlocks.TRAPPED_WINDSWEPT_CHEST.get(), trappedMaterial("windswept"));

        MATERIALS = builder.build();
    }

    public ModChestRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected Material getMaterial(T blockEntity, ChestType chestType) {
        EnumMap<ChestType, Material> b = MATERIALS.get(blockEntity.getBlockState().getBlock());

        if (b == null) return super.getMaterial(blockEntity, chestType);

        Material material = b.get(chestType);

        return material != null ? material : super.getMaterial(blockEntity, chestType);
    }

    private static EnumMap<ChestType, Material> chestMaterial(String type) {
        EnumMap<ChestType, Material> map = new EnumMap<>(ChestType.class);

        map.put(ChestType.SINGLE, new Material(Sheets.CHEST_SHEET, Goety.location("entity/chest/" + type + "/" + "normal")));
        map.put(ChestType.LEFT, new Material(Sheets.CHEST_SHEET, Goety.location("entity/chest/" + type + "/normal_left")));
        map.put(ChestType.RIGHT, new Material(Sheets.CHEST_SHEET, Goety.location("entity/chest/" + type + "/normal_right")));

        return map;
    }

    private static EnumMap<ChestType, Material> trappedMaterial(String type) {
        EnumMap<ChestType, Material> map = new EnumMap<>(ChestType.class);

        map.put(ChestType.SINGLE, new Material(Sheets.CHEST_SHEET, Goety.location("entity/chest/" + type + "/" + "trapped")));
        map.put(ChestType.LEFT, new Material(Sheets.CHEST_SHEET, Goety.location("entity/chest/" + type + "/trapped_left")));
        map.put(ChestType.RIGHT, new Material(Sheets.CHEST_SHEET, Goety.location("entity/chest/" + type + "/trapped_right")));

        return map;
    }
}
