package com.Polarice3.Goety.common.world.processors;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;

public class RotPotProcessor extends StructureProcessor {
    public static final Codec<RotPotProcessor> CODEC = Codec.unit(RotPotProcessor::new);

    @Nullable
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader p_74016_, BlockPos p_74017_, BlockPos p_74018_, StructureTemplate.StructureBlockInfo p_74019_, StructureTemplate.StructureBlockInfo p_74020_, StructurePlaceSettings p_74021_) {
        RandomSource randomsource = p_74021_.getRandom(p_74020_.pos());
        BlockState blockstate = p_74020_.state();
        BlockPos blockpos = p_74020_.pos();
        BlockState blockstate1 = null;
        if (blockstate.is(BlockTags.FLOWER_POTS) && !blockstate.is(Blocks.FLOWER_POT) && !blockstate.is(Blocks.POTTED_DEAD_BUSH) && !blockstate.is(Blocks.POTTED_WITHER_ROSE)) {
            blockstate1 = this.replacePots(randomsource);
        }

        return blockstate1 != null ? new StructureTemplate.StructureBlockInfo(blockpos, blockstate1, p_74020_.nbt()) : p_74020_;
    }

    private BlockState replacePots(RandomSource p_230256_) {
        BlockState blockState = Blocks.FLOWER_POT.defaultBlockState();
        if (p_230256_.nextFloat() <= 0.25F) {
            blockState = Blocks.POTTED_DEAD_BUSH.defaultBlockState();
        } else if (p_230256_.nextFloat() <= 0.025F) {
            blockState = Blocks.POTTED_WITHER_ROSE.defaultBlockState();
        }
        return blockState;
    }

    protected StructureProcessorType<?> getType() {
        return ModProcessors.ROT_POT_PROCESSOR.get();
    }
}
