package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.init.ModSoundTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class ModBlockSetType {
    public static final BlockSetType HAUNTED =
            BlockSetType.register(new BlockSetType(Goety.location("haunted").toString()));
    public static final BlockSetType ROTTEN =
            BlockSetType.register(new BlockSetType(Goety.location("rotten").toString()));
    public static final BlockSetType WINDSWEPT =
            BlockSetType.register(new BlockSetType(Goety.location("windswept").toString()));
    public static final BlockSetType PINE =
            BlockSetType.register(new BlockSetType(Goety.location("pine").toString()));
    public static final BlockSetType MOD_METAL =
            BlockSetType.register(new BlockSetType(Goety.location("metal").toString(), false, ModSoundTypes.MOD_METAL, SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN, SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON, SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON));
}
