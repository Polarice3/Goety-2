package com.Polarice3.Goety.common.world.processors;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader p_74016_, BlockPos p_74017_, BlockPos p_74018_, StructureTemplate.StructureBlockInfo p_74019_, StructureTemplate.StructureBlockInfo p_74020_, StructurePlaceSettings p_74021_) {
        RandomSource randomsource = p_74021_.getRandom(p_74020_.pos());
        BlockState blockstate = p_74020_.state();
        BlockPos blockpos = p_74020_.pos();
        BlockState blockstate1 = null;
        if (blockstate.is(this.toMarsh)) {
            blockstate1 = this.maybeReplaceFullStoneBlock(p_74016_, blockpos, randomsource);
        }

        return blockstate1 != null ? new StructureTemplate.StructureBlockInfo(blockpos, blockstate1, p_74020_.nbt()) : p_74020_;
    }

    @Nullable
    private BlockState maybeReplaceFullStoneBlock(LevelReader p_74016_, BlockPos p_74017_, RandomSource p_230256_) {
        if (p_230256_.nextFloat() >= CHANCE || Arrays.stream(Direction.values()).anyMatch(direction -> p_74016_.getBlockState(p_74017_.relative(direction)).getBlock() instanceof CrossCollisionBlock)) {
            return ModBlocks.MARSH_PLANKS.get().defaultBlockState();
        } else {
            List<BlockState> list = new ArrayList<>();
            list.add(getSlab(ModBlocks.MARSH_SLAB.get()));
            return getRandomBlock(p_230256_, list);
        }
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
