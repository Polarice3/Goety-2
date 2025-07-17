package com.Polarice3.Goety.common.world.features.trees;

import com.Polarice3.Goety.common.world.features.ConfiguredFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class ChorusTree extends AbstractMegaTreeGrower {
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource p_222938_, boolean p_222939_) {
        return ConfiguredFeatures.CHORUS_TREE;
    }

    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredMegaFeature(RandomSource p_255928_) {
        if (p_255928_.nextFloat() <= 0.25F) {
            return ConfiguredFeatures.MEGA_CHORUS_VOID_TREE;
        }
        return ConfiguredFeatures.MEGA_CHORUS_TREE;
    }
}
