package com.Polarice3.Goety.common.world.processors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WreckProcessor extends StructureProcessor {
    public static final Codec<WreckProcessor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("wreck").forGetter((processor) -> processor.wreck),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("to_wreck").forGetter((processor) -> processor.toWreck),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("slab").forGetter((processor) -> processor.slab),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("stairs").forGetter((processor) -> processor.stairs),
            BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("other").forGetter((processor) -> processor.other)
    ).apply(instance, WreckProcessor::new));
    private final float wreck;
    private final Block toWreck;
    private final Block slab;
    private final Block stairs;
    private final Optional<Block> other;

    public WreckProcessor(float wreck, Block toWreck, Block slab, Block stairs, Optional<Block> other) {
        this.wreck = wreck;
        this.toWreck = toWreck;
        this.slab = slab;
        this.stairs = stairs;
        this.other = other;
    }

    @Nullable
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader p_74016_, BlockPos p_74017_, BlockPos p_74018_, StructureTemplate.StructureBlockInfo p_74019_, StructureTemplate.StructureBlockInfo p_74020_, StructurePlaceSettings p_74021_) {
        RandomSource randomsource = p_74021_.getRandom(p_74020_.pos());
        BlockState blockstate = p_74020_.state();
        BlockPos blockpos = p_74020_.pos();
        BlockState blockstate1 = null;
        if (blockstate.is(this.toWreck)) {
            blockstate1 = this.maybeReplaceFullStoneBlock(randomsource);
        }

        return blockstate1 != null ? new StructureTemplate.StructureBlockInfo(blockpos, blockstate1, p_74020_.nbt()) : p_74020_;
    }

    @Nullable
    private BlockState maybeReplaceFullStoneBlock(RandomSource p_230256_) {
        if (p_230256_.nextFloat() > this.wreck) {
            return null;
        } else {
            List<BlockState> list = new ArrayList<>();
            list.add(getRandomHalfSlab(p_230256_, this.slab));
            list.add(getRandomFacingStairs(p_230256_, this.stairs));
            this.other.ifPresent(block -> list.add(block.defaultBlockState()));
            return getRandomBlock(p_230256_, list);
        }
    }

    private static BlockState getRandomHalfSlab(RandomSource p_230258_, Block p_230259_) {
        if (!p_230259_.defaultBlockState().hasProperty(SlabBlock.TYPE)) {
            return p_230259_.defaultBlockState();
        }
        return p_230259_.defaultBlockState().setValue(SlabBlock.TYPE, p_230258_.nextBoolean() ? SlabType.BOTTOM : SlabType.TOP);
    }

    private static BlockState getRandomFacingStairs(RandomSource p_230258_, Block p_230259_) {
        if (!p_230259_.defaultBlockState().hasProperty(StairBlock.FACING) || !p_230259_.defaultBlockState().hasProperty(StairBlock.HALF)) {
            return p_230259_.defaultBlockState();
        }
        return p_230259_.defaultBlockState().setValue(StairBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(p_230258_)).setValue(StairBlock.HALF, Util.getRandom(Half.values(), p_230258_));
    }

    private static BlockState getRandomBlock(RandomSource p_230264_, List<BlockState> p_230265_) {
        return p_230265_.get(p_230264_.nextInt(p_230265_.size()));
    }

    protected StructureProcessorType<?> getType() {
        return ModProcessors.WRECK_PROCESSOR.get();
    }
}
