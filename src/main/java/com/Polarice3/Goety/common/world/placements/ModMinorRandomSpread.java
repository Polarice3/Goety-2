package com.Polarice3.Goety.common.world.placements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

import java.util.Optional;

public class ModMinorRandomSpread extends RandomSpreadStructurePlacement {
    public static final Codec<ModMinorRandomSpread> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Vec3i.offsetCodec(16).optionalFieldOf("locate_offset", Vec3i.ZERO).forGetter(ModMinorRandomSpread::locateOffset),
            FrequencyReductionMethod.CODEC.optionalFieldOf("frequency_reduction_method", FrequencyReductionMethod.DEFAULT).forGetter(ModMinorRandomSpread::frequencyReductionMethod),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("frequency", 1.0F).forGetter(ModMinorRandomSpread::frequency),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("salt").forGetter(ModMinorRandomSpread::salt),
            ExclusionZone.CODEC.optionalFieldOf("exclusion_zone").forGetter(ModMinorRandomSpread::exclusionZone),
            SuperExclusionZone.CODEC.optionalFieldOf("super_exclusion_zone").forGetter(ModMinorRandomSpread::superExclusionZone),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("spacing").forGetter(ModMinorRandomSpread::spacing),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("separation").forGetter(ModMinorRandomSpread::separation),
            RandomSpreadType.CODEC.optionalFieldOf("spread_type", RandomSpreadType.LINEAR).forGetter(ModMinorRandomSpread::spreadType),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("min_distance_from_world_origin").forGetter(ModMinorRandomSpread::minDistanceFromWorldOrigin)
    ).apply(instance, instance.stable(ModMinorRandomSpread::new)));

    private final int spacing;
    private final int separation;
    private final RandomSpreadType spreadType;
    private final Optional<Integer> minDistanceFromWorldOrigin;
    private final Optional<SuperExclusionZone> superExclusionZone;

    public ModMinorRandomSpread(Vec3i locationOffset, FrequencyReductionMethod frequencyReductionMethod, float frequency, int salt, Optional<ExclusionZone> exclusionZone, Optional<SuperExclusionZone> superExclusionZone, int spacing, int separation, RandomSpreadType spreadType, Optional<Integer> minDistanceFromWorldOrigin) {
        super(locationOffset, frequencyReductionMethod, frequency, salt, exclusionZone, spacing, separation, spreadType);
        this.spacing = spacing;
        this.separation = separation;
        this.spreadType = spreadType;
        this.minDistanceFromWorldOrigin = minDistanceFromWorldOrigin;
        this.superExclusionZone = superExclusionZone;
    }

    public int spacing() {
        return this.spacing;
    }

    public int separation() {
        return this.separation;
    }

    public RandomSpreadType spreadType() {
        return this.spreadType;
    }

    public Optional<Integer> minDistanceFromWorldOrigin() {
        return this.minDistanceFromWorldOrigin;
    }

    public Optional<SuperExclusionZone> superExclusionZone() {
        return this.superExclusionZone;
    }

    public boolean isStructureChunk(ChunkGeneratorStructureState p_256635_, int p_255959_, int p_256065_) {
        if (this.superExclusionZone.isEmpty() || !this.superExclusionZone.get().isPlacementForbidden(p_256635_, p_255959_, p_256065_)) {
            return super.isStructureChunk(p_256635_, p_255959_, p_256065_);
        }
        return false;
    }

    public StructurePlacementType<?> type() {
        return ModPlacementType.MOD_MINOR_RANDOM_SPREAD.get();
    }

    public record SuperExclusionZone(HolderSet<StructureSet> otherSet, int chunkCount) {
        public static final Codec<SuperExclusionZone> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                RegistryCodecs.homogeneousList(Registries.STRUCTURE_SET, StructureSet.DIRECT_CODEC).fieldOf("other_set").forGetter(SuperExclusionZone::otherSet),
                Codec.intRange(1, 16).fieldOf("chunk_count").forGetter(SuperExclusionZone::chunkCount)
        ).apply(builder, SuperExclusionZone::new));

        boolean isPlacementForbidden(ChunkGeneratorStructureState chunkGenerator, int l, int j) {
            for (Holder<StructureSet> holder : this.otherSet) {
                if (chunkGenerator.hasStructureChunkInRange(holder, l, j, this.chunkCount)) {
                    return true;
                }
            }

            return false;
        }

        public HolderSet<StructureSet> otherSet() {
            return this.otherSet;
        }

        public int chunkCount() {
            return this.chunkCount;
        }
    }
}
