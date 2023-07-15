package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.ForbiddenGrassBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.BlockLightEngine;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import javax.annotation.Nullable;

public class ForbiddenGrassBlock extends SnowyDirtBlock implements EntityBlock {
    public ForbiddenGrassBlock() {
        super(Properties.of(Material.GRASS, MaterialColor.COLOR_PURPLE)
                .randomTicks()
                .strength(0.6F)
                .sound(SoundType.GRASS));
    }

    private static boolean canBeGrass(BlockState pState, WorldGenLevel pLevelReader, BlockPos pPos) {
        BlockPos blockpos = pPos.above();
        BlockState blockstate = pLevelReader.getBlockState(blockpos);
        if (blockstate.is(Blocks.SNOW) && blockstate.getValue(SnowLayerBlock.LAYERS) == 1) {
            return true;
        } else if (blockstate.getFluidState().getAmount() == 8) {
            return false;
        } else {
            int i = BlockLightEngine.getLightBlockInto(pLevelReader, pState, pPos, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(pLevelReader, blockpos));
            return i < pLevelReader.getMaxLightLevel();
        }
    }

    private static boolean canPropagate(WorldGenLevel pLevel, BlockPos pPos) {
        BlockPos blockpos = pPos.above();
        return pLevel.getBlockState(blockpos).isAir() && !pLevel.getFluidState(blockpos).is(FluidTags.WATER);
    }

    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pLevel.getBlockState(pPos.above()).getBlock() instanceof BaseFireBlock || !canBeGrass(pState, pLevel, pPos)){
            if (!pLevel.isAreaLoaded(pPos, 3)) {
                return;
            }
            pLevel.setBlockAndUpdate(pPos, Blocks.DIRT.defaultBlockState());
        } else {
            BlockState blockstate = this.defaultBlockState();

            for(int i = 0; i < 4; ++i) {
                BlockPos blockpos = pPos.offset(pRandom.nextInt(3) - 1, pRandom.nextInt(5) - 3, pRandom.nextInt(3) - 1);
                if (pLevel.getBlockState(blockpos).is(Blocks.DIRT) || pLevel.getBlockState(blockpos).is(Blocks.GRASS_BLOCK)) {
                    if (canPropagate(pLevel, blockpos)) {
                        pLevel.setBlockAndUpdate(blockpos, blockstate.setValue(SNOWY, pLevel.getBlockState(blockpos.above()).is(Blocks.SNOW)));
                    }
                }
            }

        }
    }

    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRand) {
        if (pLevel.getBlockState(pPos.above()).isAir()) {
            if (pRand.nextFloat() <= 0.25F) {
                double d0 = (double) pPos.getX() + 0.5D;
                double d1 = (double) pPos.getY() + 0.6D;
                double d2 = (double) pPos.getZ() + 0.5D;
                pLevel.addParticle(ParticleTypes.WITCH, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new ForbiddenGrassBlockEntity(p_153215_, p_153216_);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152755_, BlockState p_152756_, BlockEntityType<T> p_152757_) {
        return (world, pos, state, blockEntity) -> {
            if (blockEntity instanceof ForbiddenGrassBlockEntity arcaBlock)
                arcaBlock.tick();
        };
    }
}
