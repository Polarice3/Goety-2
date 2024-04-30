package com.Polarice3.Goety.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class FreezeLampBlock extends Block {
    public FreezeLampBlock() {
        super(Properties.of()
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
                    pLevel.gameEvent(null, GameEvent.BLOCK_CHANGE, blockPos);
                }

                if (blockState.isAir() && Blocks.SNOW.defaultBlockState().canSurvive(pLevel, blockPos) && !(pLevel.getBlockState(blockPos.below()).getBlock() instanceof FreezeLampBlock)) {
                    pLevel.setBlockAndUpdate(blockPos, Blocks.SNOW.defaultBlockState());
                    pLevel.gameEvent(null, GameEvent.BLOCK_CHANGE, blockPos);
                }

                if (blockState.getBlock() instanceof CauldronBlock){
                    pLevel.setBlockAndUpdate(blockPos, Blocks.POWDER_SNOW_CAULDRON.defaultBlockState());
                    pLevel.gameEvent(null, GameEvent.BLOCK_CHANGE, blockPos);
                } else if (blockState.getBlock() instanceof LayeredCauldronBlock && blockState.getValue(LayeredCauldronBlock.LEVEL) != 3){
                    BlockState blockstate = blockState.cycle(LayeredCauldronBlock.LEVEL);
                    pLevel.setBlockAndUpdate(blockPos, blockstate);
                    pLevel.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(blockstate));
                }
            }
        }
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRand) {
        double d0 = 0.5625D;
        RandomSource random = pLevel.random;

        for(Direction direction : Direction.values()) {
            BlockPos blockpos = pPos.relative(direction);
            if (!pLevel.getBlockState(blockpos).isSolidRender(pLevel, blockpos)) {
                Direction.Axis direction$axis = direction.getAxis();
                double d1 = direction$axis == Direction.Axis.X ? 0.5D + d0 * (double)direction.getStepX() : (double)random.nextFloat();
                double d2 = direction$axis == Direction.Axis.Y ? 0.5D + d0 * (double)direction.getStepY() : (double)random.nextFloat();
                double d3 = direction$axis == Direction.Axis.Z ? 0.5D + d0 * (double)direction.getStepZ() : (double)random.nextFloat();
                pLevel.addParticle(ParticleTypes.SNOWFLAKE, (double)pPos.getX() + d1, (double)pPos.getY() + d2, (double)pPos.getZ() + d3, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
