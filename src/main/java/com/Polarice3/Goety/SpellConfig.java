package com.Polarice3.Goety;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class SpellConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> SpellDamageMultiplier;

    public static final ForgeConfigSpec.ConfigValue<Integer> VexCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FangCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ZombieCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SkeletonCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> WraithCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> HauntedSkullCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FeastCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FireballCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> LavaballCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SoulBoltCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> IllusionCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SoulLightCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> GlowLightCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> IceChunkCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> LaunchCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FlyingCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> UpdraftCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> WindBlastCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> LightningCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SonicBoomCost;

    public static final ForgeConfigSpec.ConfigValue<Integer> VexDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> FangDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> ZombieDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> SkeletonDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> WraithDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> HauntedSkullDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> FeastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> LavaballDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> IllusionDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> IceChunkDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> UpdraftDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> WindBlastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> LightningDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> SonicBoomDuration;

    public static final ForgeConfigSpec.ConfigValue<Double> FangDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> FireballDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> LavaballDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> HauntedSkullDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> SoulBoltDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> NecroBoltDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> IceChunkDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> UpdraftBlastDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> LightningDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> SonicBoomDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> WandVexLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> StaffVexLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> ZombieLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> SkeletonLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> WraithLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> SkullLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> UndeadMinionHealCost;

    public static final ForgeConfigSpec.ConfigValue<Integer> VexCooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> ZombieCooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> SkeletonCooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> WraithCooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> HauntedSkullCooldown;

    public static final ForgeConfigSpec.ConfigValue<Integer> MaxSoulEaterLevel;
    public static final ForgeConfigSpec.ConfigValue<Integer> MaxWantingLevel;
    public static final ForgeConfigSpec.ConfigValue<Integer> MaxPotencyLevel;
    public static final ForgeConfigSpec.ConfigValue<Integer> MaxRadiusLevel;
    public static final ForgeConfigSpec.ConfigValue<Integer> MaxRangeLevel;
    public static final ForgeConfigSpec.ConfigValue<Integer> MaxDurationLevel;

    public static final ForgeConfigSpec.ConfigValue<Boolean> UndeadTeleport;
    public static final ForgeConfigSpec.ConfigValue<Boolean> VexTeleport;
    public static final ForgeConfigSpec.ConfigValue<Boolean> MinionsAttackCreepers;
    public static final ForgeConfigSpec.ConfigValue<Boolean> MinionsMasterImmune;
    public static final ForgeConfigSpec.ConfigValue<Boolean> OwnerAttackCancel;
    public static final ForgeConfigSpec.ConfigValue<Boolean> UndeadMinionHeal;

    static {
        BUILDER.push("General");
        SpellDamageMultiplier = BUILDER.comment("Multiplies the damage of spells by this amount, Default: 1")
                .defineInRange("spellDamageMultiplier", 1, 1, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Spell Costs");
        VexCost = BUILDER.comment("Vex Spell Cost, Default: 18")
                .defineInRange("vexCost", 18, 0, Integer.MAX_VALUE);
        FangCost = BUILDER.comment("Fang Spell Cost, Default: 8")
                .defineInRange("fangCost", 8, 0, Integer.MAX_VALUE);
        ZombieCost = BUILDER.comment("Rotting Spell Cost, Default: 5")
                .defineInRange("zombieCost", 5, 0, Integer.MAX_VALUE);
        SkeletonCost = BUILDER.comment("Osseous Spell Cost, Default: 8")
                .defineInRange("skeletonCost", 8, 0, Integer.MAX_VALUE);
        WraithCost = BUILDER.comment("Spooky Spell Cost, Default: 24")
                .defineInRange("wraithCost", 24, 0, Integer.MAX_VALUE);
        HauntedSkullCost = BUILDER.comment("Skull Spell Cost, Default: 24")
                .defineInRange("hauntedSkullCost", 16, 0, Integer.MAX_VALUE);
        FeastCost = BUILDER.comment("Feasting Spell Cost, Default: 8")
                .defineInRange("feastCost", 8, 0, Integer.MAX_VALUE);
        FireballCost = BUILDER.comment("Fireball Spell Cost, Default: 4")
                .defineInRange("fireballCost", 4, 0, Integer.MAX_VALUE);
        LavaballCost = BUILDER.comment("Lava Bomb Spell Cost, Default: 16")
                .defineInRange("lavaBombCost", 16, 0, Integer.MAX_VALUE);
        SoulBoltCost = BUILDER.comment("Soul Bolt Spell Cost, Default: 4")
                .defineInRange("soulBoltCost", 4, 0, Integer.MAX_VALUE);
        IllusionCost = BUILDER.comment("Illusion Spell Cost, Default: 20")
                .defineInRange("illusionCost", 20, 0, Integer.MAX_VALUE);
        SoulLightCost = BUILDER.comment("Soul Light Spell Cost, Default: 1")
                .defineInRange("soulLightCost", 1, 0, Integer.MAX_VALUE);
        GlowLightCost = BUILDER.comment("Glow Light Spell Cost, Default: 2")
                .defineInRange("glowLightCost", 2, 0, Integer.MAX_VALUE);
        IceChunkCost = BUILDER.comment("Ice Chunk Spell Cost, Default: 16")
                .defineInRange("iceChunkCost", 16, 0, Integer.MAX_VALUE);
        LaunchCost = BUILDER.comment("Launch Spell Cost, Default: 4")
                .defineInRange("launchCost", 4, 0, Integer.MAX_VALUE);
        FlyingCost = BUILDER.comment("Flight Spell Cost, Default: 4")
                .defineInRange("flightCost", 4, 0, Integer.MAX_VALUE);
        UpdraftCost = BUILDER.comment("Updraft Spell Cost, Default: 8")
                .defineInRange("updraftCost", 8, 0, Integer.MAX_VALUE);
        WindBlastCost = BUILDER.comment("Wind Blast Spell Cost, Default: 4")
                .defineInRange("windBlastCost", 4, 0, Integer.MAX_VALUE);
        LightningCost = BUILDER.comment("Lightning Spell Cost, Default: 16")
                .defineInRange("lightningCost", 16, 0, Integer.MAX_VALUE);
        SonicBoomCost = BUILDER.comment("Sonic Boom Spell Cost, Default: 16")
                .defineInRange("sonicBoomCost", 16, 0, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Casting Time");
        VexDuration = BUILDER.comment("Time to cast Vex Spell, Default: 100")
                .defineInRange("vexTime", 100, 0, 72000);
        FangDuration = BUILDER.comment("Time to cast Fang Spell, Default: 40")
                .defineInRange("fangTime", 40, 0, 72000);
        ZombieDuration = BUILDER.comment("Time to cast Rotting Spell, Default: 20")
                .defineInRange("zombieTime", 20, 0, 72000);
        SkeletonDuration = BUILDER.comment("Time to cast Osseous Spell, Default: 60")
                .defineInRange("skeletonTime", 60, 0, 72000);
        WraithDuration = BUILDER.comment("Time to cast Wraith Spell, Default: 60")
                .defineInRange("wraithTime", 60, 0, 72000);
        HauntedSkullDuration = BUILDER.comment("Time to cast Haunted Skull Spell, Default: 20")
                .defineInRange("hauntedSkullTime", 20, 0, 72000);
        FeastDuration = BUILDER.comment("Time to cast Feasting Spell per second, Default: 20")
                .defineInRange("feastTime", 20, 0, 72000);
        LavaballDuration = BUILDER.comment("Time to cast Lava Bomb Spell, Default: 20")
                .defineInRange("lavaBombTime", 20, 0, 72000);
        IllusionDuration = BUILDER.comment("Time to cast Illusion Spell, Default: 40")
                .defineInRange("illusionTime", 40, 0, 72000);
        IceChunkDuration = BUILDER.comment("Time to cast Ice Chunk Spell, Default: 40")
                .defineInRange("iceChunkTime", 40, 0, 72000);
        UpdraftDuration = BUILDER.comment("Time to cast Updraft Spell, Default: 20")
                .defineInRange("updraftTime", 20, 0, 72000);
        WindBlastDuration = BUILDER.comment("Time to cast Wind Blast Spell, Default: 20")
                .defineInRange("windBlastTime", 20, 0, 72000);
        LightningDuration = BUILDER.comment("Time to cast Lightning Spell, Default: 60")
                .defineInRange("lightningTime", 60, 0, 72000);
        SonicBoomDuration = BUILDER.comment("Time to cast Sonic Boom Spell, Default: 60")
                .defineInRange("sonicBoomTime", 60, 0, 72000);
        BUILDER.pop();
        BUILDER.push("Summon Down Duration");
        VexCooldown = BUILDER.comment("Vex Spell Cooldown, Default: 340")
                .defineInRange("vexCooldown", 340, 0, 72000);
        ZombieCooldown = BUILDER.comment("Rotting Spell Cooldown, Default: 120")
                .defineInRange("zombieCooldown", 120, 0, 72000);
        SkeletonCooldown = BUILDER.comment("Osseous Spell Cooldown, Default: 280")
                .defineInRange("skeletonCooldown", 280, 0, 72000);
        WraithCooldown = BUILDER.comment("Spooky Spell Cooldown, Default: 300")
                .defineInRange("wraithCooldown", 300, 0, 72000);
        HauntedSkullCooldown = BUILDER.comment("Skull Spell Cooldown, Default: 0")
                .defineInRange("hauntedSkullCooldown", 0, 0, 72000);
        BUILDER.pop();
        BUILDER.push("Spell Power");
        FangDamage = BUILDER.comment("How much base damage Magic Fangs deals, Default: 6.0")
                .defineInRange("fangDamage", 6.0, 1.0, Double.MAX_VALUE);
        FireballDamage = BUILDER.comment("How much base damage Fireballs deal when directly hitting a mob, Default: 5.0")
                .defineInRange("fireballDamage", 5.0, 1.0, Double.MAX_VALUE);
        LavaballDamage = BUILDER.comment("How much base damage Lavaballs deal when directly hitting a mob, Default: 6.0")
                .defineInRange("lavaballDamage", 6.0, 1.0, Double.MAX_VALUE);
        HauntedSkullDamage = BUILDER.comment("How much base damage Haunted Skulls deal when directly hitting a mob, Default: 5.0")
                .defineInRange("hauntedSkullDamage", 5.0, 1.0, Double.MAX_VALUE);
        SoulBoltDamage = BUILDER.comment("How much base damage Soul Bolts deals, Default: 4.0")
                .defineInRange("soulBoltDamage", 4.0, 1.0, Double.MAX_VALUE);
        NecroBoltDamage = BUILDER.comment("How much base damage Necro Bolts deals, Default: 10.0")
                .defineInRange("necroBoltDamage", 10.0, 1.0, Double.MAX_VALUE);
        IceChunkDamage = BUILDER.comment("How much base damage Ice Chunks deals, Default: 8.0")
                .defineInRange("iceChunkDamage", 8.0, 1.0, Double.MAX_VALUE);
        UpdraftBlastDamage = BUILDER.comment("How much base damage Updraft Blasts deals, Default: 5.0")
                .defineInRange("updraftBlastDamage", 5.0, 1.0, Double.MAX_VALUE);
        LightningDamage = BUILDER.comment("How much base damage Lightning from the spell deals, Default: 5.0")
                .defineInRange("lightningDamage", 5.0, 1.0, Double.MAX_VALUE);
        SonicBoomDamage = BUILDER.comment("How much base damage Sonic Boom Spell deals, Default: 10.0")
                .defineInRange("sonicBoomDamage", 10.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Minions");
        UndeadTeleport = BUILDER.comment("Whether Undead Servants can teleport to Players, Default: false")
                .define("undeadTeleport", false);
        VexTeleport = BUILDER.comment("Whether Vex Servants can teleport to Players, Default: true")
                .define("vexTeleport", true);
        MinionsAttackCreepers = BUILDER.comment("Whether Servants can attack Creepers if Mob Griefing Rule is False, Default: true")
                .define("minionAttackCreepers", true);
        MinionsMasterImmune = BUILDER.comment("Whether Servants or their owner are immune to attacks made by other servants that are summoned by the same owner, Default: true")
                .define("minionMasterImmune", true);
        OwnerAttackCancel = BUILDER.comment("Owners can't attack their servants, Default: true")
                .define("ownerAttackCancel", true);
        UndeadMinionHeal = BUILDER.comment("Whether Undead Servants can heal if summoned while wearing Necro Robes, Default: true")
                .define("undeadMinionHeal", true);
        UndeadMinionHealCost = BUILDER.comment("How much Soul Energy it cost per second for an Undead Servant to heal, Default: 1")
                .defineInRange("undeadMinionHealCost", 1, 0, Integer.MAX_VALUE);
        WandVexLimit = BUILDER.comment("Number of Vex Servants that can be spawn with a wand, without instantly dying, around the player, Default: 8")
                .defineInRange("wandVexLimit", 8, 1, Integer.MAX_VALUE);
        StaffVexLimit = BUILDER.comment("Number of Vex Servants that can be spawn with a staff, without instantly dying, around the player, Default: 16")
                .defineInRange("staffVexLimit", 16, 1, Integer.MAX_VALUE);
        ZombieLimit = BUILDER.comment("Number of Zombie Servants that can exist around the player without instantly dying, Default: 32")
                .defineInRange("zombieLimit", 32, 1, Integer.MAX_VALUE);
        SkeletonLimit = BUILDER.comment("Number of Skeleton Servants that can exist around the player without instantly dying, Default: 32")
                .defineInRange("skeletonLimit", 32, 1, Integer.MAX_VALUE);
        WraithLimit = BUILDER.comment("Number of Wraith Servants that can exist around the player without instantly dying, Default: 6")
                .defineInRange("wraithLimit", 6, 1, Integer.MAX_VALUE);
        SkullLimit = BUILDER.comment("Number of Haunted Skulls that can exist around the player without instantly dying, Default: 8")
                .defineInRange("skullLimit", 8, 1, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Enchantments");
        MaxSoulEaterLevel = BUILDER.comment("Soul Eater Maximum Enchantment Level, Default: 5")
                .defineInRange("maxSoulEaterLevel", 5, 1, 10);
        MaxWantingLevel = BUILDER.comment("Wanting Maximum Enchantment Level, Default: 3")
                .defineInRange("maxWantingLevel", 3, 1, 10);
        MaxPotencyLevel = BUILDER.comment("Potency Maximum Enchantment Level, Default: 5")
                .defineInRange("maxPotencyLevel", 5, 1, 10);
        MaxRadiusLevel = BUILDER.comment("Radius Maximum Enchantment Level, Default: 2")
                .defineInRange("maxRadiusLevel", 2, 1, 10);
        MaxRangeLevel = BUILDER.comment("Range Maximum Enchantment Level, Default: 10")
                .defineInRange("maxRangeLevel", 10, 1, 10);
        MaxDurationLevel = BUILDER.comment("Duration Maximum Enchantment Level, Default: 3")
                .defineInRange("maxDurationLevel", 3, 1, 10);
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
