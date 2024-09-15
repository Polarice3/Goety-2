package com.Polarice3.Goety.init;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.MobsConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;

import java.util.ArrayList;
import java.util.List;

public class RaidAdditions {

    public static final List<Raid.RaiderType> NEW_RAID_MEMBERS = new ArrayList<>();

    public static void addRaiders(){
        if (MobsConfig.WarlockRaid.get()) {
            addWaves("WARLOCK", ModEntityType.WARLOCK.get(), MobsConfig.WarlockRaidCount.get());
        }
        if (MobsConfig.IllagerRaid.get()) {
            if (MobsConfig.PikerRaid.get()) {
                addWaves("PIKER", ModEntityType.PIKER.get(), MobsConfig.PikerRaidCount.get());
            }
            if (MobsConfig.RipperRaid.get()) {
                addWaves("RIPPER", ModEntityType.RIPPER.get(), MobsConfig.RipperRaidCount.get());
            }
            if (MobsConfig.CrusherRaid.get()) {
                addWaves("CRUSHER", ModEntityType.CRUSHER.get(), MobsConfig.CrusherRaidCount.get());
            }
            if (MobsConfig.StormCasterRaid.get()) {
                addWaves("STORM_CASTER", ModEntityType.STORM_CASTER.get(), MobsConfig.StormCasterRaidCount.get());
            }
            if (MobsConfig.CryologerRaid.get()) {
                addWaves("CRYOLOGER", ModEntityType.CRYOLOGER.get(), MobsConfig.CryologerRaidCount.get());
            }
            if (MobsConfig.ConquillagerRaid.get()) {
                addWaves("CONQUILLAGER", ModEntityType.CONQUILLAGER.get(), MobsConfig.ConquillagerRaidCount.get());
            }
            if (MobsConfig.InquillagerRaid.get()) {
                addWaves("INQUILLAGER", ModEntityType.INQUILLAGER.get(), MobsConfig.InquillagerRaidCount.get());
            }
            if (MobsConfig.EnviokerRaid.get()) {
                addWaves("ENVIOKER", ModEntityType.ENVIOKER.get(), MobsConfig.EnviokerRaidCount.get());
            }
            if (MobsConfig.HostileRedstoneGolemRaid.get()){
                addWaves("HOSTILE_RED_GOLEM", ModEntityType.HOSTILE_REDSTONE_GOLEM.get(), MobsConfig.HostileRedstoneGolemRaidCount.get());
            }
            if (MobsConfig.HostileRedstoneMonstrosityRaid.get()){
                addWaves("HOSTILE_RED_MONSTER", ModEntityType.HOSTILE_REDSTONE_MONSTROSITY.get(), MobsConfig.HostileRedstoneMonstrosityRaidCount.get());
            }
            if (MobsConfig.MinisterRaid.get()) {
                addWaves("MINISTER", ModEntityType.MINISTER.get(), MobsConfig.MinisterRaidCount.get());
            }
        }
    }

    private static Raid.RaiderType addWaves(String name, EntityType<? extends Raider> type, List<? extends Integer> list) {
        Raid.RaiderType member = Raid.RaiderType.create(name, type, new int[]{list.get(0), list.get(1), list.get(2), list.get(3), list.get(4), list.get(5), list.get(6), list.get(7)});
        NEW_RAID_MEMBERS.add(member);
        return member;
    }
}
