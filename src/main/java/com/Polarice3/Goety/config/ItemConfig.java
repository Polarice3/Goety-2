package com.Polarice3.Goety.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class ItemConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> CraftingSouls;
    public static final ForgeConfigSpec.ConfigValue<Integer> DarkScytheSouls;
    public static final ForgeConfigSpec.ConfigValue<Integer> PendantOfHungerLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> SeaAmuletChargeConsume;
    public static final ForgeConfigSpec.ConfigValue<Integer> SeaAmuletMax;
    public static final ForgeConfigSpec.ConfigValue<Integer> WindRobeSouls;
    public static final ForgeConfigSpec.ConfigValue<Integer> ItemsRepairAmount;
    public static final ForgeConfigSpec.ConfigValue<Integer> SpitefulBeltUseAmount;

    public static final ForgeConfigSpec.ConfigValue<Integer> WitchRobeResistance;
    public static final ForgeConfigSpec.ConfigValue<Integer> WarlockRobeResistance;
    public static final ForgeConfigSpec.ConfigValue<Integer> FrostRobeResistance;
    public static final ForgeConfigSpec.ConfigValue<Integer> StormRobeResistance;

    public static final ForgeConfigSpec.ConfigValue<Double> OminousStaffDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> NecroStaffDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> WindStaffDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> StormStaffDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> FrostStaffDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> WildStaffDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> NamelessStaffDamage;

    public static final ForgeConfigSpec.ConfigValue<Double> ScytheBaseDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ScytheAttackSpeed;

    public static final ForgeConfigSpec.ConfigValue<Double> HammerBaseDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> HammerAttackSpeed;

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
    public static final ForgeConfigSpec.ConfigValue<Integer> PhilosophersMaceEnchantability;

    public static final ForgeConfigSpec.ConfigValue<Integer> CursedKnightDurability;
    public static final ForgeConfigSpec.ConfigValue<Integer> CursedKnightFeet;
    public static final ForgeConfigSpec.ConfigValue<Integer> CursedKnightLegs;
    public static final ForgeConfigSpec.ConfigValue<Integer> CursedKnightChest;
    public static final ForgeConfigSpec.ConfigValue<Integer> CursedKnightHead;
    public static final ForgeConfigSpec.ConfigValue<Integer> CursedKnightEnchantability;
    public static final ForgeConfigSpec.ConfigValue<Double> CursedKnightToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> CursedKnightKnockResist;

    public static final ForgeConfigSpec.ConfigValue<Integer> CursedPaladinDurability;
    public static final ForgeConfigSpec.ConfigValue<Integer> CursedPaladinFeet;
    public static final ForgeConfigSpec.ConfigValue<Integer> CursedPaladinLegs;
    public static final ForgeConfigSpec.ConfigValue<Integer> CursedPaladinChest;
    public static final ForgeConfigSpec.ConfigValue<Integer> CursedPaladinHead;
    public static final ForgeConfigSpec.ConfigValue<Integer> CursedPaladinEnchantability;
    public static final ForgeConfigSpec.ConfigValue<Double> CursedPaladinToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> CursedPaladinKnockResist;

    public static final ForgeConfigSpec.ConfigValue<Integer> BlackIronDurability;
    public static final ForgeConfigSpec.ConfigValue<Integer> BlackIronFeet;
    public static final ForgeConfigSpec.ConfigValue<Integer> BlackIronLegs;
    public static final ForgeConfigSpec.ConfigValue<Integer> BlackIronChest;
    public static final ForgeConfigSpec.ConfigValue<Integer> BlackIronHead;
    public static final ForgeConfigSpec.ConfigValue<Integer> BlackIronEnchantability;
    public static final ForgeConfigSpec.ConfigValue<Double> BlackIronToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> BlackIronKnockResist;

    public static final ForgeConfigSpec.ConfigValue<Integer> DarkArmorDurability;
    public static final ForgeConfigSpec.ConfigValue<Integer> DarkArmorFeet;
    public static final ForgeConfigSpec.ConfigValue<Integer> DarkArmorLegs;
    public static final ForgeConfigSpec.ConfigValue<Integer> DarkArmorChest;
    public static final ForgeConfigSpec.ConfigValue<Integer> DarkArmorHead;
    public static final ForgeConfigSpec.ConfigValue<Integer> DarkArmorEnchantability;
    public static final ForgeConfigSpec.ConfigValue<Double> DarkArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> DarkArmorKnockResist;

    public static final ForgeConfigSpec.ConfigValue<Boolean> RobeCape;
    public static final ForgeConfigSpec.ConfigValue<Boolean> NecroSetUndeadNeutral;
    public static final ForgeConfigSpec.ConfigValue<Boolean> NamelessSetUndeadNeutral;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SoulRepair;
    public static final ForgeConfigSpec.ConfigValue<Boolean> FirstPersonGloves;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ShowRobeHoods;
    public static final ForgeConfigSpec.ConfigValue<Boolean> FireSpawnCage;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ScytheSlashBreaks;

    static {
        BUILDER.push("General");
        SoulRepair = BUILDER.comment("Certain Items repair themselves using Soul Energy, Default: true")
                .define("soulRepair", true);
        CraftingSouls = BUILDER.comment("How much Souls is consumed when crafting with Totem, Default: 1")
                .defineInRange("craftSouls", 1, 0, Integer.MAX_VALUE);
        ItemsRepairAmount = BUILDER.comment("Amount of Souls needed to repair certain Equipments per second, Default: 5")
                .defineInRange("itemsRepairSouls", 5, 1, Integer.MAX_VALUE);
        FireSpawnCage = BUILDER.comment("Fire Spawn Cage are enabled, Default: true")
                .define("fireSpawnCage", true);
        BUILDER.pop();
        BUILDER.push("Curios");
            BUILDER.push("Robes");
            ShowRobeHoods = BUILDER.comment("Show Hoods when wearing certain robes ie, Illusive Robes, Default: true")
                    .define("showRobeHoods", true);
            RobeCape = BUILDER.comment("Render Capes on certain Robes, Default: true")
                    .define("windRobeCape", true);
            WindRobeSouls = BUILDER.comment("How much Soul Energy is taken per second when wearer is falling slowly, Default: 1")
                    .defineInRange("windRobeSouls", 1, 1, Integer.MAX_VALUE);
            WitchRobeResistance = BUILDER.comment("How much magic resistance Witches Robes provides by percent, Default: 85")
                    .defineInRange("witchRobeResistance", 85, 0, 100);
            WarlockRobeResistance = BUILDER.comment("How much explosive resistance Warlock Robes provides by percent, Default: 85")
                    .defineInRange("warlockRobeResistance", 85, 0, 100);
            FrostRobeResistance = BUILDER.comment("How much frost resistance Frost Robes provides by percent, Default: 85")
                    .defineInRange("frostRobeResistance", 85, 0, 100);
            StormRobeResistance = BUILDER.comment("How much shock resistance Storm Robes provides by percent, Default: 85")
                    .defineInRange("stormRobeResistance", 85, 0, 100);
            NecroSetUndeadNeutral = BUILDER.comment("Whether wearing both Necro Cape and Crown will cause Undead mobs to be neutral, Default: true")
                    .define("necroSetUndeadNeutral", true);
            NamelessSetUndeadNeutral = BUILDER.comment("Whether wearing both Nameless Cape and Crown will cause Undead mobs to be neutral, Default: true")
                    .define("namelessSetUndeadNeutral", true);
            BUILDER.pop();
        FirstPersonGloves = BUILDER.comment("Show gloves in first person, Default: true")
                .define("firstPersonGloves", true);
        SpitefulBeltUseAmount = BUILDER.comment("Amount of Souls needed to use Spiteful Belt functionality, Default: 5")
                .defineInRange("spitefulBeltUseAmount", 5, 1, Integer.MAX_VALUE);
        PendantOfHungerLimit = BUILDER.comment("The total amount of Rotten Flesh a Pendant of Hunger can hold, Default: 1024")
                .defineInRange("pendantOfHungerLimit", 1024, 1, Integer.MAX_VALUE);
        SeaAmuletChargeConsume = BUILDER.comment("How much Charges the Sea Amulet needs to consume to function, Default: 1")
                .defineInRange("seaAmuletChargeConsume", 1, 1, Integer.MAX_VALUE);
        SeaAmuletMax = BUILDER.comment("The total amount of Charges a Sea Amulet can hold, Default: 256")
                .defineInRange("seaAmuletMax", 256, 1, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Tools & Weapons");
            BUILDER.push("Staffs");
            OminousStaffDamage = BUILDER.comment("How much base damage Ominous Staffs deals, Default: 4.0")
                    .defineInRange("ominousStaffDamage", 4.0, 1.0, Double.MAX_VALUE);
            NecroStaffDamage = BUILDER.comment("How much base damage Necro Staffs deals, Default: 4.0")
                    .defineInRange("necroStaffDamage", 4.0, 1.0, Double.MAX_VALUE);
            WindStaffDamage = BUILDER.comment("How much base damage Wind Staffs deals, Default: 4.0")
                    .defineInRange("windStaffDamage", 4.0, 1.0, Double.MAX_VALUE);
            StormStaffDamage = BUILDER.comment("How much base damage Storm Staffs deals, Default: 4.0")
                    .defineInRange("stormStaffDamage", 4.0, 1.0, Double.MAX_VALUE);
            FrostStaffDamage = BUILDER.comment("How much base damage Frost Staffs deals, Default: 4.0")
                    .defineInRange("frostStaffDamage", 4.0, 1.0, Double.MAX_VALUE);
            WildStaffDamage = BUILDER.comment("How much base damage Wild Staffs deals, Default: 4.0")
                    .defineInRange("wildStaffDamage", 4.0, 1.0, Double.MAX_VALUE);
            NamelessStaffDamage = BUILDER.comment("How much base damage Nameless Staffs deals, Default: 6.0")
                    .defineInRange("namelessStaffDamage", 6.0, 1.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Scythes");
            DarkScytheSouls = BUILDER.comment("Amount of Soul Energy Dark Scythe gives when hitting mob(s), Default: 1")
                    .defineInRange("darkScytheSouls", 1, 1, Integer.MAX_VALUE);
            ScytheSlashBreaks = BUILDER.comment("Scythe Slashes from Death Scythe breaks blocks that regular Scythes easily breaks, Default: true")
                    .define("scytheSlashBreaks", true);
            ScytheBaseDamage = BUILDER.comment("How much base damage Scythes deals, the damage added depends on material the scythe is made off (ie. Iron = 2.0), Default: 4.5")
                    .defineInRange("scytheBaseDamage", 4.5, 1.0, Double.MAX_VALUE);
            ScytheAttackSpeed = BUILDER.comment("How fast it takes to fully swing a Scythe with item offhand and not wearing Grave Gloves. The lower the number the slower it takes to recharge, Default: 0.6")
                    .defineInRange("scytheAttackSpeed", 0.6, 0.0, Double.MAX_VALUE);
            DeathScytheDamage = BUILDER.comment("How much damage Death Scythe deals, the configured number is added to Scythe Base Damage, Default: 4.0")
                    .defineInRange("deathScytheDamage", 4.0, 1.0, Double.MAX_VALUE);
            DeathScytheDurability = BUILDER.comment("How many uses before Death Scythe breaks, Default: 444")
                    .defineInRange("deathScytheDurability", 444, 1, Integer.MAX_VALUE);
            DeathScytheEnchantability = BUILDER.comment("Define the Enchantability for Death Scythe, higher number the better, Default: 22")
                    .defineInRange("deathScytheEnchantability", 22, 1, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Hammers");
            HammerBaseDamage = BUILDER.comment("How much base damage Hammers deals, the damage added depends on material the hammer is made off (ie. Iron = 2.0), Default: 5.0")
                    .defineInRange("hammerBaseDamage", 5.0, 1.0, Double.MAX_VALUE);
            HammerAttackSpeed = BUILDER.comment("How fast it takes to fully swing a Hammers. The lower the number the slower it takes to recharge, Default: 0.5")
                    .defineInRange("hammerAttackSpeed", 0.5, 0.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Dark Tools");
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
            BUILDER.pop();
            BUILDER.push("Special Tools");
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
            BUILDER.pop();
            BUILDER.push("Misc");
            PhilosophersMaceDamage = BUILDER.comment("How much damage Philosopher's Mace deals, Default: 9.0")
                    .defineInRange("philosophersMaceDamage", 9.0, 1.0, Double.MAX_VALUE);
            PhilosophersMaceDurability = BUILDER.comment("How many uses before the Philosopher's Mace breaks, Default: 128")
                    .defineInRange("philosophersMaceDurability", 128, 1, Integer.MAX_VALUE);
            PhilosophersMaceEnchantability = BUILDER.comment("Define the Enchantability for Philosopher's Mace, higher number the better, Default: 20")
                    .defineInRange("philosophersMaceEnchantability", 20, 1, Integer.MAX_VALUE);
            BUILDER.pop();
        BUILDER.pop();
        BUILDER.push("Armor");
            BUILDER.push("Cursed Knight Armor");
                BUILDER.push("Armor Points");
                CursedKnightFeet = BUILDER.comment("Define how much armor points wearing the Boots provides, Default: 2")
                        .defineInRange("cursedKnightFeet", 2, 1, Integer.MAX_VALUE);
                CursedKnightLegs = BUILDER.comment("Define how much armor points wearing the Leggings provides, Default: 5")
                        .defineInRange("cursedKnightLegs", 5, 1, Integer.MAX_VALUE);
                CursedKnightChest = BUILDER.comment("Define how much armor points wearing the Chestplate provides, Default: 6")
                        .defineInRange("cursedKnightChest", 6, 1, Integer.MAX_VALUE);
                CursedKnightHead = BUILDER.comment("Define how much armor points wearing the Helmet provides, Default: 2")
                        .defineInRange("cursedKnightHead", 2, 1, Integer.MAX_VALUE);
                BUILDER.pop();
            CursedKnightDurability = BUILDER.comment("Define the Durability Multiplier for the armor, higher number the better, Default: 15")
                    .defineInRange("cursedKnightDurability", 15, 1, Integer.MAX_VALUE);
            CursedKnightEnchantability = BUILDER.comment("Define the Enchantability for the armor, higher number the better, Default: 15")
                    .defineInRange("cursedKnightEnchantability", 15, 1, Integer.MAX_VALUE);
            CursedKnightToughness = BUILDER.comment("Define the toughness for each armor piece, Default: 0.5")
                    .defineInRange("cursedKnightToughness", 0.5, 0.0, Double.MAX_VALUE);
            CursedKnightKnockResist = BUILDER.comment("Define how much knockback resistance each armor piece provides, Default: 0.0")
                    .defineInRange("cursedKnightKnockResist", 0.0, 0.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Cursed Paladin Armor");
                BUILDER.push("Armor Points");
                CursedPaladinFeet = BUILDER.comment("Define how much armor points wearing the Boots provides, Default: 3")
                        .defineInRange("cursedPaladinFeet", 3, 1, Integer.MAX_VALUE);
                CursedPaladinLegs = BUILDER.comment("Define how much armor points wearing the Leggings provides, Default: 6")
                        .defineInRange("cursedPaladinLegs", 6, 1, Integer.MAX_VALUE);
                CursedPaladinChest = BUILDER.comment("Define how much armor points wearing the Chestplate provides, Default: 7")
                        .defineInRange("cursedPaladinChest", 7, 1, Integer.MAX_VALUE);
                CursedPaladinHead = BUILDER.comment("Define how much armor points wearing the Helmet provides, Default: 3")
                        .defineInRange("cursedPaladinHead", 3, 1, Integer.MAX_VALUE);
                BUILDER.pop();
            CursedPaladinDurability = BUILDER.comment("Define the Durability Multiplier for the armor, higher number the better, Default: 30")
                    .defineInRange("cursedPaladinDurability", 30, 1, Integer.MAX_VALUE);
            CursedPaladinEnchantability = BUILDER.comment("Define the Enchantability for the armor, higher number the better, Default: 20")
                    .defineInRange("cursedPaladinEnchantability", 20, 1, Integer.MAX_VALUE);
            CursedPaladinToughness = BUILDER.comment("Define the toughness for each armor piece, Default: 1.0")
                    .defineInRange("cursedPaladinToughness", 1.0, 0.0, Double.MAX_VALUE);
            CursedPaladinKnockResist = BUILDER.comment("Define how much knockback resistance each armor piece provides, Default: 0.0")
                    .defineInRange("cursedPaladinKnockResist", 0.0, 0.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Black Iron Armor");
                BUILDER.push("Armor Points");
                BlackIronFeet = BUILDER.comment("Define how much armor points wearing the Boots provides, Default: 2")
                        .defineInRange("blackIronFeet", 2, 1, Integer.MAX_VALUE);
                BlackIronLegs = BUILDER.comment("Define how much armor points wearing the Leggings provides, Default: 5")
                        .defineInRange("blackIronLegs", 5, 1, Integer.MAX_VALUE);
                BlackIronChest = BUILDER.comment("Define how much armor points wearing the Chestplate provides, Default: 6")
                        .defineInRange("blackIronChest", 6, 1, Integer.MAX_VALUE);
                BlackIronHead = BUILDER.comment("Define how much armor points wearing the Helmet provides, Default: 2")
                        .defineInRange("blackIronHead", 2, 1, Integer.MAX_VALUE);
                BUILDER.pop();
            BlackIronDurability = BUILDER.comment("Define the Durability Multiplier for the armor, higher number the better, Default: 15")
                    .defineInRange("blackIronDurability", 30, 1, Integer.MAX_VALUE);
            BlackIronEnchantability = BUILDER.comment("Define the Enchantability for the armor, higher number the better, Default: 15")
                    .defineInRange("blackIronEnchantability", 15, 1, Integer.MAX_VALUE);
            BlackIronToughness = BUILDER.comment("Define the toughness for each armor piece, Default: 2.0")
                    .defineInRange("blackIronToughness", 2.0, 0.0, Double.MAX_VALUE);
            BlackIronKnockResist = BUILDER.comment("Define how much knockback resistance each armor piece provides, Default: 0.0")
                    .defineInRange("blackIronKnockResist", 0.0, 0.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Dark Armor");
                BUILDER.push("Armor Points");
                DarkArmorFeet = BUILDER.comment("Define how much armor points wearing the Boots provides, Default: 3")
                        .defineInRange("darkArmorFeet", 3, 1, Integer.MAX_VALUE);
                DarkArmorLegs = BUILDER.comment("Define how much armor points wearing the Leggings provides, Default: 6")
                        .defineInRange("darkArmorLegs", 6, 1, Integer.MAX_VALUE);
                DarkArmorChest = BUILDER.comment("Define how much armor points wearing the Chestplate provides, Default: 8")
                        .defineInRange("darkArmorChest", 8, 1, Integer.MAX_VALUE);
                DarkArmorHead = BUILDER.comment("Define how much armor points wearing the Helmet provides, Default: 3")
                        .defineInRange("darkArmorHead", 3, 1, Integer.MAX_VALUE);
                BUILDER.pop();
            DarkArmorDurability = BUILDER.comment("Define the Durability Multiplier for the armor, higher number the better, Default: 15")
                    .defineInRange("darkArmorDurability", 15, 1, Integer.MAX_VALUE);
            DarkArmorEnchantability = BUILDER.comment("Define the Enchantability for the armor, higher number the better, Default: 20")
                    .defineInRange("darkArmorEnchantability", 20, 1, Integer.MAX_VALUE);
            DarkArmorToughness = BUILDER.comment("Define the toughness for each armor piece, Default: 2.0")
                    .defineInRange("darkArmorToughness", 2.0, 0.0, Double.MAX_VALUE);
            DarkArmorKnockResist = BUILDER.comment("Define how much knockback resistance each armor piece provides, Default: 0.3")
                    .defineInRange("darkArmorKnockResist", 0.3, 0.0, Double.MAX_VALUE);
            BUILDER.pop();
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
