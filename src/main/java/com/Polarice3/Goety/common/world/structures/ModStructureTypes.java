package com.Polarice3.Goety.common.world.structures;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModStructureTypes {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPE = DeferredRegister.create(Registries.STRUCTURE_TYPE, Goety.MOD_ID);

    public static RegistryObject<StructureType<BiggerJigsawStructure>> BIGGER_JIGSAW_STRUCTURE = STRUCTURE_TYPE.register("bigger_jigsaw", () -> () -> BiggerJigsawStructure.CODEC);
}
