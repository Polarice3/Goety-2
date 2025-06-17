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
            addWaves("GOETY_WARLOCK", ModEntityType.WARLOCK.get(), MobsConfig.WarlockRaidCount.get());
        }
        if (MobsConfig.MaverickRaid.get()) {
            addWaves("GOETY_MAVERICK", ModEntityType.MAVERICK.get(), MobsConfig.MaverickRaidCount.get());
        }
        if (MobsConfig.HereticRaid.get()) {
            addWaves("GOETY_HERETIC", ModEntityType.HERETIC.get(), MobsConfig.HereticRaidCount.get());
        }
        if (MobsConfig.IllagerRaid.get()) {
            if (MobsConfig.PikerRaid.get()) {
                addWaves("GOETY_PIKER", ModEntityType.PIKER.get(), MobsConfig.PikerRaidCount.get());
            }
            if (MobsConfig.RipperRaid.get()) {
                addWaves("GOETY_RIPPER", ModEntityType.RIPPER.get(), MobsConfig.RipperRaidCount.get());
            }
            if (MobsConfig.CrusherRaid.get()) {
                addWaves("GOETY_CRUSHER", ModEntityType.CRUSHER.get(), MobsConfig.CrusherRaidCount.get());
            }
            if (MobsConfig.StormCasterRaid.get()) {
                addWaves("GOETY_STORM_CASTER", ModEntityType.STORM_CASTER.get(), MobsConfig.StormCasterRaidCount.get());
            }
            if (MobsConfig.CryologerRaid.get()) {
                addWaves("GOETY_CRYOLOGER", ModEntityType.CRYOLOGER.get(), MobsConfig.CryologerRaidCount.get());
            }
            if (MobsConfig.PreacherRaid.get()) {
                addWaves("GOETY_PREACHER", ModEntityType.PREACHER.get(), MobsConfig.PreacherRaidCount.get());
            }
            if (MobsConfig.ConquillagerRaid.get()) {
                addWaves("GOETY_CONQUILLAGER", ModEntityType.CONQUILLAGER.get(), MobsConfig.ConquillagerRaidCount.get());
            }
            if (MobsConfig.InquillagerRaid.get()) {
                addWaves("GOETY_INQUILLAGER", ModEntityType.INQUILLAGER.get(), MobsConfig.InquillagerRaidCount.get());
            }
            if (MobsConfig.EnviokerRaid.get()) {
                addWaves("GOETY_ENVIOKER", ModEntityType.ENVIOKER.get(), MobsConfig.EnviokerRaidCount.get());
            }
            if (MobsConfig.SorcererRaid.get()) {
                addWaves("GOETY_SORCERER", ModEntityType.SORCERER.get(), MobsConfig.SorcererRaidCount.get());
            }
            if (MobsConfig.HostileRedstoneGolemRaid.get()){
                addWaves("GOETY_HOSTILE_RED_GOLEM", ModEntityType.HOSTILE_REDSTONE_GOLEM.get(), MobsConfig.HostileRedstoneGolemRaidCount.get());
            }
            if (MobsConfig.HostileRedstoneMonstrosityRaid.get()){
                EntityType<? extends Raider> entityType = ModEntityType.HOSTILE_REDSTONE_MONSTROSITY.get();
                if (MobsConfig.HRMSpawnNoRaiders.get()) {
                    entityType = ModEntityType.RAID_BOSS_SUMMON.get();
                }
                addWaves("GOETY_HOSTILE_RED_MONSTER", entityType, MobsConfig.HostileRedstoneMonstrosityRaidCount.get());
            }
            if (MobsConfig.MinisterRaid.get()) {
                addWaves("GOETY_MINISTER", ModEntityType.MINISTER.get(), MobsConfig.MinisterRaidCount.get());
            }
        }
    }

    private static Raid.RaiderType addWaves(String name, EntityType<? extends Raider> type, List<? extends Integer> list) {
        Raid.RaiderType member = Raid.RaiderType.create(name, type, new int[]{list.get(0), list.get(1), list.get(2), list.get(3), list.get(4), list.get(5), list.get(6), list.get(7)});
        NEW_RAID_MEMBERS.add(member);
        return member;
    }
}
