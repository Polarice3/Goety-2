package com.Polarice3.Goety.common.blocks.entities.void_spawner;

import com.Polarice3.Goety.utils.ModLootTables;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.SpawnData;

public record VoidSpawnerConfig(
        int requiredPlayerRange,
        int spawnRange,
        float totalMobs,
        float simultaneousMobs,
        float totalMobsAddedPerPlayer,
        float simultaneousMobsAddedPerPlayer,
        int ticksBetweenSpawn,
        int targetCooldownLength,
        SimpleWeightedRandomList<SpawnData> spawnPotentialsDefinition,
        SimpleWeightedRandomList<ResourceLocation> lootTablesToEject
) {
    public static VoidSpawnerConfig DEFAULT = new VoidSpawnerConfig(
            14,
            4,
            6.0F,
            2.0F,
            2.0F,
            1.0F,
            40,
            36000,
            SimpleWeightedRandomList.empty(),
            SimpleWeightedRandomList.<ResourceLocation>builder().add(ModLootTables.VOID_SPAWNER_LOOT, 1).add(ModLootTables.VOID_SPAWNER_KEY, 1).build()
    );
    public static MapCodec<VoidSpawnerConfig> MAP_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            Codec.intRange(1, 128).optionalFieldOf("required_player_range", DEFAULT.requiredPlayerRange).forGetter(VoidSpawnerConfig::requiredPlayerRange),
                            Codec.intRange(1, 128).optionalFieldOf("spawn_range", DEFAULT.spawnRange).forGetter(VoidSpawnerConfig::spawnRange),
                            Codec.floatRange(0.0F, Float.MAX_VALUE).optionalFieldOf("total_mobs", DEFAULT.totalMobs).forGetter(VoidSpawnerConfig::totalMobs),
                            Codec.floatRange(0.0F, Float.MAX_VALUE).optionalFieldOf("simultaneous_mobs", DEFAULT.simultaneousMobs).forGetter(VoidSpawnerConfig::simultaneousMobs),
                            Codec.floatRange(0.0F, Float.MAX_VALUE)
                                    .optionalFieldOf("total_mobs_added_per_player", DEFAULT.totalMobsAddedPerPlayer)
                                    .forGetter(VoidSpawnerConfig::totalMobsAddedPerPlayer),
                            Codec.floatRange(0.0F, Float.MAX_VALUE)
                                    .optionalFieldOf("simultaneous_mobs_added_per_player", DEFAULT.simultaneousMobsAddedPerPlayer)
                                    .forGetter(VoidSpawnerConfig::simultaneousMobsAddedPerPlayer),
                            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("ticks_between_spawn", DEFAULT.ticksBetweenSpawn).forGetter(VoidSpawnerConfig::ticksBetweenSpawn),
                            Codec.intRange(0, Integer.MAX_VALUE)
                                    .optionalFieldOf("target_cooldown_length", DEFAULT.targetCooldownLength)
                                    .forGetter(VoidSpawnerConfig::targetCooldownLength),
                            SpawnData.LIST_CODEC.optionalFieldOf("spawn_potentials", SimpleWeightedRandomList.empty()).forGetter(VoidSpawnerConfig::spawnPotentialsDefinition),
                            SimpleWeightedRandomList.wrappedCodecAllowingEmpty(ResourceLocation.CODEC)
                                    .optionalFieldOf("loot_tables_to_eject", SimpleWeightedRandomList.empty())
                                    .forGetter(VoidSpawnerConfig::lootTablesToEject)
                    )
                    .apply(instance, VoidSpawnerConfig::new)
    );

    public int calculateTargetTotalMobs(int i) {
        return (int)Math.floor(this.totalMobs + this.totalMobsAddedPerPlayer * i);
    }

    public int calculateTargetSimultaneousMobs(int i) {
        return (int)Math.floor(this.simultaneousMobs + this.simultaneousMobsAddedPerPlayer * i);
    }

    @Override
    public int requiredPlayerRange() {
        return requiredPlayerRange;
    }

    @Override
    public int targetCooldownLength() {
        return targetCooldownLength;
    }
}
