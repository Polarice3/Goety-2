package com.Polarice3.Goety.common.world.processors;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModProcessors {
    public static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSOR = DeferredRegister.create(Registry.STRUCTURE_PROCESSOR_REGISTRY, Goety.MOD_ID);

    public static final RegistryObject<StructureProcessorType<WaterloggingStopProcessor>> WATERLOGGING_STOP_PROCESSOR = STRUCTURE_PROCESSOR.register("waterlogging_stop_processor", () -> () -> WaterloggingStopProcessor.CODEC);
}
