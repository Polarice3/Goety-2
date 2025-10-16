package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.world.features.ConfiguredFeatures;
import com.Polarice3.Goety.init.ModSoundTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.material.MapColor;

public class EndGrowthBlock extends Block implements BonemealableBlock {

    public EndGrowthBlock() {
        super(ModBlocks.EndStoneProperties()
                .mapColor(MapColor.COLOR_PURPLE)
                .randomTicks()
                .sound(ModSoundTypes.CHORUS_GRASS));
    }

    private static boolean canBeGrass(BlockState p_56824_, LevelReader p_56825_, BlockPos p_56826_) {
        BlockPos blockpos = p_56826_.above();
        BlockState blockstate = p_56825_.getBlockState(blockpos);
        if (blockstate.is(Blocks.SNOW) && blockstate.getValue(SnowLayerBlock.LAYERS) == 1) {
            return true;
        } else if (blockstate.getFluidState().getAmount() == 8) {
            return false;
        } else if (!blockstate.isCollisionShapeFullBlock(p_56825_, blockpos)) {
            return true;
        } else {
            int i = LightEngine.getLightBlockInto(p_56825_, p_56824_, p_56826_, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(p_56825_, blockpos));
            return i < p_56825_.getMaxLightLevel();
        }
    }

    private static boolean canPropagate(BlockState p_56828_, LevelReader p_56829_, BlockPos p_56830_) {
        BlockPos blockpos = p_56830_.above();
        return canBeGrass(p_56828_, p_56829_, p_56830_) && p_56829_.getFluidState(blockpos).isEmpty();
    }

    public void randomTick(BlockState p_222508_, ServerLevel p_222509_, BlockPos p_222510_, RandomSource p_222511_) {
        if (!canBeGrass(p_222508_, p_222509_, p_222510_)) {
            if (!p_222509_.isLoaded(p_222510_)) {
                return;
            }
            p_222509_.setBlockAndUpdate(p_222510_, ModBlocks.END_ROCK.get().defaultBlockState());
        } else {
            if (!p_222509_.isLoaded(p_222510_)) {
                return;
            }
            if (p_222509_.getMaxLocalRawBrightness(p_222510_.above()) >= 9) {
                BlockState blockstate = this.defaultBlockState();

                for (int i = 0; i < 4; ++i) {
                    BlockPos blockpos = p_222510_.offset(p_222511_.nextInt(3) - 1, p_222511_.nextInt(5) - 3, p_222511_.nextInt(3) - 1);
                    if (p_222509_.getBlockState(blockpos).is(ModBlocks.END_ROCK.get()) && canPropagate(blockstate, p_222509_, blockpos)) {
                        p_222509_.setBlockAndUpdate(blockpos, blockstate);
                    }
                }
            }

        }
    }

    public boolean isValidBonemealTarget(LevelReader p_256229_, BlockPos p_256432_, BlockState p_255677_, boolean p_256630_) {
        return p_256229_.getBlockState(p_256432_.above()).isAir();
    }

    public boolean isBonemealSuccess(Level p_221275_, RandomSource p_221276_, BlockPos p_221277_, BlockState p_221278_) {
        return true;
    }

    public void performBonemeal(ServerLevel p_221270_, RandomSource p_221271_, BlockPos p_221272_, BlockState p_221273_) {
        BlockPos blockpos = p_221272_.above();
        BlockState blockstate = ModBlocks.END_GROWTH_VINES.get().defaultBlockState();

        label49:
        for(int i = 0; i < 128; ++i) {
            BlockPos blockpos1 = blockpos;

            for(int j = 0; j < i / 16; ++j) {
                blockpos1 = blockpos1.offset(p_221271_.nextInt(3) - 1, (p_221271_.nextInt(3) - 1) * p_221271_.nextInt(3) / 2, p_221271_.nextInt(3) - 1);
                if (!p_221270_.getBlockState(blockpos1.below()).is(this) || p_221270_.getBlockState(blockpos1).isCollisionShapeFullBlock(p_221270_, blockpos1)) {
                    continue label49;
                }
            }

            BlockState blockstate1 = p_221270_.getBlockState(blockpos1);
            BlockState blockstate2 = p_221270_.getBlockState(blockpos1.below());
            if (blockstate1.is(blockstate.getBlock()) && p_221271_.nextInt(10) == 0) {
                ((BonemealableBlock)blockstate.getBlock()).performBonemeal(p_221270_, p_221271_, blockpos1, blockstate1);
            }

            if (blockstate1.isAir() && blockstate2.is(this)) {
                ResourceKey<ConfiguredFeature<?, ?>> resourcekey = ConfiguredFeatures.END_GROWTH_BLOCK_BONEMEAL;

                Holder<ConfiguredFeature<?, ?>> holder = p_221270_.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(resourcekey).orElse(null);
                if (holder != null) {
                    holder.value().place(p_221270_, p_221270_.getChunkSource().getGenerator(), p_221271_, blockpos1);
                }
            }
        }

    }
}
