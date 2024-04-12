package com.Polarice3.Goety.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;

public class FreezeLampBlock extends Block {
    public FreezeLampBlock() {
        super(BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS)
                .lightLevel((e) -> 11)
                .strength(0.3F)
                .randomTicks()
                .sound(SoundType.GLASS));
    }

    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pLevel.isLoaded(pPos)) {
            for (int h = 0; h < 16; ++h) {
                int i = pLevel.random.nextIntBetweenInclusive(-4, 4);
                int j = pLevel.random.nextIntBetweenInclusive(-4, 4);
                int k = pLevel.random.nextIntBetweenInclusive(-4, 4);
                BlockPos blockPos = pPos.offset(i, j, k);
                BlockState blockState = pLevel.getBlockState(blockPos);
                FluidState fluidState = pLevel.getFluidState(blockPos);
                if (fluidState.getType() == Fluids.WATER && blockState.getBlock() instanceof LiquidBlock) {
                    pLevel.setBlockAndUpdate(blockPos, Blocks.ICE.defaultBlockState());
                }

                if (blockState.isAir() && Blocks.SNOW.defaultBlockState().canSurvive(pLevel, blockPos)) {
                    pLevel.setBlockAndUpdate(blockPos, Blocks.SNOW.defaultBlockState());
                }
            }
        }
    }
}
