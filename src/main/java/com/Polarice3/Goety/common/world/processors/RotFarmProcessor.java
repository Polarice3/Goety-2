package com.Polarice3.Goety.common.world.processors;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;

public class RotFarmProcessor extends StructureProcessor {
    public static final Codec<RotFarmProcessor> CODEC = Codec.unit(RotFarmProcessor::new);

    @Nullable
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader p_74016_, BlockPos p_74017_, BlockPos p_74018_, StructureTemplate.StructureBlockInfo p_74019_, StructureTemplate.StructureBlockInfo p_74020_, StructurePlaceSettings p_74021_) {
        RandomSource randomsource = p_74021_.getRandom(p_74020_.pos());
        BlockState blockstate = p_74020_.state();
        BlockPos blockpos = p_74020_.pos();
        BlockState blockstate1 = null;
        if (blockstate.is(BlockTags.CROPS)) {
            blockstate1 = this.replaceCrops(randomsource);
        } else if (blockstate.getBlock() instanceof FarmBlock) {
            blockstate1 = this.replaceFarmland(randomsource);
        } else if (blockstate.getBlock() instanceof SaplingBlock) {
            blockstate1 = this.replaceSapling(randomsource);
        }

        return blockstate1 != null ? new StructureTemplate.StructureBlockInfo(blockpos, blockstate1, p_74020_.nbt()) : p_74020_;
    }

    private BlockState replaceCrops(RandomSource p_230256_) {
        BlockState blockState = Blocks.CAVE_AIR.defaultBlockState();
        if (p_230256_.nextFloat() <= 0.25F) {
            blockState = Blocks.DEAD_BUSH.defaultBlockState();
        }
        return blockState;
    }

    private BlockState replaceFarmland(RandomSource p_230256_) {
        BlockState blockState = Blocks.DIRT.defaultBlockState();
        if (p_230256_.nextFloat() <= 0.25F) {
            blockState = Blocks.COARSE_DIRT.defaultBlockState();
        }
        return blockState;
    }

    private BlockState replaceSapling(RandomSource p_230256_) {
        BlockState blockState = Blocks.CAVE_AIR.defaultBlockState();
        if (p_230256_.nextFloat() <= 0.25F) {
            blockState = Blocks.DEAD_BUSH.defaultBlockState();
        }
        return blockState;
    }

    protected StructureProcessorType<?> getType() {
        return ModProcessors.ROT_FARM_PROCESSOR.get();
    }
}
