package com.Polarice3.Goety.init;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTags {

    public static void init() {
        Blocks.init();
        Items.init();
        Effects.init();
        Paintings.init();
        BannerPatterns.init();
        EntityTypes.init();
        Biomes.init();
        GameEvents.init();
        Structures.init();
        DamageTypes.init();
    }

    public static class Blocks {
        private static void init(){}

        public static final TagKey<Block> HAUNTED_LOGS = tag("haunted_logs");
        public static final TagKey<Block> SHADE_STONE = tag("shade_stone");
        public static final TagKey<Block> MARBLE_BLOCKS = tag("marble_blocks");
        public static final TagKey<Block> INDENTED_GOLD_BLOCKS = tag("indented_gold_blocks");
        public static final TagKey<Block> JADE_BLOCKS = tag("jade_blocks");
        public static final TagKey<Block> END_STONE = tag("end_stone");
        public static final TagKey<Block> CHORUS_GRASS_BLOCKS = tag("chorus_grass_blocks");
        public static final TagKey<Block> END_SOIL_BLOCKS = tag("end_soil_blocks");
        public static final TagKey<Block> PLUSHIE = tag("plushie");
        public static final TagKey<Block> PHILOSOPHERS_MACE_HARD = tag("philosophers_mace_hard");
        public static final TagKey<Block> RECALL_BLOCKS = tag("recall_blocks");
        public static final TagKey<Block> RAIDING_CHESTS = tag("raiding_chests");
        public static final TagKey<Block> DARK_ANVILS = tag("dark_anvils");
        public static final TagKey<Block> VOID_BLOCKS = tag("void_blocks");
        public static final TagKey<Block> TUNNEL_BLACKLIST = tag("tunnel_blacklist");
        public static final TagKey<Block> NETHER_SPREAD = tag("nether_spread");
        public static final TagKey<Block> NETHER_SPREAD_REPLACEABLE = tag("nether_spread_replaceable");
        public static final TagKey<Block> CHORUS_GROW = tag("chorus_grow");
        public static final TagKey<Block> END_PLANTABLES = tag("end_plantables");
        public static final TagKey<Block> END_GROWTH_BLOCKS = tag("end_growth_blocks");
        public static final TagKey<Block> CHORUS_SAPLING_GROW = tag("chorus_sapling_grow");
        public static final TagKey<Block> CHORUS_BLOSSOM_GROW = tag("chorus_blossom_grow");
        public static final TagKey<Block> REDSTONE_CUBE_DETECT = tag("redstone_cube_detect");
        public static final TagKey<Block> REDSTONE_CUBE_EXEMPT = tag("redstone_cube_exempt");
        public static final TagKey<Block> PRISONER_MINEABLE = tag("prisoner_mineable");
        public static final TagKey<Block> PRISONER_UNMINEABLE = tag("prisoner_unmineable");
        public static final TagKey<Block> PRISONER_RARE_ORES = tag("prisoner_rare_ores");
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
        public static final TagKey<Item> TOTEMS = tag("totems");
        public static final TagKey<Item> ROBES = tag("robes");
        public static final TagKey<Item> CAPES = tag("capes");
        public static final TagKey<Item> CROWNS = tag("crowns");
        public static final TagKey<Item> FOCUSES = tag("focuses");
        public static final TagKey<Item> GRIMOIRES = tag("grimoires");
        public static final TagKey<Item> PLUSHIE = tag("plushie");
        public static final TagKey<Item> PILLAGER_WEAPONS = tag("pillager_weapons");
        public static final TagKey<Item> VINDICATOR_WEAPONS = tag("vindicator_weapons");
        public static final TagKey<Item> BREWABLE_FOOD = tag("brewable_food");
        public static final TagKey<Item> GRAVE_GLOVE_BOOST = tag("grave_glove_boost");
        public static final TagKey<Item> THRASH_GLOVE_BOOST = tag("thrash_glove_boost");
        public static final TagKey<Item> MAGIC_SWORD_SHOOTABLE = tag("magic_sword_shootable");
        public static final TagKey<Item> LICH_WITHER_ITEMS = tag("lich_wither_items");
        public static final TagKey<Item> RESPAWN_BOSS = tag("respawn_boss");
        public static final TagKey<Item> WITCH_CURRENCY = tag("witch_currency");
        public static final TagKey<Item> WITCH_BETTER_CURRENCY = tag("witch_better_currency");

        private static TagKey<Item> tag(String name)
        {
            return ItemTags.create(Goety.location(name));
        }
    }

    public static class Effects {
        private static void init(){}

        public static final TagKey<MobEffect> LICH_IMMUNE = tag("lich_immune");

        private static TagKey<MobEffect> tag(String name) {
            return TagKey.create(Registries.MOB_EFFECT, Goety.location(name));
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

    public static class BannerPatterns {
        private static void init(){}

        public static final TagKey<BannerPattern> PATTERN_ITEM_CROSS = tag("pattern_item/cross");
        public static final TagKey<BannerPattern> PATTERN_ITEM_GALE = tag("pattern_item/gale");
        public static final TagKey<BannerPattern> PATTERN_ITEM_MOON = tag("pattern_item/moon");

        private static TagKey<BannerPattern> tag(String name) {
            return create(Goety.location(name));
        }

        private static TagKey<BannerPattern> create(ResourceLocation p_215874_) {
            return TagKey.create(Registries.BANNER_PATTERN, p_215874_);
        }
    }

    public static class EntityTypes {
        private static void init(){}

        public static final TagKey<EntityType<?>> CREEPERS = tag("creepers");
        public static final TagKey<EntityType<?>> ENDERMEN = tag("endermen");
        public static final TagKey<EntityType<?>> VILLAGERS = tag("villagers");
        public static final TagKey<EntityType<?>> SERVANTS = tag("servants");
        public static final TagKey<EntityType<?>> VILLAGE_GUARDS = tag("village_guards");
        public static final TagKey<EntityType<?>> ZOMBIE_SERVANTS = tag("zombie_servants");
        public static final TagKey<EntityType<?>> SKELETON_SERVANTS = tag("skeleton_servants");
        public static final TagKey<EntityType<?>> NO_HEAL_SERVANTS = tag("no_heal_servants");
        public static final TagKey<EntityType<?>> POWERFUL_SERVANTS = tag("powerful_servants");
        public static final TagKey<EntityType<?>> TESSERACT_SMALL = tag("tesseract_small");
        public static final TagKey<EntityType<?>> TESSERACT_MEDIUM = tag("tesseract_medium");
        public static final TagKey<EntityType<?>> TESSERACT_LARGE = tag("tesseract_large");
        public static final TagKey<EntityType<?>> HOLE_IMMUNE = tag("hole_immune");
        public static final TagKey<EntityType<?>> VOID_TOUCHED_IMMUNE = tag("void_touched_immune");
        public static final TagKey<EntityType<?>> WANTING_ENTITIES = tag("wanting_entities");
        public static final TagKey<EntityType<?>> SUMMON_KILL = tag("summon_kill");
        public static final TagKey<EntityType<?>> IGNORE_SERVANTS = tag("ignore_servants");
        public static final TagKey<EntityType<?>> UNSTUNNABLE = tag("unstunnable");
        public static final TagKey<EntityType<?>> UNTANGLEABLE = tag("untangleable");
        public static final TagKey<EntityType<?>> UNBLOWABLE_ENTITIES = tag("unblowable_entities");
        public static final TagKey<EntityType<?>> SKELETON_WOLF_BUFF = tag("skeleton_wolf_buff");
        public static final TagKey<EntityType<?>> SERVANT_RIDEABLE = tag("servant_rideable");
        public static final TagKey<EntityType<?>> FRAYED_CONVERT = tag("convert/frayed");
        public static final TagKey<EntityType<?>> RATTLED_CONVERT = tag("convert/rattled");
        public static final TagKey<EntityType<?>> REGULAR_CONVERT = tag("necromancer/regular_convert");
        public static final TagKey<EntityType<?>> CAIRN_CONVERT = tag("necromancer/cairn_convert");
        public static final TagKey<EntityType<?>> MOSSY_CONVERT = tag("necromancer/mossy_convert");
        public static final TagKey<EntityType<?>> DROWNED_CONVERT = tag("necromancer/drowned_convert");
        public static final TagKey<EntityType<?>> WITHER_CONVERT = tag("necromancer/wither_convert");
        public static final TagKey<EntityType<?>> MINI_BOSSES = tag("mini_bosses");
        public static final TagKey<EntityType<?>> GLOBAL_MUSIC_BOSS = tag("global_music_boss");
        public static final TagKey<EntityType<?>> RAID_BOSS = tag("raid_boss");
        public static final TagKey<EntityType<?>> BIC_SHIELDED_MOBS = tag("bic_shielded_mobs");
        public static final TagKey<EntityType<?>> APOSTLE_OTHER_ALLIES = tag("apostle_other_allies");
        public static final TagKey<EntityType<?>> WITCH_SET_NEUTRAL = tag("witch_set_neutral");
        public static final TagKey<EntityType<?>> ABYSS_SET_NEUTRAL = tag("abyss_set_neutral");
        public static final TagKey<EntityType<?>> GEO_SET_NEUTRAL = tag("geo_set_neutral");
        public static final TagKey<EntityType<?>> FROST_SET_NEUTRAL = tag("frost_set_neutral");
        public static final TagKey<EntityType<?>> WIND_SET_NEUTRAL = tag("wind_set_neutral");
        public static final TagKey<EntityType<?>> STORM_SET_NEUTRAL = tag("storm_set_neutral");
        public static final TagKey<EntityType<?>> WILD_SET_NEUTRAL = tag("wild_set_neutral");
        public static final TagKey<EntityType<?>> NETHER_SET_NEUTRAL = tag("nether_set_neutral");
        public static final TagKey<EntityType<?>> VOID_SET_NEUTRAL = tag("void_set_neutral");
        public static final TagKey<EntityType<?>> NECRO_SET_NEUTRAL = tag("necro_set_neutral");
        public static final TagKey<EntityType<?>> LICH_NEUTRAL = tag("lich_neutral");
        public static final TagKey<EntityType<?>> ABYSS_HEAL = tag("abyss_heal");
        public static final TagKey<EntityType<?>> FROST_HEAL = tag("frost_heal");
        public static final TagKey<EntityType<?>> WIND_HEAL = tag("wind_heal");
        public static final TagKey<EntityType<?>> STORM_HEAL = tag("storm_heal");
        public static final TagKey<EntityType<?>> WILD_HEAL = tag("wild_heal");
        public static final TagKey<EntityType<?>> GEO_HEAL = tag("geo_heal");
        public static final TagKey<EntityType<?>> NETHER_HEAL = tag("nether_heal");
        public static final TagKey<EntityType<?>> VOID_HEAL = tag("void_heal");
        public static final TagKey<EntityType<?>> NECRO_HEAL = tag("necro_heal");
        public static final TagKey<EntityType<?>> NECRO_NO_DEBUFF = tag("necro_no_debuff");

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
        public static final TagKey<Biome> REAPER_SPAWN = tag("mob_spawn/reaper");
        public static final TagKey<Biome> REAPER_EXCLUDE_SPAWN = tag("mob_spawn/reaper_exclude");
        public static final TagKey<Biome> WRAITH_SPAWN = tag("mob_spawn/wraith");
        public static final TagKey<Biome> WRAITH_EXCLUDE_SPAWN = tag("mob_spawn/wraith_exclude");
        public static final TagKey<Biome> MUCK_WRAITH_SPAWN = tag("mob_spawn/muck_wraith");
        public static final TagKey<Biome> MUCK_WRAITH_EXCLUDE_SPAWN = tag("mob_spawn/muck_wraith_exclude");
        public static final TagKey<Biome> WEB_SPIDER_SPAWN = tag("mob_spawn/web_spider");
        public static final TagKey<Biome> WEB_SPIDER_EXCLUDE_SPAWN = tag("mob_spawn/web_spider_exclude");
        public static final TagKey<Biome> ICY_SPIDER_SPAWN = tag("mob_spawn/icy_spider");
        public static final TagKey<Biome> ICY_SPIDER_EXCLUDE_SPAWN = tag("mob_spawn/icy_spider_exclude");
        public static final TagKey<Biome> FRAYED_SPAWN = tag("mob_spawn/frayed");
        public static final TagKey<Biome> FRAYED_EXCLUDE_SPAWN = tag("mob_spawn/frayed_exclude");
        public static final TagKey<Biome> RATTLED_SPAWN = tag("mob_spawn/rattled");
        public static final TagKey<Biome> RATTLED_EXCLUDE_SPAWN = tag("mob_spawn/rattled_exclude");
        public static final TagKey<Biome> NECROMANCER_SPAWN = tag("mob_spawn/necromancer");
        public static final TagKey<Biome> NECROMANCER_EXCLUDE_SPAWN = tag("mob_spawn/necromancer_exclude");
        public static final TagKey<Biome> WARLOCK_SPAWN = tag("mob_spawn/warlock");
        public static final TagKey<Biome> WARLOCK_EXCLUDE_SPAWN = tag("mob_spawn/warlock_exclude");
        public static final TagKey<Biome> HERETIC_SPAWN = tag("mob_spawn/heretic");
        public static final TagKey<Biome> HERETIC_EXCLUDE_SPAWN = tag("mob_spawn/heretic_exclude");
        public static final TagKey<Biome> MAVERICK_SPAWN = tag("mob_spawn/maverick");
        public static final TagKey<Biome> MAVERICK_EXCLUDE_SPAWN = tag("mob_spawn/maverick_exclude");
        public static final TagKey<Biome> WIGHT_SPAWN = tag("mob_spawn/wight");
        public static final TagKey<Biome> WIGHT_EXCLUDE_SPAWN = tag("mob_spawn/wight_exclude");

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

        public static final TagKey<Biome> ILLAGER_ASSAULT_BLACKLIST = tag("illager_assault_blacklist");

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
        public static final TagKey<Structure> CAN_SUMMON_BRUTES = tag("can_summon_brutes");
        public static final TagKey<Structure> CAN_SUMMON_WITHER_SKELETONS = tag("can_summon_wither_skeletons");
        public static final TagKey<Structure> CAN_SUMMON_BORDER_WRAITHS = tag("can_summon_border_wraiths");
        public static final TagKey<Structure> NECROMANCER_SPAWN = tag("mob_spawn/necromancer");

        private static TagKey<Structure> tag(String name) {
            return create(Goety.location(name));
        }

        private static TagKey<Structure> create(ResourceLocation p_215874_) {
            return TagKey.create(Registries.STRUCTURE, p_215874_);
        }
    }

    public static class DamageTypes {
        private static void init(){}

        public static final TagKey<DamageType> PHYSICAL = tag("physical");
        public static final TagKey<DamageType> FIRE_ATTACKS = tag("fire_attacks");
        public static final TagKey<DamageType> FROST_ATTACKS = tag("frost_attacks");
        public static final TagKey<DamageType> SHOCK_ATTACKS = tag("shock_attacks");
        public static final TagKey<DamageType> WATER_ATTACKS = tag("water_attacks");
        public static final TagKey<DamageType> MAGIC_FIRE = tag("magic_fire");
        public static final TagKey<DamageType> HELLFIRE = tag("hellfire");
        public static final TagKey<DamageType> NO_KNOCKBACK = tag("no_knockback");
        public static final TagKey<DamageType> WANTING_DAMAGE = tag("wanting_damage");
        public static final TagKey<DamageType> LICH_IMMUNE = tag("lich_immune");

        private static TagKey<DamageType> tag(String name) {
            return create(Goety.location(name));
        }

        private static TagKey<DamageType> create(ResourceLocation p_215874_) {
            return TagKey.create(Registries.DAMAGE_TYPE, p_215874_);
        }
    }
}
