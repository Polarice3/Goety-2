package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.vehicle.ModBoat;
import com.google.common.collect.Sets;
import net.minecraft.client.model.geom.ModelLayerLocation;

import java.util.Set;

public class ModModelLayer {
    private static final Set<ModelLayerLocation> ALL_MODELS = Sets.newHashSet();
    public static final ModelLayerLocation SPIKE = register("spike");
    public static final ModelLayerLocation ICE_BOUQUET = register("ice_bouquet");
    public static final ModelLayerLocation ICE_CHUNK = register("ice_chunk");
    public static final ModelLayerLocation MINISTER_TOOTH = register("minister_tooth");
    public static final ModelLayerLocation SOUL_BOLT = register("soul_bolt");
    public static final ModelLayerLocation BLAST_FUNGUS = register("blast_fungus");
    public static final ModelLayerLocation SUMMON_CIRCLE = register("summon_circle");
    public static final ModelLayerLocation FIRE_TORNADO = register("fire_tornado");
    public static final ModelLayerLocation MONOLITH = register("pillar");
    public static final ModelLayerLocation BLOCK = register("block");
    public static final ModelLayerLocation WARLOCK = register("warlock");
    public static final ModelLayerLocation CRONE = register("crone");
    public static final ModelLayerLocation APOSTLE = register("apostle");
    public static final ModelLayerLocation ZOMBIE_VILLAGER_SERVANT = register("zombie_villager_servant");
    public static final ModelLayerLocation SKELETON_VILLAGER_SERVANT = register("skeleton_villager_servant");
    public static final ModelLayerLocation RAVAGED = register("ravaged");
    public static final ModelLayerLocation RAVAGER = register("ravager");
    public static final ModelLayerLocation RAVAGER_ARMOR = register("ravager_armor");
    public static final ModelLayerLocation ZPIGLIN_SERVANT = register("zpiglin_servant");
    public static final ModelLayerLocation MALGHAST = register("malghast");
    public static final ModelLayerLocation WRAITH = register("wraith");
    public static final ModelLayerLocation NECROMANCER = register("necromancer");
    public static final ModelLayerLocation VILLAGER_ARMOR_INNER = registerInnerArmor("villager_armor");
    public static final ModelLayerLocation VILLAGER_ARMOR_OUTER = registerOuterArmor("villager_armor");
    public static final ModelLayerLocation CURSED_KNIGHT_ARMOR_INNER = registerInnerArmor("cursed_knight_armor");
    public static final ModelLayerLocation CURSED_KNIGHT_ARMOR_OUTER = registerOuterArmor("cursed_knight_armor");
    public static final ModelLayerLocation DARK_ARMOR_INNER = registerInnerArmor("dark_armor");
    public static final ModelLayerLocation DARK_ARMOR_OUTER = registerOuterArmor("dark_armor");
    public static final ModelLayerLocation MINION = register("minion");
    public static final ModelLayerLocation HAUNTED_SKULL = register("cursed_skull");
    public static final ModelLayerLocation SKULL_LORD = register("skull_lord");
    public static final ModelLayerLocation TORMENTOR = register("tormentor");
    public static final ModelLayerLocation INQUILLAGER = register("inquillager");
    public static final ModelLayerLocation CONQUILLAGER = register("conquillager");
    public static final ModelLayerLocation VIZIER = register("vizier");
    public static final ModelLayerLocation IRK = register("irk");
    public static final ModelLayerLocation VIZIER_ARMOR = register("vizier", "armor");
    public static final ModelLayerLocation DARK_HAT = register("dark_hat");
    public static final ModelLayerLocation GRAND_TURBAN = register("grand_turban");
    public static final ModelLayerLocation WITCH_HAT = register("witch_hat");
    public static final ModelLayerLocation CRONE_HAT = register("crone_hat");
    public static final ModelLayerLocation DARK_ROBE = register("dark_robe");
    public static final ModelLayerLocation NECRO_CROWN = register("necro_crown");
    public static final ModelLayerLocation NECRO_CAPE = register("necro_cape");
    public static final ModelLayerLocation NECRO_SET = register("necro_set");
    public static final ModelLayerLocation NAMELESS_CROWN = register("nameless_crown");
    public static final ModelLayerLocation NAMELESS_SET = register("nameless_set");
    public static final ModelLayerLocation GLOVE = register("glove");
    public static final ModelLayerLocation FOCUS_BAG = register("focus_bag");
    public static final ModelLayerLocation AMULET = register("amulet");
    public static final ModelLayerLocation BELT = register("belt");
    public static final ModelLayerLocation MONOCLE = register("monocle");
    public static final ModelLayerLocation AMETHYST_NECKLACE = register("amethyst_necklace");
    public static final ModelLayerLocation SOUL_SHIELD = register("soul_shield");
    public static final ModelLayerLocation SOUL_ARMOR = register("soul_armor");

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
