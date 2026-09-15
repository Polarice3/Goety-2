package com.Polarice3.Goety.common.world.structures;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.List;
import java.util.Optional;

public class WreckedMansionStructure extends Structure {
    public static final Codec<WreckedMansionStructure> CODEC = simpleCodec(WreckedMansionStructure::new);

    public WreckedMansionStructure(Structure.StructureSettings p_230225_) {
        super(p_230225_);
    }

    public Optional<GenerationStub> findGenerationPoint(Structure.GenerationContext p_230235_) {
        Rotation rotation = Rotation.getRandom(p_230235_.random());
        BlockPos blockpos = this.getLowestYIn5by5BoxOffset7Blocks(p_230235_, rotation);
        return blockpos.getY() < 60 ? Optional.empty() : Optional.of(new Structure.GenerationStub(blockpos, (p_230240_) -> {
            this.generatePieces(p_230240_, p_230235_, blockpos, rotation);
        }));
    }

    private void generatePieces(StructurePiecesBuilder p_230242_, Structure.GenerationContext p_230243_, BlockPos p_230244_, Rotation p_230245_) {
        List<WreckedMansionPieces.MansionPiece> list = Lists.newLinkedList();
        WreckedMansionPieces.generateMansion(p_230243_.structureTemplateManager(), p_230244_, p_230245_, list, p_230243_.random());
        list.forEach(p_230242_::addPiece);
    }

    public void afterPlace(WorldGenLevel p_230227_, StructureManager p_230228_, ChunkGenerator p_230229_, RandomSource p_230230_, BoundingBox p_230231_, ChunkPos p_230232_, PiecesContainer p_230233_) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        int i = p_230227_.getMinBuildHeight();
        BoundingBox boundingbox = p_230233_.calculateBoundingBox();
        int j = boundingbox.minY();

        for(int k = p_230231_.minX(); k <= p_230231_.maxX(); ++k) {
            for(int l = p_230231_.minZ(); l <= p_230231_.maxZ(); ++l) {
                blockpos$mutableblockpos.set(k, j, l);
                if (!p_230227_.isEmptyBlock(blockpos$mutableblockpos) && boundingbox.isInside(blockpos$mutableblockpos) && p_230233_.isInsidePiece(blockpos$mutableblockpos)) {
                    for(int i1 = j - 1; i1 > i; --i1) {
                        blockpos$mutableblockpos.setY(i1);
                        if (!p_230227_.isEmptyBlock(blockpos$mutableblockpos) && !p_230227_.getBlockState(blockpos$mutableblockpos).liquid()) {
                            break;
                        }

                        BlockState blockState = Blocks.COBBLESTONE.defaultBlockState();
                        if (p_230230_.nextBoolean()) {
                            blockState = Blocks.MOSSY_COBBLESTONE.defaultBlockState();
                        }
                        p_230227_.setBlock(blockpos$mutableblockpos, blockState, 2);
                    }
                }
            }
        }

    }

    public StructureType<?> type() {
        return ModStructureTypes.WRECKED_MANSION.get();
    }
}
