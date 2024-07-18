package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.vehicle.ModBoat;
import com.google.common.collect.Sets;
import net.minecraft.client.model.geom.ModelLayerLocation;

import java.util.Set;

public class ModModelLayer {
    private static final Set<ModelLayerLocation> ALL_MODELS = Sets.newHashSet();
    public static final ModelLayerLocation SPIKE = register("spike");
    public static final ModelLayerLocation HARPOON = register("harpoon");
    public static final ModelLayerLocation POISON_QUILL = register("poison_quill");
    public static final ModelLayerLocation ICE_BOUQUET = register("ice_bouquet");
    public static final ModelLayerLocation ICE_CHUNK = register("ice_chunk");
    public static final ModelLayerLocation VICIOUS_TOOTH = register("vicious_tooth");
    public static final ModelLayerLocation VICIOUS_PIKE = register("vicious_pike");
    public static final ModelLayerLocation SOUL_BOLT = register("soul_bolt");
    public static final ModelLayerLocation HELL_BLAST = register("hell_blast");
    public static final ModelLayerLocation SCATTER_MINE = register("scatter_mine");
    public static final ModelLayerLocation BLAST_FUNGUS = register("blast_fungus");
    public static final ModelLayerLocation WEB_SHOT = register("web_shot");
    public static final ModelLayerLocation SOUL_BOMB = register("soul_bomb");
    public static final ModelLayerLocation SUMMON_CIRCLE = register("summon_circle");
    public static final ModelLayerLocation SUMMON_CIRCLE_BOSS = register("summon_circle_boss");
    public static final ModelLayerLocation ENTANGLE_VINES = register("entangle_vines");
    public static final ModelLayerLocation FIRE_TORNADO = register("fire_tornado");
    public static final ModelLayerLocation MONOLITH = register("pillar");
    public static final ModelLayerLocation VOLCANO = register("volcano");
    public static final ModelLayerLocation QUICK_GROWING_VINE = register("quick_growing_vine");
    public static final ModelLayerLocation POISON_QUILL_VINE = register("poison_quill_vine");
    public static final ModelLayerLocation BLOCK = register("block");
    public static final ModelLayerLocation WARLOCK = register("warlock");
    public static final ModelLayerLocation CRONE = register("crone");
    public static final ModelLayerLocation APOSTLE = register("apostle");
    public static final ModelLayerLocation APOSTLE_SHADE = register("apostle_shade");
    public static final ModelLayerLocation ZOMBIE_VILLAGER_SERVANT = register("zombie_villager_servant");
    public static final ModelLayerLocation SKELETON_VILLAGER_SERVANT = register("skeleton_villager_servant");
    public static final ModelLayerLocation BOUND_ILLAGER = register("bound_illager");
    public static final ModelLayerLocation BOUND_ILLAGER_ANIMATED = register("bound_illager_animated");
    public static final ModelLayerLocation DAMNED = register("damned");
    public static final ModelLayerLocation DAMNED_HUMAN = register("damned_human");
    public static final ModelLayerLocation RAVAGED = register("ravaged");
    public static final ModelLayerLocation RAVAGER = register("ravager");
    public static final ModelLayerLocation RAVAGER_ARMOR = register("ravager_armor");
    public static final ModelLayerLocation BLACK_WOLF = register("black_wolf");
    public static final ModelLayerLocation BEAR = register("bear");
    public static final ModelLayerLocation WHISPERER = register("whisperer");
    public static final ModelLayerLocation LEAPLEAF = register("leapleaf");
    public static final ModelLayerLocation ICE_GOLEM = register("ice_golem");
    public static final ModelLayerLocation SQUALL_GOLEM = register("squall_golem");
    public static final ModelLayerLocation REDSTONE_GOLEM = register("redstone_golem");
    public static final ModelLayerLocation GRAVE_GOLEM = register("grave_golem");
    public static final ModelLayerLocation HAUNT = register("haunt");
    public static final ModelLayerLocation REDSTONE_MONSTROSITY = register("redstone_monstrosity");
    public static final ModelLayerLocation REDSTONE_CUBE = register("redstone_cube");
    public static final ModelLayerLocation ZPIGLIN_SERVANT = register("zpiglin_servant");
    public static final ModelLayerLocation MALGHAST = register("malghast");
    public static final ModelLayerLocation INFERNO = register("inferno");
    public static final ModelLayerLocation MINI_GHAST = register("mini_ghast");
    public static final ModelLayerLocation MAGMA_CUBE = register("magma_cube");
    public static final ModelLayerLocation MOD_SPIDER = register("mod_spider");
    public static final ModelLayerLocation ICY_SPIDER = register("icy_spider");
    public static final ModelLayerLocation WRAITH = register("wraith");
    public static final ModelLayerLocation SUNKEN_SKELETON = register("sunken_skeleton");
    public static final ModelLayerLocation NECROMANCER = register("necromancer");
    public static final ModelLayerLocation WITHER_NECROMANCER = register("wither_necromancer");
    public static final ModelLayerLocation VANGUARD = register("vanguard");
    public static final ModelLayerLocation VILLAGER_ARMOR_INNER = registerInnerArmor("villager_armor");
    public static final ModelLayerLocation VILLAGER_ARMOR_OUTER = registerOuterArmor("villager_armor");
    public static final ModelLayerLocation CURSED_KNIGHT_ARMOR_INNER = registerInnerArmor("cursed_knight_armor");
    public static final ModelLayerLocation CURSED_KNIGHT_ARMOR_OUTER = registerOuterArmor("cursed_knight_armor");
    public static final ModelLayerLocation CURSED_PALADIN_ARMOR_INNER = registerInnerArmor("cursed_paladin_armor");
    public static final ModelLayerLocation CURSED_PALADIN_ARMOR_OUTER = registerOuterArmor("cursed_paladin_armor");
    public static final ModelLayerLocation BLACK_IRON_ARMOR_INNER = registerInnerArmor("black_iron_armor");
    public static final ModelLayerLocation BLACK_IRON_ARMOR_OUTER = registerOuterArmor("black_iron_armor");
    public static final ModelLayerLocation DARK_ARMOR_INNER = registerInnerArmor("dark_armor");
    public static final ModelLayerLocation DARK_ARMOR_OUTER = registerOuterArmor("dark_armor");
    public static final ModelLayerLocation MINION = register("minion");
    public static final ModelLayerLocation HAUNTED_SKULL = register("cursed_skull");
    public static final ModelLayerLocation HAUNTED_SKULL_FIRELESS = register("fireless_skull");
    public static final ModelLayerLocation SKULL_LORD = register("skull_lord");
    public static final ModelLayerLocation TORMENTOR = register("tormentor");
    public static final ModelLayerLocation INQUILLAGER = register("inquillager");
    public static final ModelLayerLocation CONQUILLAGER = register("conquillager");
    public static final ModelLayerLocation PIKER = register("piker");
    public static final ModelLayerLocation RIPPER = register("ripper");
    public static final ModelLayerLocation TRAMPLER = register("trampler");
    public static final ModelLayerLocation CRUSHER = register("crusher");
    public static final ModelLayerLocation STORM_CASTER = register("storm_caster");
    public static final ModelLayerLocation CRYOLOGER = register("cryologer");
    public static final ModelLayerLocation PREACHER = register("preacher");
    public static final ModelLayerLocation MINISTER = register("minister");
    public static final ModelLayerLocation VIZIER = register("vizier");
    public static final ModelLayerLocation VIZIER_CLONE = register("vizier_clone");
    public static final ModelLayerLocation IRK = register("irk");
    public static final ModelLayerLocation VIZIER_ARMOR = register("vizier", "armor");
    public static final ModelLayerLocation DARK_HAT = register("dark_hat");
    public static final ModelLayerLocation GRAND_TURBAN = register("grand_turban");
    public static final ModelLayerLocation WITCH_HAT = register("witch_hat");
    public static final ModelLayerLocation CRONE_HAT = register("crone_hat");
    public static final ModelLayerLocation IRON_CROWN = register("iron_crown");
    public static final ModelLayerLocation DARK_ROBE = register("dark_robe");
    public static final ModelLayerLocation NECRO_CROWN = register("necro_crown");
    public static final ModelLayerLocation NECRO_CAPE = register("necro_cape");
    public static final ModelLayerLocation NECRO_SET = register("necro_set");
    public static final ModelLayerLocation NAMELESS_CROWN = register("nameless_crown");
    public static final ModelLayerLocation NAMELESS_SET = register("nameless_set");
    public static final ModelLayerLocation LICH = register("lich");
    public static final ModelLayerLocation GLOVE = register("glove");
    public static final ModelLayerLocation FOCUS_BAG = register("focus_bag");
    public static final ModelLayerLocation BREW_BAG = register("brew_bag");
    public static final ModelLayerLocation AMULET = register("amulet");
    public static final ModelLayerLocation BELT = register("belt");
    public static final ModelLayerLocation MONOCLE = register("monocle");
    public static final ModelLayerLocation AMETHYST_NECKLACE = register("amethyst_necklace");
    public static final ModelLayerLocation SOUL_SHIELD = register("soul_shield");
    public static final ModelLayerLocation SOUL_ARMOR = register("soul_armor");
    public static final ModelLayerLocation HAUNTED_ARMOR_STAND = register("haunted_armor_stand");
    public static final ModelLayerLocation HAS_INNER = registerInnerArmor("haunted_armor_stand");
    public static final ModelLayerLocation HAS_OUTER = registerOuterArmor("haunted_armor_stand");
    public static final ModelLayerLocation SMALL_PAINTING = register("small_painting");
    public static final ModelLayerLocation MEDIUM_PAINTING = register("medium_painting");
    public static final ModelLayerLocation LARGE_PAINTING = register("large_painting");
    public static final ModelLayerLocation TALL_PAINTING = register("tall_painting");
    public static final ModelLayerLocation WIDE_PAINTING = register("wide_painting");

    private static ModelLayerLocation register(String p_171294_) {
        return register(p_171294_, "main");
    }

    private static ModelLayerLocation register(String p_171296_, String p_171297_) {
        ModelLayerLocation modellayerlocation = createLocation(p_171296_, p_171297_);
        if (!ALL_MODELS.add(modellayerlocation)) {
            throw new IllegalStateException("Duplicate registration for " + modellayerlocation);
        } else {
            return modellayerlocation;
        }
    }

    private static ModelLayerLocation createLocation(String p_171301_, String p_171302_) {
        return new ModelLayerLocation(Goety.location(p_171301_), p_171302_);
    }

    private static ModelLayerLocation registerInnerArmor(String p_171299_) {
        return register(p_171299_, "inner_armor");
    }

    private static ModelLayerLocation registerOuterArmor(String p_171304_) {
        return register(p_171304_, "outer_armor");
    }

    public static ModelLayerLocation createBoatModelName(ModBoat.Type p_171290_) {
        return createLocation("boat/" + p_171290_.getName(), "main");
    }

    public static ModelLayerLocation createChestBoatModelName(ModBoat.Type p_233551_) {
        return createLocation("chest_boat/" + p_233551_.getName(), "main");
    }

}
