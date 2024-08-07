package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.Goety;
import net.minecraft.world.level.block.state.properties.WoodType;

public class ModWoodType {
    public static final WoodType HAUNTED =
            WoodType.create(Goety.location("haunted").toString());
    public static final WoodType ROTTEN =
            WoodType.create(Goety.location("rotten").toString());
    public static final WoodType WINDSWEPT =
            WoodType.create(Goety.location("windswept").toString());
    public static final WoodType PINE =
            WoodType.create(Goety.location("pine").toString());
}
