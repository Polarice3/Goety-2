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
            () -> new ModSpawnEggItem(ModEntityType.APOSTLE, 0x080808, 0xf5da2a, new Item.Properties().tab(Goety.TAB)));

    public static final RegistryObject<ModSpawnEggItem> WARLOCK_SPAWN_EGG = ITEMS.register("warlock_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.WARLOCK, 0x2c0b00, 0xa01064, new Item.Properties().tab(Goety.TAB)));

    public static final RegistryObject<ModSpawnEggItem> CRONE_SPAWN_EGG = ITEMS.register("crone_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.CRONE, 0x732424, 0x916b1a, new Item.Properties().tab(Goety.TAB)));

    public static final RegistryObject<ModSpawnEggItem> ENVIOKER_SPAWN_EGG = ITEMS.register("envioker_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.ENVIOKER, 0x1e1c1a, 0xca272a, new Item.Properties().tab(Goety.TAB)));

    public static final RegistryObject<ModSpawnEggItem> TORMENTOR_SPAWN_EGG = ITEMS.register("tormentor_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.TORMENTOR, 0x89a1b8, 0x657787, new Item.Properties().tab(Goety.TAB)));

    public static final RegistryObject<ModSpawnEggItem> INQUILLAGER_SPAWN_EGG = ITEMS.register("inquillager_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.INQUILLAGER, 0xbca341, 0x5c121b, new Item.Properties().tab(Goety.TAB)));

    public static final RegistryObject<ModSpawnEggItem> CONQUILLAGER_SPAWN_EGG = ITEMS.register("conquillager_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.CONQUILLAGER, 0xd3d3d3, 0x0f0f0f, new Item.Properties().tab(Goety.TAB)));

    public static final RegistryObject<ModSpawnEggItem> MINISTER_SPAWN_EGG = ITEMS.register("minister_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.MINISTER, 0x331908, 0x53432c, new Item.Properties().tab(Goety.TAB)));

    public static final RegistryObject<ModSpawnEggItem> VIZIER_SPAWN_EGG = ITEMS.register("vizier_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.VIZIER, 0x1e1c1a, 0x440a67, new Item.Properties().tab(Goety.TAB)));

    public static final RegistryObject<ModSpawnEggItem> IRK_SPAWN_EGG = ITEMS.register("irk_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.IRK, 8032420, 8032420, new Item.Properties().tab(Goety.TAB)));

    public static final RegistryObject<ModSpawnEggItem> SKULL_LORD_SPAWN_EGG = ITEMS.register("skull_lord_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.SKULL_LORD, 0xd3d3d3, 0x74f1f5, new Item.Properties().tab(Goety.TAB)));

    public static final RegistryObject<ModSpawnEggItem> ZOMBIE_SERVANT_SPAWN_EGG = ITEMS.register("zombie_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.ZOMBIE_SERVANT, 0x192927, 0x737885, new Item.Properties().tab(Goety.TAB)));

    public static final RegistryObject<ModSpawnEggItem> HUSK_SERVANT_SPAWN_EGG = ITEMS.register("husk_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.HUSK_SERVANT, 0x322921, 0x64492a, new Item.Properties().tab(Goety.TAB)));

    public static final RegistryObject<ModSpawnEggItem> DROWNED_SERVANT_SPAWN_EGG = ITEMS.register("drowned_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.DROWNED_SERVANT, 0x182d37, 0x2f8209, new Item.Properties().tab(Goety.TAB)));

    public static final RegistryObject<ModSpawnEggItem> SKELETON_SERVANT_SPAWN_EGG = ITEMS.register("skeleton_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.SKELETON_SERVANT, 0x1f1f1f, 0x6e6473, new Item.Properties().tab(Goety.TAB)));

    public static final RegistryObject<ModSpawnEggItem> STRAY_SERVANT_SPAWN_EGG = ITEMS.register("stray_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.STRAY_SERVANT, 0x495959, 0xb3d4e3, new Item.Properties().tab(Goety.TAB)));

    public static final RegistryObject<ModSpawnEggItem> NECROMANCER_SERVANT_SPAWN_EGG = ITEMS.register("necromancer_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.NECROMANCER_SERVANT, 0x99896c, 0x0097c2, new Item.Properties().tab(Goety.TAB)));

    public static final RegistryObject<ModSpawnEggItem> VANGUARD_SERVANT_SPAWN_EGG = ITEMS.register("vanguard_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.VANGUARD_SERVANT, 0xd6d0c9, 0xe8b42f, new Item.Properties().tab(Goety.TAB)));

    public static final RegistryObject<ModSpawnEggItem> WRAITH_SERVANT_SPAWN_EGG = ITEMS.register("wraith_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.WRAITH_SERVANT, 0x0e0d36, 0x2586d9, new Item.Properties().tab(Goety.TAB)));

    public static final RegistryObject<ModSpawnEggItem> HAUNTED_SKULL_SPAWN_EGG = ITEMS.register("haunted_skull_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.HAUNTED_SKULL, 0x8fe2e3, 0x505050, new Item.Properties().tab(Goety.TAB)));

    public static final RegistryObject<ModSpawnEggItem> REDSTONE_GOLEM_SPAWN_EGG = ITEMS.register("redstone_golem_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.REDSTONE_GOLEM, 0xaeaaa6, 0xe3260c, new Item.Properties().tab(Goety.TAB)));

    public static final RegistryObject<ModSpawnEggItem> ZPIGLIN_SERVANT_SPAWN_EGG = ITEMS.register("zpiglin_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.ZPIGLIN_SERVANT, 0x594036, 0xf5da2a, new Item.Properties().tab(Goety.TAB)));

    public static final RegistryObject<ModSpawnEggItem> ZPIGLIN_BRUTE_SERVANT_SPAWN_EGG = ITEMS.register("zpiglin_brute_servant_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.ZPIGLIN_BRUTE_SERVANT, 0x1c1c1c, 0xf5da2a, new Item.Properties().tab(Goety.TAB)));

    public static final RegistryObject<ModSpawnEggItem> WRAITH_SPAWN_EGG = ITEMS.register("wraith_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.WRAITH, 0x16215c, 0x82d8f8, new Item.Properties().tab(Goety.TAB)));

    public static final RegistryObject<ModSpawnEggItem> BORDER_WRAITH_SPAWN_EGG = ITEMS.register("border_wraith_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.BORDER_WRAITH, 0x0d0b1c, 0x2586d9, new Item.Properties().tab(Goety.TAB)));

    public static final RegistryObject<ModSpawnEggItem> CAIRN_NECROMANCER_SPAWN_EGG = ITEMS.register("cairn_necromancer_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.CAIRN_NECROMANCER, 0x446888, 0x00bfbf, new Item.Properties().tab(Goety.TAB)));

    public static final RegistryObject<ModSpawnEggItem> HAUNTED_ARMOR_SPAWN_EGG = ITEMS.register("haunted_armor_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityType.HAUNTED_ARMOR, 0x1e1827, 0x233e4a, new Item.Properties().tab(Goety.TAB)));
}
