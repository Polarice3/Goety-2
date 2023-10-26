package com.Polarice3.Goety.common.world.structures;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public interface ModStructureTags {
    TagKey<Structure> CRYPT = create("explorer_maps/crypt");

    private static TagKey<Structure> create(String p_215896_) {
        return TagKey.create(Registries.STRUCTURE, Goety.location(p_215896_));
    }
}
