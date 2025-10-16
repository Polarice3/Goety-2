package com.Polarice3.Goety.common.world.features.trees;

import com.Polarice3.Goety.common.world.features.ConfiguredFeatures;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class ChorusTree extends AbstractMegaTreeGrower {
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource p_222938_, boolean p_222939_) {
        return ConfiguredFeatures.CHORUS_TREE;
    }

    public boolean growTree(ServerLevel p_222905_, ChunkGenerator p_222906_, BlockPos p_222907_, BlockState p_222908_, RandomSource p_222909_) {
        for(int i = 0; i >= -1; --i) {
            for(int j = 0; j >= -1; --j) {
                if (isTwoByTwoSapling(p_222908_, p_222905_, p_222907_, i, j)) {
                    return this.placeMega(p_222905_, p_222906_, p_222907_, p_222908_, p_222909_, i, j);
                }
            }
        }

        ResourceKey<ConfiguredFeature<?, ?>> resourcekey = this.getConfiguredFeature(p_222909_, this.hasFlowers(p_222905_, p_222907_));
        if (p_222905_.getBlockState(p_222907_.below()).is(ModTags.Blocks.CHORUS_BLOSSOM_GROW)) {
            resourcekey = ConfiguredFeatures.CHORUS_BLOSSOM_TREE;
        }
        if (resourcekey == null) {
            return false;
        } else {
            Holder<ConfiguredFeature<?, ?>> holder = p_222905_.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(resourcekey).orElse(null);
            var event = net.minecraftforge.event.ForgeEventFactory.blockGrowFeature(p_222905_, p_222909_, p_222907_, holder);
            holder = event.getFeature();
            if (event.getResult() == net.minecraftforge.eventbus.api.Event.Result.DENY) return false;
            if (holder == null) {
                return false;
            } else {
                ConfiguredFeature<?, ?> configuredfeature = holder.value();
                BlockState blockstate = p_222905_.getFluidState(p_222907_).createLegacyBlock();
                p_222905_.setBlock(p_222907_, blockstate, 4);
                if (configuredfeature.place(p_222905_, p_222906_, p_222909_, p_222907_)) {
                    if (p_222905_.getBlockState(p_222907_) == blockstate) {
                        p_222905_.sendBlockUpdated(p_222907_, p_222908_, blockstate, 2);
                    }

                    return true;
                } else {
                    p_222905_.setBlock(p_222907_, p_222908_, 4);
                    return false;
                }
            }
        }
    }

    private boolean hasFlowers(LevelAccessor p_60012_, BlockPos p_60013_) {
        for(BlockPos blockpos : BlockPos.MutableBlockPos.betweenClosed(p_60013_.below().north(2).west(2), p_60013_.above().south(2).east(2))) {
            if (p_60012_.getBlockState(blockpos).is(BlockTags.FLOWERS)) {
                return true;
            }
        }

        return false;
    }

    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredMegaFeature(RandomSource p_255928_) {
        if (p_255928_.nextFloat() <= 0.25F) {
            return ConfiguredFeatures.MEGA_CHORUS_VOID_TREE;
        }
        return ConfiguredFeatures.MEGA_CHORUS_TREE;
    }

    public boolean placeMega(ServerLevel p_222897_, ChunkGenerator p_222898_, BlockPos p_222899_, BlockState p_222900_, RandomSource p_222901_, int p_222902_, int p_222903_) {
        ResourceKey<ConfiguredFeature<?, ?>> resourcekey = this.getConfiguredMegaFeature(p_222901_);
        if (p_222897_.getBlockState(p_222899_.below()).is(ModTags.Blocks.CHORUS_BLOSSOM_GROW)) {
            if (p_222901_.nextFloat() <= 0.25F) {
                resourcekey = ConfiguredFeatures.MEGA_CHORUS_BLOSSOM_VOID_TREE;
            } else {
                resourcekey = ConfiguredFeatures.MEGA_CHORUS_BLOSSOM_TREE;
            }
        }
        if (resourcekey == null) {
            return false;
        } else {
            Holder<ConfiguredFeature<?, ?>> holder = p_222897_.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(resourcekey).orElse((Holder.Reference<ConfiguredFeature<?, ?>>)null);
            var event = net.minecraftforge.event.ForgeEventFactory.blockGrowFeature(p_222897_, p_222901_, p_222899_, holder);
            holder = event.getFeature();
            if (event.getResult() == net.minecraftforge.eventbus.api.Event.Result.DENY) return false;
            if (holder == null) {
                return false;
            } else {
                ConfiguredFeature<?, ?> configuredfeature = holder.value();
                BlockState blockstate = Blocks.AIR.defaultBlockState();
                p_222897_.setBlock(p_222899_.offset(p_222902_, 0, p_222903_), blockstate, 4);
                p_222897_.setBlock(p_222899_.offset(p_222902_ + 1, 0, p_222903_), blockstate, 4);
                p_222897_.setBlock(p_222899_.offset(p_222902_, 0, p_222903_ + 1), blockstate, 4);
                p_222897_.setBlock(p_222899_.offset(p_222902_ + 1, 0, p_222903_ + 1), blockstate, 4);
                if (configuredfeature.place(p_222897_, p_222898_, p_222901_, p_222899_.offset(p_222902_, 0, p_222903_))) {
                    return true;
                } else {
                    p_222897_.setBlock(p_222899_.offset(p_222902_, 0, p_222903_), p_222900_, 4);
                    p_222897_.setBlock(p_222899_.offset(p_222902_ + 1, 0, p_222903_), p_222900_, 4);
                    p_222897_.setBlock(p_222899_.offset(p_222902_, 0, p_222903_ + 1), p_222900_, 4);
                    p_222897_.setBlock(p_222899_.offset(p_222902_ + 1, 0, p_222903_ + 1), p_222900_, 4);
                    return false;
                }
            }
        }
    }
}
