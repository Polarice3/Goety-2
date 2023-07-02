package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.Goety;
import net.minecraft.world.level.block.state.properties.WoodType;

public class ModWoodType {
    public static final WoodType HAUNTED =
            WoodType.create(Goety.location("haunted").toString());
    public static final WoodType ROTTEN =
            WoodType.create(Goety.location("rotten").toString());
}
