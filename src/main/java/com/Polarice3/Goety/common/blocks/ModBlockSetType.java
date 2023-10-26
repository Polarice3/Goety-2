package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.Goety;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class ModBlockSetType {
    public static final BlockSetType HAUNTED =
            BlockSetType.register(new BlockSetType(Goety.location("haunted").toString()));
    public static final BlockSetType ROTTEN =
            BlockSetType.register(new BlockSetType(Goety.location("rotten").toString()));
}
