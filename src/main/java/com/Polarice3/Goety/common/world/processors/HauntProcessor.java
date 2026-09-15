package com.Polarice3.Goety.common.world.processors;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
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
import java.util.Arrays;
import java.util.List;

public class HauntProcessor extends StructureProcessor {
    public static final Codec<HauntProcessor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("to_haunt").forGetter((processor) -> processor.toHaunt)
    ).apply(instance, HauntProcessor::new));
    private static final float CHANCE = 0.4F;
    private final Block toHaunt;

    public HauntProcessor(Block toHaunt) {
        this.toHaunt = toHaunt;
    }

    @Nullable
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader p_74016_, BlockPos p_74017_, BlockPos p_74018_, StructureTemplate.StructureBlockInfo p_74019_, StructureTemplate.StructureBlockInfo p_74020_, StructurePlaceSettings p_74021_) {
        RandomSource randomsource = p_74021_.getRandom(p_74020_.pos());
        BlockState blockstate = p_74020_.state();
        BlockPos blockpos = p_74020_.pos();
        BlockState blockstate1 = null;
        if (blockstate.is(this.toHaunt)) {
            blockstate1 = this.maybeReplaceFullStoneBlock(p_74016_, blockpos, randomsource);
        }

        return blockstate1 != null ? new StructureTemplate.StructureBlockInfo(blockpos, blockstate1, p_74020_.nbt()) : p_74020_;
    }

    @Nullable
    private BlockState maybeReplaceFullStoneBlock(LevelReader p_74016_, BlockPos p_74017_, RandomSource p_230256_) {
        if (p_230256_.nextFloat() >= CHANCE || Arrays.stream(Direction.values()).anyMatch(direction -> p_74016_.getBlockState(p_74017_.relative(direction)).getBlock() instanceof CrossCollisionBlock)) {
            return ModBlocks.HAUNTED_PLANKS.get().defaultBlockState();
        } else {
            List<BlockState> list = new ArrayList<>();
            list.add(getRandomHalfSlab(p_230256_, ModBlocks.HAUNTED_SLAB.get()));
            list.add(getRandomFacingStairs(p_230256_, ModBlocks.HAUNTED_STAIRS.get()));
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
        return ModProcessors.HAUNT_PROCESSOR.get();
    }
}
