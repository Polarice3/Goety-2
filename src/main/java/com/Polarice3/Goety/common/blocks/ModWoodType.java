package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.Goety;
import net.minecraft.world.level.block.state.properties.WoodType;

public class ModWoodType {
    public static final WoodType HAUNTED =
            WoodType.register(new WoodType(Goety.location("haunted").toString(), ModBlockSetType.HAUNTED));
    public static final WoodType ROTTEN =
            WoodType.register(new WoodType(Goety.location("rotten").toString(), ModBlockSetType.ROTTEN));
    public static final WoodType WINDSWEPT =
            WoodType.register(new WoodType(Goety.location("windswept").toString(), ModBlockSetType.WINDSWEPT));
    public static final WoodType PINE =
            WoodType.register(new WoodType(Goety.location("pine").toString(), ModBlockSetType.PINE));
}
