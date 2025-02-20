package com.Polarice3.Goety.common.world.features;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.google.common.collect.ImmutableList;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.*;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.AlterGroundDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.GiantTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;

import java.util.OptionalInt;

public class ConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> SAPLING_HAUNTED_TREE = FeatureUtils.createKey("goety:haunted_tree_sapling");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SAPLING_ROTTEN_TREE = FeatureUtils.createKey("goety:rotten_tree_sapling");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SAPLING_FANCY_ROTTEN_TREE = FeatureUtils.createKey("goety:fancy_rotten_tree_sapling");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WINDSWEPT_TREE = FeatureUtils.createKey("goety:windswept_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WINDSWEPT_TREE_2 = FeatureUtils.createKey("goety:second_windswept_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PINE_TREE = FeatureUtils.createKey("goety:pine_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MEGA_PINE_TREE = FeatureUtils.createKey("goety:mega_pine_tree");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> p_256171_) {
        FeatureUtils.register(p_256171_, SAPLING_HAUNTED_TREE, Feature.TREE, createHaunted().ignoreVines().build());
        FeatureUtils.register(p_256171_, SAPLING_ROTTEN_TREE, Feature.TREE, createRotten().ignoreVines().build());
        FeatureUtils.register(p_256171_, SAPLING_FANCY_ROTTEN_TREE, Feature.TREE, createFancyRotten().build());
        FeatureUtils.register(p_256171_, WINDSWEPT_TREE_2, Feature.TREE, createWindswept2().build());
    }

    private static TreeConfiguration.TreeConfigurationBuilder createHaunted() {
        return (new TreeConfiguration
                .TreeConfigurationBuilder(BlockStateProvider.simple(ModBlocks.HAUNTED_LOG.get()),
                new FancyTrunkPlacer(9, 12, 0),
                BlockStateProvider.simple(Blocks.AIR),
                new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4),
                new TwoLayersFeatureSize(1, 0, 1, OptionalInt.of(4))))
                .ignoreVines();
    }

    private static TreeConfiguration.TreeConfigurationBuilder createRotten() {
        return createStraightBlobTree(ModBlocks.ROTTEN_LOG.get(), ModBlocks.ROTTEN_LEAVES.get(), 4, 8, 0, 2);
    }

    private static TreeConfiguration.TreeConfigurationBuilder createFancyRotten() {
        return (new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(ModBlocks.ROTTEN_LOG.get()), new FancyTrunkPlacer(3, 11, 0), BlockStateProvider.simple(ModBlocks.ROTTEN_LEAVES.get()), new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4), new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4)))).ignoreVines();
    }

    private static TreeConfiguration.TreeConfigurationBuilder createWindswept() {
        return (new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(ModBlocks.WINDSWEPT_LOG.get()), new ForkingTrunkPlacer(5, 2, 2), BlockStateProvider.simple(ModBlocks.WINDSWEPT_LEAVES.get()), new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)), new TwoLayersFeatureSize(1, 0, 2))).ignoreVines();
    }

    private static TreeConfiguration.TreeConfigurationBuilder createWindswept2() {
        return createStraightBlobTree(ModBlocks.WINDSWEPT_LOG.get(), ModBlocks.WINDSWEPT_LEAVES.get(), 4, 8, 0, 2);
    }

    private static TreeConfiguration.TreeConfigurationBuilder createPine() {
        return (new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(ModBlocks.PINE_LOG.get()), new StraightTrunkPlacer(5, 2, 1), BlockStateProvider.simple(ModBlocks.PINE_LEAVES.get()), new SpruceFoliagePlacer(UniformInt.of(2, 3), UniformInt.of(0, 2), UniformInt.of(1, 2)), new TwoLayersFeatureSize(2, 0, 2))).ignoreVines();
    }

    private static TreeConfiguration.TreeConfigurationBuilder createMegaPine() {
        return (new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(ModBlocks.PINE_LOG.get()), new GiantTrunkPlacer(13, 2, 14), BlockStateProvider.simple(ModBlocks.PINE_LEAVES.get()), new MegaPineFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0), UniformInt.of(13, 17)), new TwoLayersFeatureSize(1, 1, 2))).decorators(ImmutableList.of(new AlterGroundDecorator(BlockStateProvider.simple(Blocks.PODZOL))));
    }

    private static TreeConfiguration.TreeConfigurationBuilder createStraightBlobTree(Block p_195147_, Block p_195148_, int p_195149_, int p_195150_, int p_195151_, int p_195152_) {
        return new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(p_195147_), new StraightTrunkPlacer(p_195149_, p_195150_, p_195151_), BlockStateProvider.simple(p_195148_), new BlobFoliagePlacer(ConstantInt.of(p_195152_), ConstantInt.of(0), 3), new TwoLayersFeatureSize(1, 0, 1));
    }
}
