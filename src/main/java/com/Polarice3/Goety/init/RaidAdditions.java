package com.Polarice3.Goety.init;

import com.Polarice3.Goety.common.entities.ModEntityType;
import net.minecraft.world.entity.raid.Raid;

public class RaidAdditions {

    public static void addRaiders(){
        Raid.RaiderType.create("WARLOCK", ModEntityType.WARLOCK.get(), new int[]{0, 0, 0, 0, 1, 2, 0, 1});
    }
}
