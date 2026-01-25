package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ModEntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSpawnEggs {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Goety.MOD_ID);

    public static void init(){
        ModSpawnEggs.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<ModSpawnEggItem> APOSTLE_SPAWN_EGG = ITEMS.register("apostle_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.APOSTLE, 0x080808, 0xf5da2a, egg()));

    public static final RegistryObject<ModSpawnEggItem> WARLOCK_SPAWN_EGG = ITEMS.register("warlock_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.WARLOCK, 0x2c0b00, 0xa01064, egg()));

    public static final RegistryObject<ModSpawnEggItem> OBSIDIAN_MONOLITH_SPAWN_EGG = ITEMS.register("obsidian_monolith_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.OBSIDIAN_MONOLITH, 0x0e031a, 0x3c284a, egg()));

    public static final RegistryObject<ModSpawnEggItem> HERETIC_SPAWN_EGG = ITEMS.register("heretic_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.HERETIC, 0x6e0453, 0xff9700, egg()));

    public static final RegistryObject<ModSpawnEggItem> MAVERICK_SPAWN_EGG = ITEMS.register("maverick_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.MAVERICK, 0x383b2c, 0x352427, egg()));

    public static final RegistryObject<ModSpawnEggItem> CRONE_SPAWN_EGG = ITEMS.register("crone_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.CRONE, 0x732424, 0x916b1a, egg()));

    public static final RegistryObject<ModSpawnEggItem> SORCERER_SPAWN_EGG = ITEMS.register("sorcerer_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.SORCERER, 0x180b20, 0x99112f, egg()));

    public static final RegistryObject<ModSpawnEggItem> ENVIOKER_SPAWN_EGG = ITEMS.register("envioker_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.ENVIOKER, 0x1e1c1a, 0xca272a, egg()));

    public static final RegistryObject<ModSpawnEggItem> TORMENTOR_SPAWN_EGG = ITEMS.register("tormentor_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.TORMENTOR, 0x89a1b8, 0x657787, egg()));

    public static final RegistryObject<ModSpawnEggItem> INQUILLAGER_SPAWN_EGG = ITEMS.register("inquillager_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.INQUILLAGER, 0xbca341, 0x5c121b, egg()));

    public static final RegistryObject<ModSpawnEggItem> CONQUILLAGER_SPAWN_EGG = ITEMS.register("conquillager_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.CONQUILLAGER, 0xd3d3d3, 0x0f0f0f, egg()));

    public static final RegistryObject<ModSpawnEggItem> PIKER_SPAWN_EGG = ITEMS.register("piker_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.PIKER, 0x230a07, 0x8f9097, egg()));

    public static final RegistryObject<ModSpawnEggItem> RIPPER_SPAWN_EGG = ITEMS.register("ripper_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.RIPPER, 0xd6ae7e, 0x2a2926, egg()));

    public static final RegistryObject<ModSpawnEggItem> TRAMPLER_SPAWN_EGG = ITEMS.register("trampler_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.TRAMPLER, 0x363437, 0x7e7057, egg()));

    public static final RegistryObject<ModSpawnEggItem> CRUSHER_SPAWN_EGG = ITEMS.register("crusher_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.CRUSHER, 0x51391c, 0x7c818b, egg()));

    public static final RegistryObject<ModSpawnEggItem> STORM_CASTER_SPAWN_EGG = ITEMS.register("storm_caster_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.STORM_CASTER, 0x5e360f, 0xd3d4c9, egg()));

    public static final RegistryObject<ModSpawnEggItem> CRYOLOGER_SPAWN_EGG = ITEMS.register("cryologer_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.CRYOLOGER, 0x87cdf1, 0x5e8f83, egg()));

    public static final RegistryObject<ModSpawnEggItem> PREACHER_SPAWN_EGG = ITEMS.register("preacher_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.PREACHER, 0xe5d387, 0x8e1e0d, egg()));

    public static final RegistryObject<ModSpawnEggItem> MINISTER_SPAWN_EGG = ITEMS.register("minister_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.MINISTER, 0x331908, 0x53432c, egg()));

    public static final RegistryObject<ModSpawnEggItem> VIZIER_SPAWN_EGG = ITEMS.register("vizier_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.VIZIER, 0x1e1c1a, 0x440a67, egg()));

    public static final RegistryObject<ModSpawnEggItem> IRK_SPAWN_EGG = ITEMS.register("irk_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.IRK, 0x89bdef, 0x4e7294, egg()));

    public static final RegistryObject<ModSpawnEggItem> HOSTILE_REDSTONE_GOLEM_SPAWN_EGG = ITEMS.register("hostile_redstone_golem_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.HOSTILE_REDSTONE_GOLEM, 0x442b30, 0xd12b13, egg()));

    public static final RegistryObject<ModSpawnEggItem> HOSTILE_REDSTONE_MONSTROSITY_SPAWN_EGG = ITEMS.register("hostile_redstone_monstrosity_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.HOSTILE_REDSTONE_MONSTROSITY, 0x1a1012, 0xd12b13, egg()));

    public static final RegistryObject<ModSpawnEggItem> WIGHT_SPAWN_EGG = ITEMS.register("wight_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.WIGHT, 0x19161d, 0x282534, egg()));

    public static final RegistryObject<ModSpawnEggItem> CARRION_MAGGOT_SPAWN_EGG = ITEMS.register("carrion_maggot_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.CARRION_MAGGOT, 0x181a16, 0xe28e42, egg()));

    public static final RegistryObject<ModSpawnEggItem> CARRION_FLY_SPAWN_EGG = ITEMS.register("carrion_fly_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.CARRION_FLY, 0x13140f, 0xff3225, egg()));

    public static final RegistryObject<ModSpawnEggItem> SKULL_LORD_SPAWN_EGG = ITEMS.register("skull_lord_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.SKULL_LORD, 0xd3d3d3, 0x74f1f5, egg()));

    public static final RegistryObject<ModSpawnEggItem> MALGHAST_SPAWN_EGG = ITEMS.register("malghast_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.MALGHAST, 0xe0e0e0, 0xbda6a6, egg()));

    public static final RegistryObject<ModSpawnEggItem> INFERNO_SPAWN_EGG = ITEMS.register("inferno_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.INFERNO, 0x1f0004, 0xfcee4b, egg()));

    public static final RegistryObject<ModSpawnEggItem> HOSTILE_BLACK_WOLF_SPAWN_EGG = ITEMS.register("hostile_black_wolf_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.HOSTILE_BLACK_WOLF, 0x132025, 0x495065, egg()));

    public static final RegistryObject<ModSpawnEggItem> FRAYED_SPAWN_EGG = ITEMS.register("frayed_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.FRAYED, 0x170f0c, 0xf6a600, egg()));

    public static final RegistryObject<ModSpawnEggItem> RATTLED_SPAWN_EGG = ITEMS.register("rattled_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.RATTLED, 0x392f13, 0x494949, egg()));

    public static final RegistryObject<ModSpawnEggItem> REAPER_SPAWN_EGG = ITEMS.register("reaper_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.REAPER, 0x1c1b25, 0x454545, egg()));

    public static final RegistryObject<ModSpawnEggItem> WRAITH_SPAWN_EGG = ITEMS.register("wraith_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.WRAITH, 0x16215c, 0x82d8f8, egg()));

    public static final RegistryObject<ModSpawnEggItem> BORDER_WRAITH_SPAWN_EGG = ITEMS.register("border_wraith_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.BORDER_WRAITH, 0x341523, 0x869696, egg()));

    public static final RegistryObject<ModSpawnEggItem> MUCK_WRAITH_SPAWN_EGG = ITEMS.register("muck_wraith_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.MUCK_WRAITH, 0x1e1220, 0x82d8f8, egg()));

    public static final RegistryObject<ModSpawnEggItem> CRYPT_SLIME_SPAWN_EGG = ITEMS.register("crypt_slime_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.CRYPT_SLIME, 0x22252a, 0x4d6577, egg()));

    public static final RegistryObject<ModSpawnEggItem> WEB_SPIDER_SPAWN_EGG = ITEMS.register("web_spider_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.WEB_SPIDER, 0x2b341c, 11013646, egg()));

    public static final RegistryObject<ModSpawnEggItem> ICY_SPIDER_SPAWN_EGG = ITEMS.register("icy_spider_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.ICY_SPIDER, 0x5b92b0, 0xcccccc, egg()));

    public static final RegistryObject<ModSpawnEggItem> BONE_SPIDER_SPAWN_EGG = ITEMS.register("bone_spider_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.BONE_SPIDER, 0x4f1434, 0xf3f7ef, egg()));

    public static final RegistryObject<ModSpawnEggItem> BROOD_MOTHER_SPAWN_EGG = ITEMS.register("brood_mother_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.BROOD_MOTHER, 0x644a30, 0xff0e0e, egg()));

    public static final RegistryObject<ModSpawnEggItem> NECROMANCER_SPAWN_EGG = ITEMS.register("necromancer_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.NECROMANCER, 0x2c1a2c, 0x00c6ea, egg()));

    public static final RegistryObject<ModSpawnEggItem> CAIRN_NECROMANCER_SPAWN_EGG = ITEMS.register("cairn_necromancer_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.CAIRN_NECROMANCER, 0x446888, 0x00bfbf, egg()));

    public static final RegistryObject<ModSpawnEggItem> MOSSY_NECROMANCER_SPAWN_EGG = ITEMS.register("mossy_necromancer_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.MOSSY_NECROMANCER, 0x484408, 0x3f9a02, egg()));

    public static final RegistryObject<ModSpawnEggItem> WITHER_NECROMANCER_SPAWN_EGG = ITEMS.register("wither_necromancer_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.WITHER_NECROMANCER, 0x4d000f, 0xffa300, egg()));

    public static final RegistryObject<ModSpawnEggItem> HAUNTED_ARMOR_SPAWN_EGG = ITEMS.register("haunted_armor_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.HAUNTED_ARMOR, 0x1e1827, 0x233e4a, egg()));

    public static final RegistryObject<ModSpawnEggItem> WATCHLING_SPAWN_EGG = ITEMS.register("watchling_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.WATCHLING, 1447446, 0xe079fa, egg()));

    public static final RegistryObject<ModSpawnEggItem> BLASTLING_SPAWN_EGG = ITEMS.register("blastling_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.BLASTLING, 1447446, 0xae00d5, egg()));

    public static final RegistryObject<ModSpawnEggItem> SNARELING_SPAWN_EGG = ITEMS.register("snareling_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.SNARELING, 1447446, 0xd4dd4f, egg()));

    public static final RegistryObject<ModSpawnEggItem> ENDERSENT_SPAWN_EGG = ITEMS.register("endersent_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.ENDERSENT, 1447446, 0x88c35a, egg()));

    public static final RegistryObject<ModSpawnEggItem> ENDER_KEEPER_SPAWN_EGG = ITEMS.register("ender_keeper_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.ENDER_KEEPER, 0x390c46, 0xb4d2b4, egg()));

    public static Item.Properties egg(){
        return new Item.Properties();
    }
}
