package com.Polarice3.Goety.common.world.features;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.world.features.configs.ModTreeFeatureConfig;
import com.Polarice3.Goety.common.world.features.trees.trunkplacers.ChorusTrunkPlacer;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.util.valueproviders.WeightedListInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.CherryFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
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
    public static final ResourceKey<ConfiguredFeature<?, ?>> CHORUS_TREE = FeatureUtils.createKey("goety:chorus_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MEGA_CHORUS_TREE = FeatureUtils.createKey("goety:mega_chorus_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CHORUS_BLOSSOM_TREE = FeatureUtils.createKey("goety:chorus_blossom_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MEGA_CHORUS_BLOSSOM_TREE = FeatureUtils.createKey("goety:mega_chorus_blossom_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MEGA_CHORUS_VOID_TREE = FeatureUtils.createKey("goety:mega_chorus_void_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MEGA_CHORUS_BLOSSOM_VOID_TREE = FeatureUtils.createKey("goety:mega_chorus_blossom_void_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CHORUS_GRASS_BLOCK_BONEMEAL = FeatureUtils.createKey("goety:chorus_grass_block_bonemeal");
    public static final ResourceKey<ConfiguredFeature<?, ?>> END_GROWTH_BLOCK_BONEMEAL = FeatureUtils.createKey("goety:end_growth_block_bonemeal");
    public static final ResourceKey<ConfiguredFeature<?, ?>> END_VEGETATION_BONEMEAL = FeatureUtils.createKey("goety:end_vegetation_bonemeal");
    public static final ResourceKey<ConfiguredFeature<?, ?>> END_SOIL_BONEMEAL = FeatureUtils.createKey("goety:end_soil_bonemeal");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> p_256171_) {
        FeatureUtils.register(p_256171_, SAPLING_HAUNTED_TREE, Feature.TREE, createHaunted().ignoreVines().build());
        FeatureUtils.register(p_256171_, SAPLING_ROTTEN_TREE, Feature.TREE, createRotten().ignoreVines().build());
        FeatureUtils.register(p_256171_, SAPLING_FANCY_ROTTEN_TREE, Feature.TREE, createFancyRotten().build());
        FeatureUtils.register(p_256171_, WINDSWEPT_TREE_2, Feature.TREE, createWindswept2().build());
        FeatureUtils.register(p_256171_, CHORUS_TREE, Feature.TREE, createChorus().build());
        FeatureUtils.register(p_256171_, CHORUS_BLOSSOM_TREE, Feature.TREE, createChorusBlossom().build());
        FeatureUtils.register(p_256171_, MEGA_CHORUS_TREE, ModFeatures.CHORUS_TREE.get(), createMegaChorus().build());
        FeatureUtils.register(p_256171_, MEGA_CHORUS_VOID_TREE, ModFeatures.CHORUS_VOID_TREE.get(), createMegaChorus().build());
        FeatureUtils.register(p_256171_, MEGA_CHORUS_BLOSSOM_TREE, ModFeatures.CHORUS_TREE.get(), createMegaChorusBlossom().build());
        FeatureUtils.register(p_256171_, MEGA_CHORUS_BLOSSOM_VOID_TREE, ModFeatures.CHORUS_VOID_TREE.get(), createMegaChorusBlossom().build());
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

    private static TreeConfiguration.TreeConfigurationBuilder createWindswept2() {
        return createStraightBlobTree(ModBlocks.WINDSWEPT_LOG.get(), ModBlocks.WINDSWEPT_LEAVES.get(), 4, 8, 0, 2);
    }

    private static TreeConfiguration.TreeConfigurationBuilder createChorus() {
        return new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(ModBlocks.CHORUS_LOG.get()), new ChorusTrunkPlacer(7, 1, 0, new WeightedListInt(SimpleWeightedRandomList.<IntProvider>builder().add(ConstantInt.of(1), 1).add(ConstantInt.of(2), 1).add(ConstantInt.of(3), 1).build()), UniformInt.of(2, 4), UniformInt.of(-4, -3), UniformInt.of(-1, 0)), BlockStateProvider.simple(ModBlocks.CHORUS_LEAVES.get()), new CherryFoliagePlacer(ConstantInt.of(4), ConstantInt.of(0), ConstantInt.of(5), 0.25F, 0.5F, 0.16666667F, 0.33333334F), new TwoLayersFeatureSize(1, 0, 2)).ignoreVines();
    }

    private static TreeConfiguration.TreeConfigurationBuilder createChorusBlossom() {
        return new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(ModBlocks.CHORUS_LOG.get()), new ChorusTrunkPlacer(7, 1, 0, new WeightedListInt(SimpleWeightedRandomList.<IntProvider>builder().add(ConstantInt.of(1), 1).add(ConstantInt.of(2), 1).add(ConstantInt.of(3), 1).build()), UniformInt.of(2, 4), UniformInt.of(-4, -3), UniformInt.of(-1, 0)), BlockStateProvider.simple(ModBlocks.CHORUS_BLOSSOM_LEAVES.get()), new CherryFoliagePlacer(ConstantInt.of(4), ConstantInt.of(0), ConstantInt.of(5), 0.25F, 0.5F, 0.16666667F, 0.33333334F), new TwoLayersFeatureSize(1, 0, 2)).ignoreVines();
    }

    private static TreeConfiguration.TreeConfigurationBuilder createStraightBlobTree(Block p_195147_, Block p_195148_, int p_195149_, int p_195150_, int p_195151_, int p_195152_) {
        return new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(p_195147_), new StraightTrunkPlacer(p_195149_, p_195150_, p_195151_), BlockStateProvider.simple(p_195148_), new BlobFoliagePlacer(ConstantInt.of(p_195152_), ConstantInt.of(0), 3), new TwoLayersFeatureSize(1, 0, 1));
    }

    private static ModTreeFeatureConfig.Builder createMegaChorus() {
        return new ModTreeFeatureConfig.Builder(
                BlockStateProvider.simple(ModBlocks.CHORUS_WOOD.get()),
                BlockStateProvider.simple(ModBlocks.CHORUS_LEAVES.get()),
                BlockStateProvider.simple(ModBlocks.CHORUS_WOOD.get()),
                BlockStateProvider.simple(Blocks.AIR)
        );
    }

    private static ModTreeFeatureConfig.Builder createMegaChorusBlossom() {
        return new ModTreeFeatureConfig.Builder(
                BlockStateProvider.simple(ModBlocks.CHORUS_WOOD.get()),
                BlockStateProvider.simple(ModBlocks.CHORUS_BLOSSOM_LEAVES.get()),
                BlockStateProvider.simple(ModBlocks.CHORUS_WOOD.get()),
                BlockStateProvider.simple(Blocks.AIR)
        );
    }
}
