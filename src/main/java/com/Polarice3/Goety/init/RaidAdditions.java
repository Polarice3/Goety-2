package com.Polarice3.Goety.init;

import com.Polarice3.Goety.MobsConfig;
import com.Polarice3.Goety.common.entities.ModEntityType;
import net.minecraft.world.entity.raid.Raid;

public class RaidAdditions {

    public static void addRaiders(){
        if (MobsConfig.WarlockRaid.get()) {
            Raid.RaiderType.create("WARLOCK", ModEntityType.WARLOCK.get(), new int[]{0, 0, 0, 0, 1, 2, 0, 1});
        }
        if (MobsConfig.IllagerRaid.get()) {
            if (MobsConfig.PikerRaid.get()) {
                Raid.RaiderType.create("PIKER", ModEntityType.PIKER.get(), new int[]{0, 4, 3, 3, 4, 4, 4, 2});
            }
            if (MobsConfig.MinisterRaid.get()) {
                Raid.RaiderType.create("MINISTER", ModEntityType.MINISTER.get(), new int[]{0, 0, 0, 0, 0, 0, 0, 1});
            }
        }
    }
}
