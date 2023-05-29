package com.Polarice3.Goety.client.render.block;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.ModChestBlock;
import com.Polarice3.Goety.common.blocks.ModTrappedChestBlock;
import com.Polarice3.Goety.common.blocks.entities.ModChestBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraftforge.client.event.TextureStitchEvent;

import java.util.HashMap;
import java.util.Map;

public class ModChestRenderer<T extends ModChestBlockEntity> extends ChestRenderer<T> {
    private static final String path = "entity/chest/";
    private static Map<Block, ChestResources> RESOURCEMAP = new HashMap<>();

    public ModChestRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    public static void stitchChests(TextureStitchEvent.Pre event, Block chest) {
        ResourceLocation atlas = event.getAtlas().location();
        if (chest instanceof ModChestBlock) {
            if (chest instanceof ModTrappedChestBlock) {
                resourcePacker(event, atlas, chest, "trapped", "trapped_left", "trapped_right");
            } else {
                resourcePacker(event, atlas, chest, "normal", "normal_left", "normal_right");
            }
        }
    }

    private static void resourcePacker(TextureStitchEvent.Pre event, ResourceLocation atlas, Block chest, String middleLocation, String leftLocation, String rightLocation) {
        String deepPath;
        try {
            deepPath = path + ChestToString.getEnumFromChest(chest).getString();
        } catch (Exception e) {
            Goety.LOGGER.debug("Chest without EnumValue: " + chest);
            return;
        }
        ResourceLocation normal = new ResourceLocation(Goety.MOD_ID, deepPath + "/" + middleLocation);
        ResourceLocation left = new ResourceLocation(Goety.MOD_ID, deepPath + "/" + leftLocation);
        ResourceLocation right = new ResourceLocation(Goety.MOD_ID, deepPath + "/" + rightLocation);
        RESOURCEMAP.put(chest, new ChestResources(new Material(atlas, normal), new Material(atlas, left), new Material(atlas, right)));
        addSprites(event, normal, left, right);
    }

    private static void addSprites(TextureStitchEvent.Pre event, ResourceLocation normal, ResourceLocation left, ResourceLocation right) {
        event.addSprite(normal);
        event.addSprite(left);
        event.addSprite(right);
    }

    @Override
    protected Material getMaterial(T tileEntity, ChestType chestType) {
        ChestResources resources = RESOURCEMAP.get(tileEntity.getBlockState().getBlock());
        if (resources == null) {
            return null;
        }
        return switch (chestType) {
            default -> resources.getMain();
            case LEFT -> resources.getLeft();
            case RIGHT -> resources.getRight();
        };
    }

    private record ChestResources(Material main, Material left, Material right) {
        public Material getLeft() {
            return left;
        }

        public Material getMain() {
            return main;
        }

        public Material getRight() {
            return right;
        }
    }

    public enum ChestToString {

        HAUNTED(ModBlocks.HAUNTED_CHEST.get(), ModBlocks.TRAPPED_HAUNTED_CHEST.get(), "haunted");

        private final Block chest;
        private final Block trappedChest;
        private final String name;

        ChestToString(Block chest, Block trappedChest, String name) {
            this.chest = chest;
            this.trappedChest = trappedChest;
            this.name = name;
        }

        public static ChestToString getEnumFromChest(Block chest) {
            for (ChestToString index : ChestToString.values()) {
                if (index.getChest() == chest || index.getTrappedChest() == chest) {
                    return index;
                }
            }
            return null;
        }

        public Block getChest() {
            return chest;
        }

        public Block getTrappedChest() {
            return trappedChest;
        }

        public String getString() {
            return name;
        }

    }
}
