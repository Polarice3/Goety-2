package com.Polarice3.Goety.common.world.features.trees.trunkplacers;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTrunkPlacerTypes {
    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACER_TYPES = DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, Goety.MOD_ID);

    public static final RegistryObject<TrunkPlacerType<ChorusTrunkPlacer>> CHORUS_TRUNK_PLACER = TRUNK_PLACER_TYPES.register("chorus_trunk_placer", () -> new TrunkPlacerType<>(ChorusTrunkPlacer.CODEC));

}
