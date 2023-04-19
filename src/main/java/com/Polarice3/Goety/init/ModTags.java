package com.Polarice3.Goety.init;

import com.Polarice3.Goety.Goety;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static void init () {
        ModTags.Blocks.init();
        ModTags.Items.init();
    }

    public static class Blocks {
        private static void init(){}

        public static final TagKey<Block> HAUNTED_LOGS = tag("haunted_logs");

        private static TagKey<Block> tag(String name)
        {
            return BlockTags.create(Goety.location(name));
        }
    }

    public static class Items {
        private static void init(){}

        public static final TagKey<Item> LICH_WITHER_ITEMS = tag("lich_wither_items");

        private static TagKey<Item> tag(String name)
        {
            return ItemTags.create(Goety.location(name));
        }
    }
}
