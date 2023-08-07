package com.Polarice3.Goety.init;

import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.common.entities.ModEntityType;
import net.minecraft.world.entity.raid.Raid;

public class RaidAdditions {

    public static void addRaiders(){
        if (MainConfig.IllagerRaid.get()){
            Raid.RaiderType.create("minister", ModEntityType.MINISTER.get(), new int[]{0, 0, 0, 0, 0, 0, 0, 1});
        }
    }
}
