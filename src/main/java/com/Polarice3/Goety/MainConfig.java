package com.Polarice3.Goety;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

/**
 * Learned how to add Config from codes by @AlexModGuy
 */
public class MainConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> MaxSouls;
    public static final ForgeConfigSpec.ConfigValue<Integer> MaxArcaSouls;
    public static final ForgeConfigSpec.ConfigValue<Integer> SoulGuiHorizontal;
    public static final ForgeConfigSpec.ConfigValue<Integer> SoulGuiVertical;

    public static final ForgeConfigSpec.ConfigValue<Integer> UndeadSouls;
    public static final ForgeConfigSpec.ConfigValue<Integer> AnthropodSouls;
    public static final ForgeConfigSpec.ConfigValue<Integer> IllagerSouls;
    public static final ForgeConfigSpec.ConfigValue<Integer> VillagerSouls;
    public static final ForgeConfigSpec.ConfigValue<Integer> PiglinSouls;
    public static final ForgeConfigSpec.ConfigValue<Integer> EnderDragonSouls;
    public static final ForgeConfigSpec.ConfigValue<Integer> WardenSouls;
    public static final ForgeConfigSpec.ConfigValue<Integer> PlayerSouls;
    public static final ForgeConfigSpec.ConfigValue<Integer> DefaultSouls;

    public static final ForgeConfigSpec.ConfigValue<Integer> IllagerAssaultSpawnFreq;
    public static final ForgeConfigSpec.ConfigValue<Integer> IllagerAssaultSpawnChance;
    public static final ForgeConfigSpec.ConfigValue<Integer> IllagerAssaultSEThreshold;
    public static final ForgeConfigSpec.ConfigValue<Integer> IllagerAssaultSELimit;

    public static final ForgeConfigSpec.ConfigValue<Integer> CraftingSouls;
    public static final ForgeConfigSpec.ConfigValue<Integer> DarkScytheSouls;
    public static final ForgeConfigSpec.ConfigValue<Integer> PendantOfHungerLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> SeaAmuletChargeConsume;
    public static final ForgeConfigSpec.ConfigValue<Integer> SeaAmuletMax;
    public static final ForgeConfigSpec.ConfigValue<Integer> WindRobeSouls;
    public static final ForgeConfigSpec.ConfigValue<Integer> ItemsRepairAmount;

    public static final ForgeConfigSpec.ConfigValue<Integer> VillagerHateSpells;
    public static final ForgeConfigSpec.ConfigValue<Integer> LichHealCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> DarkAnvilRepairCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> DarkAnvilSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SoulMenderCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SculkGrowerCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SculkGrowerCharge;
    public static final ForgeConfigSpec.ConfigValue<Double> SoulMenderSeconds;

    public static final ForgeConfigSpec.ConfigValue<Integer> WarlockSpawnWeight;
    public static final ForgeConfigSpec.ConfigValue<Integer> WraithSpawnWeight;

    public static final ForgeConfigSpec.ConfigValue<Double> NecroStaffDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> WindStaffDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> NamelessStaffDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ScytheBaseDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ScytheAttackSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> DarkToolsDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> DarkToolsBreakSpeed;
    public static final ForgeConfigSpec.ConfigValue<Integer> DarkToolsDurability;
    public static final ForgeConfigSpec.ConfigValue<Integer> DarkToolsEnchantability;
    public static final ForgeConfigSpec.ConfigValue<Integer> DarkToolsMiningLevel;
    public static final ForgeConfigSpec.ConfigValue<Double> DeathScytheDamage;
    public static final ForgeConfigSpec.ConfigValue<Integer> DeathScytheDurability;
    public static final ForgeConfigSpec.ConfigValue<Integer> DeathScytheEnchantability;

    public static final ForgeConfigSpec.ConfigValue<Double> SpecialToolsDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> SpecialToolsBreakSpeed;
    public static final ForgeConfigSpec.ConfigValue<Integer> SpecialToolsDurability;
    public static final ForgeConfigSpec.ConfigValue<Integer> SpecialToolsMiningLevel;
    public static final ForgeConfigSpec.ConfigValue<Integer> HuntersBowDurability;
    public static final ForgeConfigSpec.ConfigValue<Integer> SpecialToolsEnchantability;

    public static final ForgeConfigSpec.ConfigValue<Double> PhilosophersMaceDamage;
    public static final ForgeConfigSpec.ConfigValue<Integer> PhilosophersMaceDurability;

    public static final ForgeConfigSpec.ConfigValue<Boolean> SpecialBossBar;
    public static final ForgeConfigSpec.ConfigValue<Boolean> BossMusic;
    public static final ForgeConfigSpec.ConfigValue<Boolean> WindRobeCape;

    public static final ForgeConfigSpec.ConfigValue<Boolean> SoulRepair;
    public static final ForgeConfigSpec.ConfigValue<Boolean> TotemUndying;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ArcaUndying;
    public static final ForgeConfigSpec.ConfigValue<Boolean> StarterTotem;
    public static final ForgeConfigSpec.ConfigValue<Boolean> StarterBook;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SoulGuiShow;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ShowNum;
    public static final ForgeConfigSpec.ConfigValue<Boolean> FirstPersonGloves;

    public static final ForgeConfigSpec.ConfigValue<Boolean> DarkAnvilNoCap;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SculkGrowerContinue;

    public static final ForgeConfigSpec.ConfigValue<Boolean> IllagerAssault;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SoulEnergyBadOmen;
    public static final ForgeConfigSpec.ConfigValue<Boolean> IllagueSpread;
    public static final ForgeConfigSpec.ConfigValue<Boolean> IllagerSteal;
    public static final ForgeConfigSpec.ConfigValue<Boolean> IllagerRaid;

    public static final ForgeConfigSpec.ConfigValue<Boolean> VillagerHate;
    public static final ForgeConfigSpec.ConfigValue<Boolean> VillagerConvertWarlock;
    public static final ForgeConfigSpec.ConfigValue<Boolean> TallSkullDrops;
    public static final ForgeConfigSpec.ConfigValue<Boolean> WraithAggressiveTeleport;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ApocalypseMode;

    public static final ForgeConfigSpec.ConfigValue<Boolean> ScytheSlashBreaks;

    public static final ForgeConfigSpec.ConfigValue<Boolean> VizierMinion;
    public static final ForgeConfigSpec.ConfigValue<Boolean> InterDimensionalMobs;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LichNightVision;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LichDamageHelmet;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LichUndeadFriends;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LichMagicResist;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LichPowerfulFoes;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LichScrollRequirement;

    public static final ForgeConfigSpec.ConfigValue<Boolean> FancierApostleDeath;

    static {
        BUILDER.push("General");
        MaxSouls = BUILDER.comment("Totem Maximum Soul Count and Threshold to save the Player, Default: 10000")
                .defineInRange("maxSouls", 10000, 10, Integer.MAX_VALUE);
        MaxArcaSouls = BUILDER.comment("Arca Maximum Soul Count, Default: 100000")
                .defineInRange("maxArcaSouls", 100000, 10, Integer.MAX_VALUE);
        SoulRepair = BUILDER.comment("Certain Items repair themselves using Soul Energy, Default: true")
                .define("soulRepair", true);
        TotemUndying = BUILDER.comment("Totem of Souls will save the Player if full of Soul Energy and its in Curio Slot or inventory, Default: true")
                .define("totemUndying", true);
        ArcaUndying = BUILDER.comment("Arca will save the Player if past Totem Maximum Soul Count, Default: true")
                .define("arcaUndying", true);
        StarterTotem = BUILDER.comment("Gives Players a Totem of Souls when first entering World, Default: false")
                .define("starterTotem", false);
        StarterBook = BUILDER.comment("Gives Players the Black Book when first entering World and Patchouli is loaded, Default: false")
                .define("starterBook", false);
        CraftingSouls = BUILDER.comment("How much Souls is consumed when crafting with Totem, Default: 1")
                .defineInRange("craftSouls", 1, 0, Integer.MAX_VALUE);
        SoulGuiShow = BUILDER.comment("Show the Soul Energy Bar if Player has Totem of Souls/Arca, Default: true")
                .define("soulGuiShow", true);
        ShowNum = BUILDER.comment("Show numerical amount of Souls on the Soul Energy Bar, Default: false")
                .define("showNumber", false);
        SoulGuiHorizontal = BUILDER.comment("Horizontal Position of where the Soul Energy Bar is located, Default: 100")
                .defineInRange("soulGuiHorizontal", 100, -Integer.MAX_VALUE, Integer.MAX_VALUE);
        SoulGuiVertical = BUILDER.comment("Vertical Position of where the Soul Energy Bar is located, Default: -5")
                .defineInRange("soulGuiVertical", -5, -Integer.MAX_VALUE, Integer.MAX_VALUE);
        FirstPersonGloves = BUILDER.comment("Show gloves in first person, Default: true")
                .define("firstPersonGloves", true);
        ApocalypseMode = BUILDER.comment("Nether Meteors deals environmental damage. WARNING: Causes lots of lag. Default: false")
                .define("apocalypseMode", false);
        SpecialBossBar = BUILDER.comment("Bosses from the Mod has custom looking Boss Bars. Default: true")
                .define("specialBossBar", true);
        BossMusic = BUILDER.comment("Bosses from the Mod has custom Music Playing. Default: true")
                .define("bossMusic", true);
        BUILDER.pop();
        BUILDER.push("Blocks");
        DarkAnvilRepairCost = BUILDER.comment("Maximum level of experience the Dark Anvil will stick to instead of capping if No Cap is enabled, or the level cap if disabled, Default: 30")
                .defineInRange("darkAnvilRepairCost", 30, 1, Integer.MAX_VALUE);
        DarkAnvilNoCap = BUILDER.comment("Whether Dark Anvils have a cap that prevents items from being repaired or enchanted if repair cost is exceeded, Default: true")
                .define("darkAnvilNoCap", true);
        DarkAnvilSoulCost = BUILDER.comment("How much Soul Energy is required for the Dark Anvil to repair itself, Default: 1000")
                .defineInRange("darkAnvilSoulCost", 1000, 0, Integer.MAX_VALUE);
        SoulMenderCost = BUILDER.comment("The amount of Soul Energy used up to repair Items per the configured amount of seconds, Default: 1")
                .defineInRange("soulMenderCost", 1, 0, Integer.MAX_VALUE);
        SoulMenderSeconds = BUILDER.comment("How many seconds the Soul Menderer repairs 1 durability from an item, Default: 0.5")
                .defineInRange("soulMenderSeconds", 0.5, 0.0, Double.MAX_VALUE);
        SculkGrowerCost = BUILDER.comment("The amount of Soul Energy used to give Charges to the Sculk Grower, Default: 20")
                .defineInRange("sculkGrowerCost", 20, 0, Integer.MAX_VALUE);
        SculkGrowerCharge = BUILDER.comment("How much Charges is given to the Sculk Grower whenever it absorbs Soul Energy, Default: 100")
                .defineInRange("sculkGrowerCharge", 100, 0, Integer.MAX_VALUE);
        SculkGrowerContinue = BUILDER.comment("Whether Sculk Grower continues to grow plants without Redstone if it still contains charges. Setting it to false will cause the Sculk Grower to slowly decay its charges, Default: true")
                .define("sculkGrowerContinue", true);
        BUILDER.pop();
        BUILDER.push("Soul Taken");
        UndeadSouls = BUILDER.comment("Undead Killed, Default: 5")
                .defineInRange("undeadSouls", 5, 0, Integer.MAX_VALUE);
        AnthropodSouls = BUILDER.comment("Anthropods Killed, Default: 5")
                .defineInRange("anthropodSouls", 5, 0, Integer.MAX_VALUE);
        IllagerSouls = BUILDER.comment("Illagers, Witches, Cultists Killed, Default: 25")
                .defineInRange("illagerSouls", 25, 0, Integer.MAX_VALUE);
        VillagerSouls = BUILDER.comment("Villagers Killed, Default: 100")
                .defineInRange("villagerSouls", 100, 0, Integer.MAX_VALUE);
        PiglinSouls = BUILDER.comment("Non-Undead Piglin Killed, Default: 10")
                .defineInRange("piglinSouls", 10, 0, Integer.MAX_VALUE);
        EnderDragonSouls = BUILDER.comment("Ender Dragon Killed, Default: 1000")
                .defineInRange("enderDragonSouls", 1000, 0, Integer.MAX_VALUE);
        WardenSouls = BUILDER.comment("Warden Killed, Default: 10000")
                .defineInRange("wardenSouls", 10000, 0, Integer.MAX_VALUE);
        PlayerSouls = BUILDER.comment("Players Killed, Default: 100")
                .defineInRange("playerSouls", 100, 0, Integer.MAX_VALUE);
        DefaultSouls = BUILDER.comment("Others Killed, Default: 5")
                .defineInRange("otherSouls", 5, 0, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Mobs");
        VizierMinion = BUILDER.comment("Viziers spawn Vexes instead of Irks, Default: false")
                .define("vizierMinion", false);
        FancierApostleDeath = BUILDER.comment("Gives Apostle an even more fancier death animation, Default: false")
                .define("fancierApostleDeath", false);
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
        BUILDER.pop();
        BUILDER.push("Items");
        DarkScytheSouls = BUILDER.comment("Amount of Soul Energy Dark Scythe gives when hitting mob(s), Default: 1")
                .defineInRange("darkScytheSouls", 1, 1, Integer.MAX_VALUE);
        ScytheSlashBreaks = BUILDER.comment("Scythe Slashes from Death Scythe breaks blocks that regular Scythes easily breaks, Default: true")
                .define("scytheSlashBreaks", true);
        ItemsRepairAmount = BUILDER.comment("Amount of Souls needed to repair certain Equipments per second, Default: 5")
                .defineInRange("itemsRepairSouls", 5, 1, Integer.MAX_VALUE);
        PendantOfHungerLimit = BUILDER.comment("The total amount of Rotten Flesh a Pendant of Hunger can hold, Default: 256")
                .defineInRange("pendantOfHungerLimit", 256, 1, Integer.MAX_VALUE);
        SeaAmuletChargeConsume = BUILDER.comment("How much Charges the Sea Amulet needs to consume to function, Default: 1")
                .defineInRange("seaAmuletChargeConsume", 1, 1, Integer.MAX_VALUE);
        SeaAmuletMax = BUILDER.comment("The total amount of Charges a Sea Amulet can hold, Default: 256")
                .defineInRange("seaAmuletMax", 256, 1, Integer.MAX_VALUE);
        WindRobeSouls = BUILDER.comment("How much Soul Energy is taken per second when wearer is falling slowly, Default: 1")
                .defineInRange("windRobeSouls", 1, 1, Integer.MAX_VALUE);
        WindRobeCape = BUILDER.comment("Render Wind Robe Cape, Default: true")
                .define("windRobeCape", true);
        BUILDER.pop();
        BUILDER.push("Tools & Weapons");
        NecroStaffDamage = BUILDER.comment("How much base damage Necro Staffs deals, Default: 4.0")
                .defineInRange("necroStaffDamage", 4.0, 1.0, Double.MAX_VALUE);
        WindStaffDamage = BUILDER.comment("How much base damage Wind Staffs deals, Default: 4.0")
                .defineInRange("windStaffDamage", 4.0, 1.0, Double.MAX_VALUE);
        NamelessStaffDamage = BUILDER.comment("How much base damage Nameless Staffs deals, Default: 6.0")
                .defineInRange("namelessStaffDamage", 6.0, 1.0, Double.MAX_VALUE);
        ScytheBaseDamage = BUILDER.comment("How much base damage Scythes deals, the damage added depends on material the scythe is made off (ie. Iron = 2.0), Default: 4.5")
                .defineInRange("scytheBaseDamage", 4.5, 1.0, Double.MAX_VALUE);
        ScytheAttackSpeed = BUILDER.comment("How fast it takes to fully swing a Scythe with item offhand and not wearing Grave Gloves. The lower the number the slower it takes to recharge, Default: 0.6")
                .defineInRange("scytheAttackSpeed", 0.6, 0.0, Double.MAX_VALUE);
        DarkToolsDamage = BUILDER.comment("How much damage Dark Tools deals, the configured number is added to Tool Base Damage, Default: 3.0")
                .defineInRange("darkToolsDamage", 3.0, 1.0, Double.MAX_VALUE);
        DarkToolsBreakSpeed = BUILDER.comment("How fast Dark Tools mines, the higher the better, Default: 8.0")
                .defineInRange("darkToolsBreakSpeed", 8.0, 1.0, Double.MAX_VALUE);
        DarkToolsDurability = BUILDER.comment("How many uses before Dark Tools breaks, Default: 166")
                .defineInRange("darkToolsDurability", 166, 1, Integer.MAX_VALUE);
        DarkToolsEnchantability = BUILDER.comment("Define the Enchantability for Dark Tools, higher number the better, Default: 20")
                .defineInRange("darkToolsEnchantability", 20, 1, Integer.MAX_VALUE);
        DarkToolsMiningLevel = BUILDER.comment("Define the Mining Level for Dark Tools, example, 3 = Diamond, 4 = Netherite, Default: 4")
                .defineInRange("darkToolsMiningLevel", 4, 0, Integer.MAX_VALUE);
        DeathScytheDamage = BUILDER.comment("How much damage Death Scythe deals, the configured number is added to Scythe Base Damage, Default: 4.0")
                .defineInRange("deathScytheDamage", 4.0, 1.0, Double.MAX_VALUE);
        DeathScytheDurability = BUILDER.comment("How many uses before Death Scythe breaks, Default: 444")
                .defineInRange("deathScytheDurability", 444, 1, Integer.MAX_VALUE);
        DeathScytheEnchantability = BUILDER.comment("Define the Enchantability for Death Scythe, higher number the better, Default: 22")
                .defineInRange("deathScytheEnchantability", 22, 1, Integer.MAX_VALUE);
        SpecialToolsDamage = BUILDER.comment("How much damage Special Tools deals, the configured number is added to Tool Base Damage, Default: 3.0")
                .defineInRange("specialToolsDamage", 3.0, 1.0, Double.MAX_VALUE);
        SpecialToolsBreakSpeed = BUILDER.comment("How fast Special Tools mines, the higher the better, Default: 8.0")
                .defineInRange("specialToolsBreakSpeed", 8.0, 1.0, Double.MAX_VALUE);
        SpecialToolsDurability = BUILDER.comment("How many uses before a Special Tool breaks, Default: 1280")
                .defineInRange("specialToolsDurability", 1280, 1, Integer.MAX_VALUE);
        HuntersBowDurability = BUILDER.comment("How many uses before a Hunter's Bow breaks, Default: 133")
                .defineInRange("huntersBowDurability", 133, 1, Integer.MAX_VALUE);
        SpecialToolsEnchantability = BUILDER.comment("Define the Enchantability for Special Tools, higher number the better, Default: 22")
                .defineInRange("specialToolsEnchantability", 22, 1, Integer.MAX_VALUE);
        SpecialToolsMiningLevel = BUILDER.comment("Define the Mining Level for Special Tools, example, 3 = Diamond, 4 = Netherite, Default: 3")
                .defineInRange("specialToolsMiningLevel", 3, 0, Integer.MAX_VALUE);
        PhilosophersMaceDamage = BUILDER.comment("How much damage Philosopher's Mace deals, Default: 9.0")
                .defineInRange("philosophersMaceDamage", 9.0, 1.0, Double.MAX_VALUE);
        PhilosophersMaceDurability = BUILDER.comment("How many uses before the Philosopher's Mace breaks, Default: 128")
                .defineInRange("philosophersMaceDurability", 128, 1, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Villagers");
        VillagerHate = BUILDER.comment("Wearing a Dark Helm and Robe, along with variants, causes Villagers around the Player to have a negative Reputation unless said Player has 100 or more reputation among them, Default: false")
                .define("villagerHate", false);
        VillagerHateSpells = BUILDER.comment("Casting Spell in the presence of Villagers will cause the Player to lose a number of Reputation, set 0 to disable, Default: 0")
                .defineInRange("villagerHateSpells", 0, 0, Integer.MAX_VALUE);
        VillagerConvertWarlock = BUILDER.comment("Villagers have a chance of converting into Warlocks if they're underneath a Block of Crying Obsidian, Default: true")
                .define("villagerConvertToWarlock", true);
        BUILDER.pop();
        BUILDER.push("Lich");
        LichHealCost = BUILDER.comment("How much Soul Energy is cost to heal the Player per second if they've become a Lich, Default: 1")
                .defineInRange("lichHealCost", 1, 0, Integer.MAX_VALUE);
        LichNightVision = BUILDER.comment("Enable to get infinite Night Vision when being a Lich. If set true, wearing Fel Helm will no longer give Blindness during day, Default: true")
                .define("lichNightVision", true);
        LichDamageHelmet = BUILDER.comment("Wearing Helmet in Sunlight as a Lich periodically damages it, Default: true")
                .define("lichDamageHelmet", true);
        LichUndeadFriends = BUILDER.comment("Undead Mobs will not attack you if you're a Lich and will even defend you if you're attack by another mob and wearing the Necro Set, Default: true")
                .define("lichUndeadFriendly", true);
        LichMagicResist = BUILDER.comment("Enable to make Liches 85% more resistant to Magic Attacks, Default: false")
                .define("lichMagicResist", false);
        LichPowerfulFoes = BUILDER.comment("If Lich Undead Friendly is set to true, Only undead that have lower than 50 Hearts are friendly, Default: true")
                .define("lichPowerfulHostile", true);
        LichScrollRequirement = BUILDER.comment("Whether the player needs to read a Forbidden Scroll to start the Potion of Transformation ritual, Default: true")
                .define("lichScrollRequirement", true);
        BUILDER.pop();
        BUILDER.push("Illagers");
        IllagerAssault = BUILDER.comment("Special Illagers Spawning based of Player's Soul Energy amount, Default: true")
                .define("illagerAssault", true);
        IllagerAssaultSpawnFreq = BUILDER.comment("Spawn Frequency for Illagers Hunting the Player, Default: 12000")
                .defineInRange("illagerAssaultSpawnFreq", 12000, 0, Integer.MAX_VALUE);
        IllagerAssaultSpawnChance = BUILDER.comment("Spawn Chance for Illagers Hunting the Player every Infamy Spawn Frequency, the lower the more likelier, Default: 5")
                .defineInRange("illagerAssaultSpawnChance", 5, 0, Integer.MAX_VALUE);
        IllagerAssaultSEThreshold = BUILDER.comment("How much Soul Energy the Player has is required for Special Illagers to spawn, Default: 2500")
                .defineInRange("illagerAssaultThreshold", 2500, 0, Integer.MAX_VALUE);
        IllagerAssaultSELimit = BUILDER.comment("The maximum amount of Soul Energy the Player has that is taken consideration for the Assaults, Default: 30000")
                .defineInRange("illagerAssaultLimit", 30000, 0, Integer.MAX_VALUE);
        SoulEnergyBadOmen = BUILDER.comment("Hitting the Illager Assault Limit of Soul Energy have a chance of giving Player Bad Omen effect, Default: true")
                .define("soulEnergyBadOmen", true);
        IllagueSpread = BUILDER.comment("Whether Illague Effect can spread from non Conquillagers that has the effect, Default: true")
                .define("illagueSpread", true);
        IllagerSteal = BUILDER.comment("Whether Enviokers, Inquillagers and Conquillagers can steal Totems of Souls or Totems of Undying, Default: true")
                .define("illagerSteal", true);
        IllagerRaid = BUILDER.comment("Whether Enviokers, Inquillagers and Conquillagers can join Raids, Default: true")
                .define("specialIllagerRaid", true);
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
