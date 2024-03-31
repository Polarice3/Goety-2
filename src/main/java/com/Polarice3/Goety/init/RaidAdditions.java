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
                Raid.RaiderType.create("PIKER", ModEntityType.PIKER.get(), new int[]{0, 0, 0, 2, 0, 3, 3, 5});
            }
            if (MobsConfig.RipperRaid.get()) {
                Raid.RaiderType.create("RIPPER", ModEntityType.RIPPER.get(), new int[]{0, 0, 0, 4, 0, 6, 6, 10});
            }
            if (MobsConfig.CrusherRaid.get()) {
                Raid.RaiderType.create("CRUSHER", ModEntityType.CRUSHER.get(), new int[]{0, 0, 0, 0, 2, 2, 0, 2});
            }
            if (MobsConfig.StormCasterRaid.get()) {
                Raid.RaiderType.create("STORM_CASTER", ModEntityType.STORM_CASTER.get(), new int[]{0, 0, 0, 0, 1, 1, 0, 2});
            }
            if (MobsConfig.CryologerRaid.get()) {
                Raid.RaiderType.create("CRYOLOGER", ModEntityType.CRYOLOGER.get(), new int[]{0, 0, 1, 1, 0, 0, 0, 2});
            }
            if (MobsConfig.HostileRedstoneGolemRaid.get()){
                Raid.RaiderType.create("HOSTILE_RED_GOLEM", ModEntityType.HOSTILE_REDSTONE_GOLEM.get(), new int[]{0, 0, 0, 0, 0, 1, 0, 0});
            }
            if (MobsConfig.MinisterRaid.get()) {
                Raid.RaiderType.create("MINISTER", ModEntityType.MINISTER.get(), new int[]{0, 0, 0, 0, 0, 0, 0, 1});
            }
        }
    }
}
