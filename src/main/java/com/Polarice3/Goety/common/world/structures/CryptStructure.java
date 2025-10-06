package com.Polarice3.Goety.common.world.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.Optional;
import java.util.function.Function;

//Based/Stolen from @Fuzss codes:https://github.com/Fuzss/illagerinvasion/blob/main/1.20.1/Common/src/main/java/fuzs/illagerinvasion/world/level/levelgen/structure/structure/structures/LabyrinthStructure.java
public class CryptStructure extends BiggerJigsawStructure {
    public static final Codec<CryptStructure> CODEC = RecordCodecBuilder.<CryptStructure>mapCodec((p_227640_) -> {
        return p_227640_.group(settingsCodec(p_227640_), StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter((p_227656_) -> {
            return p_227656_.startPool;
        }), ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter((p_227654_) -> {
            return p_227654_.startJigsawName;
        }), Codec.intRange(0, 30).fieldOf("size").forGetter((p_227652_) -> {
            return p_227652_.maxDepth;
        }), HeightProvider.CODEC.fieldOf("start_height").forGetter((p_227649_) -> {
            return p_227649_.startHeight;
        }), Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter((p_227644_) -> {
            return p_227644_.projectStartToHeightmap;
        }), Codec.intRange(1, 1024).fieldOf("max_distance_from_center").forGetter((p_227642_) -> {
            return p_227642_.maxDistanceFromCenter;
        })).apply(p_227640_, CryptStructure::new);
    }).flatXmap(verifyCryptRange(), verifyCryptRange()).codec();
    private static final int SEA_LEVEL = 63;
    private static final int SAFE_UNDERGROUND_TUNNEL_HEIGHT = 16;

    public CryptStructure(StructureSettings p_227627_, Holder<StructureTemplatePool> p_227628_, Optional<ResourceLocation> p_227629_, int p_227630_, HeightProvider p_227631_, Optional<Heightmap.Types> p_227633_, int p_227634_) {
        super(p_227627_, p_227628_, p_227629_, p_227630_, p_227631_, p_227633_, p_227634_);
    }

    public static Function<CryptStructure, DataResult<CryptStructure>> verifyCryptRange() {
        return (p_227638_) -> {
            int i = switch (p_227638_.terrainAdaptation()) {
                case NONE -> 0;
                case BURY, BEARD_THIN, BEARD_BOX -> 12;
            };

            return p_227638_.maxDistanceFromCenter + i > 1024 ? DataResult.error(() -> "Structure size including terrain adaptation must not exceed 1024") : DataResult.success(p_227638_);
        };
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        return this._findGenerationPoint(context)
                .filter(generationStub -> generationStub.position().getY() <=
                        SEA_LEVEL - SAFE_UNDERGROUND_TUNNEL_HEIGHT);
    }

    public Optional<Structure.GenerationStub> _findGenerationPoint(Structure.GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        int i = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
        BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), i, chunkPos.getMinBlockZ());
        return JigsawPlacement.addPieces(context, this.startPool, this.startJigsawName, this.maxDepth, blockPos, false, this.projectStartToHeightmap, this.maxDistanceFromCenter);
    }

    public StructureType<?> type() {
        return ModStructureTypes.CRYPT_STRUCTURE.get();
    }
}
