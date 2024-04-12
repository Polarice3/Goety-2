package com.Polarice3.Goety.init;

import com.Polarice3.Goety.Goety;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModLootInject {

    private static final List<String> CHEST_TABLES = List.of("abandoned_mineshaft", "ancient_city", "ancient_city_ice_box", "desert_pyramid", "jungle_temple", "nether_bridge", "pillager_outpost", "simple_dungeon", "stronghold_crossing", "stronghold_library", "woodland_mansion");

    private static final List<String> ENTITY_TABLES = List.of("cave_spider", "frog", "ravager", "spider", "witch", "zoglin");

    @SubscribeEvent
    public static void InjectLootTables(LootTableLoadEvent evt) {
        String chestsPrefix = "minecraft:chests/";
        String entitiesPrefix = "minecraft:entities/";
        String name = evt.getName().toString();

        if ((name.startsWith(chestsPrefix) && CHEST_TABLES.contains(name.substring(chestsPrefix.length())))
                || (name.startsWith(entitiesPrefix) && ENTITY_TABLES.contains(name.substring(entitiesPrefix.length())))) {
            String file = name.substring("minecraft:".length());
            evt.getTable().addPool(getInjectPool(file));
        }
    }

    private static LootPool getInjectPool(String entryName) {
        return LootPool.lootPool().add(getInjectEntry(entryName)).name("goety_inject_pool").build();
    }

    private static LootPoolEntryContainer.Builder<?> getInjectEntry(String name) {
        return LootTableReference.lootTableReference(Goety.location("inject/" + name));
    }
}
