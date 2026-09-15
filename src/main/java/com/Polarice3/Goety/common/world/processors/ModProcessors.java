package com.Polarice3.Goety.common.world.processors;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModProcessors {
    public static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSOR = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, Goety.MOD_ID);

    public static final RegistryObject<StructureProcessorType<WaterloggingStopProcessor>> WATERLOGGING_STOP_PROCESSOR = STRUCTURE_PROCESSOR.register("waterlogging_stop_processor", () -> () -> WaterloggingStopProcessor.CODEC);
    public static final RegistryObject<StructureProcessorType<CobbleAgeProcessor>> COBBLE_AGE_PROCESSOR = STRUCTURE_PROCESSOR.register("cobble_age_processor", () -> () -> CobbleAgeProcessor.CODEC);
    public static final RegistryObject<StructureProcessorType<ReplaceProcessor>> REPLACE_PROCESSOR = STRUCTURE_PROCESSOR.register("replace_processor", () -> () -> ReplaceProcessor.CODEC);
    public static final RegistryObject<StructureProcessorType<HauntProcessor>> HAUNT_PROCESSOR = STRUCTURE_PROCESSOR.register("haunt_processor", () -> () -> HauntProcessor.CODEC);
    public static final RegistryObject<StructureProcessorType<WreckProcessor>> WRECK_PROCESSOR = STRUCTURE_PROCESSOR.register("wreck_processor", () -> () -> WreckProcessor.CODEC);
    public static final RegistryObject<StructureProcessorType<RotPotProcessor>> ROT_POT_PROCESSOR = STRUCTURE_PROCESSOR.register("rot_pot_processor", () -> () -> RotPotProcessor.CODEC);
    public static final RegistryObject<StructureProcessorType<RotFarmProcessor>> ROT_FARM_PROCESSOR = STRUCTURE_PROCESSOR.register("rot_farm_processor", () -> () -> RotFarmProcessor.CODEC);
}
