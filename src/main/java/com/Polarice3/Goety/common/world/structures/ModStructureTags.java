package com.Polarice3.Goety.common.world.structures;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

@Deprecated
public interface ModStructureTags {
    TagKey<Structure> CRYPT = ModTags.Structures.CRYPT_EXPLORER;
    TagKey<Structure> BLIGHTED_SHACK = ModTags.Structures.BLIGHTED_SHACK;
    TagKey<Structure> RUINED_MONASTERY = ModTags.Structures.RUINED_MONASTERY;
    TagKey<Structure> WIND_SHRINE = ModTags.Structures.WIND_SHRINE;
    TagKey<Structure> OMINOUS_BLACKSMITH = ModTags.Structures.OMINOUS_BLACKSMITH;
    TagKey<Structure> FINAL_TERMINAL = ModTags.Structures.FINAL_TERMINAL;
    TagKey<Structure> NECRO_HOSTILE = ModTags.Structures.NECRO_HOSTILE;
    TagKey<Structure> VOID_HOSTILE = ModTags.Structures.VOID_HOSTILE;

    private static TagKey<Structure> create(String p_215896_) {
        return TagKey.create(Registries.STRUCTURE, Goety.location(p_215896_));
    }
}
