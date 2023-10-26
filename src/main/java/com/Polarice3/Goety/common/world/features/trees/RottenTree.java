package com.Polarice3.Goety.common.world.features.trees;

import com.Polarice3.Goety.common.world.features.ConfiguredFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class RottenTree extends AbstractTreeGrower {
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource p_222938_, boolean p_222939_) {
        if (p_222938_.nextInt(10) == 0) {
            return ConfiguredFeatures.SAPLING_FANCY_ROTTEN_TREE;
        } else {
            return ConfiguredFeatures.SAPLING_ROTTEN_TREE;
        }
    }
}
