package com.Polarice3.Goety.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MobsConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> RavagerRoarCooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> ServantGuardingRange;
    public static final ForgeConfigSpec.ConfigValue<Integer> ServantHealHalt;

    public static final ForgeConfigSpec.ConfigValue<Integer> UndeadMinionHealCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> UndeadMinionHealTime;
    public static final ForgeConfigSpec.ConfigValue<Double> UndeadMinionHealAmount;
    public static final ForgeConfigSpec.ConfigValue<Double> ZombieServantBabyChance;

    public static final ForgeConfigSpec.ConfigValue<Integer> WaterMinionHealCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> WaterMinionHealTime;
    public static final ForgeConfigSpec.ConfigValue<Double> WaterMinionHealAmount;

    public static final ForgeConfigSpec.ConfigValue<Integer> NaturalMinionHealCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> NaturalMinionHealTime;
    public static final ForgeConfigSpec.ConfigValue<Double> NaturalMinionHealAmount;

    public static final ForgeConfigSpec.ConfigValue<Integer> GeoMinionHealCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> GeoMinionHealTime;
    public static final ForgeConfigSpec.ConfigValue<Double> GeoMinionHealAmount;

    public static final ForgeConfigSpec.ConfigValue<Integer> FrostMinionHealCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FrostMinionHealTime;
    public static final ForgeConfigSpec.ConfigValue<Double> FrostMinionHealAmount;

    public static final ForgeConfigSpec.ConfigValue<Integer> WindMinionHealCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> WindMinionHealTime;
    public static final ForgeConfigSpec.ConfigValue<Double> WindMinionHealAmount;

    public static final ForgeConfigSpec.ConfigValue<Integer> StormMinionHealCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> StormMinionHealTime;
    public static final ForgeConfigSpec.ConfigValue<Double> StormMinionHealAmount;

    public static final ForgeConfigSpec.ConfigValue<Integer> NetherMinionHealCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> NetherMinionHealTime;
    public static final ForgeConfigSpec.ConfigValue<Double> NetherMinionHealAmount;

    public static final ForgeConfigSpec.ConfigValue<Integer> VoidMinionHealCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> VoidMinionHealTime;
    public static final ForgeConfigSpec.ConfigValue<Double> VoidMinionHealAmount;

    public static final ForgeConfigSpec.ConfigValue<Integer> IllagerServantTrainTime;
    public static final ForgeConfigSpec.ConfigValue<Integer> IllagerServantMaxMentors;
    public static final ForgeConfigSpec.ConfigValue<Integer> IllagerServantChestRange;
    public static final ForgeConfigSpec.ConfigValue<Integer> EvokerServantRavagedCooldown;

    public static final ForgeConfigSpec.ConfigValue<Integer> PrisonerMiningSwings;
    public static final ForgeConfigSpec.ConfigValue<Integer> PrisonerMiningRange;
    public static final ForgeConfigSpec.ConfigValue<Integer> PrisonerMiningDurability;
    public static final ForgeConfigSpec.ConfigValue<Integer> PrisonerMiningChance;
    public static final ForgeConfigSpec.ConfigValue<Integer> PrisonerMiningRareChance;

    public static final ForgeConfigSpec.ConfigValue<Integer> IllagerAssaultSpawnFreq;
    public static final ForgeConfigSpec.ConfigValue<Integer> IllagerAssaultSpawnChance;
    public static final ForgeConfigSpec.ConfigValue<Integer> IllagerAssaultSEThreshold;
    public static final ForgeConfigSpec.ConfigValue<Integer> IllagerAssaultSELimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> IllagerAssaultRestDeath;
    public static final ForgeConfigSpec.ConfigValue<Integer> IllagerAssaultRestMinister;

    public static final ForgeConfigSpec.ConfigValue<Integer> WightSpawnFreq;
    public static final ForgeConfigSpec.ConfigValue<Integer> WightSpawnChance;

    public static final ForgeConfigSpec.ConfigValue<Integer> ObsidianMonolithStartUpTime;

    public static final ForgeConfigSpec.ConfigValue<Integer> VillagerHateSpells;

    public static final ForgeConfigSpec.ConfigValue<Integer> MaxSlimeSize;

    public static final ForgeConfigSpec.ConfigValue<Integer> WarlockSpawnWeight;
    public static final ForgeConfigSpec.ConfigValue<Integer> WarlockSpawnMinCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> WarlockSpawnMaxCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> ReaperSpawnWeight;
    public static final ForgeConfigSpec.ConfigValue<Integer> ReaperSpawnMinCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> ReaperSpawnMaxCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> WraithSpawnWeight;
    public static final ForgeConfigSpec.ConfigValue<Integer> WraithSpawnMinCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> WraithSpawnMaxCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> MuckWraithSpawnWeight;
    public static final ForgeConfigSpec.ConfigValue<Integer> MuckWraithSpawnMinCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> MuckWraithSpawnMaxCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> WebSpiderSpawnWeight;
    public static final ForgeConfigSpec.ConfigValue<Integer> WebSpiderSpawnMinCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> WebSpiderSpawnMaxCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> IcySpiderSpawnWeight;
    public static final ForgeConfigSpec.ConfigValue<Integer> IcySpiderSpawnMinCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> IcySpiderSpawnMaxCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> FrayedSpawnWeight;
    public static final ForgeConfigSpec.ConfigValue<Integer> FrayedSpawnMinCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> FrayedSpawnMaxCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> RattledSpawnWeight;
    public static final ForgeConfigSpec.ConfigValue<Integer> RattledSpawnMinCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> RattledSpawnMaxCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> NecromancerSpawnWeight;
    public static final ForgeConfigSpec.ConfigValue<Integer> NecromancerSpawnMinCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> NecromancerSpawnMaxCount;
    public static final ForgeConfigSpec.ConfigValue<Boolean> NecromancerSpawnStructure;
    public static final ForgeConfigSpec.ConfigValue<Integer> HereticSpawnWeight;
    public static final ForgeConfigSpec.ConfigValue<Integer> HereticSpawnMinCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> HereticSpawnMaxCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> MaverickSpawnWeight;
    public static final ForgeConfigSpec.ConfigValue<Integer> MaverickSpawnMinCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> MaverickSpawnMaxCount;

    public static final ForgeConfigSpec.ConfigValue<Integer> BossInvulnerabilityTime;

    public static final ForgeConfigSpec.ConfigValue<Integer> ApostleNetherDamageReduction;

    public static final ForgeConfigSpec.ConfigValue<Boolean> ZombieServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> DrownedServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HuskServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> FrozenZombieServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> JungleZombieServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> FrayedServantTexture;

    public static final ForgeConfigSpec.ConfigValue<Boolean> SkeletonServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> StrayServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> WitherSkeletonServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> MossySkeletonServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SunkenSkeletonServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> RattledServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> NecromancerServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> VanguardServantTexture;

    public static final ForgeConfigSpec.ConfigValue<Boolean> WraithServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ReaperServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> PhantomServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> PhantomServantTranslucent;

    public static final ForgeConfigSpec.ConfigValue<Boolean> ZPiglinServantTexture;

    public static final ForgeConfigSpec.ConfigValue<Boolean> GhastServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> BlazeServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> WildfireTexture;

    public static final ForgeConfigSpec.ConfigValue<Boolean> VexTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> PillagerServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> PikerServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> VindicatorServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> MountaineerServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CrusherServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> EvokerServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> GeomancerServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> IceologerServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CryologerServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> WindCallerServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> StormCasterServantTexture;

    public static final ForgeConfigSpec.ConfigValue<Boolean> RipperServantTexture;

    public static final ForgeConfigSpec.ConfigValue<Boolean> WitchServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> WarlockServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> MaverickServantTexture;

    public static final ForgeConfigSpec.ConfigValue<Boolean> SpiderServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CaveSpiderServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> WebSpiderServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> IcySpiderServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> BoneSpiderServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> BroodMotherOldTexture;

    public static final ForgeConfigSpec.ConfigValue<Boolean> SlimeServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> MagmaCubeServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CryptSlimeServantTexture;

    public static final ForgeConfigSpec.ConfigValue<Boolean> GuardianServantTexture;

    public static final ForgeConfigSpec.ConfigValue<Boolean> QuickGrowingVineTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> PoisonQuillVineTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> WhispererTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LeapleafTexture;

    public static final ForgeConfigSpec.ConfigValue<Boolean> WatchlingServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> BlastlingServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SnarelingServantTexture;

    public static final ForgeConfigSpec.ConfigValue<Boolean> SquallGolemTexture;

    public static final ForgeConfigSpec.ConfigValue<Boolean> RedstoneGolemCrack;
    public static final ForgeConfigSpec.ConfigValue<Boolean> RedstoneGolemTexture;

    public static final ForgeConfigSpec.ConfigValue<Boolean> RedstoneMonstrosityTexture;

    public static final ForgeConfigSpec.ConfigValue<Boolean> HolidaySkins;

    public static final ForgeConfigSpec.ConfigValue<Boolean> HierarchicalArmorTrim;

    public static final ForgeConfigSpec.ConfigValue<Boolean> ServantTeleport;
    public static final ForgeConfigSpec.ConfigValue<Boolean> VexTeleport;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ServantRideAutonomous;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ServantsAttackCreepers;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ServantsCanWearPumpkin;
    public static final ForgeConfigSpec.ConfigValue<Boolean> NecroRobeUndead;
    public static final ForgeConfigSpec.ConfigValue<Boolean> NecroSetDebuff;
    public static final ForgeConfigSpec.ConfigValue<Boolean> NamelessSetDebuff;
    public static final ForgeConfigSpec.ConfigValue<Boolean> VariousRobeWitch;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ServantsMasterImmune;
    public static final ForgeConfigSpec.ConfigValue<Boolean> OwnerAttackCancel;
    public static final ForgeConfigSpec.ConfigValue<Boolean> MobSense;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ServantsHarmEffectApply;
    public static final ForgeConfigSpec.ConfigValue<Boolean> UndeadMinionHeal;
    public static final ForgeConfigSpec.ConfigValue<Boolean> WaterMinionHeal;
    public static final ForgeConfigSpec.ConfigValue<Boolean> NaturalMinionHeal;
    public static final ForgeConfigSpec.ConfigValue<Boolean> GeoMinionHeal;
    public static final ForgeConfigSpec.ConfigValue<Boolean> FrostMinionHeal;
    public static final ForgeConfigSpec.ConfigValue<Boolean> WindMinionHeal;
    public static final ForgeConfigSpec.ConfigValue<Boolean> StormMinionHeal;
    public static final ForgeConfigSpec.ConfigValue<Boolean> NetherMinionHeal;
    public static final ForgeConfigSpec.ConfigValue<Boolean> VoidMinionHeal;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CompatMinionHeal;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CompatNightmareStalker;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ServantOwnedServantPlayerBenefit;
    public static final ForgeConfigSpec.ConfigValue<Boolean> NecromancerSoulJar;
    public static final ForgeConfigSpec.ConfigValue<Boolean> NecromancerSummonsLife;
    public static final ForgeConfigSpec.ConfigValue<Boolean> WildfireBlazingHelm;
    public static final ForgeConfigSpec.ConfigValue<Boolean> WildfireSummonsLife;
    public static final ForgeConfigSpec.ConfigValue<Boolean> BlackBeastDayStrength;
    public static final ForgeConfigSpec.ConfigValue<Boolean> BlackBeastChunkLoad;
    public static final ForgeConfigSpec.ConfigValue<Boolean> BlackBeastHowlingSoul;
    public static final ForgeConfigSpec.ConfigValue<Boolean> TramplerHostileConvert;
    public static final ForgeConfigSpec.ConfigValue<Boolean> RavagerHostileConvert;
    public static final ForgeConfigSpec.ConfigValue<Boolean> RedstoneGolemHostileConvert;
    public static final ForgeConfigSpec.ConfigValue<Boolean> RedstoneMonstrosityHostileConvert;

    public static final ForgeConfigSpec.ConfigValue<Boolean> VillagerHate;
    public static final ForgeConfigSpec.ConfigValue<Boolean> VillagerHateRavager;
    public static final ForgeConfigSpec.ConfigValue<Boolean> VillagerConvertWarlock;
    public static final ForgeConfigSpec.ConfigValue<Boolean> VillagerConvertHeretic;
    public static final ForgeConfigSpec.ConfigValue<Boolean> TraderConvertMaverick;

    public static final ForgeConfigSpec.ConfigValue<Boolean> IllagerAssault;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SoulEnergyBadOmen;
    public static final ForgeConfigSpec.ConfigValue<Boolean> IllagueSpread;
    public static final ForgeConfigSpec.ConfigValue<Boolean> IllagerSteal;
    public static final ForgeConfigSpec.ConfigValue<Boolean> IllagerRaid;
    public static final ForgeConfigSpec.ConfigValue<Boolean> PikerRaid;
    public static final ForgeConfigSpec.ConfigValue<Boolean> RipperRaid;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CrusherRaid;
    public static final ForgeConfigSpec.ConfigValue<Boolean> StormCasterRaid;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CryologerRaid;
    public static final ForgeConfigSpec.ConfigValue<Boolean> PreacherRaid;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ConquillagerRaid;
    public static final ForgeConfigSpec.ConfigValue<Boolean> InquillagerRaid;
    public static final ForgeConfigSpec.ConfigValue<Boolean> EnviokerRaid;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SorcererRaid;
    public static final ForgeConfigSpec.ConfigValue<Boolean> MinisterRaid;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HostileRedstoneGolemRaid;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HostileRedstoneMonstrosityRaid;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HRMSpawnNoRaiders;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ArmoredRavagerRaid;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ArmoredTramplerRaid;
    public static final ForgeConfigSpec.ConfigValue<Boolean> WarlockRaid;
    public static final ForgeConfigSpec.ConfigValue<Boolean> MaverickRaid;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HereticRaid;

    public static ForgeConfigSpec.ConfigValue<List<? extends Integer>> PikerRaidCount;
    public static ForgeConfigSpec.ConfigValue<List<? extends Integer>> RipperRaidCount;
    public static ForgeConfigSpec.ConfigValue<List<? extends Integer>> CrusherRaidCount;
    public static ForgeConfigSpec.ConfigValue<List<? extends Integer>> StormCasterRaidCount;
    public static ForgeConfigSpec.ConfigValue<List<? extends Integer>> CryologerRaidCount;
    public static ForgeConfigSpec.ConfigValue<List<? extends Integer>> PreacherRaidCount;
    public static ForgeConfigSpec.ConfigValue<List<? extends Integer>> ConquillagerRaidCount;
    public static ForgeConfigSpec.ConfigValue<List<? extends Integer>> InquillagerRaidCount;
    public static ForgeConfigSpec.ConfigValue<List<? extends Integer>> EnviokerRaidCount;
    public static ForgeConfigSpec.ConfigValue<List<? extends Integer>> SorcererRaidCount;
    public static ForgeConfigSpec.ConfigValue<List<? extends Integer>> MinisterRaidCount;
    public static ForgeConfigSpec.ConfigValue<List<? extends Integer>> HostileRedstoneGolemRaidCount;
    public static ForgeConfigSpec.ConfigValue<List<? extends Integer>> HostileRedstoneMonstrosityRaidCount;
    public static ForgeConfigSpec.ConfigValue<List<? extends Integer>> WarlockRaidCount;
    public static ForgeConfigSpec.ConfigValue<List<? extends Integer>> MaverickRaidCount;
    public static ForgeConfigSpec.ConfigValue<List<? extends Integer>> HereticRaidCount;

    public static final ForgeConfigSpec.ConfigValue<Boolean> CryologerIceChunk;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SorcererHPIncrease;

    public static final ForgeConfigSpec.ConfigValue<Boolean> IllagerServantAutoTrain;
    public static final ForgeConfigSpec.ConfigValue<Boolean> IllagerServantAllBreed;
    public static final ForgeConfigSpec.ConfigValue<Boolean> IllagerServantLootVillagers;
    public static final ForgeConfigSpec.ConfigValue<Boolean> IllagerServantLootTraders;
    public static final ForgeConfigSpec.ConfigValue<Boolean> IllagerServantPickUpDrops;
    public static final ForgeConfigSpec.ConfigValue<Boolean> IllagerServantCollectLoot;
    public static final ForgeConfigSpec.ConfigValue<Boolean> IllagerServantAllOpenDoors;
    public static final ForgeConfigSpec.ConfigValue<Boolean> IllagerServantGhostArrows;
    public static final ForgeConfigSpec.ConfigValue<Boolean> IllagerServantTrainArmor;
    public static final ForgeConfigSpec.ConfigValue<Boolean> IllagerServantChunkLoadMark;
    public static final ForgeConfigSpec.ConfigValue<Boolean> IllagerServantChunkLoadRaid;

    public static final ForgeConfigSpec.ConfigValue<Boolean> MountaineerClimb;

    public static final ForgeConfigSpec.ConfigValue<Boolean> PrisonerMining;
    public static final ForgeConfigSpec.ConfigValue<Boolean> PrisonerMiningSeeBlocks;
    public static final ForgeConfigSpec.ConfigValue<Boolean> PrisonerMiningBreakBlocks;
    public static final ForgeConfigSpec.ConfigValue<Boolean> PrisonerUnshackleDamage;
    public static final ForgeConfigSpec.ConfigValue<Boolean> PrisonerPickUpPickaxe;

    public static final ForgeConfigSpec.ConfigValue<Boolean> RaiderServantWearArmor;

    public static final ForgeConfigSpec.ConfigValue<Boolean> WightSpawn;

    public static final ForgeConfigSpec.ConfigValue<Boolean> TallSkullDrops;
    public static final ForgeConfigSpec.ConfigValue<Boolean> WraithAggressiveTeleport;

    public static final ForgeConfigSpec.ConfigValue<Boolean> ZombieConvertFrayed;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SkeletonConvertRattled;

    public static final ForgeConfigSpec.ConfigValue<Boolean> StayingServantChunkLoad;
    public static final ForgeConfigSpec.ConfigValue<Boolean> GuardingServantChunkLoad;
    public static final ForgeConfigSpec.ConfigValue<Boolean> FollowingServantChunkLoad;

    public static final ForgeConfigSpec.ConfigValue<Boolean> UndeadServantSunlightBurn;
    public static final ForgeConfigSpec.ConfigValue<Boolean> UndeadServantSunlightHelmet;

    public static final ForgeConfigSpec.ConfigValue<Boolean> VizierPersistent;
    public static final ForgeConfigSpec.ConfigValue<Boolean> VizierMinion;

    public static final ForgeConfigSpec.ConfigValue<Boolean> ApocalypseMode;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ApostlePersistent;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ApostleBoilsWater;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ApostleConvertsVillagers;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ApostleTornado;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ApostleHellCloud;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ApostleQuickerRegen;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ApostleResistance;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ApostleHardMagicResistance;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ApostleCritArrows;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ApostleShootIndicator;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ApostleHalvedArmor;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ApostleDelayedTeleport;
    public static final ForgeConfigSpec.ConfigValue<Boolean> FancierApostleDeath;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ObsidianMonolithSpread;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ObsidianMonolithBiome;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ObsidianMonolithSpawner;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HellfireFireImmune;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HellfireFireProtection;
    public static final ForgeConfigSpec.ConfigValue<Boolean> EnderKeeperAfterImage;
    public static final ForgeConfigSpec.ConfigValue<Boolean> RedstoneMonstrosityLeafBreak;
    public static final ForgeConfigSpec.ConfigValue<Boolean> RedstoneCubeBlockFind;
    public static final ForgeConfigSpec.ConfigValue<Boolean> PlayerRavagerArmorDrop;
    public static final ForgeConfigSpec.ConfigValue<Boolean> DamnedShootIndicator;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CroneThornDefense;

    public static final ForgeConfigSpec.ConfigValue<Boolean> HostileCryptUndead;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HostileTerminalEnder;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ObsidianMonolithBlackList;

    static {
        BUILDER.push("Textures");
        HolidaySkins = BUILDER.comment("If certain mobs have a different texture during some holiday months, Default: true")
                .define("holidaySkins", true);
        BroodMotherOldTexture = BUILDER.comment("If Brood Mothers uses their old textures, Default: false")
                .define("broodMotherOldTexture", false);
        HierarchicalArmorTrim = BUILDER.comment("If armor worn by mobs that uses HierarchicalArmorLayer(ie, Illager Servants) shows Armor Trim, Default: true")
                .define("hierarchicalArmorTrim", true);
            BUILDER.push("Summoned Mobs");
                BUILDER.push("Zombie Servants");
                ZombieServantTexture = BUILDER.comment("If Zombie Servants have custom textures, Default: true")
                        .define("zombieServantTexture", true);
                DrownedServantTexture = BUILDER.comment("If Drowned Servants have custom textures, Default: true")
                        .define("drownedServantTexture", true);
                HuskServantTexture = BUILDER.comment("If Husk Servants have custom textures, Default: true")
                        .define("huskServantTexture", true);
                FrozenZombieServantTexture = BUILDER.comment("If Frozen Zombie Servants have custom textures, Default: true")
                        .define("frozenZombieServantTexture", true);
                JungleZombieServantTexture = BUILDER.comment("If Jungle Zombie Servants have custom textures, Default: true")
                        .define("jungleZombieServantTexture", true);
                FrayedServantTexture = BUILDER.comment("If Frayed Servants have custom textures, Default: true")
                        .define("frayedServantTexture", true);
                BUILDER.pop();
                BUILDER.push("Skeleton Servants");
                SkeletonServantTexture = BUILDER.comment("If Skeleton Servants have custom textures, Default: true")
                        .define("skeletonServantTexture", true);
                StrayServantTexture = BUILDER.comment("If Stray Servants have custom textures, Default: true")
                        .define("strayServantTexture", true);
                WitherSkeletonServantTexture = BUILDER.comment("If Wither Skeleton Servants have custom textures, Default: true")
                        .define("witherSkeletonServantTexture", true);
                MossySkeletonServantTexture = BUILDER.comment("If Mossy Skeleton Servants have custom textures, Default: true")
                        .define("mossySkeletonServantTexture", true);
                SunkenSkeletonServantTexture = BUILDER.comment("If Sunken Skeleton Servants have custom textures, Default: true")
                        .define("sunkenSkeletonServantTexture", true);
                RattledServantTexture = BUILDER.comment("If Rattled Servants have custom textures, Default: true")
                        .define("rattledServantTexture", true);
                NecromancerServantTexture = BUILDER.comment("If Necromancer Servants have custom textures, Default: true")
                        .define("necromancerServantTexture", true);
                VanguardServantTexture = BUILDER.comment("If Vanguard Servants have custom textures, Default: true")
                        .define("vanguardServantTexture", true);
                BUILDER.pop();
                BUILDER.push("Wraith Servants");
                WraithServantTexture = BUILDER.comment("If Wraith Servants have custom textures, Default: true")
                        .define("wraithServantTexture", true);
                BUILDER.pop();
                BUILDER.push("Reaper Servants");
                ReaperServantTexture = BUILDER.comment("If Reaper Servants have custom textures, Default: true")
                        .define("reaperServantTexture", true);
                BUILDER.pop();
                BUILDER.push("Phantom Servants");
                PhantomServantTexture = BUILDER.comment("If Phantom Servants have custom textures, Default: true")
                        .define("phantomServantTexture", true);
                PhantomServantTranslucent = BUILDER.comment("If Phantom Servants are translucent when upgraded, Default: true")
                        .define("phantomServantTranslucent", true);
                BUILDER.pop();
                BUILDER.push("Zombified Piglin Servants");
                ZPiglinServantTexture = BUILDER.comment("If Zombified Piglin Servants have custom textures, Default: true")
                        .define("zPiglinServantTexture", true);
                BUILDER.pop();
                BUILDER.push("Ghast Servants");
                GhastServantTexture = BUILDER.comment("If Ghast Servants have custom textures, Default: true")
                        .define("ghastServantTexture", true);
                BUILDER.pop();
                BUILDER.push("Blaze Servants");
                BlazeServantTexture = BUILDER.comment("If Blaze Servants have custom textures, Default: true")
                        .define("blazeServantTexture", true);
                WildfireTexture = BUILDER.comment("If Wildfires have custom textures, Default: true")
                        .define("wildfireTexture", true);
                BUILDER.pop();
                BUILDER.push("Vexes");
                VexTexture = BUILDER.comment("If Vexes have custom textures, Default: true")
                        .define("vexTexture", true);
                BUILDER.pop();
                BUILDER.push("Illager Servants");
                PillagerServantTexture = BUILDER.comment("If Pillager Servants have custom textures, Default: true")
                        .define("pillagerServantTexture", true);
                PikerServantTexture = BUILDER.comment("If Piker Servants have custom textures, Default: true")
                        .define("pikerServantTexture", true);
                VindicatorServantTexture = BUILDER.comment("If Vindicator Servants have custom textures, Default: true")
                        .define("vindicatorServantTexture", true);
                MountaineerServantTexture = BUILDER.comment("If Mountaineer Servants have custom textures, Default: true")
                        .define("mountaineerServantTexture", true);
                CrusherServantTexture = BUILDER.comment("If Crusher Servants have custom textures, Default: true")
                        .define("crusherServantTexture", true);
                EvokerServantTexture = BUILDER.comment("If Evoker Servants have custom textures, Default: true")
                        .define("evokerServantTexture", true);
                GeomancerServantTexture = BUILDER.comment("If Geomancer Servants have custom textures, Default: true")
                        .define("geomancerServantTexture", true);
                IceologerServantTexture = BUILDER.comment("If Iceologer Servants have custom textures, Default: true")
                        .define("iceologerServantTexture", true);
                CryologerServantTexture = BUILDER.comment("If Cryologer Servants have custom textures, Default: true")
                        .define("cryologerServantTexture", true);
                WindCallerServantTexture = BUILDER.comment("If Wind Caller Servants have custom textures, Default: true")
                        .define("windCallerServantTexture", true);
                StormCasterServantTexture = BUILDER.comment("If Storm Caster Servants have custom textures, Default: true")
                        .define("stormCasterServantTexture", true);
                BUILDER.pop();
                BUILDER.push("Raider Servants");
                RipperServantTexture = BUILDER.comment("If Ripper Servants have custom textures, Default: true")
                        .define("ripperServantTexture", true);
                WitchServantTexture = BUILDER.comment("If Witch Servants have custom textures, Default: true")
                        .define("witchServantTexture", true);
                WarlockServantTexture = BUILDER.comment("If Warlock Servants have custom textures, Default: true")
                        .define("warlockServantTexture", true);
                MaverickServantTexture = BUILDER.comment("If Maverick Servants have custom textures, Default: true")
                        .define("maverickServantTexture", true);
                BUILDER.pop();
                BUILDER.push("Spider Servants");
                SpiderServantTexture = BUILDER.comment("If Spiders Servants have custom textures, Default: true")
                        .define("spiderServantTexture", true);
                CaveSpiderServantTexture = BUILDER.comment("If Cave Spiders Servants have custom textures, Default: true")
                        .define("caveSpiderServantTexture", true);
                WebSpiderServantTexture = BUILDER.comment("If Web Spiders Servants have different textures from hostile versions, Default: true")
                        .define("webSpiderServantTexture", true);
                IcySpiderServantTexture = BUILDER.comment("If Icy Spiders Servants have different textures from hostile versions, Default: true")
                        .define("icySpiderServantTexture", true);
                BoneSpiderServantTexture = BUILDER.comment("If Bone Spiders Servants have custom textures, Default: true")
                        .define("boneSpiderServantTexture", true);
                BUILDER.pop();
                BUILDER.push("Slime Servants");
                SlimeServantTexture = BUILDER.comment("If Slime Servants have custom textures, Default: true")
                        .define("slimeServantTexture", true);
                MagmaCubeServantTexture = BUILDER.comment("If Magma Cube Servants have custom textures, Default: true")
                        .define("magmaCubeServantTexture", true);
                CryptSlimeServantTexture = BUILDER.comment("If Crypt Slime Servants have custom textures, Default: true")
                        .define("cryptSlimeServantTexture", true);
                BUILDER.pop();
                BUILDER.push("Guardian Servants");
                GuardianServantTexture = BUILDER.comment("If Guardian Servants have custom textures, Default: true")
                        .define("guardianServantTexture", true);
                BUILDER.pop();
                BUILDER.push("Wild Servants");
                QuickGrowingVineTexture = BUILDER.comment("If Quick Growing Vine servants have custom textures, Default: true")
                        .define("quickGrowingVineTexture", true);
                PoisonQuillVineTexture = BUILDER.comment("If Poison-Quill Vine servants have custom textures, Default: true")
                        .define("poisonQuillVineTexture", true);
                WhispererTexture = BUILDER.comment("If Whisperer Servants have custom textures, Default: true")
                        .define("whispererTexture", true);
                LeapleafTexture = BUILDER.comment("If Leapleaf Servants have custom textures, Default: true")
                        .define("leapleafTexture", true);
                BUILDER.pop();
                BUILDER.push("Void Servants");
                WatchlingServantTexture = BUILDER.comment("If Watchling servants have custom textures, Default: true")
                        .define("watchlingServantTexture", true);
                BlastlingServantTexture = BUILDER.comment("If Blastling servants have custom textures, Default: true")
                        .define("blastlingServantTexture", true);
                SnarelingServantTexture = BUILDER.comment("If Snareling Servants have custom textures, Default: true")
                        .define("snarelingServantTexture", true);
                BUILDER.pop();
                BUILDER.push("Squall Golem");
                SquallGolemTexture = BUILDER.comment("If Squall Golems have custom textures, Default: true")
                        .define("squallGolemTexture", true);
                BUILDER.pop();
                BUILDER.push("Redstone Golem");
                RedstoneGolemCrack = BUILDER.comment("If Redstone Golems show cracks when damaged sufficiently, Default: true")
                        .define("redstoneGolemCrack", true);
                RedstoneGolemTexture = BUILDER.comment("If Redstone Golems have custom textures, Default: true")
                        .define("redstoneGolemTexture", true);
                BUILDER.pop();
                BUILDER.push("Redstone Monstrosity");
                RedstoneMonstrosityTexture = BUILDER.comment("If Redstone Monstrosities have custom textures, Default: true")
                        .define("redstoneMonstrosityTexture", true);
                BUILDER.pop();
            BUILDER.pop();
        BUILDER.pop();
        BUILDER.push("Servants");
            BUILDER.push("Undead Servants");
            NecroRobeUndead = BUILDER.comment("Whether Servants would naturally attack Undead mobs if owner wears a full Necro Set, Default: false")
                    .define("necroRobeUndead", false);
            UndeadMinionHeal = BUILDER.comment("Whether Undead Servants can heal if summoned while wearing Necro Cape, Default: true")
                    .define("undeadServantsHeal", true);
            UndeadServantSunlightBurn = BUILDER.comment("Whether Undead Servants burn in Sunlight if not wearing helmet, Default: true")
                    .define("undeadServantSunlightBurn", true);
            UndeadServantSunlightHelmet = BUILDER.comment("Whether Undead Servants' helmet take damage when in Sunlight. Only takes effect if undeadServantSunlightBurn is enabled, Default: true")
                    .define("undeadServantSunlightHelmet", true);
            NecromancerSoulJar = BUILDER.comment("Whether owned Necromancers fills Empty Soul Jars, Default: true")
                    .define("necromancerSoulJar", true);
            NecromancerSummonsLife = BUILDER.comment("Whether Necromancer's summons have limited lifespans, can be affected by servantOwnedServantPlayerBenefit config, Default: true")
                    .define("necromancerSummonsLife", true);
            UndeadMinionHealCost = BUILDER.comment("How much Soul Energy it cost per second for an Undead Servant to heal, Default: 1")
                    .defineInRange("undeadServantsHealCost", 1, 0, Integer.MAX_VALUE);
            UndeadMinionHealTime = BUILDER.comment("How frequent Undead Servants heal, count seconds, Default: 1")
                    .defineInRange("undeadServantsHealTime", 1, 0, Integer.MAX_VALUE);
            UndeadMinionHealAmount = BUILDER.comment("How much Health Undead Servants heal, numerically, Default: 1.0")
                    .defineInRange("undeadServantsHealAmount", 1.0, 0.0, Double.MAX_VALUE);
            ZombieServantBabyChance = BUILDER.comment("Chance that a zombie (or subclass) servant is summoned as a baby, Default: 0.05")
                    .defineInRange("zombieServantBabyChance", 0.05, 0.0, 1.0D);
            BUILDER.pop();
            BUILDER.push("Water Servants");
            WaterMinionHeal = BUILDER.comment("Whether Water Servants can heal if summoned while wearing Abyss Robe, Default: true")
                    .define("waterMinionHeal", true);
            WaterMinionHealCost = BUILDER.comment("How much Soul Energy it cost per second for a Water Servant to heal, Default: 1")
                    .defineInRange("waterMinionHealCost", 1, 0, Integer.MAX_VALUE);
            WaterMinionHealTime = BUILDER.comment("How frequent Water Servants heal, count seconds, Default: 1")
                    .defineInRange("waterMinionHealTime", 1, 0, Integer.MAX_VALUE);
            WaterMinionHealAmount = BUILDER.comment("How much Health Water Servants heal, numerically, Default: 1.0")
                    .defineInRange("waterMinionHealAmount", 1.0, 0.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Natural Servants");
            NaturalMinionHeal = BUILDER.comment("Whether Natural Servants can heal if summoned while wearing Wild Robe, Default: true")
                    .define("naturalMinionHeal", true);
            NaturalMinionHealCost = BUILDER.comment("How much Soul Energy it cost per second for a Natural Servant to heal, Default: 1")
                    .defineInRange("naturalMinionHealCost", 1, 0, Integer.MAX_VALUE);
            NaturalMinionHealTime = BUILDER.comment("How frequent Natural Servants heal, count seconds, Default: 1")
                    .defineInRange("naturalMinionHealTime", 1, 0, Integer.MAX_VALUE);
            NaturalMinionHealAmount = BUILDER.comment("How much Health Natural Servants heal, numerically, Default: 1.0")
                    .defineInRange("naturalMinionHealAmount", 1.0, 0.0, Double.MAX_VALUE);
            BlackBeastDayStrength = BUILDER.comment("Whether Black Beasts gain buffs if total game time has gone long enough, Default: true")
                    .define("blackBeastDayStrength", true);
            BlackBeastChunkLoad = BUILDER.comment("Whether Black Beasts chunk load areas around themselves and their target when hunting down with Taglock Kit, Default: true")
                    .define("blackBeastChunkLoad", true);
            BlackBeastHowlingSoul = BUILDER.comment("Whether owned Black Beasts drop Howling Soul, Default: true")
                    .define("blackBeastHowlingSoul", true);
            TramplerHostileConvert = BUILDER.comment("Whether Trampler Servants when summoned as hostile will convert into their hostile counterpart, Default: true")
                    .define("tramplerHostileConvert", true);
            RavagerHostileConvert = BUILDER.comment("Whether Ravager Servants when summoned as hostile will convert into their hostile counterpart, Default: true")
                    .define("ravagerHostileConvert", true);
            RedstoneGolemHostileConvert = BUILDER.comment("Whether Redstone Golems when summoned as hostile will convert into their hostile counterpart, Default: true")
                    .define("redstoneGolemHostileConvert", true);
            RedstoneMonstrosityHostileConvert = BUILDER.comment("Whether Redstone Monstrosities when summoned as hostile will convert into their hostile counterpart, Default: true")
                    .define("redstoneMonstrosityHostileConvert", true);
            BUILDER.pop();
            BUILDER.push("Geo Servants");
            GeoMinionHeal = BUILDER.comment("Whether Geo Servants can heal if summoned while wearing Frost Robe, Default: true")
                    .define("geoMinionHeal", true);
            GeoMinionHealCost = BUILDER.comment("How much Soul Energy it cost per second for a Geo Servant to heal, Default: 1")
                    .defineInRange("geoMinionHealCost", 1, 0, Integer.MAX_VALUE);
            GeoMinionHealTime = BUILDER.comment("How frequent Geo Servants heal, count seconds, Default: 1")
                    .defineInRange("geoMinionHealTime", 1, 0, Integer.MAX_VALUE);
            GeoMinionHealAmount = BUILDER.comment("How much Health Geo Servants heal, numerically, Default: 1.0")
                    .defineInRange("geoMinionHealAmount", 1.0, 0.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Frost Servants");
            FrostMinionHeal = BUILDER.comment("Whether Frost Servants can heal if summoned while wearing Frost Robe, Default: true")
                    .define("frostMinionHeal", true);
            FrostMinionHealCost = BUILDER.comment("How much Soul Energy it cost per second for a Frost Servant to heal, Default: 1")
                    .defineInRange("frostMinionHealCost", 1, 0, Integer.MAX_VALUE);
            FrostMinionHealTime = BUILDER.comment("How frequent Frost Servants heal, count seconds, Default: 1")
                    .defineInRange("frostMinionHealTime", 1, 0, Integer.MAX_VALUE);
            FrostMinionHealAmount = BUILDER.comment("How much Health Frost Servants heal, numerically, Default: 1.0")
                    .defineInRange("frostMinionHealAmount", 1.0, 0.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Wind Servants");
            WindMinionHeal = BUILDER.comment("Whether Wind Servants can heal if summoned while wearing Frost Robe, Default: true")
                    .define("windMinionHeal", true);
            WindMinionHealCost = BUILDER.comment("How much Soul Energy it cost per second for a Wind Servant to heal, Default: 1")
                    .defineInRange("windMinionHealCost", 1, 0, Integer.MAX_VALUE);
            WindMinionHealTime = BUILDER.comment("How frequent Wind Servants heal, count seconds, Default: 1")
                    .defineInRange("windMinionHealTime", 1, 0, Integer.MAX_VALUE);
            WindMinionHealAmount = BUILDER.comment("How much Health Wind Servants heal, numerically, Default: 1.0")
                    .defineInRange("windMinionHealAmount", 1.0, 0.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Storm Servants");
            StormMinionHeal = BUILDER.comment("Whether Storm Servants can heal if summoned while wearing Frost Robe, Default: true")
                    .define("stormMinionHeal", true);
            StormMinionHealCost = BUILDER.comment("How much Soul Energy it cost per second for a Storm Servant to heal, Default: 1")
                    .defineInRange("stormMinionHealCost", 1, 0, Integer.MAX_VALUE);
            StormMinionHealTime = BUILDER.comment("How frequent Storm Servants heal, count seconds, Default: 1")
                    .defineInRange("stormMinionHealTime", 1, 0, Integer.MAX_VALUE);
            StormMinionHealAmount = BUILDER.comment("How much Health Storm Servants heal, numerically, Default: 1.0")
                    .defineInRange("stormMinionHealAmount", 1.0, 0.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Nether Servants");
            NetherMinionHeal = BUILDER.comment("Whether Nether Servants can heal if summoned while wearing Nether Robe, Default: true")
                    .define("netherMinionHeal", true);
            NetherMinionHealCost = BUILDER.comment("How much Soul Energy it cost per second for a Nether Servant to heal, Default: 1")
                    .defineInRange("netherMinionHealCost", 1, 0, Integer.MAX_VALUE);
            NetherMinionHealTime = BUILDER.comment("How frequent Nether Servants heal, count seconds, Default: 1")
                    .defineInRange("netherMinionHealTime", 1, 0, Integer.MAX_VALUE);
            NetherMinionHealAmount = BUILDER.comment("How much Health Nether Servants heal, numerically, Default: 1.0")
                    .defineInRange("netherMinionHealAmount", 1.0, 0.0, Double.MAX_VALUE);
            WildfireBlazingHelm = BUILDER.comment("Whether owned Wildfires drop Blazing Helms, Default: true")
                    .define("wildfireBlazingHelm", true);
            WildfireSummonsLife = BUILDER.comment("Whether Wildfire's summons have limited lifespans, Default: true")
                    .define("wildfireSummonsLife", true);
            BUILDER.pop();
            BUILDER.push("Void Servants");
            VoidMinionHeal = BUILDER.comment("Whether Void Servants can heal if summoned while wearing Nether Robe, Default: true")
                    .define("voidMinionHeal", true);
            VoidMinionHealCost = BUILDER.comment("How much Soul Energy it cost per second for a Void Servant to heal, Default: 1")
                    .defineInRange("voidMinionHealCost", 1, 0, Integer.MAX_VALUE);
            VoidMinionHealTime = BUILDER.comment("How frequent Void Servants heal, count seconds, Default: 1")
                    .defineInRange("voidMinionHealTime", 1, 0, Integer.MAX_VALUE);
            VoidMinionHealAmount = BUILDER.comment("How much Health Void Servants heal, numerically, Default: 1.0")
                    .defineInRange("voidMinionHealAmount", 1.0, 0.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Illager Servants");
            IllagerServantTrainTime = BUILDER.comment("How long does it take for Illagers to fully train, count in seconds, Default: 1200")
                    .defineInRange("illagerServantTrainTime", 1200, 0, Integer.MAX_VALUE);
            IllagerServantMaxMentors = BUILDER.comment("How many trained Illagers can reduce training time for training Illagers, Default: 2")
                    .defineInRange("illagerServantMaxMentors", 2, 0, Integer.MAX_VALUE);
            IllagerServantChestRange = BUILDER.comment("How many blocks from their registered chest trained Illagers will attempt to start using it, Default: 16")
                    .defineInRange("illagerServantChestRange", 16, 4, Integer.MAX_VALUE);
            EvokerServantRavagedCooldown = BUILDER.comment("How long the cooldown for turning Villagers into Ravaged for Evokers, count in seconds, Default: 1200")
                    .defineInRange("evokerServantRavagedCooldown", 1200, 0, Integer.MAX_VALUE);
            IllagerServantAutoTrain = BUILDER.comment("Whether eligible Illagers will automatically train just by being near an active Ominous Pyre, instead of having been commanded to it, Default: false")
                    .define("illagerServantAutoTrain", false);
            IllagerServantAllBreed = BUILDER.comment("Whether every Illager Servant type can breed or only Neollagers can, Default: true")
                    .define("illagerServantAllBreed", true);
            IllagerServantLootVillagers = BUILDER.comment("Whether Illagers will gain loot based on current offers by killing Villagers, Default: true")
                    .define("illagerServantLootVillagers", true);
            IllagerServantLootTraders = BUILDER.comment("Whether Illagers will gain loot based on current offers by killing Wandering Traders, Default: true")
                    .define("illagerServantLootTraders", true);
            IllagerServantPickUpDrops = BUILDER.comment("Whether Illagers will take certain items from the ground, Default: true")
                    .define("illagerServantPickUpDrops", true);
            IllagerServantCollectLoot = BUILDER.comment("Whether Illagers will instantly store drops from their kills into their inventory, before throwing them back to their owner or store them in bounded chest, Default: true")
                    .define("illagerServantCollectLoot", true);
            IllagerServantAllOpenDoors = BUILDER.comment("Whether every Illagers can open doors when raiding instead of just Vindicators, Default: true")
                    .define("illagerServantAllOpenDoors", true);
            IllagerServantGhostArrows = BUILDER.comment("Whether Illagers shoot arrows that pass through allied mobs, Default: true")
                    .define("illagerServantGhostArrows", true);
            IllagerServantTrainArmor = BUILDER.comment("Whether Neollagers gain armor after training if their owner wears Ring of the Forge, if 'illagerServantWearArmor' is enabled, Default: true")
                    .define("illagerServantTrainArmor", true);
            IllagerServantChunkLoadMark = BUILDER.comment("Whether Illagers chunk load areas around themselves and their target when hunting down with Taglock Kit, Default: true")
                    .define("illagerServantChunkLoadMark", true);
            IllagerServantChunkLoadRaid = BUILDER.comment("Whether Illagers chunk load areas around themselves and their raid position when raiding, Default: true")
                    .define("illagerServantChunkLoadRaid", true);
            MountaineerClimb = BUILDER.comment("Whether Mountaineers can climb up blocks like Spiders, Default: true")
                    .define("mountaineerClimb", true);
            BUILDER.pop();
            BUILDER.push("Prisoners");
            PrisonerMining = BUILDER.comment("Whether Prisoners can mine ores when given a Pickaxe, Default: true")
                    .define("prisonerMining", true);
            PrisonerMiningSeeBlocks = BUILDER.comment("Whether Prisoners can only mine ores they have on line of sight, Default: true")
                    .define("prisonerMiningSeeBlocks", true);
            PrisonerMiningBreakBlocks = BUILDER.comment("Whether Prisoners break ore blocks when mining, Default: false")
                    .define("prisonerMiningBreakBlocks", false);
            PrisonerMiningSwings = BUILDER.comment("How many times a Prisoner has to swing their pickaxe before collecting drops when mining, Default: 5")
                    .defineInRange("prisonerMiningSwings", 5, 0, Integer.MAX_VALUE);
            PrisonerMiningRange = BUILDER.comment("How far Prisoners can scan for ores and mine it, in blocks, Default: 4")
                    .defineInRange("prisonerMiningRange", 4, 1, 64);
            PrisonerMiningDurability = BUILDER.comment("How much durability is used up on Prisoner's pickaxe after swinging, Default: 1")
                    .defineInRange("prisonerMiningDurability", 1, 0, Integer.MAX_VALUE);
            PrisonerMiningChance = BUILDER.comment("What are the chances of Prisoners successfully mining a block, the lower the number, the more likely, setting to 0 will cause Prisoners to always get the drop, Default: 0")
                    .defineInRange("prisonerMiningChance", 0, 0, Integer.MAX_VALUE);
            PrisonerMiningRareChance = BUILDER.comment("What are the chances of Prisoners successfully mining a rare ore (ie, Diamonds), the lower the number, the more likely, setting to 0 will cause Prisoners to always get the drop, Default: 10")
                    .defineInRange("prisonerMiningRareChance", 10, 0, Integer.MAX_VALUE);
            PrisonerUnshackleDamage = BUILDER.comment("Whether Prisoners unshackles after taking enough damage away from their owner and or captain, Default: true")
                    .define("prisonerUnshackleDamage", true);
            PrisonerPickUpPickaxe = BUILDER.comment("Whether Prisoners can pick up Pickaxes that are dropped near them and mobGriefing is true, Default: true")
                    .define("prisonerPickUpPickaxe", true);
            BUILDER.pop();
        NecroSetDebuff = BUILDER.comment("Whether wearing Necro Crown and/or Necro Cape gives massive debuffs to non-undead servant, Default: false")
                .define("necroSetDebuff", false);
        NamelessSetDebuff = BUILDER.comment("Whether wearing Nameless Crown and/or Nameless Cape gives massive debuffs to non-undead servant, Default: false")
                .define("namelessSetDebuff", false);
        CompatMinionHeal = BUILDER.comment("Whether mobs owned by player outside of Goety Servants can heal while wearing appropriate robes, Default: true")
                .define("compatMinionHeal", true);
        CompatNightmareStalker = BUILDER.comment("Whether Nightmare Stalker from Born In Chaos mod no longer gain massive buffs when fighting certain Goety mobs, Default: true")
                .define("compatNightmareStalker", true);
        ServantTeleport = BUILDER.comment("Whether Servants can teleport to Players, Default: false")
                .define("servantTeleport", false);
        ServantOwnedServantPlayerBenefit = BUILDER.comment("Whether Servants owned by other Servants benefits from Player Robes and Crown, Default: true")
                .define("servantOwnedServantPlayerBenefit", true);
        RedstoneMonstrosityLeafBreak = BUILDER.comment("Whether Redstone Monstrosity breaks leaves and certain blocks if mob griefing is enabled, Default: true")
                .define("redstoneMonstrosityLeafBreak", true);
        RedstoneCubeBlockFind = BUILDER.comment("Enable Redstone Cubes block detection mechanic, Default: true")
                .define("RedstoneCubeBlockFind", true);
        VexTeleport = BUILDER.comment("Whether Vex Servants can teleport to Players, Default: true")
                .define("vexTeleport", true);
        ServantsAttackCreepers = BUILDER.comment("Whether Servants can attack Creepers if Mob Griefing Rule is False, Default: true")
                .define("servantsAttackCreepers", true);
        ServantsMasterImmune = BUILDER.comment("Whether Servants or their owner are immune to attacks made by other servants that are summoned by the same owner, Default: true")
                .define("servantsMasterImmune", true);
        OwnerAttackCancel = BUILDER.comment("Owners can't attack or hurt their servants, Default: true")
                .define("ownerAttackCancel", true);
        MobSense = BUILDER.comment("Mobs will automatically be hostile to servants, if servant is hostile towards the mob, Default: true")
                .define("mobSense", true);
        ServantsHarmEffectApply = BUILDER.comment("Whether servants can apply harmful status effects to their owner/allies, Default: false")
                .define("servantsHarmEffectApply", false);
        VariousRobeWitch = BUILDER.comment("Whether Servants would naturally attack Witches or Warlocks if owner wears a full a robe that renders the former two neutral, Default: false")
                .define("variousRobeWitch", false);
        StayingServantChunkLoad = BUILDER.comment("Servants, when on staying mode, will load chunks around it, Default: true")
                .define("stayingServantChunkLoad", true);
        GuardingServantChunkLoad = BUILDER.comment("Servants, when on guarding mode, will load chunks around it, Default: true")
                .define("guardingServantChunkLoad", true);
        FollowingServantChunkLoad = BUILDER.comment("Servants, when on following mode, will load chunks around it, Default: true")
                .define("followingServantChunkLoad", true);
        ServantsCanWearPumpkin = BUILDER.comment("Whether Servants be equipped with Carved Pumpkins, which would allow Servants to not be affected by sunlight, Default: false")
                .define("servantsCanWearPumpkin", false);
        ServantRideAutonomous = BUILDER.comment("Whether certain servants being ridden by other servants will not be controlled by their rider., Default: false")
                .define("servantRideAutonomous", false);
        MaxSlimeSize = BUILDER.comment("Maximum size Slime and Magma Cube Servants can obtain through Slime/Magma Block, Default: 4")
                .defineInRange("maxSlimeSize", 4, 1, 127);
        RavagerRoarCooldown = BUILDER.comment("How many seconds it takes before Ravager can manually roar again, Default: 10")
                .defineInRange("ravagerRoarCooldown", 10, 0, Integer.MAX_VALUE);
        PlayerRavagerArmorDrop = BUILDER.comment("Whether armored Ravagers owned by players will drop their armor, Default: true")
                .define("playerRavagerArmorDrop", true);
        RaiderServantWearArmor = BUILDER.comment("Whether armor wearing Raider Servants can be equipped with Armor, Default: true")
                .define("raiderServantWearArmor", true);
        ServantGuardingRange = BUILDER.comment("How far servants can guard from their guarding location, Default: 16")
                .defineInRange("servantGuardingRange", 16, 2, Integer.MAX_VALUE);
        ServantHealHalt = BUILDER.comment("How many seconds a servant can't heal through Soul Energy after being injured, Default: 5")
                .defineInRange("servantHealHalt", 5, 0, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Illagers");
            BUILDER.push("Illager Assaults");
            IllagerAssault = BUILDER.comment("Modded Illagers Spawning based of Player's Soul Energy amount, Default: true")
                    .define("illagerAssault", true);
            IllagerAssaultSpawnFreq = BUILDER.comment("How many ticks it takes for Illager Assaults to spawn, Default: 12000")
                    .defineInRange("illagerAssaultSpawnFreq", 12000, 0, Integer.MAX_VALUE);
            IllagerAssaultSpawnChance = BUILDER.comment("Spawn Chance for Illagers Hunting the Player every Spawn Frequency, the lower the more likelier, Default: 5")
                    .defineInRange("illagerAssaultSpawnChance", 5, 1, Integer.MAX_VALUE);
            IllagerAssaultSEThreshold = BUILDER.comment("How much Soul Energy the Player has is required for Special Illagers to spawn, Default: 2500")
                    .defineInRange("illagerAssaultThreshold", 2500, 0, Integer.MAX_VALUE);
            IllagerAssaultSELimit = BUILDER.comment("The maximum amount of Soul Energy the Player has that is taken consideration for the Assaults, Default: 30000")
                    .defineInRange("illagerAssaultLimit", 30000, 0, Integer.MAX_VALUE);
            IllagerAssaultRestDeath = BUILDER.comment("How many Minecraft days of rest from Illager Assaults are added to Players killed by an Illager, Default: 2")
                    .defineInRange("illagerAssaultRestDeath", 2, 0, Integer.MAX_VALUE);
            IllagerAssaultRestMinister = BUILDER.comment("How many Minecraft days of rest from Illager Assaults the Players have after killing a Minister, Default: 10")
                    .defineInRange("illagerAssaultRestMinister", 10, 0, Integer.MAX_VALUE);
            SoulEnergyBadOmen = BUILDER.comment("Hitting the Illager Assault Limit of Soul Energy have a chance of giving Player Bad Omen effect, Default: true")
                    .define("soulEnergyBadOmen", true);
            BUILDER.pop();
            BUILDER.push("Raid");
            IllagerRaid = BUILDER.comment("Whether Modded Illagers appears in Raids, Default: true")
                    .define("specialIllagerRaid", true);
            ArmoredRavagerRaid = BUILDER.comment("Whether Armored Ravagers spawn in Raids, Default: true")
                    .define("armoredRavagerRaid", true);
            ArmoredTramplerRaid = BUILDER.comment("Whether Armored Tramplers spawn in Raids, Default: true")
                    .define("armoredTramplerRaid", true);
                BUILDER.push("Piker");
                PikerRaid = BUILDER.comment("Whether Pikers appear in Raids, Default: true")
                        .define("pikerRaid", true);
                PikerRaidCount = BUILDER.comment("How many Pikers each wave", "Requires game restart", "Must have no more and no less than 8 integers")
                        .worldRestart()
                        .defineList("pikerRaidCount",
                                Arrays.asList(0, 0, 0, 2, 0, 3, 3, 5), (i) -> i instanceof Integer);
                BUILDER.pop();
                BUILDER.push("Ripper");
                RipperRaid = BUILDER.comment("Whether Rippers appear in Raids. They do not count as part of the Raid bar, Default: true")
                        .define("ripperRaid", true);
                RipperRaidCount = BUILDER.comment("How many Rippers each wave", "Requires game restart", "Must have no more and no less than 8 integers")
                        .worldRestart()
                        .defineList("ripperRaidCount",
                                Arrays.asList(0, 0, 0, 4, 0, 6, 6, 10), (i) -> i instanceof Integer);
                BUILDER.pop();
                BUILDER.push("Crusher");
                CrusherRaid = BUILDER.comment("Whether Crushers appear in Raids, Default: true")
                        .define("crusherRaid", true);
                CrusherRaidCount = BUILDER.comment("How many Crushers each wave", "Requires game restart", "Must have no more and no less than 8 integers")
                        .worldRestart()
                        .defineList("crusherRaidCount",
                                Arrays.asList(0, 0, 0, 0, 2, 2, 0, 2), (i) -> i instanceof Integer);
                BUILDER.pop();
                BUILDER.push("Storm Caster");
                StormCasterRaid = BUILDER.comment("Whether Storm Casters appear in Raids, Default: true")
                        .define("stormCasterRaid", true);
                StormCasterRaidCount = BUILDER.comment("How many Storm Casters each wave", "Requires game restart", "Must have no more and no less than 8 integers")
                        .worldRestart()
                        .defineList("stormCasterRaidCount",
                                Arrays.asList(0, 0, 0, 0, 1, 1, 0, 2), (i) -> i instanceof Integer);
                BUILDER.pop();
                BUILDER.push("Cryologer");
                CryologerRaid = BUILDER.comment("Whether Cryologers appear in Raids, Default: true")
                        .define("cryologerRaid", true);
                CryologerRaidCount = BUILDER.comment("How many Cryologers each wave", "Requires game restart", "Must have no more and no less than 8 integers")
                        .worldRestart()
                        .defineList("cryologerRaidCount",
                                Arrays.asList(0, 0, 1, 1, 0, 0, 0, 2), (i) -> i instanceof Integer);
                BUILDER.pop();
                BUILDER.push("Preacher");
                PreacherRaid = BUILDER.comment("Whether Preachers appear in Raids, Default: true")
                        .define("preacherRaid", true);
                PreacherRaidCount = BUILDER.comment("How many Preachers each wave", "Requires game restart", "Must have no more and no less than 8 integers")
                        .worldRestart()
                        .defineList("preacherRaidCount",
                                Arrays.asList(0, 0, 1, 1, 0, 0, 0, 2), (i) -> i instanceof Integer);
                BUILDER.pop();
                BUILDER.push("Conquillager");
                ConquillagerRaid = BUILDER.comment("Whether Conquillagers appear in Raids, Default: true")
                        .define("conquillagerRaid", true);
                ConquillagerRaidCount = BUILDER.comment("How many Conquillagers each wave", "Requires game restart", "Must have no more and no less than 8 integers")
                        .worldRestart()
                        .defineList("conquillagerRaidCount",
                                Arrays.asList(0, 4, 3, 3, 4, 4, 4, 2), (i) -> i instanceof Integer);
                BUILDER.pop();
                BUILDER.push("Inquillager");
                InquillagerRaid = BUILDER.comment("Whether Inquillagers appear in Raids, Default: true")
                        .define("inquillagerRaid", true);
                InquillagerRaidCount = BUILDER.comment("How many Inquillagers each wave", "Requires game restart", "Must have no more and no less than 8 integers")
                        .worldRestart()
                        .defineList("inquillagerRaidCount",
                                Arrays.asList(0, 0, 2, 0, 1, 2, 2, 3), (i) -> i instanceof Integer);
                BUILDER.pop();
                BUILDER.push("Envioker");
                EnviokerRaid = BUILDER.comment("Whether Enviokers appear in Raids, Default: true")
                        .define("enviokerRaid", true);
                EnviokerRaidCount = BUILDER.comment("How many Enviokers each wave", "Requires game restart", "Must have no more and no less than 8 integers")
                        .worldRestart()
                        .defineList("enviokerRaidCount",
                                Arrays.asList(0, 0, 0, 1, 0, 1, 1, 2), (i) -> i instanceof Integer);
                BUILDER.pop();
                BUILDER.push("Sorcerer");
                SorcererRaid = BUILDER.comment("Whether Sorcerers appear in Raids, Default: true")
                        .define("sorcererRaid", true);
                SorcererRaidCount = BUILDER.comment("How many Sorcerers each wave", "Requires game restart", "Must have no more and no less than 8 integers")
                        .worldRestart()
                        .defineList("sorcererRaidCount",
                                Arrays.asList(0, 0, 1, 0, 1, 0, 1, 1), (i) -> i instanceof Integer);
                BUILDER.pop();
                BUILDER.push("Minister");
                MinisterRaid = BUILDER.comment("Whether Ministers appear in Raids, Default: true")
                        .define("ministerRaid", true);
                MinisterRaidCount = BUILDER.comment("How many Ministers each wave", "Requires game restart", "Must have no more and no less than 8 integers")
                        .worldRestart()
                        .defineList("ministerRaidCount",
                                Arrays.asList(0, 0, 0, 0, 0, 0, 0, 1), (i) -> i instanceof Integer);
                BUILDER.pop();
                BUILDER.push("Hostile Redstone Golem");
                HostileRedstoneGolemRaid = BUILDER.comment("Whether Hostile Redstone Golems appear in Raids, Default: false")
                        .define("hostileRedstoneGolemRaid", false);
                HostileRedstoneGolemRaidCount = BUILDER.comment("How many Hostile Redstone Golems each wave", "Requires game restart", "Must have no more and no less than 8 integers")
                        .worldRestart()
                        .defineList("hostileRedstoneGolemRaidCount",
                                Arrays.asList(0, 0, 0, 0, 0, 1, 1, 0), (i) -> i instanceof Integer);
                BUILDER.pop();
                BUILDER.push("Hostile Redstone Monstrosity");
                HostileRedstoneMonstrosityRaid = BUILDER.comment("Whether Hostile Redstone Monstrosities appear in Raids, Default: false")
                        .define("hostileRedstoneMonstrosityRaid", false);
                HRMSpawnNoRaiders = BUILDER.comment("Whether Hostile Redstone Monstrosities only appear when all other non-boss raiders are defeated", "Requires game restart", "Default: true")
                        .worldRestart()
                        .define("hRMSpawnNoRaiders", true);
                HostileRedstoneMonstrosityRaidCount = BUILDER.comment("How many Hostile Redstone Monstrosities each wave", "Requires game restart", "Must have no more and no less than 8 integers")
                        .worldRestart()
                        .defineList("hostileRedstoneMonstrosityRaidCount",
                                Arrays.asList(0, 0, 0, 0, 0, 0, 0, 1), (i) -> i instanceof Integer);
                BUILDER.pop();
                BUILDER.push("Warlock");
                WarlockRaid = BUILDER.comment("Whether Warlocks appear in Raids, Default: true")
                        .define("warlockRaid", true);
                WarlockRaidCount = BUILDER.comment("How many Warlocks each wave", "Requires game restart", "Must have no more and no less than 8 integers")
                        .worldRestart()
                        .defineList("warlockRaidCount",
                                Arrays.asList(0, 0, 0, 0, 1, 2, 0, 1), (i) -> i instanceof Integer);
                BUILDER.pop();
                BUILDER.push("Maverick");
                MaverickRaid = BUILDER.comment("Whether Mavericks appear in Raids, Default: true")
                        .define("maverickRaid", true);
                MaverickRaidCount = BUILDER.comment("How many Mavericks each wave", "Requires game restart", "Must have no more and no less than 8 integers")
                        .worldRestart()
                        .defineList("maverickRaidCount",
                                Arrays.asList(0, 1, 0, 1, 0, 0, 0, 1), (i) -> i instanceof Integer);
                BUILDER.pop();
                BUILDER.push("Heretic");
                HereticRaid = BUILDER.comment("Whether Heretics appear in Raids, Default: true")
                        .define("hereticRaid", true);
                HereticRaidCount = BUILDER.comment("How many Heretics each wave", "Requires game restart", "Must have no more and no less than 8 integers")
                        .worldRestart()
                        .defineList("hereticRaidCount",
                                Arrays.asList(0, 0, 0, 1, 0, 0, 2, 1), (i) -> i instanceof Integer);
                BUILDER.pop();
            BUILDER.pop();
        CryologerIceChunk = BUILDER.comment("Whether Cryologers can summon Ice Chunks on Hard Difficulty, Default: false")
                .define("cryologerIceChunk", false);
        SorcererHPIncrease = BUILDER.comment("Whether Sorcerers' max health increases the higher level they are, Default: true")
                .define("sorcererHPIncrease", true);
        IllagueSpread = BUILDER.comment("Whether Illague Effect can spread from non Conquillagers that has the effect, Default: true")
                .define("illagueSpread", true);
        IllagerSteal = BUILDER.comment("Whether Enviokers, Inquillagers and Conquillagers can steal Totems of Souls or Totems of Undying, Default: true")
                .define("illagerSteal", true);
        BUILDER.pop();
        BUILDER.push("Villagers");
        VillagerHate = BUILDER.comment("Wearing a Robe, along with variants, causes Villagers around the Player to have a negative Reputation unless said Player has 25 or more reputation among them, Default: false")
                .define("villagerHate", false);
        VillagerHateRavager = BUILDER.comment("Having an owned Ravaged or Ravager, causes Villagers around the Player to have a negative Reputation, Default: false")
                .define("villagerHateRavager", false);
        VillagerHateSpells = BUILDER.comment("Casting Spell in the presence of Villagers will cause the Player to lose a number of Reputation, set 0 to disable, Default: 0")
                .defineInRange("villagerHateSpells", 0, 0, Integer.MAX_VALUE);
        VillagerConvertWarlock = BUILDER.comment("Villagers have a chance of converting into Warlocks if they're underneath a Block of Crying Obsidian, Default: true")
                .define("villagerConvertToWarlock", true);
        VillagerConvertHeretic = BUILDER.comment("Villagers have a chance of converting into Heretics if they're sleeping near an active Nether Portal, Default: false")
                .define("villagerConvertToHeretic", false);
        TraderConvertMaverick = BUILDER.comment("Wandering Traders transforms into Mavericks when struck by lightning, Default: true")
                .define("traderConvertMaverick", true);
        BUILDER.pop();
        BUILDER.push("Spawning");
            BUILDER.push("Wight");
            WightSpawn = BUILDER.comment("Whether Wights can spawn near players that have a high amount of Soul Energy, Default: true")
                    .define("wightSpawn", true);
            WightSpawnFreq = BUILDER.comment("How many ticks it takes for Wights to spawn, Default: 24000")
                    .defineInRange("wightSpawnFreq", 24000, 0, Integer.MAX_VALUE);
            WightSpawnChance = BUILDER.comment("Spawn Chance for Wights spawning near the Player every Spawn Frequency, the lower the more likelier, Default: 10")
                    .defineInRange("wightSpawnChance", 10, 1, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Warlock");
            WarlockSpawnWeight = BUILDER.comment("Spawn Weight for Warlock, Default: 5")
                    .defineInRange("warlockSpawnWeight", 5, 0, Integer.MAX_VALUE);
            WarlockSpawnMinCount = BUILDER.comment("Spawn minimum group count for Warlock, Default: 1")
                    .defineInRange("warlockSpawnMinCount", 1, 1, Integer.MAX_VALUE);
            WarlockSpawnMaxCount = BUILDER.comment("Spawn maximum group count for Warlock, must be equal or higher than min count, Default: 1")
                    .defineInRange("warlockSpawnMaxCount", 1, 1, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Reaper");
            ReaperSpawnWeight = BUILDER.comment("Spawn Weight for Reaper, Default: 10")
                    .defineInRange("reaperSpawnWeight", 10, 0, Integer.MAX_VALUE);
            ReaperSpawnMinCount = BUILDER.comment("Spawn minimum group count for Reaper, Default: 1")
                    .defineInRange("reaperSpawnMinCount", 1, 1, Integer.MAX_VALUE);
            ReaperSpawnMaxCount = BUILDER.comment("Spawn maximum group count for Reaper, must be equal or higher than min count, Default: 1")
                    .defineInRange("reaperSpawnMaxCount", 1, 1, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Wraith");
            WraithSpawnWeight = BUILDER.comment("Spawn Weight for Wraith, Default: 20")
                    .defineInRange("wraithSpawnWeight", 20, 0, Integer.MAX_VALUE);
            WraithSpawnMinCount = BUILDER.comment("Spawn minimum group count for Wraith, Default: 1")
                    .defineInRange("wraithSpawnMinCount", 1, 1, Integer.MAX_VALUE);
            WraithSpawnMaxCount = BUILDER.comment("Spawn maximum group count for Wraith, must be equal or higher than min count, Default: 1")
                    .defineInRange("wraithSpawnMaxCount", 1, 1, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Muck Wraith");
            MuckWraithSpawnWeight = BUILDER.comment("Spawn Weight for Muck Wraith, Default: 20")
                    .defineInRange("muckWraithSpawnWeight", 20, 0, Integer.MAX_VALUE);
            MuckWraithSpawnMinCount = BUILDER.comment("Spawn minimum group count for Muck Wraith, Default: 1")
                    .defineInRange("muckWraithSpawnMinCount", 1, 1, Integer.MAX_VALUE);
            MuckWraithSpawnMaxCount = BUILDER.comment("Spawn maximum group count for Muck Wraith, must be equal or higher than min count, Default: 1")
                    .defineInRange("muckWraithSpawnMaxCount", 1, 1, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Web Spider");
            WebSpiderSpawnWeight = BUILDER.comment("Spawn Weight for Web Spiders, Default: 40")
                    .defineInRange("webSpiderSpawnWeight", 40, 0, Integer.MAX_VALUE);
            WebSpiderSpawnMinCount = BUILDER.comment("Spawn minimum group count for Web Spiders, Default: 4")
                    .defineInRange("webSpiderSpawnMinCount", 4, 1, Integer.MAX_VALUE);
            WebSpiderSpawnMaxCount = BUILDER.comment("Spawn maximum group count for Web Spiders, must be equal or higher than min count, Default: 4")
                    .defineInRange("webSpiderSpawnMaxCount", 4, 1, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Icy Spider");
            IcySpiderSpawnWeight = BUILDER.comment("Spawn Weight for Icy Spiders, Default: 40")
                    .defineInRange("icySpiderSpawnWeight", 40, 0, Integer.MAX_VALUE);
            IcySpiderSpawnMinCount = BUILDER.comment("Spawn minimum group count for Icy Spiders, Default: 4")
                    .defineInRange("icySpiderSpawnMinCount", 4, 1, Integer.MAX_VALUE);
            IcySpiderSpawnMaxCount = BUILDER.comment("Spawn maximum group count for Icy Spiders, must be equal or higher than min count, Default: 4")
                    .defineInRange("icySpiderSpawnMaxCount", 4, 1, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Frayed");
            FrayedSpawnWeight = BUILDER.comment("Spawn Weight for Frayeds, Default: 80")
                    .defineInRange("frayedSpawnWeight", 80, 0, Integer.MAX_VALUE);
            FrayedSpawnMinCount = BUILDER.comment("Spawn minimum group count for Frayeds, Default: 4")
                    .defineInRange("frayedSpawnMinCount", 4, 1, Integer.MAX_VALUE);
            FrayedSpawnMaxCount = BUILDER.comment("Spawn maximum group count for Frayeds, must be equal or higher than min count, Default: 4")
                    .defineInRange("frayedSpawnMaxCount", 4, 1, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Rattled");
            RattledSpawnWeight = BUILDER.comment("Spawn Weight for Rattleds, Default: 80")
                    .defineInRange("rattledSpawnWeight", 80, 0, Integer.MAX_VALUE);
            RattledSpawnMinCount = BUILDER.comment("Spawn minimum group count for Rattleds, Default: 4")
                    .defineInRange("rattledSpawnMinCount", 4, 1, Integer.MAX_VALUE);
            RattledSpawnMaxCount = BUILDER.comment("Spawn maximum group count for Rattleds, must be equal or higher than min count, Default: 4")
                    .defineInRange("rattledSpawnMaxCount", 4, 1, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Necromancer");
            NecromancerSpawnWeight = BUILDER.comment("Spawn Weight for Necromancer, Default: 1")
                    .defineInRange("necromancerSpawnWeight", 1, 0, Integer.MAX_VALUE);
            NecromancerSpawnMinCount = BUILDER.comment("Spawn minimum group count for Necromancer, Default: 1")
                    .defineInRange("necromancerSpawnMinCount", 1, 1, Integer.MAX_VALUE);
            NecromancerSpawnMaxCount = BUILDER.comment("Spawn maximum group count for Necromancer, must be equal or higher than min count, Default: 1")
                    .defineInRange("necromancerSpawnMaxCount", 1, 1, Integer.MAX_VALUE);
            NecromancerSpawnStructure = BUILDER.comment("Whether Necromancers can be part of structure spawn pools. Default: false")
                    .define("necromancerSpawnStructure", false);
            BUILDER.pop();
            BUILDER.push("Heretic");
            HereticSpawnWeight = BUILDER.comment("Spawn Weight for Heretic, Default: 5")
                    .defineInRange("hereticSpawnWeight", 5, 0, Integer.MAX_VALUE);
            HereticSpawnMinCount = BUILDER.comment("Spawn minimum group count for Heretic, Default: 1")
                    .defineInRange("hereticSpawnMinCount", 1, 1, Integer.MAX_VALUE);
            HereticSpawnMaxCount = BUILDER.comment("Spawn maximum group count for Heretic, must be equal or higher than min count, Default: 1")
                    .defineInRange("hereticSpawnMaxCount", 1, 1, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Maverick");
            MaverickSpawnWeight = BUILDER.comment("Spawn Weight for Maverick, Default: 5")
                    .defineInRange("maverickSpawnWeight", 5, 0, Integer.MAX_VALUE);
            MaverickSpawnMinCount = BUILDER.comment("Spawn minimum group count for Maverick, Default: 1")
                    .defineInRange("maverickSpawnMinCount", 1, 1, Integer.MAX_VALUE);
            MaverickSpawnMaxCount = BUILDER.comment("Spawn maximum group count for Maverick, must be equal or higher than min count, Default: 1")
                    .defineInRange("maverickSpawnMaxCount", 1, 1, Integer.MAX_VALUE);
            BUILDER.pop();
        BUILDER.pop();
        BUILDER.push("Misc");
            BUILDER.push("Apostle");
            ApocalypseMode = BUILDER.comment("Apostle spreads Nether biomes, and Nether Meteors deals environmental damage and spread Nether biomes as well. WARNING: Causes lots of lag. Default: false")
                    .define("apocalypseMode", false);
            ApostlePersistent = BUILDER.comment("Whether Apostles are persistent and do not naturally despawn. Default: false")
                    .define("apostlePersistent", true);
            ApostleBoilsWater = BUILDER.comment("Whether Apostles causes entities within 32 blocks of themselves to take damage when in water. Default: true")
                    .define("apostleBoilsWater", true);
            ApostleTornado = BUILDER.comment("Whether Apostles can summon Fire Tornadoes. Default: true")
                    .define("apostleTornado", true);
            ApostleHellCloud = BUILDER.comment("Whether Apostles can summon Hell Clouds. Default: true")
                    .define("apostleHellCloud", true);
            ApostleQuickerRegen = BUILDER.comment("Enable Apostle Nether/The Risen quicker regeneration. Disabling will cause them to regenerate 10x more slowly. Default: true")
                    .define("apostleQuickerRegen", true);
            ApostleResistance = BUILDER.comment("Enable Apostle The Glorious getting Resistance effect. Disabling will give them Iron Hide instead. Default: true")
                    .define("apostleResistance", true);
            ApostleShootIndicator = BUILDER.comment("Whether Apostle shoot indicator is enabled. Default: true")
                    .define("apostleShootIndicator", true);
            ApostleHardMagicResistance = BUILDER.comment("Whether Apostles gain magic resistance if difficulty is on Hard. Default: false")
                    .define("apostleHardMagicResistance", false);
            ApostleHalvedArmor = BUILDER.comment("Whether Apostle's armor and armor toughness values are halved when fought outside of the Nether. Default: true")
                    .define("apostleHalvedArmor", true);
            ApostleCritArrows = BUILDER.comment("Whether Apostles can shoot Critical Hits on their Arrows. Default: true")
                    .define("apostleCritArrows", true);
            ApostleDelayedTeleport = BUILDER.comment("Whether Apostles' teleport has a delay. Disabling will cause them to teleport instantly. Default: true")
                    .define("apostleDelayedTeleport", true);
            ApostleConvertsVillagers = BUILDER.comment("Whether Apostles causes Villagers within 32 blocks of themselves to have a chance of converting into a Witch or Warlock. Default: true")
                    .define("apostleConvertsVillagers", true);
            FancierApostleDeath = BUILDER.comment("Gives Apostle an even more fancier death animation, Default: false")
                    .define("fancierApostleDeath", false);
            ApostleNetherDamageReduction = BUILDER.comment("How much damage is reduced, by percentage, on the Apostle when in the Nether, setting to 100 will make them invulnerable, Default: 50")
                    .defineInRange("apostleNetherDamageReduction", 50, 0, 100);
            BUILDER.pop();
            BUILDER.push("Damned");
            DamnedShootIndicator = BUILDER.comment("Whether Damned shoot indicator is enabled. Default: true")
                    .define("damnedShootIndicator", true);
            BUILDER.pop();
            BUILDER.push("Vizier");
            VizierPersistent = BUILDER.comment("Whether Viziers are persistent and do not naturally despawn. Default: false")
                    .define("vizierPersistent", false);
            VizierMinion = BUILDER.comment("Viziers spawn Vexes instead of Irks, Default: false")
                    .define("vizierMinion", false);
            BUILDER.pop();
            BUILDER.push("Ender Keeper");
            EnderKeeperAfterImage = BUILDER.comment("Whether Ender Keeper has their after-image effect. Disable can prevent frame drop. Default: true")
                    .define("enderKeeperAfterImage", true);
            BUILDER.pop();
            BUILDER.push("Obsidian Monolith");
            ObsidianMonolithSpread = BUILDER.comment("Whether unowned Obsidian Monoliths, empowered by Heretics, converts nearby Overworld blocks to Nether blocks. Default: true")
                    .define("obsidianMonolithSpread", true);
            ObsidianMonolithBiome = BUILDER.comment("Whether unowned Obsidian Monoliths, empowered by Heretics, change Overworld biomes to Nether biomes, Default: true")
                    .define("obsidianMonolithBiome", true);
            ObsidianMonolithSpawner = BUILDER.comment("Whether unowned Obsidian Monoliths can spawn Mobs, Default: true")
                    .define("obsidianMonolithSpawner", true);
            ObsidianMonolithStartUpTime = BUILDER.comment("How many Minecraft days, since spawning in, until unowned Obsidian Monoliths start spreading, Default: 3")
                    .defineInRange("obsidianMonolithStartUpTime", 3, 1, Integer.MAX_VALUE);
            ObsidianMonolithBlackList = BUILDER.comment("""
                            Add mobs that ownerless Obsidian Monolith will not spawn.\s
                            To do so, enter the namespace ID of the mob, like "minecraft:zombie, minecraft:skeleton".""")
                    .defineList("obsidianMonolithBlackList", Arrays.asList("minecraft:slime", "minecraft:ghast", "minecraft:piglin", "minecraft:piglin_brute", "minecraft:hoglin"),
                            (itemRaw) -> itemRaw instanceof String);
            BUILDER.pop();
            BUILDER.push("Crone");
            CroneThornDefense = BUILDER.comment("Whether Crones inflict thorn damage at attackers, when attacked. Default: true")
                    .define("croneThornDefense", true);
            BUILDER.pop();
        BossInvulnerabilityTime = BUILDER.comment("How long invulnerability, Default: 15")
                .defineInRange("bossInvulnerabilityTime", 15, 0, Integer.MAX_VALUE);
        TallSkullDrops = BUILDER.comment("Whether Mobs with Tall Heads(ie. Villagers, Illagers, etc.) will drop Tall Skulls, Default: true")
                .define("tallSkullDrop", true);
        WraithAggressiveTeleport = BUILDER.comment("Whether Wraiths should teleport towards their targets if they can't see them instead of just teleporting away when they're near them, Default: true")
                .define("wraithAggressiveTeleport", true);
        ZombieConvertFrayed = BUILDER.comment("Whether Zombies convert into Frayed when struck by lightning, Default: true")
                .define("zombieConvertFrayed", true);
        SkeletonConvertRattled = BUILDER.comment("Whether Skeletons convert into Rattled when struck by lightning, Default: true")
                .define("skeletonConvertRattled", true);
        HellfireFireImmune = BUILDER.comment("Whether Hellfire damage is halved on entities that are fire-immune, Default: true")
                .define("hellfireFireImmune", true);
        HellfireFireProtection = BUILDER.comment("Whether Hellfire damage is mitigated by Fire Protection enchantment, Default: true")
                .define("hellfireFireProtection", true);
        HostileCryptUndead = BUILDER.comment("Whether undead mobs in the Crypts and certain tagged structures remain hostile even if players wear Necro Set, Default: true")
                .define("hostileCryptUndead", true);
        HostileTerminalEnder = BUILDER.comment("Whether ender mobs in the Final Terminal and certain tagged structures remain hostile even if players wear Void Set, Default: true")
                .define("hostileTerminalEnder", true);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static void loadConfig(ForgeConfigSpec config, String path) {
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path))
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        file.load();
        config.setConfig(file);
    }
}
