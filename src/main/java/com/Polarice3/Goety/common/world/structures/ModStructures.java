package com.Polarice3.Goety.common.world.structures;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public class ModStructures {
    public static final ResourceKey<Structure> GRAVEYARD_KEY = ResourceKey.create(Registry.STRUCTURE_REGISTRY, Goety.location("graveyard"));
    public static final ResourceKey<Structure> CRYPT_KEY = ResourceKey.create(Registry.STRUCTURE_REGISTRY, Goety.location("crypt"));
    public static final ResourceKey<Structure> BLIGHTED_SHACK_KEY = ResourceKey.create(Registry.STRUCTURE_REGISTRY, Goety.location("blighted_shack"));
}
