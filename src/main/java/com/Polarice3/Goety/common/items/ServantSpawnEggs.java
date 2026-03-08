package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ModEntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ServantSpawnEggs {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Goety.MOD_ID);

    public static void init(){
        ServantSpawnEggs.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<ServantSpawnEggItem> ZOMBIE_SERVANT_SPAWN_EGG = ITEMS.register("zombie_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.ZOMBIE_SERVANT, 0x192927, 0x737885, egg()));

    public static final RegistryObject<ServantSpawnEggItem> ZOMBIE_VILLAGER_SERVANT_SPAWN_EGG = ITEMS.register("zombie_villager_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.ZOMBIE_VILLAGER_SERVANT, 0x3b3131, 0x6f8a52, egg()));

    public static final RegistryObject<ServantSpawnEggItem> HUSK_SERVANT_SPAWN_EGG = ITEMS.register("husk_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.HUSK_SERVANT, 0x322921, 0x64492a, egg()));

    public static final RegistryObject<ServantSpawnEggItem> DROWNED_SERVANT_SPAWN_EGG = ITEMS.register("drowned_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.DROWNED_SERVANT, 0x182d37, 0x2f8209, egg()));

    public static final RegistryObject<ServantSpawnEggItem> FROZEN_ZOMBIE_SERVANT_SPAWN_EGG = ITEMS.register("frozen_zombie_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.FROZEN_ZOMBIE_SERVANT, 0x2b4550, 0x6aa8c5, egg()));

    public static final RegistryObject<ServantSpawnEggItem> JUNGLE_ZOMBIE_SERVANT_SPAWN_EGG = ITEMS.register("jungle_zombie_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.JUNGLE_ZOMBIE_SERVANT, 0x383028, 0x2d3c21, egg()));

    public static final RegistryObject<ServantSpawnEggItem> FRAYED_SERVANT_SPAWN_EGG = ITEMS.register("frayed_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.FRAYED_SERVANT, 0x25241f, 0x664400, egg()));

    public static final RegistryObject<ServantSpawnEggItem> ZOMBIE_VINDICATOR_SERVANT_SPAWN_EGG = ITEMS.register("zombie_vindicator_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.ZOMBIE_VINDICATOR_SERVANT, 0x8aa2a2, 0x4c6240, egg()));

    public static final RegistryObject<ServantSpawnEggItem> BLACKGUARD_SERVANT_SPAWN_EGG = ITEMS.register("blackguard_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.BLACKGUARD_SERVANT, 0x400000, 0xD9F2F2, egg()));

    public static final RegistryObject<ServantSpawnEggItem> SKELETON_SERVANT_SPAWN_EGG = ITEMS.register("skeleton_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.SKELETON_SERVANT, 0x1f1f1f, 0x6e6473, egg()));

    public static final RegistryObject<ServantSpawnEggItem> STRAY_SERVANT_SPAWN_EGG = ITEMS.register("stray_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.STRAY_SERVANT, 0x495959, 0xb3d4e3, egg()));

    public static final RegistryObject<ServantSpawnEggItem> WITHER_SKELETON_SERVANT_SPAWN_EGG = ITEMS.register("wither_skeleton_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.WITHER_SKELETON_SERVANT, 0x222222, 0x6d7f7f, egg()));

    public static final RegistryObject<ServantSpawnEggItem> MOSSY_SKELETON_SERVANT_SPAWN_EGG = ITEMS.register("mossy_skeleton_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.MOSSY_SKELETON_SERVANT, 0xbdbea5, 0x4a5d21, egg()));

    public static final RegistryObject<ServantSpawnEggItem> SUNKEN_SKELETON_SERVANT_SPAWN_EGG = ITEMS.register("sunken_skeleton_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.SUNKEN_SKELETON_SERVANT, 0xd6d0c9, 0xa154bc, egg()));

    public static final RegistryObject<ServantSpawnEggItem> RATTLED_SERVANT_SPAWN_EGG = ITEMS.register("rattled_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.RATTLED_SERVANT, 0x4c2c00, 0x6b6b6b, egg()));

    public static final RegistryObject<ServantSpawnEggItem> SKELETON_PILLAGER_SERVANT_SPAWN_EGG = ITEMS.register("skeleton_pillager_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.SKELETON_PILLAGER_SERVANT, 0x363636, 0x606060, egg()));

    public static final RegistryObject<ServantSpawnEggItem> NECROMANCER_SERVANT_SPAWN_EGG = ITEMS.register("necromancer_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.NECROMANCER_SERVANT, 0x99896c, 0x0097c2, egg()));

    public static final RegistryObject<ServantSpawnEggItem> CAIRN_NECROMANCER_SERVANT_SPAWN_EGG = ITEMS.register("cairn_necromancer_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.CAIRN_NECROMANCER_SERVANT, 0x0b2830, 0xd2e5ff, egg()));

    public static final RegistryObject<ServantSpawnEggItem> MOSSY_NECROMANCER_SERVANT_SPAWN_EGG = ITEMS.register("mossy_necromancer_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.MOSSY_NECROMANCER_SERVANT, 0x707819, 0x271d06, egg()));

    public static final RegistryObject<ServantSpawnEggItem> DROWNED_NECROMANCER_SERVANT_SPAWN_EGG = ITEMS.register("drowned_necromancer_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.DROWNED_NECROMANCER_SERVANT, 0x0c1e2a, 0xf9be26, egg()));

    public static final RegistryObject<ServantSpawnEggItem> WITHER_NECROMANCER_SERVANT_SPAWN_EGG = ITEMS.register("wither_necromancer_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.WITHER_NECROMANCER_SERVANT, 0xbc0c0c, 0xffff6e, egg()));

    public static final RegistryObject<ServantSpawnEggItem> VANGUARD_SERVANT_SPAWN_EGG = ITEMS.register("vanguard_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.VANGUARD_SERVANT, 0xd6d0c9, 0xe8b42f, egg()));

    public static final RegistryObject<ServantSpawnEggItem> REAPER_SERVANT_SPAWN_EGG = ITEMS.register("reaper_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.REAPER_SERVANT, 0x1f1f2e, 0xd8e1e6, egg()));

    public static final RegistryObject<ServantSpawnEggItem> WRAITH_SERVANT_SPAWN_EGG = ITEMS.register("wraith_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.WRAITH_SERVANT, 0x0e0d36, 0x2586d9, egg()));

    public static final RegistryObject<ServantSpawnEggItem> BORDER_WRAITH_SERVANT_SPAWN_EGG = ITEMS.register("border_wraith_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.BORDER_WRAITH_SERVANT, 0x18090f, 0x869696, egg()));

    public static final RegistryObject<ServantSpawnEggItem> MUCK_WRAITH_SERVANT_SPAWN_EGG = ITEMS.register("muck_wraith_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.MUCK_WRAITH_SERVANT, 0x311c32, 0x869696, egg()));

    public static final RegistryObject<ServantSpawnEggItem> PHANTOM_SERVANT_SPAWN_EGG = ITEMS.register("phantom_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.PHANTOM_SERVANT, 0x881214, 0x7ef0fb, egg()));

    public static final RegistryObject<ServantSpawnEggItem> BOUND_EVOKER_SPAWN_EGG = ITEMS.register("bound_evoker_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.BOUND_EVOKER, 0x5a6363, 0x363636, egg()));

    public static final RegistryObject<ServantSpawnEggItem> BOUND_GEOMANCER_SPAWN_EGG = ITEMS.register("bound_geomancer_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.BOUND_GEOMANCER, 0x45394c, 0x363636, egg()));

    public static final RegistryObject<ServantSpawnEggItem> BOUND_ICEOLOGER_SPAWN_EGG = ITEMS.register("bound_iceologer_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.BOUND_ICEOLOGER, 0x466470, 0x363636, egg()));

    public static final RegistryObject<ServantSpawnEggItem> BOUND_CRYOLOGER_SPAWN_EGG = ITEMS.register("bound_cryologer_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.BOUND_CRYOLOGER, 0x8da7b5, 0x363636, egg()));

    public static final RegistryObject<ServantSpawnEggItem> BOUND_WIND_CALLER_SPAWN_EGG = ITEMS.register("bound_wind_caller_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.BOUND_WIND_CALLER, 0x2d413f, 0x363636, egg()));

    public static final RegistryObject<ServantSpawnEggItem> BOUND_STORM_CASTER_SPAWN_EGG = ITEMS.register("bound_storm_caster_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.BOUND_STORM_CASTER, 0x736849, 0x363636, egg()));

    public static final RegistryObject<ServantSpawnEggItem> HAUNTED_SKULL_SPAWN_EGG = ITEMS.register("haunted_skull_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.HAUNTED_SKULL, 0x8fe2e3, 0x505050, egg()));

    public static final RegistryObject<ServantSpawnEggItem> MINI_GHAST_SPAWN_EGG = ITEMS.register("mini_ghast_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.MINI_GHAST, 0xe4e2e2, 0xddb5b7, egg()));

    public static final RegistryObject<ServantSpawnEggItem> GHAST_SERVANT_SPAWN_EGG = ITEMS.register("ghast_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.GHAST_SERVANT, 0xd6d6d6, 0xa74d54, egg()));

    public static final RegistryObject<ServantSpawnEggItem> BLAZE_SERVANT_SPAWN_EGG = ITEMS.register("blaze_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.BLAZE_SERVANT, 0xff9c0c, 0xfffb82, egg()));

    public static final RegistryObject<ServantSpawnEggItem> WILDFIRE_SERVANT_SPAWN_EGG = ITEMS.register("wildfire_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.WILDFIRE, 0xfc9600, 0xff0000, egg()));

    public static final RegistryObject<ServantSpawnEggItem> SLIME_SERVANT_SPAWN_EGG = ITEMS.register("slime_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.SLIME_SERVANT, 0x51a03e, 0x162810, egg()));

    public static final RegistryObject<ServantSpawnEggItem> MAGMA_CUBE_SERVANT_SPAWN_EGG = ITEMS.register("magma_cube_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.MAGMA_CUBE_SERVANT, 0x241112, 0xffde3a, egg()));

    public static final RegistryObject<ServantSpawnEggItem> CRYPT_SLIME_SERVANT_SPAWN_EGG = ITEMS.register("crypt_slime_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.CRYPT_SLIME_SERVANT, 0x091c25, 0x4b585f, egg()));

    public static final RegistryObject<ServantSpawnEggItem> TROPICAL_SLIME_SERVANT_SPAWN_EGG = ITEMS.register("tropical_slime_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.TROPICAL_SLIME_SERVANT, 0x0e696e, 0x4bc536, egg()));

    public static final RegistryObject<ServantSpawnEggItem> WARTLING_SPAWN_EGG = ITEMS.register("wartling_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.WARTLING, 0x16100b, 0xb32e2e, egg()));

    public static final RegistryObject<ServantSpawnEggItem> SPIDER_SERVANT_SPAWN_EGG = ITEMS.register("spider_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.SPIDER_SERVANT, 0x0a0a0a, 0xb32e2e, egg()));

    public static final RegistryObject<ServantSpawnEggItem> CAVE_SPIDER_SERVANT_SPAWN_EGG = ITEMS.register("cave_spider_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.CAVE_SPIDER_SERVANT, 0x052d32, 11013646, egg()));

    public static final RegistryObject<ServantSpawnEggItem> WEB_SPIDER_SERVANT_SPAWN_EGG = ITEMS.register("web_spider_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.WEB_SPIDER_SERVANT, 0x2b4226, 11013646, egg()));

    public static final RegistryObject<ServantSpawnEggItem> ICY_SPIDER_SERVANT_SPAWN_EGG = ITEMS.register("icy_spider_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.ICY_SPIDER_SERVANT, 0x5ccff8, 0xcccccc, egg()));

    public static final RegistryObject<ServantSpawnEggItem> BONE_SPIDER_SERVANT_SPAWN_EGG = ITEMS.register("bone_spider_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.BONE_SPIDER_SERVANT, 0x311a3b, 0xedf7f5, egg()));

    public static final RegistryObject<ServantSpawnEggItem> BROOD_MOTHER_SERVANT_SPAWN_EGG = ITEMS.register("brood_mother_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.BROOD_MOTHER_SERVANT, 0x261e23, 0xff0e0e, egg()));

    public static final RegistryObject<ServantSpawnEggItem> BLACK_WOLF_SPAWN_EGG = ITEMS.register("black_wolf_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.BLACK_WOLF, 0x1c161e, 0x495065, egg()));

    public static final RegistryObject<ServantSpawnEggItem> SKELETON_WOLF_SPAWN_EGG = ITEMS.register("skeleton_wolf_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.SKELETON_WOLF, 0xe3e3e3, 0x494949, egg()));

    public static final RegistryObject<ServantSpawnEggItem> WINTER_WOLF_SPAWN_EGG = ITEMS.register("winter_wolf_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.WINTER_WOLF, 0xa8aeb2, 0xe7e7e7, egg()));

    public static final RegistryObject<ServantSpawnEggItem> STORMHOUND_SPAWN_EGG = ITEMS.register("stormhound_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.STORMHOUND, 0x1a110e, 0x5e4123, egg()));

    public static final RegistryObject<ServantSpawnEggItem> HELLHOUND_SPAWN_EGG = ITEMS.register("hellhound_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.HELLHOUND, 0x8d2600, 0x140000, egg()));

    public static final RegistryObject<ServantSpawnEggItem> TWILIGHT_GOAT_SPAWN_EGG = ITEMS.register("twilight_goat_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.TWILIGHT_GOAT, 0x1d1d26, 0xdbd5c2, egg()));

    public static final RegistryObject<ServantSpawnEggItem> SNAPPER_SPAWN_EGG = ITEMS.register("snapper_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.SNAPPER, 0x141817, 0x481e00, egg()));

    public static final RegistryObject<ServantSpawnEggItem> GNASHER_SPAWN_EGG = ITEMS.register("gnasher_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.GNASHER, 0x3c4454, 0x10121e, egg()));

    public static final RegistryObject<ServantSpawnEggItem> GUARDIAN_SERVANT_SPAWN_EGG = ITEMS.register("guardian_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.GUARDIAN_SERVANT, 0x5aafa4, 0xff822f, egg()));

    public static final RegistryObject<ServantSpawnEggItem> BEAR_SERVANT_SPAWN_EGG = ITEMS.register("bear_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.BEAR_SERVANT, 0x3a281d, 0xfa2e14, egg()));

    public static final RegistryObject<ServantSpawnEggItem> POLAR_BEAR_SERVANT_SPAWN_EGG = ITEMS.register("polar_bear_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.POLAR_BEAR_SERVANT, 0xf6f6f6, 0xfa2e14, egg()));

    public static final RegistryObject<ServantSpawnEggItem> HOGLIN_SERVANT_SPAWN_EGG = ITEMS.register("hoglin_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.HOGLIN_SERVANT, 0x532e24, 0xbc6529, egg()));

    public static final RegistryObject<ServantSpawnEggItem> BLACK_BEAST_SPAWN_EGG = ITEMS.register("black_beast_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.BLACK_BEAST, 0x2f2532, 0xb60f0f, egg()));

    public static final RegistryObject<ServantSpawnEggItem> WHISPERER_SPAWN_EGG = ITEMS.register("whisperer_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.WHISPERER, 0xa0c051, 0xe20703, egg()));

    public static final RegistryObject<ServantSpawnEggItem> WAVEWHISPERER_SPAWN_EGG = ITEMS.register("wavewhisperer_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.WAVEWHISPERER, 0x1e4730, 0x69ebff, egg()));

    public static final RegistryObject<ServantSpawnEggItem> LEAPLEAF_SPAWN_EGG = ITEMS.register("leapleaf_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.LEAPLEAF, 0x382b13, 0x818a1a, egg()));

    public static final RegistryObject<ServantSpawnEggItem> STONE_MINISTROSITY_SPAWN_EGG = ITEMS.register("stone_ministrosity_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.STONE_MINISTROSITY, 0xd7b438, 0xdad9d9, egg()));

    public static final RegistryObject<ServantSpawnEggItem> REDSTONE_MINISTROSITY_SPAWN_EGG = ITEMS.register("redstone_ministrosity_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.REDSTONE_MINISTROSITY, 0x6c1005, 0xdad9d9, egg()));

    public static final RegistryObject<ServantSpawnEggItem> ICE_GOLEM_SPAWN_EGG = ITEMS.register("ice_golem_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.ICE_GOLEM, 0x22806a, 0x94daff, egg()));

    public static final RegistryObject<ServantSpawnEggItem> SQUALL_GOLEM_SPAWN_EGG = ITEMS.register("squall_golem_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.SQUALL_GOLEM, 0xf99408, 0x70fae6, egg()));

    public static final RegistryObject<ServantSpawnEggItem> REDSTONE_GOLEM_SPAWN_EGG = ITEMS.register("redstone_golem_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.REDSTONE_GOLEM, 0xaeaaa6, 0xe3260c, egg()));

    public static final RegistryObject<ServantSpawnEggItem> GRAVE_GOLEM_SPAWN_EGG = ITEMS.register("grave_golem_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.GRAVE_GOLEM, 0x121114, 0x16665a, egg()));

    public static final RegistryObject<ServantSpawnEggItem> HAUNT_SPAWN_EGG = ITEMS.register("haunt_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.HAUNT, 0x272b2e, 0x9efdfe, egg()));

    public static final RegistryObject<ServantSpawnEggItem> REDSTONE_MONSTROSITY_SPAWN_EGG = ITEMS.register("redstone_monstrosity_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.REDSTONE_MONSTROSITY, 0x6c7478, 0xf34a32, egg()));

    public static final RegistryObject<ServantSpawnEggItem> REDSTONE_CUBE_SPAWN_EGG = ITEMS.register("redstone_cube_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.REDSTONE_CUBE, 0xcc200a, 0xffd800, egg()));

    public static final RegistryObject<ServantSpawnEggItem> ZPIGLIN_SERVANT_SPAWN_EGG = ITEMS.register("zpiglin_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.ZPIGLIN_SERVANT, 0x594036, 0xf5da2a, egg()));

    public static final RegistryObject<ServantSpawnEggItem> ZPIGLIN_BRUTE_SERVANT_SPAWN_EGG = ITEMS.register("zpiglin_brute_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.ZPIGLIN_BRUTE_SERVANT, 0x1c1c1c, 0xf5da2a, egg()));

    public static final RegistryObject<ServantSpawnEggItem> WATCHLING_SERVANT_SPAWN_EGG = ITEMS.register("watchling_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.WATCHLING_SERVANT, 0x1c001e, 0xff9af5, egg()));

    public static final RegistryObject<ServantSpawnEggItem> BLASTLING_SERVANT_SPAWN_EGG = ITEMS.register("blastling_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.BLASTLING_SERVANT, 0x1c001e, 0xb103d7, egg()));

    public static final RegistryObject<ServantSpawnEggItem> SNARELING_SERVANT_SPAWN_EGG = ITEMS.register("snareling_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.SNARELING_SERVANT, 0x1c001e, 0xfcfcc7, egg()));

    public static final RegistryObject<ServantSpawnEggItem> VEX_SERVANT_SPAWN_EGG = ITEMS.register("vex_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.VEX_SERVANT, 8032420, 0xffa700, egg()));

    public static final RegistryObject<ServantSpawnEggItem> IRK_SERVANT_SPAWN_EGG = ITEMS.register("irk_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.IRK_SERVANT, 0x89bdef, 0xffa700, egg()));

    public static final RegistryObject<ServantSpawnEggItem> NEOLLAGER_SPAWN_EGG = ITEMS.register("neollager_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.NEOLLAGER, 0x281e11, 0x464234, egg()));

    public static final RegistryObject<ServantSpawnEggItem> PILLAGER_SERVANT_SPAWN_EGG = ITEMS.register("pillager_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.PILLAGER_SERVANT, 0x341c22, 0xadbebe, egg()));

    public static final RegistryObject<ServantSpawnEggItem> PIKER_SERVANT_SPAWN_EGG = ITEMS.register("piker_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.PIKER_SERVANT, 0x341610, 0xc7cece, egg()));

    public static final RegistryObject<ServantSpawnEggItem> SIGNALER_SERVANT_SPAWN_EGG = ITEMS.register("signaler_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.SIGNALER_SERVANT, 0x32663c, 0x525858, egg()));

    public static final RegistryObject<ServantSpawnEggItem> VINDICATOR_SERVANT_SPAWN_EGG = ITEMS.register("vindicator_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.VINDICATOR_SERVANT, 0x959b9b, 0x32494e, egg()));

    public static final RegistryObject<ServantSpawnEggItem> VINDICATOR_CHEF_SERVANT_SPAWN_EGG = ITEMS.register("vindicator_chef_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.VINDICATOR_CHEF_SERVANT, 0xe0dfdb, 0x896727, egg()));

    public static final RegistryObject<ServantSpawnEggItem> MOUNTAINEER_SERVANT_SPAWN_EGG = ITEMS.register("mountaineer_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.MOUNTAINEER_SERVANT, 0x342510, 0x91b166, egg()));

    public static final RegistryObject<ServantSpawnEggItem> CRUSHER_SERVANT_SPAWN_EGG = ITEMS.register("crusher_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.CRUSHER_SERVANT, 0x362a23, 0xcfb16c, egg()));

    public static final RegistryObject<ServantSpawnEggItem> EVOKER_SERVANT_SPAWN_EGG = ITEMS.register("evoker_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.EVOKER_SERVANT, 0x959b9b, 0x0f1119, egg()));

    public static final RegistryObject<ServantSpawnEggItem> GEOMANCER_SERVANT_SPAWN_EGG = ITEMS.register("geomancer_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.GEOMANCER_SERVANT, 0x342817, 0xcdb7d8, egg()));

    public static final RegistryObject<ServantSpawnEggItem> ICEOLOGER_SERVANT_SPAWN_EGG = ITEMS.register("iceologer_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.ICEOLOGER_SERVANT, 0x022c5b, 0xb6c1ca, egg()));

    public static final RegistryObject<ServantSpawnEggItem> CRYOLOGER_SERVANT_SPAWN_EGG = ITEMS.register("cryologer_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.CRYOLOGER_SERVANT, 0xf8fbfb, 0x00384d, egg()));

    public static final RegistryObject<ServantSpawnEggItem> WIND_CALLER_SERVANT_SPAWN_EGG = ITEMS.register("wind_caller_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.WIND_CALLER_SERVANT, 0x4e7377, 0x78977f, egg()));

    public static final RegistryObject<ServantSpawnEggItem> STORM_CASTER_SERVANT_SPAWN_EGG = ITEMS.register("storm_caster_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.STORM_CASTER_SERVANT, 0x994c29, 0x00548c, egg()));

    public static final RegistryObject<ServantSpawnEggItem> RIPPER_SERVANT_SPAWN_EGG = ITEMS.register("ripper_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.RIPPER_SERVANT, 0x4e291d, 0xf8eeac, egg()));

    public static final RegistryObject<ServantSpawnEggItem> WITCH_SERVANT_SPAWN_EGG = ITEMS.register("witch_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.WITCH_SERVANT, 0x1d0b2f, 0x4f5f3a, egg()));

    public static final RegistryObject<ServantSpawnEggItem> WARLOCK_SERVANT_SPAWN_EGG = ITEMS.register("warlock_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.WARLOCK_SERVANT, 0xa42822, 0x331609, egg()));

    public static final RegistryObject<ServantSpawnEggItem> HERETIC_SERVANT_SPAWN_EGG = ITEMS.register("heretic_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.HERETIC_SERVANT, 0x100811, 0x7e1e00, egg()));

    public static final RegistryObject<ServantSpawnEggItem> MAVERICK_SERVANT_SPAWN_EGG = ITEMS.register("maverick_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.MAVERICK_SERVANT, 0x92a7cd, 0x1a2524, egg()));

    public static final RegistryObject<ServantSpawnEggItem> PRISONER_SPAWN_EGG = ITEMS.register("prisoner_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.PRISONER, 0x71544d, 0x2d4a49, egg()));

    public static final RegistryObject<ServantSpawnEggItem> RAVAGED_SPAWN_EGG = ITEMS.register("ravaged_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.RAVAGED, 0x102322, 0x757470, egg()));

    public static final RegistryObject<ServantSpawnEggItem> TRAMPLER_SERVANT_SPAWN_EGG = ITEMS.register("trampler_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.TRAMPLER_SERVANT, 0x8a795d, 0x696151, egg()));

    public static final RegistryObject<ServantSpawnEggItem> RAVAGER_SERVANT_SPAWN_EGG = ITEMS.register("ravager_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.MOD_RAVAGER, 0x757470, 0x32322c, egg()));

    public static final RegistryObject<ServantSpawnEggItem> ZOMBIE_RAVAGER_SERVANT_SPAWN_EGG = ITEMS.register("zombie_ravager_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.ZOMBIE_RAVAGER, 0x979486, 0x1f260b, egg()));

    public static final RegistryObject<ServantSpawnEggItem> QUICK_GROWING_VINE_SPAWN_EGG = ITEMS.register("quick_growing_vine_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.QUICK_GROWING_VINE, 0x6b8f34, 0x8efb83, egg()));

    public static final RegistryObject<ServantSpawnEggItem> QUICK_GROWING_KELP_SPAWN_EGG = ITEMS.register("quick_growing_kelp_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.QUICK_GROWING_KELP, 0x1c7c6b, 0x89b824, egg()));

    public static final RegistryObject<ServantSpawnEggItem> POISON_QUILL_VINE_SPAWN_EGG = ITEMS.register("poison_quill_vine_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.POISON_QUILL_VINE, 0xaaba8d, 0x754bc9, egg()));

    public static final RegistryObject<ServantSpawnEggItem> POISON_ANEMONE_SPAWN_EGG = ITEMS.register("poison_anemone_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityType.POISON_ANEMONE, 0x136d79, 0x97279f, egg()));


    public static Item.Properties egg(){
        return new Item.Properties();
    }
}
