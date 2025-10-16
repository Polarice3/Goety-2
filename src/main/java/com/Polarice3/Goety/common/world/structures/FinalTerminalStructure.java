package com.Polarice3.Goety.common.world.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.Optional;
import java.util.function.Function;

public class FinalTerminalStructure extends BiggerJigsawStructure{
    public static final Codec<FinalTerminalStructure> CODEC = RecordCodecBuilder.<FinalTerminalStructure>mapCodec((p_227640_) -> {
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
        })).apply(p_227640_, FinalTerminalStructure::new);
    }).flatXmap(verifyRangeTerminal(), verifyRangeTerminal()).codec();

    public FinalTerminalStructure(StructureSettings p_227627_, Holder<StructureTemplatePool> p_227628_, Optional<ResourceLocation> p_227629_, int p_227630_, HeightProvider p_227631_, Optional<Heightmap.Types> p_227633_, int p_227634_) {
        super(p_227627_, p_227628_, p_227629_, p_227630_, p_227631_, p_227633_, p_227634_);
    }

    public static Function<FinalTerminalStructure, DataResult<FinalTerminalStructure>> verifyRangeTerminal() {
        return (p_227638_) -> {
            int b0 = switch (p_227638_.terrainAdaptation()) {
                case NONE -> 0;
                case BURY, BEARD_THIN, BEARD_BOX -> 12;
            };

            return p_227638_.maxDistanceFromCenter + b0 > 1024 ? DataResult.error(() -> "Structure size including terrain adaptation must not exceed 1024") : DataResult.success(p_227638_);
        };
    }

    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext p_227528_) {
        int i = 5;
        int j = 5;
        ChunkPos chunkpos = p_227528_.chunkPos();
        int k = chunkpos.getBlockX(7);
        int l = chunkpos.getBlockZ(7);
        BlockPos blockpos = new BlockPos(k, getLowestY(p_227528_, k, l, i, j), l);
        return blockpos.getY() < 10 ? Optional.empty() : JigsawPlacement.addPieces(p_227528_, this.startPool, this.startJigsawName, this.maxDepth, blockpos.below(3), false, this.projectStartToHeightmap, this.maxDistanceFromCenter);
    }

    public StructureType<?> type() {
        return ModStructureTypes.FINAL_TERMINAL_STRUCTURE.get();
    }
}
