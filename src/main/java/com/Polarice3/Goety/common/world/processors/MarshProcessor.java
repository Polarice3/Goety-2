package com.Polarice3.Goety.common.world.processors;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarshProcessor extends StructureProcessor {
    public static final Codec<MarshProcessor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("to_marsh").forGetter((processor) -> processor.toMarsh)
    ).apply(instance, MarshProcessor::new));
    private static final float CHANCE = 0.4F;
    private final Block toMarsh;

    public MarshProcessor(Block toMarsh) {
        this.toMarsh = toMarsh;
    }

    @Nullable
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader level, BlockPos offset, BlockPos pos, StructureTemplate.StructureBlockInfo blockInfo, StructureTemplate.StructureBlockInfo relativeBlockInfo, StructurePlaceSettings settings) {
        return relativeBlockInfo;
    }

    @Override
    public List<StructureTemplate.StructureBlockInfo> finalizeProcessing(ServerLevelAccessor level, BlockPos offset, BlockPos pos, List<StructureTemplate.StructureBlockInfo> originalBlockInfos, List<StructureTemplate.StructureBlockInfo> processedBlockInfos, StructurePlaceSettings settings) {
        Map<BlockPos, BlockState> piece = new HashMap<>();
        for (StructureTemplate.StructureBlockInfo info : processedBlockInfos) {
            piece.put(info.pos(), info.state());
        }
        List<StructureTemplate.StructureBlockInfo> result = new ArrayList<>(processedBlockInfos.size());
        for (StructureTemplate.StructureBlockInfo info : processedBlockInfos) {
            if (info.state().is(this.toMarsh)) {
                BlockState weathered = this.maybeReplaceFullStoneBlock(level, piece, info.pos(), settings.getRandom(info.pos()));
                result.add(new StructureTemplate.StructureBlockInfo(info.pos(), weathered, info.nbt()));
            } else {
                result.add(info);
            }
        }
        return result;
    }

    private BlockState maybeReplaceFullStoneBlock(LevelReader level, Map<BlockPos, BlockState> piece, BlockPos pos, RandomSource random) {
        if (random.nextFloat() >= CHANCE || this.hasAttachedNeighbour(level, piece, pos)) {
            return ModBlocks.MARSH_PLANKS.get().defaultBlockState();
        } else {
            List<BlockState> list = new ArrayList<>();
            list.add(getSlab(ModBlocks.MARSH_SLAB.get()));
            return getRandomBlock(random, list);
        }
    }

    private boolean hasAttachedNeighbour(LevelReader level, Map<BlockPos, BlockState> piece, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos neighbourPos = pos.relative(direction);
            BlockState neighbour = piece.get(neighbourPos);
            if (neighbour == null) {
                neighbour = level.getBlockState(neighbourPos);
            }
            if (this.keepsPlank(level, neighbourPos, neighbour)) {
                return true;
            }
        }
        return false;
    }

    private boolean keepsPlank(LevelReader level, BlockPos neighbourPos, BlockState neighbour) {
        return !neighbour.isAir() && !neighbour.isCollisionShapeFullBlock(level, neighbourPos);
    }

    private static BlockState getSlab(Block p_230259_) {
        if (!p_230259_.defaultBlockState().hasProperty(SlabBlock.TYPE)) {
            return p_230259_.defaultBlockState();
        }
        return p_230259_.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.BOTTOM);
    }

    private static BlockState getRandomBlock(RandomSource p_230264_, List<BlockState> p_230265_) {
        return p_230265_.get(p_230264_.nextInt(p_230265_.size()));
    }

    protected StructureProcessorType<?> getType() {
        return ModProcessors.MARSH_PROCESSOR.get();
    }
}
