package com.Polarice3.Goety.common.world.features;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.world.features.configs.ModTreeFeatureConfig;
import com.Polarice3.Goety.common.world.features.trees.features.ChorusTreeFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NetherForestVegetationConfig;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, Goety.MOD_ID);

    public static final RegistryObject<Feature<ModTreeFeatureConfig>> CHORUS_TREE = FEATURES.register("chorus_tree", () -> new ChorusTreeFeature(ModTreeFeatureConfig.CODEC, false));
    public static final RegistryObject<Feature<ModTreeFeatureConfig>> CHORUS_VOID_TREE = FEATURES.register("chorus_void_tree", () -> new ChorusTreeFeature(ModTreeFeatureConfig.CODEC, true));
    public static final RegistryObject<Feature<NetherForestVegetationConfig>> END_VEGETATION = FEATURES.register("end_vegetation", () -> new EndVegetationFeature(NetherForestVegetationConfig.CODEC));
    public static final RegistryObject<Feature<NetherForestVegetationConfig>> END_GROWTH = FEATURES.register("end_growth", () -> new EndGrowthFeature(NetherForestVegetationConfig.CODEC));
    public static final RegistryObject<Feature<NetherForestVegetationConfig>> END_CHORUS = FEATURES.register("end_chorus", () -> new EndChorusFeature(NetherForestVegetationConfig.CODEC));
    public static final RegistryObject<Feature<RandomPatchConfiguration>> SINGLE_PATCH = FEATURES.register("single_patch", () -> new SinglePatchFeature(RandomPatchConfiguration.CODEC));
}
