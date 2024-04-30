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
            () -> new ModSpawnEggItem(ModEntityType.APOSTLE, 0x080808, 0xf5da2a, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> WARLOCK_SPAWN_EGG = ITEMS.register("warlock_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.WARLOCK, 0x2c0b00, 0xa01064, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> CRONE_SPAWN_EGG = ITEMS.register("crone_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.CRONE, 0x732424, 0x916b1a, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> ENVIOKER_SPAWN_EGG = ITEMS.register("envioker_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.ENVIOKER, 0x1e1c1a, 0xca272a, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> TORMENTOR_SPAWN_EGG = ITEMS.register("tormentor_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.TORMENTOR, 0x89a1b8, 0x657787, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> INQUILLAGER_SPAWN_EGG = ITEMS.register("inquillager_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.INQUILLAGER, 0xbca341, 0x5c121b, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> CONQUILLAGER_SPAWN_EGG = ITEMS.register("conquillager_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.CONQUILLAGER, 0xd3d3d3, 0x0f0f0f, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> PIKER_SPAWN_EGG = ITEMS.register("piker_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.PIKER, 0x230a07, 0x8f9097, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> RIPPER_SPAWN_EGG = ITEMS.register("ripper_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.RIPPER, 0xd6ae7e, 0x2a2926, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> TRAMPLER_SPAWN_EGG = ITEMS.register("trampler_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.TRAMPLER, 0x363437, 0x7e7057, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> CRUSHER_SPAWN_EGG = ITEMS.register("crusher_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.CRUSHER, 0x51391c, 0x7c818b, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> STORM_CASTER_SPAWN_EGG = ITEMS.register("storm_caster_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.STORM_CASTER, 0x5e360f, 0xd3d4c9, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> CRYOLOGER_SPAWN_EGG = ITEMS.register("cryologer_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.CRYOLOGER, 0x87cdf1, 0x5e8f83, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> PREACHER_SPAWN_EGG = ITEMS.register("preacher_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.PREACHER, 0xe5d387, 0x8e1e0d, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> MINISTER_SPAWN_EGG = ITEMS.register("minister_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.MINISTER, 0x331908, 0x53432c, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> VIZIER_SPAWN_EGG = ITEMS.register("vizier_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.VIZIER, 0x1e1c1a, 0x440a67, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> IRK_SPAWN_EGG = ITEMS.register("irk_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.IRK, 8032420, 8032420, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> SKULL_LORD_SPAWN_EGG = ITEMS.register("skull_lord_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.SKULL_LORD, 0xd3d3d3, 0x74f1f5, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> ZOMBIE_SERVANT_SPAWN_EGG = ITEMS.register("zombie_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.ZOMBIE_SERVANT, 0x192927, 0x737885, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> HUSK_SERVANT_SPAWN_EGG = ITEMS.register("husk_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.HUSK_SERVANT, 0x322921, 0x64492a, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> DROWNED_SERVANT_SPAWN_EGG = ITEMS.register("drowned_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.DROWNED_SERVANT, 0x182d37, 0x2f8209, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> FROZEN_ZOMBIE_SERVANT_SPAWN_EGG = ITEMS.register("frozen_zombie_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.FROZEN_ZOMBIE_SERVANT, 0x2b4550, 0x6aa8c5, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> JUNGLE_ZOMBIE_SERVANT_SPAWN_EGG = ITEMS.register("jungle_zombie_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.JUNGLE_ZOMBIE_SERVANT, 0x383028, 0x2d3c21, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> SKELETON_SERVANT_SPAWN_EGG = ITEMS.register("skeleton_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.SKELETON_SERVANT, 0x1f1f1f, 0x6e6473, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> STRAY_SERVANT_SPAWN_EGG = ITEMS.register("stray_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.STRAY_SERVANT, 0x495959, 0xb3d4e3, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> MOSSY_SKELETON_SERVANT_SPAWN_EGG = ITEMS.register("mossy_skeleton_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.MOSSY_SKELETON_SERVANT, 0xbdbea5, 0x4a5d21, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> SUNKEN_SKELETON_SERVANT_SPAWN_EGG = ITEMS.register("sunken_skeleton_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.SUNKEN_SKELETON_SERVANT, 0xd6d0c9, 0xa154bc, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> NECROMANCER_SERVANT_SPAWN_EGG = ITEMS.register("necromancer_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.NECROMANCER_SERVANT, 0x99896c, 0x0097c2, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> VANGUARD_SERVANT_SPAWN_EGG = ITEMS.register("vanguard_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.VANGUARD_SERVANT, 0xd6d0c9, 0xe8b42f, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> WRAITH_SERVANT_SPAWN_EGG = ITEMS.register("wraith_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.WRAITH_SERVANT, 0x0e0d36, 0x2586d9, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> HAUNTED_SKULL_SPAWN_EGG = ITEMS.register("haunted_skull_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.HAUNTED_SKULL, 0x8fe2e3, 0x505050, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> SPIDER_SERVANT_SPAWN_EGG = ITEMS.register("spider_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.SPIDER_SERVANT, 0x0a0a0a, 11013646, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> CAVE_SPIDER_SERVANT_SPAWN_EGG = ITEMS.register("cave_spider_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.CAVE_SPIDER_SERVANT, 0x052d32, 11013646, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> WEB_SPIDER_SERVANT_SPAWN_EGG = ITEMS.register("web_spider_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.WEB_SPIDER_SERVANT, 0x2b4226, 11013646, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> BONE_SPIDER_SERVANT_SPAWN_EGG = ITEMS.register("bone_spider_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.BONE_SPIDER_SERVANT, 0x311a3b, 0xedf7f5, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> WHISPERER_SPAWN_EGG = ITEMS.register("whisperer_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.WHISPERER, 0xa0c051, 0xe20703, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> WAVEWHISPERER_SPAWN_EGG = ITEMS.register("wavewhisperer_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.WAVEWHISPERER, 0x1e4730, 0x69ebff, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> LEAPLEAF_SPAWN_EGG = ITEMS.register("leapleaf_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.LEAPLEAF, 0x382b13, 0x818a1a, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> ICE_GOLEM_SPAWN_EGG = ITEMS.register("ice_golem_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.ICE_GOLEM, 0x22806a, 0x94daff, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> SQUALL_GOLEM_SPAWN_EGG = ITEMS.register("squall_golem_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.SQUALL_GOLEM, 0xf99408, 0x70fae6, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> REDSTONE_GOLEM_SPAWN_EGG = ITEMS.register("redstone_golem_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.REDSTONE_GOLEM, 0xaeaaa6, 0xe3260c, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> GRAVE_GOLEM_SPAWN_EGG = ITEMS.register("grave_golem_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.GRAVE_GOLEM, 0x121114, 0x16665a, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> HAUNT_SPAWN_EGG = ITEMS.register("haunt_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.HAUNT, 0x272b2e, 0x9efdfe, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> REDSTONE_MONSTROSITY_SPAWN_EGG = ITEMS.register("redstone_monstrosity_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.REDSTONE_MONSTROSITY, 0x6c7478, 0xf34a32, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> REDSTONE_CUBE_SPAWN_EGG = ITEMS.register("redstone_cube_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.REDSTONE_CUBE, 0xcc200a, 0xffd800, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> ZPIGLIN_SERVANT_SPAWN_EGG = ITEMS.register("zpiglin_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.ZPIGLIN_SERVANT, 0x594036, 0xf5da2a, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> ZPIGLIN_BRUTE_SERVANT_SPAWN_EGG = ITEMS.register("zpiglin_brute_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.ZPIGLIN_BRUTE_SERVANT, 0x1c1c1c, 0xf5da2a, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> QUICK_GROWING_VINE_SPAWN_EGG = ITEMS.register("quick_growing_vine_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.QUICK_GROWING_VINE, 0x6b8f34, 0x8efb83, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> QUICK_GROWING_KELP_SPAWN_EGG = ITEMS.register("quick_growing_kelp_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.QUICK_GROWING_KELP, 0x1c7c6b, 0x89b824, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> POISON_QUILL_VINE_SPAWN_EGG = ITEMS.register("poison_quill_vine_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.POISON_QUILL_VINE, 0xaaba8d, 0x754bc9, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> POISON_ANEMONE_SPAWN_EGG = ITEMS.register("poison_anemone_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.POISON_ANEMONE, 0x136d79, 0x97279f, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> MALGHAST_SPAWN_EGG = ITEMS.register("malghast_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.MALGHAST, 0xe0e0e0, 0xbda6a6, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> WRAITH_SPAWN_EGG = ITEMS.register("wraith_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.WRAITH, 0x16215c, 0x82d8f8, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> BORDER_WRAITH_SPAWN_EGG = ITEMS.register("border_wraith_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.BORDER_WRAITH, 0x0d0b1c, 0x2586d9, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> CAIRN_NECROMANCER_SPAWN_EGG = ITEMS.register("cairn_necromancer_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.CAIRN_NECROMANCER, 0x446888, 0x00bfbf, new Item.Properties()));

    public static final RegistryObject<ModSpawnEggItem> HAUNTED_ARMOR_SPAWN_EGG = ITEMS.register("haunted_armor_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.HAUNTED_ARMOR, 0x1e1827, 0x233e4a, new Item.Properties()));
}
