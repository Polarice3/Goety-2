package com.Polarice3.Goety.init;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTags {

    public static void init () {
        Blocks.init();
        Items.init();
        Paintings.init();
        EntityTypes.init();
        Biomes.init();
        GameEvents.init();
        Structures.init();
    }

    public static class Blocks {
        private static void init(){}

        public static final TagKey<Block> HAUNTED_LOGS = tag("haunted_logs");
        public static final TagKey<Block> SHADE_STONE = tag("shade_stone");
        public static final TagKey<Block> MARBLE_BLOCKS = tag("marble_blocks");
        public static final TagKey<Block> INDENTED_GOLD_BLOCKS = tag("indented_gold_blocks");
        public static final TagKey<Block> JADE_BLOCKS = tag("jade_blocks");
        public static final TagKey<Block> PHILOSOPHERS_MACE_HARD = tag("philosophers_mace_hard");
        public static final TagKey<Block> RECALL_BLOCKS = tag("recall_blocks");
        public static final TagKey<Block> DARK_ANVILS = tag("dark_anvils");
        public static final TagKey<Block> TUNNEL_BLACKLIST = tag("tunnel_blacklist");
        public static final TagKey<Block> REDSTONE_CUBE_DETECT = tag("redstone_cube_detect");
        public static final TagKey<Block> REDSTONE_CUBE_EXEMPT = tag("redstone_cube_exempt");
        public static final TagKey<Block> MONSTROSITY_BREAKS = tag("monstrosity_breaks");

        private static TagKey<Block> tag(String name)
        {
            return BlockTags.create(Goety.location(name));
        }
    }

    public static class Items {
        private static void init(){}

        public static final TagKey<Item> WANDS = tag("wands");
        public static final TagKey<Item> STAFFS = tag("staffs");
        public static final TagKey<Item> BREWABLE_FOOD = tag("brewable_food");
        public static final TagKey<Item> LICH_WITHER_ITEMS = tag("lich_wither_items");
        public static final TagKey<Item> RESPAWN_BOSS = tag("respawn_boss");
        public static final TagKey<Item> WITCH_CURRENCY = tag("witch_currency");
        public static final TagKey<Item> WITCH_BETTER_CURRENCY = tag("witch_better_currency");

        private static TagKey<Item> tag(String name)
        {
            return ItemTags.create(Goety.location(name));
        }
    }

    public static class Paintings {
        private static void init(){}

        public static final TagKey<PaintingVariant> MODDED_PAINTINGS = tag("placeable");

        private static TagKey<PaintingVariant> tag(String name)
        {
            return create(Goety.location(name));
        }

        private static TagKey<PaintingVariant> create(ResourceLocation p_215874_) {
            return TagKey.create(ForgeRegistries.PAINTING_VARIANTS.getRegistryKey(), p_215874_);
        }
    }

    public static class EntityTypes {
        private static void init(){}

        public static final TagKey<EntityType<?>> CREEPERS = tag("creepers");
        public static final TagKey<EntityType<?>> ENDERMEN = tag("endermen");
        public static final TagKey<EntityType<?>> VILLAGERS = tag("villagers");
        public static final TagKey<EntityType<?>> APOSTLE_OTHER_ALLIES = tag("apostle_other_allies");
        public static final TagKey<EntityType<?>> WITCH_SET_NEUTRAL = tag("witch_set_neutral");

        private static TagKey<EntityType<?>> tag(String name) {
            return create(Goety.location(name));
        }

        private static TagKey<EntityType<?>> create(ResourceLocation p_215874_) {
            return TagKey.create(ForgeRegistries.ENTITY_TYPES.getRegistryKey(), p_215874_);
        }
    }

    public static class Biomes {
        private static void init(){}

        public static final TagKey<Biome> COMMON_BLACKLIST = tag("mob_spawn/common_blacklist");
        public static final TagKey<Biome> WRAITH_SPAWN = tag("mob_spawn/wraith");
        public static final TagKey<Biome> WARLOCK_SPAWN = tag("mob_spawn/warlock");

        public static final TagKey<Biome> ABYSS_DISCOUNT = tag("spell_discount/abyss");
        public static final TagKey<Biome> FROST_DISCOUNT = tag("spell_discount/frost");
        public static final TagKey<Biome> GEOMANCY_DISCOUNT = tag("spell_discount/geomancy");
        public static final TagKey<Biome> NECROMANCY_DISCOUNT = tag("spell_discount/necromancy");
        public static final TagKey<Biome> NETHER_DISCOUNT = tag("spell_discount/nether");
        public static final TagKey<Biome> STORM_DISCOUNT = tag("spell_discount/storm");
        public static final TagKey<Biome> VOID_DISCOUNT = tag("spell_discount/void");
        public static final TagKey<Biome> WILD_DISCOUNT = tag("spell_discount/wild");
        public static final TagKey<Biome> WIND_DISCOUNT = tag("spell_discount/wind");

        public static final TagKey<Biome> ABYSS_MARKUP = tag("spell_markup/abyss");
        public static final TagKey<Biome> FROST_MARKUP = tag("spell_markup/frost");
        public static final TagKey<Biome> GEOMANCY_MARKUP = tag("spell_markup/geomancy");
        public static final TagKey<Biome> NECROMANCY_MARKUP = tag("spell_markup/necromancy");
        public static final TagKey<Biome> NETHER_MARKUP = tag("spell_markup/nether");
        public static final TagKey<Biome> STORM_MARKUP = tag("spell_markup/storm");
        public static final TagKey<Biome> VOID_MARKUP = tag("spell_markup/void");
        public static final TagKey<Biome> WILD_MARKUP = tag("spell_markup/wild");
        public static final TagKey<Biome> WIND_MARKUP = tag("spell_markup/wind");

        private static TagKey<Biome> tag(String name) {
            return create(Goety.location(name));
        }

        private static TagKey<Biome> create(ResourceLocation p_215874_) {
            return TagKey.create(ForgeRegistries.BIOMES.getRegistryKey(), p_215874_);
        }
    }

    public static class GameEvents {
        private static void init(){}

        public static final TagKey<GameEvent> BLOCK_EVENTS = tag("block_events");

        private static TagKey<GameEvent> tag(String name) {
            return create(Goety.location(name));
        }

        private static TagKey<GameEvent> create(ResourceLocation p_215874_) {
            return TagKey.create(Registries.GAME_EVENT, p_215874_);
        }
    }

    public static class Structures {
        private static void init(){}

        public static final TagKey<Structure> WITHER_NECROMANCER_SPAWNS = tag("wither_necromancer_spawns");
        public static final TagKey<Structure> VIZIER_SPAWNS = tag("vizier_spawns");
        public static final TagKey<Structure> CRONE_SPAWNS = tag("crone_spawns");
        public static final TagKey<Structure> SKULL_LORD_SPAWNS = tag("skull_lord_spawns");
        public static final TagKey<Structure> CRYPT = tag("crypt");
        public static final TagKey<Structure> NECROMANCER_POWER = tag("necromancer_power");

        private static TagKey<Structure> tag(String name) {
            return create(Goety.location(name));
        }

        private static TagKey<Structure> create(ResourceLocation p_215874_) {
            return TagKey.create(Registries.STRUCTURE, p_215874_);
        }
    }
}
