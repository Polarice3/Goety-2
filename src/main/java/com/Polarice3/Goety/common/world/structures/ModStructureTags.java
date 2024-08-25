package com.Polarice3.Goety.common.world.structures;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public interface ModStructureTags {
    TagKey<Structure> CRYPT = create("explorer_maps/crypt");
    TagKey<Structure> BLIGHTED_SHACK = create("explorer_maps/blighted_shack");
    TagKey<Structure> WIND_SHRINE = create("explorer_maps/wind_shrine");
    TagKey<Structure> NECRO_HOSTILE = create("necro_hostile");

    private static TagKey<Structure> create(String p_215896_) {
        return TagKey.create(Registries.STRUCTURE, Goety.location(p_215896_));
    }
}
