package com.Polarice3.Goety.common.world.processors;

import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;

public class CobbleAgeProcessor extends StructureProcessor {
    public static final Codec<CobbleAgeProcessor> CODEC = Codec.FLOAT.fieldOf("mossiness").xmap(CobbleAgeProcessor::new, (p_74023_) -> {
        return p_74023_.mossiness;
    }).codec();
    private final float mossiness;

    public CobbleAgeProcessor(float p_74013_) {
        this.mossiness = p_74013_;
    }

    @Nullable
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader p_74016_, BlockPos p_74017_, BlockPos p_74018_, StructureTemplate.StructureBlockInfo p_74019_, StructureTemplate.StructureBlockInfo p_74020_, StructurePlaceSettings p_74021_) {
        RandomSource randomsource = p_74021_.getRandom(p_74020_.pos());
        BlockState blockstate = p_74020_.state();
        BlockPos blockpos = p_74020_.pos();
        BlockState blockstate1 = null;
        if (blockstate.is(Blocks.COBBLESTONE_STAIRS)) {
            blockstate1 = this.maybeReplaceStairs(randomsource, p_74020_.state());
        } else if (blockstate.is(Blocks.COBBLESTONE_SLAB)) {
            blockstate1 = this.maybeReplaceSlab(randomsource);
        } else if (blockstate.is(Blocks.COBBLESTONE_WALL)) {
            blockstate1 = this.maybeReplaceWall(randomsource);
        } else if (blockstate.is(Blocks.COBBLESTONE)) {
            blockstate1 = this.maybeReplaceFullStoneBlock(randomsource);
        }

        return blockstate1 != null ? new StructureTemplate.StructureBlockInfo(blockpos, blockstate1, p_74020_.nbt()) : p_74020_;
    }

    @Nullable
    private BlockState maybeReplaceFullStoneBlock(RandomSource p_230256_) {
        if (p_230256_.nextFloat() > this.mossiness) {
            return null;
        } else {
            BlockState[] ablockstate1 = new BlockState[]{Blocks.MOSSY_COBBLESTONE.defaultBlockState(), getRandomFacingStairs(p_230256_, Blocks.MOSSY_COBBLESTONE_STAIRS)};
            return getRandomBlock(p_230256_, ablockstate1);
        }
    }

    @Nullable
    private BlockState maybeReplaceStairs(RandomSource p_230261_, BlockState p_230262_) {
        Direction direction = p_230262_.getValue(StairBlock.FACING);
        Half half = p_230262_.getValue(StairBlock.HALF);
        if (p_230261_.nextFloat() > this.mossiness) {
            return null;
        } else {
            BlockState[] ablockstate = new BlockState[]{Blocks.MOSSY_COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, direction).setValue(StairBlock.HALF, half), Blocks.MOSSY_COBBLESTONE_SLAB.defaultBlockState()};
            return getRandomBlock(p_230261_, ablockstate);
        }
    }

    @Nullable
    private BlockState maybeReplaceSlab(RandomSource p_230271_) {
        return p_230271_.nextFloat() < this.mossiness ? Blocks.MOSSY_COBBLESTONE_SLAB.defaultBlockState() : null;
    }

    @Nullable
    private BlockState maybeReplaceWall(RandomSource p_230273_) {
        return p_230273_.nextFloat() < this.mossiness ? Blocks.MOSSY_COBBLESTONE_WALL.defaultBlockState() : null;
    }

    private static BlockState getRandomFacingStairs(RandomSource p_230258_, Block p_230259_) {
        return p_230259_.defaultBlockState().setValue(StairBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(p_230258_)).setValue(StairBlock.HALF, Util.getRandom(Half.values(), p_230258_));
    }

    private static BlockState getRandomBlock(RandomSource p_230264_, BlockState[] p_230265_) {
        return p_230265_[p_230264_.nextInt(p_230265_.length)];
    }

    protected StructureProcessorType<?> getType() {
        return ModProcessors.COBBLE_AGE_PROCESSOR.get();
    }
}
