package com.Polarice3.Goety.common.world.features;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;

import java.util.OptionalInt;

public class ConfiguredFeatures {

    public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> SAPLING_HAUNTED_TREE =
            FeatureUtils.register("haunted_tree_sapling", Feature.TREE, createHaunted().ignoreVines().build());

    private static TreeConfiguration.TreeConfigurationBuilder createHaunted() {
        return (new TreeConfiguration
                .TreeConfigurationBuilder(BlockStateProvider.simple(ModBlocks.HAUNTED_LOG.get()),
                new FancyTrunkPlacer(9, 12, 0),
                BlockStateProvider.simple(Blocks.AIR),
                new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4),
                new TwoLayersFeatureSize(1, 0, 1, OptionalInt.of(4))))
                .ignoreVines();
    }
}
