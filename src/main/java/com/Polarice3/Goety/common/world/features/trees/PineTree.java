package com.Polarice3.Goety.common.world.features.trees;

import com.Polarice3.Goety.common.world.features.ConfiguredFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class PineTree extends AbstractMegaTreeGrower {
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource p_222938_, boolean p_222939_) {
        return ConfiguredFeatures.PINE_TREE;
    }

    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredMegaFeature(RandomSource p_255928_) {
        return ConfiguredFeatures.MEGA_PINE_TREE;
    }
}
