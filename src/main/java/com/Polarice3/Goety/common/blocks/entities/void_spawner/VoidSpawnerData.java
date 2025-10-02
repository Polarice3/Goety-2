package com.Polarice3.Goety.common.blocks.entities.void_spawner;

import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ModUUIDUtil;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class VoidSpawnerData {
    public static final String TAG_SPAWN_DATA = "spawn_data";
    private static final String TAG_NEXT_MOB_SPAWNS_AT = "next_mob_spawns_at";
    public static MapCodec<VoidSpawnerData> MAP_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            ModUUIDUtil.CODEC_SET.optionalFieldOf("registered_players", Sets.<UUID>newHashSet()).forGetter(trialSpawnerData -> trialSpawnerData.detectedPlayers),
                            ModUUIDUtil.CODEC_SET.optionalFieldOf("current_mobs", Sets.<UUID>newHashSet()).forGetter(trialSpawnerData -> trialSpawnerData.currentMobs),
                            Codec.LONG.optionalFieldOf("cooldown_ends_at", 0L).forGetter(trialSpawnerData -> trialSpawnerData.cooldownEndsAt),
                            Codec.LONG.optionalFieldOf("next_mob_spawns_at", 0L).forGetter(trialSpawnerData -> trialSpawnerData.nextMobSpawnsAt),
                            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("total_mobs_spawned", 0).forGetter(trialSpawnerData -> trialSpawnerData.totalMobsSpawned),
                            SpawnData.CODEC.optionalFieldOf("spawn_data").forGetter(trialSpawnerData -> trialSpawnerData.nextSpawnData),
                            ResourceLocation.CODEC.optionalFieldOf("ejecting_loot_table").forGetter(trialSpawnerData -> trialSpawnerData.ejectingLootTable)
                    )
                    .apply(instance, VoidSpawnerData::new)
    );
    protected final Set<UUID> detectedPlayers = new HashSet<>();
    protected final Set<UUID> currentMobs = new HashSet<>();
    protected long cooldownEndsAt;
    protected long nextMobSpawnsAt;
    protected int totalMobsSpawned;
    protected Optional<SpawnData> nextSpawnData;
    protected Optional<ResourceLocation> ejectingLootTable;
    protected SimpleWeightedRandomList<SpawnData> spawnPotentials;
    @Nullable
    protected Entity displayEntity;
    protected double spin;
    protected double oSpin;

    public VoidSpawnerData() {
        this(Collections.emptySet(), Collections.emptySet(), 0L, 0L, 0, Optional.empty(), Optional.empty());
    }

    public VoidSpawnerData(Set<UUID> set, Set<UUID> set2, long l, long m, int i, Optional<SpawnData> optional, Optional<ResourceLocation> optional2) {
        this.detectedPlayers.addAll(set);
        this.currentMobs.addAll(set2);
        this.cooldownEndsAt = l;
        this.nextMobSpawnsAt = m;
        this.totalMobsSpawned = i;
        this.nextSpawnData = optional;
        this.ejectingLootTable = optional2;
    }

    public void setSpawnPotentialsFromConfig(VoidSpawnerConfig trialSpawnerConfig) {
        SimpleWeightedRandomList<SpawnData> simpleWeightedRandomList = trialSpawnerConfig.spawnPotentialsDefinition();
        if (simpleWeightedRandomList.isEmpty()) {
            this.spawnPotentials = SimpleWeightedRandomList.single(this.nextSpawnData.orElseGet(SpawnData::new));
        } else {
            this.spawnPotentials = simpleWeightedRandomList;
        }
    }

    public void reset() {
        this.detectedPlayers.clear();
        this.totalMobsSpawned = 0;
        this.nextMobSpawnsAt = 0L;
        this.cooldownEndsAt = 0L;
        this.currentMobs.clear();
    }

    public boolean hasMobToSpawn() {
        boolean bl = this.nextSpawnData.isPresent() && ((SpawnData)this.nextSpawnData.get()).getEntityToSpawn().contains("id", 8);
        return bl || !this.spawnPotentials.isEmpty();
    }

    public boolean hasFinishedSpawningAllMobs(VoidSpawnerConfig trialSpawnerConfig, int i) {
        return this.totalMobsSpawned >= trialSpawnerConfig.calculateTargetTotalMobs(i);
    }

    public boolean haveAllCurrentMobsDied() {
        return this.currentMobs.isEmpty();
    }

    public boolean isReadyToSpawnNextMob(ServerLevel serverLevel, VoidSpawnerConfig trialSpawnerConfig, int i) {
        return serverLevel.getGameTime() >= this.nextMobSpawnsAt && this.currentMobs.size() < trialSpawnerConfig.calculateTargetSimultaneousMobs(i);
    }

    public int countAdditionalPlayers(BlockPos blockPos) {
        if (this.detectedPlayers.isEmpty()) {
            Util.logAndPauseIfInIde("Void Spawner at " + blockPos + " has no detected players");
        }

        return Math.max(0, this.detectedPlayers.size() - 1);
    }

    public void tryDetectPlayers(ServerLevel serverLevel, BlockPos blockPos, VoidSpawner voidSpawner) {
        List<UUID> list = voidSpawner.getPlayerDetector().detect(serverLevel, voidSpawner.getEntitySelector(), blockPos, voidSpawner.getRequiredPlayerRange(), true);
        boolean bl = this.detectedPlayers.addAll(list);
        if (bl) {
            this.nextMobSpawnsAt = Math.max(serverLevel.getGameTime() + 40L, this.nextMobSpawnsAt);
            serverLevel.playSound(null, blockPos, ModSounds.VOID_SPAWNER_DETECT_PLAYER.get(), SoundSource.BLOCKS, 1.0F, (serverLevel.getRandom().nextFloat() - serverLevel.getRandom().nextFloat()) * 0.2F + 1.0F);
            VoidSpawner.addDetectPlayerParticles(serverLevel, blockPos, serverLevel.getRandom(), this.detectedPlayers.size());
        }
    }

    public boolean isReadyToOpenShutter(ServerLevel serverLevel, VoidSpawnerConfig trialSpawnerConfig, float f) {
        long l = this.cooldownEndsAt - trialSpawnerConfig.targetCooldownLength();
        return (float)serverLevel.getGameTime() >= (float)l + f;
    }

    public boolean isReadyToEjectItems(ServerLevel serverLevel, VoidSpawnerConfig trialSpawnerConfig, float f) {
        long l = this.cooldownEndsAt - trialSpawnerConfig.targetCooldownLength();
        return (float)(serverLevel.getGameTime() - l) % f == 0.0F;
    }

    public boolean isCooldownFinished(ServerLevel serverLevel) {
        return serverLevel.getGameTime() >= this.cooldownEndsAt;
    }

    public void setEntityId(VoidSpawner trialSpawner, RandomSource randomSource, EntityType<?> entityType) {
        this.getOrCreateNextSpawnData(trialSpawner, randomSource).getEntityToSpawn().putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString());
    }

    protected SpawnData getOrCreateNextSpawnData(VoidSpawner trialSpawner, RandomSource randomSource) {
        if (this.nextSpawnData.isPresent()) {
            return this.nextSpawnData.get();
        } else {
            this.nextSpawnData = Optional.of(this.spawnPotentials.getRandom(randomSource).map(WeightedEntry.Wrapper::getData).orElseGet(SpawnData::new));
            trialSpawner.markUpdated();
            return this.nextSpawnData.get();
        }
    }

    @Nullable
    public Entity getOrCreateDisplayEntity(VoidSpawner trialSpawner, Level level, VoidSpawnerState trialSpawnerState) {
        if (trialSpawner.canSpawnInLevel(level) && trialSpawnerState.hasSpinningMob()) {
            if (this.displayEntity == null) {
                CompoundTag compoundTag = this.getOrCreateNextSpawnData(trialSpawner, level.getRandom()).getEntityToSpawn();
                if (compoundTag.contains("id", 8)) {
                    this.displayEntity = EntityType.loadEntityRecursive(compoundTag, level, Function.identity());
                }
            }

            return this.displayEntity;
        } else {
            return null;
        }
    }

    public CompoundTag getUpdateTag(VoidSpawnerState trialSpawnerState) {
        CompoundTag compoundTag = new CompoundTag();
        if (trialSpawnerState == VoidSpawnerState.ACTIVE) {
            compoundTag.putLong("next_mob_spawns_at", this.nextMobSpawnsAt);
        }

        this.nextSpawnData.ifPresent(
                spawnData -> compoundTag.put(
                        "spawn_data",
                        SpawnData.CODEC.encodeStart(NbtOps.INSTANCE, spawnData)
                                .result()
                                .orElseThrow(() -> new IllegalStateException("Invalid SpawnData"))
                )
        );
        return compoundTag;
    }

    public double getSpin() {
        return this.spin;
    }

    public double getOSpin() {
        return this.oSpin;
    }
}
