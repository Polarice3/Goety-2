package com.Polarice3.Goety.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class MobsConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> RavagerRoarCooldown;

    public static final ForgeConfigSpec.ConfigValue<Integer> UndeadMinionHealCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> UndeadMinionHealTime;
    public static final ForgeConfigSpec.ConfigValue<Double> UndeadMinionHealAmount;
    public static final ForgeConfigSpec.ConfigValue<Double> ZombieServantBabyChance;

    public static final ForgeConfigSpec.ConfigValue<Integer> NaturalMinionHealCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> NaturalMinionHealTime;
    public static final ForgeConfigSpec.ConfigValue<Double> NaturalMinionHealAmount;

    public static final ForgeConfigSpec.ConfigValue<Integer> FrostMinionHealCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FrostMinionHealTime;
    public static final ForgeConfigSpec.ConfigValue<Double> FrostMinionHealAmount;

    public static final ForgeConfigSpec.ConfigValue<Integer> NetherMinionHealCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> NetherMinionHealTime;
    public static final ForgeConfigSpec.ConfigValue<Double> NetherMinionHealAmount;

    public static final ForgeConfigSpec.ConfigValue<Integer> IllagerAssaultSpawnFreq;
    public static final ForgeConfigSpec.ConfigValue<Integer> IllagerAssaultSpawnChance;
    public static final ForgeConfigSpec.ConfigValue<Integer> IllagerAssaultSEThreshold;
    public static final ForgeConfigSpec.ConfigValue<Integer> IllagerAssaultSELimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> IllagerAssaultRestDeath;
    public static final ForgeConfigSpec.ConfigValue<Integer> IllagerAssaultRestMinister;

    public static final ForgeConfigSpec.ConfigValue<Integer> VillagerHateSpells;

    public static final ForgeConfigSpec.ConfigValue<Integer> MaxSlimeSize;

    public static final ForgeConfigSpec.ConfigValue<Integer> WarlockSpawnWeight;
    public static final ForgeConfigSpec.ConfigValue<Integer> WraithSpawnWeight;

    public static final ForgeConfigSpec.ConfigValue<Boolean> ZombieServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> DrownedServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HuskServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> FrozenZombieServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> JungleZombieServantTexture;

    public static final ForgeConfigSpec.ConfigValue<Boolean> SkeletonServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> StrayServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> WitherSkeletonServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> MossySkeletonServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SunkenSkeletonServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> NecromancerServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> VanguardServantTexture;

    public static final ForgeConfigSpec.ConfigValue<Boolean> WraithServantTexture;

    public static final ForgeConfigSpec.ConfigValue<Boolean> ZPiglinServantTexture;

    public static final ForgeConfigSpec.ConfigValue<Boolean> GhastServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> BlazeServantTexture;

    public static final ForgeConfigSpec.ConfigValue<Boolean> VexTexture;

    public static final ForgeConfigSpec.ConfigValue<Boolean> SpiderServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CaveSpiderServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> BoneSpiderServantTexture;

    public static final ForgeConfigSpec.ConfigValue<Boolean> SlimeServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> MagmaCubeServantTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CryptSlimeServantTexture;

    public static final ForgeConfigSpec.ConfigValue<Boolean> QuickGrowingVineTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> PoisonQuillVineTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> WhispererTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LeapleafTexture;

    public static final ForgeConfigSpec.ConfigValue<Boolean> RedstoneGolemCrack;
    public static final ForgeConfigSpec.ConfigValue<Boolean> RedstoneGolemTexture;

    public static final ForgeConfigSpec.ConfigValue<Boolean> RedstoneMonstrosityTexture;

    public static final ForgeConfigSpec.ConfigValue<Boolean> HolidaySkins;

    public static final ForgeConfigSpec.ConfigValue<Boolean> UndeadTeleport;
    public static final ForgeConfigSpec.ConfigValue<Boolean> VexTeleport;
    public static final ForgeConfigSpec.ConfigValue<Boolean> MinionsAttackCreepers;
    public static final ForgeConfigSpec.ConfigValue<Boolean> NecroRobeUndead;
    public static final ForgeConfigSpec.ConfigValue<Boolean> VariousRobeWitch;
    public static final ForgeConfigSpec.ConfigValue<Boolean> MinionsMasterImmune;
    public static final ForgeConfigSpec.ConfigValue<Boolean> OwnerAttackCancel;
    public static final ForgeConfigSpec.ConfigValue<Boolean> MobSense;
    public static final ForgeConfigSpec.ConfigValue<Boolean> UndeadMinionHeal;
    public static final ForgeConfigSpec.ConfigValue<Boolean> NaturalMinionHeal;
    public static final ForgeConfigSpec.ConfigValue<Boolean> FrostMinionHeal;
    public static final ForgeConfigSpec.ConfigValue<Boolean> NetherMinionHeal;
    public static final ForgeConfigSpec.ConfigValue<Boolean> NecromancerSoulJar;
    public static final ForgeConfigSpec.ConfigValue<Boolean> NecromancerSummonsLife;

    public static final ForgeConfigSpec.ConfigValue<Boolean> VillagerHate;
    public static final ForgeConfigSpec.ConfigValue<Boolean> VillagerHateRavager;
    public static final ForgeConfigSpec.ConfigValue<Boolean> VillagerConvertWarlock;

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
    public static final ForgeConfigSpec.ConfigValue<Boolean> MinisterRaid;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HostileRedstoneGolemRaid;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ArmoredRavagerRaid;
    public static final ForgeConfigSpec.ConfigValue<Boolean> WarlockRaid;

    public static final ForgeConfigSpec.ConfigValue<Boolean> CryologerIceChunk;

    public static final ForgeConfigSpec.ConfigValue<Boolean> InterDimensionalMobs;
    public static final ForgeConfigSpec.ConfigValue<Boolean> TallSkullDrops;
    public static final ForgeConfigSpec.ConfigValue<Boolean> WraithAggressiveTeleport;

    public static final ForgeConfigSpec.ConfigValue<Boolean> StayingServantChunkLoad;

    public static final ForgeConfigSpec.ConfigValue<Boolean> UndeadServantSunlightBurn;
    public static final ForgeConfigSpec.ConfigValue<Boolean> UndeadServantSunlightHelmet;

    public static final ForgeConfigSpec.ConfigValue<Boolean> VizierPersistent;
    public static final ForgeConfigSpec.ConfigValue<Boolean> VizierMinion;

    public static final ForgeConfigSpec.ConfigValue<Boolean> ApocalypseMode;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ApostlePersistent;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ApostleBoilsWater;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ApostleConvertsVillagers;
    public static final ForgeConfigSpec.ConfigValue<Boolean> FancierApostleDeath;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HellfireFireImmune;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HellfireFireProtection;
    public static final ForgeConfigSpec.ConfigValue<Boolean> RedstoneGolemMold;
    public static final ForgeConfigSpec.ConfigValue<Boolean> RedstoneMonstrosityMold;
    public static final ForgeConfigSpec.ConfigValue<Boolean> RedstoneMonstrosityLeafBreak;
    public static final ForgeConfigSpec.ConfigValue<Boolean> RedstoneCubeBlockFind;

    public static final ForgeConfigSpec.ConfigValue<Boolean> HostileCryptUndead;

    static {
        BUILDER.push("Textures");
        HolidaySkins = BUILDER.comment("If certain mobs have a different texture during some holiday months, Default: true")
                .define("holidaySkins", true);
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
                NecromancerServantTexture = BUILDER.comment("If Necromancer Servants have custom textures, Default: true")
                        .define("necromancerServantTexture", true);
                VanguardServantTexture = BUILDER.comment("If Vanguard Servants have custom textures, Default: true")
                        .define("vanguardServantTexture", true);
                BUILDER.pop();
                BUILDER.push("Wraith Servants");
                WraithServantTexture = BUILDER.comment("If Wraith Servants have custom textures, Default: true")
                        .define("wraithServantTexture", true);
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
                BUILDER.pop();
                BUILDER.push("Vexes");
                VexTexture = BUILDER.comment("If Vexes have custom textures, Default: true")
                        .define("vexTexture", true);
                BUILDER.pop();
                BUILDER.push("Spider Servants");
                SpiderServantTexture = BUILDER.comment("If Spiders Servants have custom textures, Default: true")
                        .define("spiderServantTexture", true);
                CaveSpiderServantTexture = BUILDER.comment("If Cave Spiders Servants have custom textures, Default: true")
                        .define("caveSpiderServantTexture", true);
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
            UndeadTeleport = BUILDER.comment("Whether Undead Servants can teleport to Players, Default: false")
                    .define("undeadTeleport", false);
            NecroRobeUndead = BUILDER.comment("Whether Servants would naturally attack Undead mobs if owner wears a full Necro Set, Default: false")
                    .define("necroRobeUndead", false);
            UndeadMinionHeal = BUILDER.comment("Whether Undead Servants can heal if summoned while wearing Necro Cape, Default: true")
                    .define("undeadServantsHeal", true);
            UndeadServantSunlightBurn = BUILDER.comment("Whether Undead Servants burn in Sunlight if not wearing helmet, Default: true")
                    .define("undeadServantSunlightBurn", true);
            UndeadServantSunlightHelmet = BUILDER.comment("Whether Undead Servants' helmet take damage when in Sunlight. Only takes effect if undeadServantSunlightBurn is enabled, Default: true")
                    .define("undeadServantSunlightHelmet", true);
            NecromancerSoulJar = BUILDER.comment("Whether owned Necromancers drop Soul Jars, Default: true")
                    .define("necromancerSoulJar", true);
            NecromancerSummonsLife = BUILDER.comment("Whether Necromancer's summons have limited lifespans, Default: true")
                    .define("necromancerSummonsLife", true);
            UndeadMinionHealCost = BUILDER.comment("How much Soul Energy it cost per second for an Undead Servant to heal, Default: 5")
                    .defineInRange("undeadServantsHealCost", 5, 0, Integer.MAX_VALUE);
            UndeadMinionHealTime = BUILDER.comment("How frequent Undead Servants heal, count seconds, Default: 1")
                    .defineInRange("undeadServantsHealTime", 1, 0, Integer.MAX_VALUE);
            UndeadMinionHealAmount = BUILDER.comment("How much Health Undead Servants heal, numerically, Default: 0.5")
                    .defineInRange("undeadServantsHealAmount", 0.5, 0.0, Double.MAX_VALUE);
            ZombieServantBabyChance = BUILDER.comment("Chance that a zombie (or subclass) servant is summoned as a baby, Default: 0.05")
                    .defineInRange("zombieServantBabyChance", 0.05, 0.0, 1.0D);
            BUILDER.pop();
            BUILDER.push("Natural Servants");
            NaturalMinionHeal = BUILDER.comment("Whether Natural Servants can heal if summoned while wearing Wild Robe, Default: true")
                    .define("naturalMinionHeal", true);
            NaturalMinionHealCost = BUILDER.comment("How much Soul Energy it cost per second for a Natural Servant to heal, Default: 5")
                    .defineInRange("naturalMinionHealCost", 5, 0, Integer.MAX_VALUE);
            NaturalMinionHealTime = BUILDER.comment("How frequent Natural Servants heal, count seconds, Default: 1")
                    .defineInRange("naturalMinionHealTime", 1, 0, Integer.MAX_VALUE);
            NaturalMinionHealAmount = BUILDER.comment("How much Health Natural Servants heal, numerically, Default: 0.5")
                    .defineInRange("naturalMinionHealAmount", 0.5, 0.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Frost Servants");
            FrostMinionHeal = BUILDER.comment("Whether Frost Servants can heal if summoned while wearing Frost Robe, Default: true")
                    .define("frostMinionHeal", true);
            FrostMinionHealCost = BUILDER.comment("How much Soul Energy it cost per second for a Frost Servant to heal, Default: 5")
                    .defineInRange("frostMinionHealCost", 5, 0, Integer.MAX_VALUE);
            FrostMinionHealTime = BUILDER.comment("How frequent Frost Servants heal, count seconds, Default: 1")
                    .defineInRange("frostMinionHealTime", 1, 0, Integer.MAX_VALUE);
            FrostMinionHealAmount = BUILDER.comment("How much Health Frost Servants heal, numerically, Default: 0.5")
                    .defineInRange("frostMinionHealAmount", 0.5, 0.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Nether Servants");
            NetherMinionHeal = BUILDER.comment("Whether Nether Servants can heal if summoned while wearing Nether Robe, Default: true")
                    .define("netherMinionHeal", true);
            NetherMinionHealCost = BUILDER.comment("How much Soul Energy it cost per second for a Nether Servant to heal, Default: 5")
                    .defineInRange("netherMinionHealCost", 5, 0, Integer.MAX_VALUE);
            NetherMinionHealTime = BUILDER.comment("How frequent Nether Servants heal, count seconds, Default: 1")
                    .defineInRange("netherMinionHealTime", 1, 0, Integer.MAX_VALUE);
            NetherMinionHealAmount = BUILDER.comment("How much Health Nether Servants heal, numerically, Default: 0.5")
                    .defineInRange("netherMinionHealAmount", 0.5, 0.0, Double.MAX_VALUE);
            BUILDER.pop();
        RedstoneGolemMold = BUILDER.comment("Whether creating a Redstone Golem causes the mold to change blocks, Default: true")
                .define("redstoneGolemMold", true);
        RedstoneMonstrosityMold = BUILDER.comment("Whether creating a Redstone Monstrosity causes the mold to change blocks, Default: true")
                .define("redstoneMonstrosityMold", true);
        RedstoneMonstrosityLeafBreak = BUILDER.comment("Whether Redstone Monstrosity breaks leaves and certain blocks if mob griefing is enabled, Default: true")
                .define("redstoneMonstrosityLeafBreak", true);
        RedstoneCubeBlockFind = BUILDER.comment("Enable Redstone Cubes block detection mechanic, Default: true")
                .define("RedstoneCubeBlockFind", true);
        VexTeleport = BUILDER.comment("Whether Vex Servants can teleport to Players, Default: true")
                .define("vexTeleport", true);
        MinionsAttackCreepers = BUILDER.comment("Whether Servants can attack Creepers if Mob Griefing Rule is False, Default: true")
                .define("servantsAttackCreepers", true);
        MinionsMasterImmune = BUILDER.comment("Whether Servants or their owner are immune to attacks made by other servants that are summoned by the same owner, Default: true")
                .define("servantsMasterImmune", true);
        OwnerAttackCancel = BUILDER.comment("Owners can't attack or hurt their servants, Default: true")
                .define("ownerAttackCancel", true);
        MobSense = BUILDER.comment("Mobs will automatically be hostile to servants, if servant is hostile towards the mob, Default: true")
                .define("mobSense", true);
        VariousRobeWitch = BUILDER.comment("Whether Servants would naturally attack Witches or Warlocks if owner wears a full a robe that renders the former two neutral, Default: false")
                .define("variousRobeWitch", false);
        StayingServantChunkLoad = BUILDER.comment("Servants, when on staying mode, will load chunks around it, Default: true")
                .define("stayingServantChunkLoad", true);
        MaxSlimeSize = BUILDER.comment("Maximum size Slime and Magma Cube Servants can obtain through Slime/Magma Block, Default: 4")
                .defineInRange("maxSlimeSize", 4, 1, 127);
        RavagerRoarCooldown = BUILDER.comment("How many seconds it takes before Ravager can manually roar again, Default: 10")
                .defineInRange("ravagerRoarCooldown", 10, 0, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Illagers");
            BUILDER.push("Illager Assaults");
            IllagerAssault = BUILDER.comment("Modded Illagers Spawning based of Player's Soul Energy amount, Default: true")
                    .define("illagerAssault", true);
            IllagerAssaultSpawnFreq = BUILDER.comment("How many ticks it takes for Illager Assaults to spawn, Default: 12000")
                    .defineInRange("illagerAssaultSpawnFreq", 12000, 0, Integer.MAX_VALUE);
            IllagerAssaultSpawnChance = BUILDER.comment("Spawn Chance for Illagers Hunting the Player every Spawn Frequency, the lower the more likelier, Default: 5")
                    .defineInRange("illagerAssaultSpawnChance", 5, 0, Integer.MAX_VALUE);
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
            PikerRaid = BUILDER.comment("Whether Pikers appear in Raids, Default: true")
                    .define("pikerRaid", true);
            RipperRaid = BUILDER.comment("Whether Rippers appear in Raids. They do not count as part of the Raid bar, Default: true")
                    .define("ripperRaid", true);
            CrusherRaid = BUILDER.comment("Whether Crushers appear in Raids, Default: true")
                    .define("crusherRaid", true);
            StormCasterRaid = BUILDER.comment("Whether Storm Casters appear in Raids, Default: true")
                    .define("stormCasterRaid", true);
            CryologerRaid = BUILDER.comment("Whether Cryologers appear in Raids, Default: true")
                    .define("cryologerRaid", true);
            PreacherRaid = BUILDER.comment("Whether Preachers appear in Raids, Default: true")
                    .define("preacherRaid", true);
            ConquillagerRaid = BUILDER.comment("Whether Conquillagers appear in Raids, Default: true")
                    .define("conquillagerRaid", true);
            InquillagerRaid = BUILDER.comment("Whether Inquillagers appear in Raids, Default: true")
                    .define("inquillagerRaid", true);
            EnviokerRaid = BUILDER.comment("Whether Enviokers appear in Raids, Default: true")
                    .define("enviokerRaid", true);
            MinisterRaid = BUILDER.comment("Whether Ministers appear in Raids, Default: true")
                    .define("ministerRaid", true);
            HostileRedstoneGolemRaid = BUILDER.comment("Whether Hostile Redstone Golems appear in Raids, Default: false")
                    .define("hostileRedstoneGolemRaid", false);
            ArmoredRavagerRaid = BUILDER.comment("Whether Armored Ravagers spawn in Raids, Default: true")
                    .define("armoredRavagerRaid", true);
            WarlockRaid = BUILDER.comment("Whether Warlocks appear in Raids, Default: true")
                    .define("warlockRaid", true);
            BUILDER.pop();
        CryologerIceChunk = BUILDER.comment("Whether Cryologers can summon Ice Chunks on Hard Difficulty, Default: false")
                .define("cryologerIceChunk", false);
        IllagueSpread = BUILDER.comment("Whether Illague Effect can spread from non Conquillagers that has the effect, Default: true")
                .define("illagueSpread", true);
        IllagerSteal = BUILDER.comment("Whether Enviokers, Inquillagers and Conquillagers can steal Totems of Souls or Totems of Undying, Default: true")
                .define("illagerSteal", true);
        BUILDER.pop();
        BUILDER.push("Villagers");
        VillagerHate = BUILDER.comment("Wearing a Dark Robe, along with variants, causes Villagers around the Player to have a negative Reputation unless said Player has 25 or more reputation among them, Default: false")
                .define("villagerHate", false);
        VillagerHateRavager = BUILDER.comment("Having an owned Ravaged or Ravager, causes Villagers around the Player to have a negative Reputation, Default: false")
                .define("villagerHateRavager", false);
        VillagerHateSpells = BUILDER.comment("Casting Spell in the presence of Villagers will cause the Player to lose a number of Reputation, set 0 to disable, Default: 0")
                .defineInRange("villagerHateSpells", 0, 0, Integer.MAX_VALUE);
        VillagerConvertWarlock = BUILDER.comment("Villagers have a chance of converting into Warlocks if they're underneath a Block of Crying Obsidian, Default: true")
                .define("villagerConvertToWarlock", true);
        BUILDER.pop();
        BUILDER.push("Misc");
            BUILDER.push("Apostle");
            ApocalypseMode = BUILDER.comment("Nether Meteors deals environmental damage. WARNING: Causes lots of lag. Default: false")
                    .define("apocalypseMode", false);
            ApostlePersistent = BUILDER.comment("Whether Apostles are persistent and do not naturally despawn. Default: false")
                    .define("apostlePersistent", true);
            ApostleBoilsWater = BUILDER.comment("Whether Apostles causes entities within 32 blocks of themselves to take damage when in water. Default: true")
                    .define("apostleBoilsWater", true);
            ApostleConvertsVillagers = BUILDER.comment("Whether Apostles causes Villagers within 32 blocks of themselves to have a chance of converting into a Witch or Warlock. Default: true")
                    .define("apostleConvertsVillagers", true);
            FancierApostleDeath = BUILDER.comment("Gives Apostle an even more fancier death animation, Default: false")
                    .define("fancierApostleDeath", false);
            BUILDER.pop();
            BUILDER.push("Vizier");
            VizierPersistent = BUILDER.comment("Whether Viziers are persistent and do not naturally despawn. Default: false")
                    .define("vizierPersistent", false);
            VizierMinion = BUILDER.comment("Viziers spawn Vexes instead of Irks, Default: false")
                    .define("vizierMinion", false);
            BUILDER.pop();
        InterDimensionalMobs = BUILDER.comment("Whether Goety Mobs can spawn in Overworld-like modded dimensions, Default: false")
                .define("interDimensionalMobs", false);
        WarlockSpawnWeight = BUILDER.comment("Spawn Weight for Warlock, Default: 5")
                .defineInRange("warlockSpawnWeight", 5, 0, Integer.MAX_VALUE);
        WraithSpawnWeight = BUILDER.comment("Spawn Weight for Wraith, Default: 20")
                .defineInRange("wraithSpawnWeight", 20, 0, Integer.MAX_VALUE);
        TallSkullDrops = BUILDER.comment("Whether Mobs with Tall Heads(ie. Villagers, Illagers, etc.) will drop Tall Skulls, Default: true")
                .define("tallSkullDrop", true);
        WraithAggressiveTeleport = BUILDER.comment("Whether Wraiths should teleport towards their targets if they can't see them instead of just teleporting away when they're near them, Default: true")
                .define("wraithAggressiveTeleport", true);
        HellfireFireImmune = BUILDER.comment("Whether Hellfire damage is halved on entities that are fire-immune, Default: true")
                .define("hellfireFireImmune", true);
        HellfireFireProtection = BUILDER.comment("Whether Hellfire damage is mitigated by Fire Protection enchantment, Default: true")
                .define("hellfireFireProtection", true);
        HostileCryptUndead = BUILDER.comment("Whether undead mobs in the Crypts remain hostile even if players wear Necro Set, Default: true")
                .define("hostileCryptUndead", true);
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
