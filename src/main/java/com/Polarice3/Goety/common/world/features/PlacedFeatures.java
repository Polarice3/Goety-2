package com.Polarice3.Goety.common.world.features;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.ArrayList;
import java.util.List;

public class PlacedFeatures {
    public static final ResourceKey<PlacedFeature> PINE_TREE = PlacementUtils.createKey("goety:pine_tree");

    public static void bootstrap(BootstapContext<PlacedFeature> p_255688_) {
        HolderGetter<ConfiguredFeature<?, ?>> holdergetter = p_255688_.lookup(Registries.CONFIGURED_FEATURE);
        Holder<ConfiguredFeature<?, ?>> holder = holdergetter.getOrThrow(ConfiguredFeatures.PINE_TREE);
        List<PlacementModifier> original = VegetationPlacements.treePlacement(CountPlacement.of(18), ModBlocks.PINE_SAPLING.get());
        List<PlacementModifier> list = new ArrayList<>(original);
        list.add(HeightRangePlacement.uniform(VerticalAnchor.absolute(64), VerticalAnchor.absolute(256)));
        PlacementUtils.register(p_255688_, PINE_TREE, holder, list);
    }

}
