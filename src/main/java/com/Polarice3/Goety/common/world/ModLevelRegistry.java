package com.Polarice3.Goety.common.world;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.common.world.ModifiableStructureInfo;

public class ModLevelRegistry {

    public static void addBiomeSpawns(Holder<Biome> biome, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (!biome.containsTag(ModTags.Biomes.COMMON_BLACKLIST) && !biome.is(biomeResourceKey -> biomeResourceKey.registry().getNamespace().contains("alexscaves"))){
            if (biome.is(ModTags.Biomes.REAPER_SPAWN) && !biome.is(ModTags.Biomes.REAPER_EXCLUDE_SPAWN) && MobsConfig.ReaperSpawnWeight.get() > 0){
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(ModEntityType.REAPER.get(), MobsConfig.ReaperSpawnWeight.get(), MobsConfig.ReaperSpawnMinCount.get(), MobsConfig.ReaperSpawnMaxCount.get()));
            }
            if (biome.is(ModTags.Biomes.WRAITH_SPAWN) && !biome.is(ModTags.Biomes.WRAITH_EXCLUDE_SPAWN) && MobsConfig.WraithSpawnWeight.get() > 0){
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(ModEntityType.WRAITH.get(), MobsConfig.WraithSpawnWeight.get(), MobsConfig.WraithSpawnMinCount.get(), MobsConfig.WraithSpawnMaxCount.get()));
            }
            if (biome.is(ModTags.Biomes.MUCK_WRAITH_SPAWN) && !biome.is(ModTags.Biomes.MUCK_WRAITH_EXCLUDE_SPAWN) && MobsConfig.MuckWraithSpawnWeight.get() > 0){
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(ModEntityType.MUCK_WRAITH.get(), MobsConfig.MuckWraithSpawnWeight.get(), MobsConfig.MuckWraithSpawnMinCount.get(), MobsConfig.MuckWraithSpawnMaxCount.get()));
            }
            if (biome.is(ModTags.Biomes.WEB_SPIDER_SPAWN) && !biome.is(ModTags.Biomes.WEB_SPIDER_EXCLUDE_SPAWN) && MobsConfig.WebSpiderSpawnWeight.get() > 0){
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(ModEntityType.WEB_SPIDER.get(), MobsConfig.WebSpiderSpawnWeight.get(), MobsConfig.WebSpiderSpawnMinCount.get(), MobsConfig.WebSpiderSpawnMaxCount.get()));
            }
            if (biome.is(ModTags.Biomes.ICY_SPIDER_SPAWN) && !biome.is(ModTags.Biomes.ICY_SPIDER_EXCLUDE_SPAWN) && MobsConfig.IcySpiderSpawnWeight.get() > 0){
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(ModEntityType.ICY_SPIDER.get(), MobsConfig.IcySpiderSpawnWeight.get(), MobsConfig.IcySpiderSpawnMinCount.get(), MobsConfig.IcySpiderSpawnMaxCount.get()));
            }
            if (biome.is(ModTags.Biomes.FRAYED_SPAWN) && !biome.is(ModTags.Biomes.FRAYED_EXCLUDE_SPAWN) && MobsConfig.FrayedSpawnWeight.get() > 0){
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(ModEntityType.FRAYED.get(), MobsConfig.FrayedSpawnWeight.get(), MobsConfig.FrayedSpawnMinCount.get(), MobsConfig.FrayedSpawnMaxCount.get()));
            }
            if (biome.is(ModTags.Biomes.RATTLED_SPAWN) && !biome.is(ModTags.Biomes.RATTLED_EXCLUDE_SPAWN) && MobsConfig.RattledSpawnWeight.get() > 0){
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(ModEntityType.RATTLED.get(), MobsConfig.RattledSpawnWeight.get(), MobsConfig.RattledSpawnMinCount.get(), MobsConfig.RattledSpawnMaxCount.get()));
            }
            if (biome.is(ModTags.Biomes.NECROMANCER_SPAWN) && !biome.is(ModTags.Biomes.NECROMANCER_EXCLUDE_SPAWN) && MobsConfig.NecromancerSpawnWeight.get() > 0){
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(ModEntityType.NECROMANCER.get(), MobsConfig.NecromancerSpawnWeight.get(), MobsConfig.NecromancerSpawnMinCount.get(), MobsConfig.NecromancerSpawnMaxCount.get()));
            }
            if (biome.is(ModTags.Biomes.WARLOCK_SPAWN) && !biome.is(ModTags.Biomes.WARLOCK_EXCLUDE_SPAWN) && MobsConfig.WarlockSpawnWeight.get() > 0){
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(ModEntityType.WARLOCK.get(), MobsConfig.WarlockSpawnWeight.get(), MobsConfig.WarlockSpawnMinCount.get(), MobsConfig.WarlockSpawnMaxCount.get()));
            }
            if (biome.is(ModTags.Biomes.HERETIC_SPAWN) && !biome.is(ModTags.Biomes.HERETIC_EXCLUDE_SPAWN) && MobsConfig.HereticSpawnWeight.get() > 0){
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(ModEntityType.HERETIC.get(), MobsConfig.HereticSpawnWeight.get(), MobsConfig.HereticSpawnMinCount.get(), MobsConfig.HereticSpawnMaxCount.get()));
            }
            if (biome.is(ModTags.Biomes.MAVERICK_SPAWN) && !biome.is(ModTags.Biomes.MAVERICK_EXCLUDE_SPAWN) && MobsConfig.MaverickSpawnWeight.get() > 0){
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(ModEntityType.MAVERICK.get(), MobsConfig.MaverickSpawnWeight.get(), MobsConfig.MaverickSpawnMinCount.get(), MobsConfig.MaverickSpawnMaxCount.get()));
            }
        }
        if (biome.is(Biomes.SOUL_SAND_VALLEY) || biome.is(new ResourceLocation("netherexp:black_ice_glaciers"))){
            if (MobsConfig.ReaperSpawnWeight.get() > 0 && !biome.is(ModTags.Biomes.REAPER_EXCLUDE_SPAWN)) {
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(ModEntityType.REAPER.get(), MobsConfig.ReaperSpawnWeight.get(), MobsConfig.ReaperSpawnMinCount.get(), MobsConfig.ReaperSpawnMaxCount.get()));
                builder.getMobSpawnSettings().addMobCharge(ModEntityType.REAPER.get(), 0.7D, 0.15D);
            }
            if (MobsConfig.WraithSpawnWeight.get() > 0 && !biome.is(ModTags.Biomes.WRAITH_EXCLUDE_SPAWN)) {
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(ModEntityType.WRAITH.get(), MobsConfig.WraithSpawnWeight.get(), MobsConfig.WraithSpawnMinCount.get(), MobsConfig.WraithSpawnMaxCount.get()));
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

    public static void addStructureSpawns(Holder<Structure> structure, ModifiableStructureInfo.StructureInfo.Builder builder) {
        if (MobsConfig.NecromancerSpawnStructure.get() && structure.is(ModTags.Structures.NECROMANCER_SPAWN) && MobsConfig.NecromancerSpawnWeight.get() > 0) {
            builder.getStructureSettings().getOrAddSpawnOverrides(MobCategory.MONSTER).addSpawn(new MobSpawnSettings.SpawnerData(ModEntityType.NECROMANCER.get(), MobsConfig.NecromancerSpawnWeight.get(), MobsConfig.NecromancerSpawnMinCount.get(), MobsConfig.NecromancerSpawnMaxCount.get()));
        }
    }
}
