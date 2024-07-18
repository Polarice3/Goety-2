package com.Polarice3.Goety.common.world.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.Pools;
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

public class BiggerJigsawStructure extends Structure {
    public static final Codec<BiggerJigsawStructure> CODEC = RecordCodecBuilder.<BiggerJigsawStructure>mapCodec((p_227640_) -> {
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
        })).apply(p_227640_, BiggerJigsawStructure::new);
    }).flatXmap(verifyRange(), verifyRange()).codec();
    private final Holder<StructureTemplatePool> startPool;
    private final Optional<ResourceLocation> startJigsawName;
    private final int maxDepth;
    private final HeightProvider startHeight;
    private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final int maxDistanceFromCenter;

    private static Function<BiggerJigsawStructure, DataResult<BiggerJigsawStructure>> verifyRange() {
        return (p_227638_) -> {
            byte b0;
            switch (p_227638_.terrainAdaptation()) {
                case NONE:
                    b0 = 0;
                    break;
                case BURY:
                case BEARD_THIN:
                case BEARD_BOX:
                    b0 = 12;
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }

            int i = b0;
            return p_227638_.maxDistanceFromCenter + i > 1024 ? DataResult.error("Structure size including terrain adaptation must not exceed 1024") : DataResult.success(p_227638_);
        };
    }

    public BiggerJigsawStructure(Structure.StructureSettings p_227627_, Holder<StructureTemplatePool> p_227628_, Optional<ResourceLocation> p_227629_, int p_227630_, HeightProvider p_227631_, Optional<Heightmap.Types> p_227633_, int p_227634_) {
        super(p_227627_);
        this.startPool = p_227628_;
        this.startJigsawName = p_227629_;
        this.maxDepth = p_227630_;
        this.startHeight = p_227631_;
        this.projectStartToHeightmap = p_227633_;
        this.maxDistanceFromCenter = p_227634_;
    }

    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext p_227636_) {
        ChunkPos chunkpos = p_227636_.chunkPos();
        int i = this.startHeight.sample(p_227636_.random(), new WorldGenerationContext(p_227636_.chunkGenerator(), p_227636_.heightAccessor()));
        BlockPos blockpos = new BlockPos(chunkpos.getMinBlockX(), i, chunkpos.getMinBlockZ());
        Pools.forceBootstrap();
        return JigsawPlacement.addPieces(p_227636_, this.startPool, this.startJigsawName, this.maxDepth, blockpos, false, this.projectStartToHeightmap, this.maxDistanceFromCenter);
    }

    public StructureType<?> type() {
        return ModStructureTypes.BIGGER_JIGSAW_STRUCTURE.get();
    }
}
