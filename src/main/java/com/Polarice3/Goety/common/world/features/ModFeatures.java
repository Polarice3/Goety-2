package com.Polarice3.Goety.common.world.features;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.world.features.configs.ModTreeFeatureConfig;
import com.Polarice3.Goety.common.world.features.trees.features.ChorusTreeFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, Goety.MOD_ID);

    public static final RegistryObject<Feature<ModTreeFeatureConfig>> CHORUS_TREE = FEATURES.register("chorus_tree", () -> new ChorusTreeFeature(ModTreeFeatureConfig.CODEC, false));
    public static final RegistryObject<Feature<ModTreeFeatureConfig>> CHORUS_VOID_TREE = FEATURES.register("chorus_void_tree", () -> new ChorusTreeFeature(ModTreeFeatureConfig.CODEC, true));
}
