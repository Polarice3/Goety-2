package com.Polarice3.Goety.common.world;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModLevelRegistry {

    public static void addBiomeSpawns(Holder<Biome> biome, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (!biome.containsTag(ModTags.Biomes.COMMON_BLACKLIST) && !biome.is(biomeResourceKey -> biomeResourceKey.registry().getNamespace().contains("alexscaves"))){
            if (biome.is(ModTags.Biomes.WRAITH_SPAWN) && !biome.is(ModTags.Biomes.WRAITH_EXCLUDE_SPAWN) && MobsConfig.WraithSpawnWeight.get() > 0){
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(ModEntityType.WRAITH.get(), MobsConfig.WraithSpawnWeight.get(), 1, 1));
            }
            if (biome.is(ModTags.Biomes.WARLOCK_SPAWN) && !biome.is(ModTags.Biomes.WARLOCK_EXCLUDE_SPAWN) && MobsConfig.WarlockSpawnWeight.get() > 0){
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(ModEntityType.WARLOCK.get(), MobsConfig.WarlockSpawnWeight.get(), 1, 1));
            }
            if (biome.is(ModTags.Biomes.HERETIC_SPAWN) && !biome.is(ModTags.Biomes.HERETIC_EXCLUDE_SPAWN) && MobsConfig.HereticSpawnWeight.get() > 0){
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(ModEntityType.HERETIC.get(), MobsConfig.HereticSpawnWeight.get(), 1, 1));
            }
            if (biome.is(ModTags.Biomes.MAVERICK_SPAWN) && !biome.is(ModTags.Biomes.MAVERICK_EXCLUDE_SPAWN) && MobsConfig.MaverickSpawnWeight.get() > 0){
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(ModEntityType.MAVERICK.get(), MobsConfig.MaverickSpawnWeight.get(), 1, 1));
            }
        }
        if (biome.is(Biomes.SOUL_SAND_VALLEY)){
            if (MobsConfig.WraithSpawnWeight.get() > 0) {
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(ModEntityType.WRAITH.get(), MobsConfig.WraithSpawnWeight.get(), 1, 1));
                builder.getMobSpawnSettings().addMobCharge(ModEntityType.WRAITH.get(), 0.7D, 0.15D);
            }
        }
    }

    public static boolean startName(ResourceKey<Biome> biomeResourceKey, String string){
        return biomeResourceKey.registry().getNamespace().startsWith(string);
    }

    public static boolean containsName(ResourceKey<Biome> biomeResourceKey, String string){
        return biomeResourceKey.registry().getNamespace().contains(string);
    }
}
