package com.Polarice3.Goety.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;
import java.util.List;

public class SpellConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> SpellDamageMultiplier;

    public static final ForgeConfigSpec.ConfigValue<Integer> VexCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> VexDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> VexCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> VexSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> WandVexLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> StaffVexLimit;

    public static final ForgeConfigSpec.ConfigValue<Integer> FangCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FangDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> FangCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> FangDamage;
    public static final ForgeConfigSpec.ConfigValue<Integer> FangGainSouls;

    public static final ForgeConfigSpec.ConfigValue<Integer> ZombieCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ZombieDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> ZombieCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> ZombieSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> ZombieLimit;

    public static final ForgeConfigSpec.ConfigValue<Integer> SkeletonCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SkeletonDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> SkeletonCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> SkeletonSummonDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> WraithCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> WraithDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> WraithCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> WraithSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> WraithLimit;

    public static final ForgeConfigSpec.ConfigValue<Integer> VanguardCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> VanguardDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> VanguardCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> VanguardSummonDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> HauntedSkullCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> HauntedSkullDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> HauntedSkullCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> HauntedSkullSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Double> HauntedSkullDamage;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HauntedSkullGriefing;
    public static final ForgeConfigSpec.ConfigValue<Integer> SkullLimit;

    public static final ForgeConfigSpec.ConfigValue<Integer> WitherSkullCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> WitherSkullDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> WitherSkullCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> WitherSkullDamage;
    public static final ForgeConfigSpec.ConfigValue<Boolean> WitherSkullGriefing;

    public static final ForgeConfigSpec.ConfigValue<Integer> GhastCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> GhastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> GhastCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> GhastSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> GhastLimit;

    public static final ForgeConfigSpec.ConfigValue<Integer> BlazeCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> BlazeDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> BlazeCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> BlazeSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> BlazeLimit;

    public static final ForgeConfigSpec.ConfigValue<Integer> FeastCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FeastChargeUp;
    public static final ForgeConfigSpec.ConfigValue<Integer> FeastDuration;

    public static final ForgeConfigSpec.ConfigValue<Integer> TeethCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> TeethDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> TeethCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> ViciousToothDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> FireballCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FireballDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> FireballCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> FireballDamage;
    public static final ForgeConfigSpec.ConfigValue<Boolean> FireballGriefing;

    public static final ForgeConfigSpec.ConfigValue<Integer> LavaballCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> LavaballDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> LavaballCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> LavaballDamage;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LavaballGriefing;

    public static final ForgeConfigSpec.ConfigValue<Integer> MagmaBombCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> MagmaBombDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> MagmaBombCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> FlameStrikeCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FlameStrikeDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> FlameStrikeCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> FlameStrikeDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> SoulBoltCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SoulBoltDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> SoulBoltCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> SoulBoltDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> NecroBoltDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> MagicBoltCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> MagicBoltDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> MagicBoltCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> MagicBoltDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> SwordCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SwordDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> SwordCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> IceSpikeCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> IceSpikeDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> IceSpikeCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> IceSpikeDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> IceStormCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> IceStormDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> IceStormCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> IceStormDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> ChargeCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ChargeCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> ChargeDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> IllusionCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> IllusionDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> IllusionCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> FireBreathCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FireBreathChargeUp;
    public static final ForgeConfigSpec.ConfigValue<Double> FireBreathDamage;
    public static final ForgeConfigSpec.ConfigValue<Boolean> DragonFireGriefing;

    public static final ForgeConfigSpec.ConfigValue<Integer> FrostBreathCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FrostBreathChargeUp;
    public static final ForgeConfigSpec.ConfigValue<Double> FrostBreathDamage;
    public static final ForgeConfigSpec.ConfigValue<Boolean> DragonFrostGriefing;

    public static final ForgeConfigSpec.ConfigValue<Integer> ShockingCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ShockingChargeUp;
    public static final ForgeConfigSpec.ConfigValue<Double> ShockingDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> SoulLightCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SoulLightDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> SoulLightCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> GlowLightCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> GlowLightDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> GlowLightCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> IceChunkCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> IceChunkDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> IceChunkCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> IceChunkDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> HailCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> HailDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> HailCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> HailDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> FrostNovaCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FrostNovaDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> FrostNovaCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> FrostNovaDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> FrostNovaMaxDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> FrostbornCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FrostbornDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> FrostbornCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> FrostbornSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> IceGolemLimit;

    public static final ForgeConfigSpec.ConfigValue<Integer> BarricadeCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> BarricadeDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> BarricadeCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> QuakingCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> QuakingDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> QuakingCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> QuakingDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> PulverizeCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> PulverizeCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> RotationCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> RotationCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> EruptionCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> EruptionDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> EruptionCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> PyroclastDamage;
    public static final ForgeConfigSpec.ConfigValue<Boolean> PyroclastGriefing;

    public static final ForgeConfigSpec.ConfigValue<Integer> ScatterCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ScatterDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> ScatterCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> ScatterMineDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> LaunchCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> LaunchDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> LaunchCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> FlyingCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FlyingChargeUp;

    public static final ForgeConfigSpec.ConfigValue<Integer> WhirlwindCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> WhirlwindChargeUp;

    public static final ForgeConfigSpec.ConfigValue<Integer> CushionCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> CushionDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> CushionCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> CycloneCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> CycloneDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> CycloneCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> UpdraftCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> UpdraftDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> UpdraftCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> UpdraftBlastDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> WindBlastCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> WindBlastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> WindBlastCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> SwarmCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SwarmChargeUp;
    public static final ForgeConfigSpec.ConfigValue<Double> SwarmDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> GrappleCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> GrappleDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> GrappleCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> OvergrowthCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> OvergrowthDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> OvergrowthCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> EntanglingCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> EntanglingDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> EntanglingCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> WhisperCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> WhisperDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> WhisperCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> WhisperSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> WhisperLimit;

    public static final ForgeConfigSpec.ConfigValue<Integer> ThunderboltCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ThunderboltDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> ThunderboltCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> ThunderboltDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> ElectroOrbCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ElectroOrbDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> ElectroOrbCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> ElectroOrbDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> MonsoonCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> MonsoonDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> MonsoonCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> DischargeCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> DischargeDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> DischargeCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> DischargeDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> DischargeMaxDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> LightningCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> LightningDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> LightningCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> LightningDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> CallCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> CallDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> CallCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> RecallCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> RecallDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> RecallCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> EnderChestCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> EnderChestDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> EnderChestCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> BanishCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> BanishCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> BanishMaxHealth;

    public static final ForgeConfigSpec.ConfigValue<Integer> BlinkCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> BlinkDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> BlinkCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> EndWalkCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> EndWalkDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> EndWalkCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> EndWalkEffectDuration;

    public static final ForgeConfigSpec.ConfigValue<Integer> TunnelCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> TunnelCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> TunnelDefaultLifespan;
    public static final ForgeConfigSpec.ConfigValue<Integer> TunnelDefaultDistance;

    public static final ForgeConfigSpec.ConfigValue<Integer> ShockwaveCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ShockwaveDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> ShockwaveCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> ShockwaveDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ShockwaveMaxDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> IronHideCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> IronHideDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> IronHideCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> BulwarkCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> BulwarkDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> BulwarkCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> BulwarkShieldAmount;
    public static final ForgeConfigSpec.ConfigValue<Integer> BulwarkShieldTime;

    public static final ForgeConfigSpec.ConfigValue<Integer> SoulHealCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SoulHealDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> SoulHealCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> SoulHealAmount;

    public static final ForgeConfigSpec.ConfigValue<Integer> TelekinesisCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> TelekinesisChargeUp;
    public static final ForgeConfigSpec.ConfigValue<Integer> TelekinesisMaxHealth;

    public static final ForgeConfigSpec.ConfigValue<Integer> SonicBoomCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SonicBoomDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> SonicBoomCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> SonicBoomDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> CorruptionCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> CorruptionChargeUp;
    public static final ForgeConfigSpec.ConfigValue<Double> CorruptedBeamDamage;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CorruptionImmobile;

    public static final ForgeConfigSpec.ConfigValue<Integer> SkeletonLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> BoundIllagerLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> RedstoneGolemLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> GraveGolemLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> RedstoneMonstrosityGlobalLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> RedstoneMonstrosityPlayerLimit;

    public static final ForgeConfigSpec.ConfigValue<Integer> MaxSoulEaterLevel;
    public static final ForgeConfigSpec.ConfigValue<Integer> MaxWantingLevel;
    public static final ForgeConfigSpec.ConfigValue<Integer> MaxPotencyLevel;
    public static final ForgeConfigSpec.ConfigValue<Integer> MaxRadiusLevel;
    public static final ForgeConfigSpec.ConfigValue<Integer> MaxRangeLevel;
    public static final ForgeConfigSpec.ConfigValue<Integer> MaxDurationLevel;
    public static final ForgeConfigSpec.ConfigValue<Integer> MaxBurningLevel;
    public static final ForgeConfigSpec.ConfigValue<Integer> MaxVelocityLevel;

    public static final ForgeConfigSpec.ConfigValue<Boolean> OwnerHitCommand;
    public static final ForgeConfigSpec.ConfigValue<Boolean> EnvironmentalCost;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SummonDown;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> TelekinesisBlackList;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> BanishBlackList;

    static {
        BUILDER.push("General");
        SpellDamageMultiplier = BUILDER.comment("Multiplies the damage of spells by this amount, Default: 1")
                .defineInRange("spellDamageMultiplier", 1, 1, Integer.MAX_VALUE);
        OwnerHitCommand = BUILDER.comment("Whether Servants change navigation modes by hitting them, put false to make them change by right-clicking on them, Default: true")
                .define("ownerHitCommand", true);
        EnvironmentalCost = BUILDER.comment("Spells have their soul cost reduced or increased depending on the surroundings around the caster, Default: true")
                .define("environmentalCost", true);
        SummonDown = BUILDER.comment("Certain spells that summons servants will give player Summon Down effect, Default: true")
                .define("summonDown", true);
        BUILDER.pop();
        BUILDER.push("Spells");
            BUILDER.push("Vexing Spell");
            VexCost = BUILDER.comment("Vexing Spell Cost, Default: 18")
                    .defineInRange("vexCost", 18, 0, Integer.MAX_VALUE);
            VexDuration = BUILDER.comment("Time to cast Vexing Spell, Default: 100")
                    .defineInRange("vexTime", 100, 0, 72000);
            VexCoolDown = BUILDER.comment("Vexing Spell Cooldown, Default: 340")
                    .defineInRange("vexCoolDown", 340, 0, Integer.MAX_VALUE);
            VexSummonDown = BUILDER.comment("Vexing Spell Summon Down, Default: 340")
                    .defineInRange("vexSummonDown", 340, 0, 72000);
            WandVexLimit = BUILDER.comment("Number of Vex Servants that can be spawn with a wand, without instantly dying, around the player, Default: 8")
                    .defineInRange("wandVexLimit", 8, 1, Integer.MAX_VALUE);
            StaffVexLimit = BUILDER.comment("Number of Vex Servants that can be spawn with a staff, without instantly dying, around the player, Default: 16")
                    .defineInRange("staffVexLimit", 16, 1, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Biting Spell && Magic Fangs");
            FangCost = BUILDER.comment("Biting Spell Cost, Default: 8")
                    .defineInRange("fangCost", 8, 0, Integer.MAX_VALUE);
            FangDuration = BUILDER.comment("Time to cast Biting Spell, Default: 40")
                    .defineInRange("fangTime", 40, 0, 72000);
            FangCoolDown = BUILDER.comment("Biting Spell Cooldown, Default: 100")
                    .defineInRange("fangCoolDown", 100, 0, Integer.MAX_VALUE);
            FangDamage = BUILDER.comment("How much base damage Magic Fangs deals, Default: 6.0")
                    .defineInRange("fangDamage", 6.0, 1.0, Double.MAX_VALUE);
            FangGainSouls = BUILDER.comment("Amount of Soul Energy Magic Fangs gives when hitting mob(s), Default: 1")
                    .defineInRange("fangGainSouls", 1, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Rotting Spell");
            ZombieCost = BUILDER.comment("Rotting Spell Cost, Default: 5")
                    .defineInRange("zombieCost", 5, 0, Integer.MAX_VALUE);
            ZombieDuration = BUILDER.comment("Time to cast Rotting Spell, Default: 20")
                    .defineInRange("zombieTime", 20, 0, 72000);
            ZombieCoolDown = BUILDER.comment("Rotting Spell Cooldown, Default: 100")
                    .defineInRange("zombieCoolDown", 100, 0, Integer.MAX_VALUE);
            ZombieSummonDown = BUILDER.comment("Rotting Spell Summon Down, Default: 120")
                    .defineInRange("zombieSummonDown", 120, 0, 72000);
            ZombieLimit = BUILDER.comment("Number of Zombie Servants that can exist around the player without instantly dying, Default: 32")
                    .defineInRange("zombieLimit", 32, 1, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Osseous Spell");
            SkeletonCost = BUILDER.comment("Osseous Spell Cost, Default: 8")
                    .defineInRange("skeletonCost", 8, 0, Integer.MAX_VALUE);
            SkeletonDuration = BUILDER.comment("Time to cast Osseous Spell, Default: 60")
                    .defineInRange("skeletonTime", 60, 0, 72000);
            SkeletonCoolDown = BUILDER.comment("Osseous Spell Cooldown, Default: 100")
                    .defineInRange("skeletonCoolDown", 100, 0, Integer.MAX_VALUE);
            SkeletonSummonDown = BUILDER.comment("Osseous Spell Summon Down, Default: 280")
                    .defineInRange("skeletonSummonDown", 280, 0, 72000);
            BUILDER.pop();
            BUILDER.push("Spooky Spell");
            WraithCost = BUILDER.comment("Spooky Spell Cost, Default: 24")
                    .defineInRange("wraithCost", 24, 0, Integer.MAX_VALUE);
            WraithDuration = BUILDER.comment("Time to cast Spooky Spell, Default: 60")
                    .defineInRange("wraithTime", 60, 0, 72000);
            WraithCoolDown = BUILDER.comment("Spooky Spell Cooldown, Default: 100")
                    .defineInRange("wraithCoolDown", 100, 0, Integer.MAX_VALUE);
            WraithSummonDown = BUILDER.comment("Spooky Spell Summon Down, Default: 300")
                    .defineInRange("wraithSummonDown", 300, 0, 72000);
            WraithLimit = BUILDER.comment("Number of Wraith Servants that can exist around the player without instantly dying, Default: 6")
                    .defineInRange("wraithLimit", 6, 1, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Vanguard Spell");
            VanguardCost = BUILDER.comment("Vanguard Spell Cost, Default: 24")
                    .defineInRange("vanguardCost", 24, 0, Integer.MAX_VALUE);
            VanguardDuration = BUILDER.comment("Time to cast Vanguard Spell, Default: 180")
                    .defineInRange("vanguardTime", 180, 0, 72000);
            VanguardCoolDown = BUILDER.comment("Vanguard Spell Cooldown, Default: 100")
                    .defineInRange("vanguardCoolDown", 100, 0, Integer.MAX_VALUE);
            VanguardSummonDown = BUILDER.comment("Vanguard Spell Summon Down, Default: 300")
                    .defineInRange("vanguardSummonDown", 300, 0, 72000);
            BUILDER.pop();
            BUILDER.push("Skull Spell");
            HauntedSkullCost = BUILDER.comment("Skull Spell Cost, Default: 16")
                    .defineInRange("hauntedSkullCost", 16, 0, Integer.MAX_VALUE);
            HauntedSkullDuration = BUILDER.comment("Time to cast Haunted Skull Spell, Default: 20")
                    .defineInRange("hauntedSkullTime", 20, 0, 72000);
            HauntedSkullCoolDown = BUILDER.comment("Skull Spell Cooldown, Default: 20")
                    .defineInRange("hauntedSkullCoolDown", 20, 0, Integer.MAX_VALUE);
            HauntedSkullSummonDown = BUILDER.comment("Skull Spell Summon Down, Default: 0")
                    .defineInRange("hauntedSkullSummonDown", 0, 0, 72000);
            HauntedSkullDamage = BUILDER.comment("How much base damage Haunted Skulls deal when directly hitting a mob, Default: 5.0")
                    .defineInRange("hauntedSkullDamage", 5.0, 1.0, Double.MAX_VALUE);
            HauntedSkullGriefing = BUILDER.comment("Enable Haunted Skull projectile griefing, Default: true")
                    .define("hauntedSkullGriefing", true);
            SkullLimit = BUILDER.comment("Number of Haunted Skulls that can exist around the player without instantly dying, Default: 8")
                    .defineInRange("skullLimit", 8, 1, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Wither Skull Spell");
            WitherSkullCost = BUILDER.comment("Wither Skull Spell Cost, Default: 24")
                    .defineInRange("witherSkullCost", 24, 0, Integer.MAX_VALUE);
            WitherSkullDuration = BUILDER.comment("Time to cast Wither Skull Spell, Default: 0")
                    .defineInRange("witherSkullTime", 0, 0, 72000);
            WitherSkullCoolDown = BUILDER.comment("Wither Skull Spell Cooldown, Default: 0")
                    .defineInRange("witherSkullCoolDown", 0, 0, Integer.MAX_VALUE);
            WitherSkullDamage = BUILDER.comment("How much base damage Wither Skulls deal when directly hitting a mob, Default: 8.0")
                    .defineInRange("witherSkullDamage", 8.0, 1.0, Double.MAX_VALUE);
            WitherSkullGriefing = BUILDER.comment("Enable Wither Skull projectile griefing, Default: true")
                    .define("witherSkullGriefing", true);
            BUILDER.pop();
            BUILDER.push("Ghastly Spell");
            GhastCost = BUILDER.comment("Ghastly Spell Cost, Default: 24")
                    .defineInRange("ghastCost", 24, 0, Integer.MAX_VALUE);
            GhastDuration = BUILDER.comment("Time to cast Ghastly Spell, Default: 100")
                    .defineInRange("ghastDuration", 100, 0, 72000);
            GhastCoolDown = BUILDER.comment("Ghastly Spell Cooldown, Default: 200")
                    .defineInRange("ghastCoolDown", 200, 0, Integer.MAX_VALUE);
            GhastSummonDown = BUILDER.comment("Ghastly Spell Summon Down, Default: 300")
                    .defineInRange("ghastSummonDown", 300, 0, 72000);
            GhastLimit = BUILDER.comment("Number of Mini Ghasts that can exist around the player, Default: 8")
                    .defineInRange("ghastLimit", 8, 1, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Blazing Spell");
            BlazeCost = BUILDER.comment("Blazing Spell Cost, Default: 16")
                    .defineInRange("blazeCost", 16, 0, Integer.MAX_VALUE);
            BlazeDuration = BUILDER.comment("Time to cast Blazing Spell, Default: 100")
                    .defineInRange("blazeDuration", 100, 0, 72000);
            BlazeCoolDown = BUILDER.comment("Blazing Spell Cooldown, Default: 200")
                    .defineInRange("blazeCoolDown", 200, 0, Integer.MAX_VALUE);
            BlazeSummonDown = BUILDER.comment("Blazing Spell Summon Down, Default: 400")
                    .defineInRange("blazeSummonDown", 400, 0, 72000);
            BlazeLimit = BUILDER.comment("Number of Blaze Servants that can exist around the player, Default: 16")
                    .defineInRange("blazeLimit", 16, 1, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Feasting Spell");
            FeastCost = BUILDER.comment("Feasting Spell Cost, Default: 8")
                    .defineInRange("feastCost", 8, 0, Integer.MAX_VALUE);
            FeastChargeUp = BUILDER.comment("How many ticks the Feasting Spell much charge before casting, Default: 0")
                    .defineInRange("feastChargeUp", 0, 0, Integer.MAX_VALUE);
            FeastDuration = BUILDER.comment("Time to cast Feasting Spell per second, Default: 20")
                    .defineInRange("feastTime", 20, 0, 72000);
            BUILDER.pop();
            BUILDER.push("Teeth Spell");
            TeethCost = BUILDER.comment("Teeth Spell Cost, Default: 8")
                    .defineInRange("teethCost", 8, 0, Integer.MAX_VALUE);
            TeethDuration = BUILDER.comment("Time to cast Teeth Spell, Default: 60")
                    .defineInRange("teethTime", 60, 0, 72000);
            TeethCoolDown = BUILDER.comment("Teeth Spell Cooldown, Default: 20")
                    .defineInRange("teethCoolDown", 20, 0, Integer.MAX_VALUE);
            ViciousToothDamage = BUILDER.comment("How much base damage Vicious Tooth deals, Default: 12.0")
                    .defineInRange("viciousToothDamage", 12.0, 1.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Fireball Spell");
            FireballCost = BUILDER.comment("Fireball Spell Cost, Default: 4")
                    .defineInRange("fireballCost", 4, 0, Integer.MAX_VALUE);
            FireballDuration = BUILDER.comment("Time to cast Fireball Spell, Default: 0")
                    .defineInRange("fireballTime", 0, 0, 72000);
            FireballCoolDown = BUILDER.comment("Fireball Spell Cooldown, Default: 20")
                    .defineInRange("fireballCoolDown", 20, 0, Integer.MAX_VALUE);
            FireballDamage = BUILDER.comment("How much base damage Fireballs deal when directly hitting a mob, Default: 5.0")
                    .defineInRange("fireballDamage", 5.0, 1.0, Double.MAX_VALUE);
            FireballGriefing = BUILDER.comment("Enable Fireball projectile griefing, Default: true")
                    .define("fireballGriefing", true);
            BUILDER.pop();
            BUILDER.push("Lava Bomb Spell");
            LavaballCost = BUILDER.comment("Lava Bomb Spell Cost, Default: 16")
                    .defineInRange("lavaBombCost", 16, 0, Integer.MAX_VALUE);
            LavaballDuration = BUILDER.comment("Time to cast Lava Bomb Spell, Default: 40")
                    .defineInRange("lavaBombTime", 40, 0, 72000);
            LavaballCoolDown = BUILDER.comment("Lava Bomb Spell Cooldown, Default: 40")
                    .defineInRange("lavaBombCoolDown", 40, 0, Integer.MAX_VALUE);
            LavaballDamage = BUILDER.comment("How much base damage Lavaballs deal when directly hitting a mob, Default: 6.0")
                    .defineInRange("lavaballDamage", 6.0, 1.0, Double.MAX_VALUE);
            LavaballGriefing = BUILDER.comment("Enable Lavaball projectile griefing, Default: true")
                    .define("lavaballGriefing", true);
            BUILDER.pop();
            BUILDER.push("Magma Bomb Spell");
            MagmaBombCost = BUILDER.comment("Magma Bomb Spell Cost, Default: 16")
                    .defineInRange("magmaBombCost", 16, 0, Integer.MAX_VALUE);
            MagmaBombDuration = BUILDER.comment("Time to cast Magma Bomb Spell, Default: 100")
                    .defineInRange("magmaBombTime", 100, 0, 72000);
            MagmaBombCoolDown = BUILDER.comment("Magma Bomb Spell Cooldown, Default: 400")
                    .defineInRange("magmaBombCoolDown", 400, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Flame Strike Spell");
            FlameStrikeCost = BUILDER.comment("Flame Strike Spell Cost, Default: 32")
                    .defineInRange("flameStrikeCost", 32, 0, Integer.MAX_VALUE);
            FlameStrikeDuration = BUILDER.comment("Time to cast Flame Strike Spell, Default: 120")
                    .defineInRange("flameStrikeTime", 120, 0, 72000);
            FlameStrikeCoolDown = BUILDER.comment("Flame Strike Spell Cooldown, Default: 200")
                    .defineInRange("flameStrikeCoolDown", 200, 0, Integer.MAX_VALUE);
            FlameStrikeDamage = BUILDER.comment("How much base damage Flame Strike deals, Default: 4.0")
                    .defineInRange("flameStrikeDamage", 4.0, 1.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Soul Bolt Spell");
            SoulBoltCost = BUILDER.comment("Soul Bolt Spell Cost, Default: 4")
                    .defineInRange("soulBoltCost", 4, 0, Integer.MAX_VALUE);
            SoulBoltDuration = BUILDER.comment("Time to cast Soul Bolt Spell, Default: 0")
                    .defineInRange("soulBoltTime", 0, 0, 72000);
            SoulBoltCoolDown = BUILDER.comment("Soul Bolt Spell Cooldown, Default: 20")
                    .defineInRange("soulBoltCoolDown", 20, 0, Integer.MAX_VALUE);
            SoulBoltDamage = BUILDER.comment("How much base damage Soul Bolts deals, Default: 4.0")
                    .defineInRange("soulBoltDamage", 4.0, 1.0, Double.MAX_VALUE);
            NecroBoltDamage = BUILDER.comment("How much base damage Necro Bolts deals, Default: 12.0")
                    .defineInRange("necroBoltDamage", 12.0, 1.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Magic Bolt Spell");
            MagicBoltCost = BUILDER.comment("Magic Bolt Spell Cost, Default: 8")
                    .defineInRange("magicBoltCost", 8, 0, Integer.MAX_VALUE);
            MagicBoltDuration = BUILDER.comment("Time to cast Magic Bolt Spell, Default: 0")
                    .defineInRange("magicBoltTime", 0, 0, 72000);
            MagicBoltCoolDown = BUILDER.comment("Magic Bolt Spell Cooldown, Default: 20")
                    .defineInRange("magicBoltCoolDown", 20, 0, Integer.MAX_VALUE);
            MagicBoltDamage = BUILDER.comment("How much base damage Magic Bolts deals, Default: 4.0")
                    .defineInRange("magicBoltDamage", 4.0, 1.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Sword Spell");
            SwordCost = BUILDER.comment("Sword Spell Cost, Default: 16")
                    .defineInRange("swordCost", 16, 0, Integer.MAX_VALUE);
            SwordDuration = BUILDER.comment("Time to cast Flying Sword Spell, Default: 0")
                    .defineInRange("swordTime", 0, 0, 72000);
            SwordCoolDown = BUILDER.comment("Sword Spell Cooldown, Default: 20")
                    .defineInRange("swordCoolDown", 20, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Ice Spike Spell");
            IceSpikeCost = BUILDER.comment("Ice Spike Spell Cost, Default: 8")
                    .defineInRange("iceSpikeCost", 8, 0, Integer.MAX_VALUE);
            IceSpikeDuration = BUILDER.comment("Time to cast Ice Spike Spell, Default: 0")
                    .defineInRange("iceSpikeTime", 0, 0, 72000);
            IceSpikeCoolDown = BUILDER.comment("Ice Spike Spell Cooldown, Default: 20")
                    .defineInRange("iceSpikeCoolDown", 20, 0, Integer.MAX_VALUE);
            IceSpikeDamage = BUILDER.comment("How much base damage Ice Spike deals, Default: 3.0")
                    .defineInRange("iceSpikeDamage", 3.0, 1.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Ice Storm Spell");
            IceStormCost = BUILDER.comment("Ice Storm Spell Cost, Default: 16")
                    .defineInRange("iceStormCost", 16, 0, Integer.MAX_VALUE);
            IceStormDuration = BUILDER.comment("Time to cast Ice Storm Spell, Default: 40")
                    .defineInRange("iceStormTime", 40, 0, 72000);
            IceStormCoolDown = BUILDER.comment("Ice Storm Spell Cooldown, Default: 100")
                    .defineInRange("iceStormCoolDown", 100, 0, Integer.MAX_VALUE);
            IceStormDamage = BUILDER.comment("How much base damage Ice Storm deals, Default: 5.0")
                    .defineInRange("iceStormDamage", 5.0, 1.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Charge Spell");
            ChargeCost = BUILDER.comment("Charge Spell Cost, Default: 4")
                    .defineInRange("chargeCost", 4, 0, Integer.MAX_VALUE);
            ChargeCoolDown = BUILDER.comment("Charge Spell Cooldown, Default: 20")
                    .defineInRange("chargeCoolDown", 20, 0, Integer.MAX_VALUE);
            ChargeDamage = BUILDER.comment("How much base damage Charge deals after gaining 2 Levels, Default: 4.0")
                    .defineInRange("chargeDamage", 4.0, 1.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Mirror Spell");
            IllusionCost = BUILDER.comment("Mirror Spell Cost, Default: 20")
                    .defineInRange("illusionCost", 20, 0, Integer.MAX_VALUE);
            IllusionDuration = BUILDER.comment("Time to cast Mirror Spell, Default: 40")
                    .defineInRange("illusionTime", 40, 0, 72000);
            IllusionCoolDown = BUILDER.comment("Mirror Spell Cooldown, Default: 340")
                    .defineInRange("illusionCoolDown", 340, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Fire Breath Spell");
            FireBreathCost = BUILDER.comment("Fire Breath Spell Cost per second, Default: 2")
                    .defineInRange("fireBreathCost", 2, 0, Integer.MAX_VALUE);
            FireBreathChargeUp = BUILDER.comment("How many ticks the Fire Breath Spell much charge before casting, Default: 0")
                    .defineInRange("fireBreathChargeUp", 0, 0, Integer.MAX_VALUE);
            FireBreathDamage = BUILDER.comment("How much base damage Fire Breath deals, Default: 2.0")
                    .defineInRange("fireBreathDamage", 2.0, 1.0, Double.MAX_VALUE);
            DragonFireGriefing = BUILDER.comment("Ring of the Dragon Fire Breath deals environmental damage, Default: true")
                    .define("dragonFireGriefing", true);
            BUILDER.pop();
            BUILDER.push("Frost Breath Spell");
            FrostBreathCost = BUILDER.comment("Frost Breath Spell Cost per second, Default: 2")
                    .defineInRange("frostBreathCost", 2, 0, Integer.MAX_VALUE);
            FrostBreathChargeUp = BUILDER.comment("How many ticks the Frost Breath Spell much charge before casting, Default: 0")
                    .defineInRange("frostBreathChargeUp", 0, 0, Integer.MAX_VALUE);
            FrostBreathDamage = BUILDER.comment("How much base damage Frost Breath deals, Default: 1.0")
                    .defineInRange("frostBreathDamage", 1.0, 1.0, Double.MAX_VALUE);
            DragonFrostGriefing = BUILDER.comment("Ring of the Dragon Frost Breath deals environmental damage, Default: true")
                    .define("dragonFrostGriefing", true);
            BUILDER.pop();
            BUILDER.push("Shocking Spell");
            ShockingCost = BUILDER.comment("Shocking Spell Cost per second, Default: 4")
                    .defineInRange("shockingCost", 4, 0, Integer.MAX_VALUE);
            ShockingChargeUp = BUILDER.comment("How many ticks the Shocking Spell much charge before casting, Default: 0")
                    .defineInRange("shockingChargeUp", 0, 0, Integer.MAX_VALUE);
            ShockingDamage = BUILDER.comment("How much base damage Shocking Sparks deals, Default: 2.0")
                    .defineInRange("shockingDamage", 2.0, 1.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Soul Light Spell");
            SoulLightCost = BUILDER.comment("Soul Light Spell Cost, Default: 1")
                    .defineInRange("soulLightCost", 1, 0, Integer.MAX_VALUE);
            SoulLightDuration = BUILDER.comment("Time to cast Soul Light Spell, Default: 0")
                    .defineInRange("soulLightTime", 0, 0, 72000);
            SoulLightCoolDown = BUILDER.comment("Soul Light Spell Cooldown, Default: 10")
                    .defineInRange("soulLightCoolDown", 10, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Glow Light Spell");
            GlowLightCost = BUILDER.comment("Glow Light Spell Cost, Default: 2")
                    .defineInRange("glowLightCost", 2, 0, Integer.MAX_VALUE);
            GlowLightDuration = BUILDER.comment("Time to cast Glow Light Spell, Default: 0")
                    .defineInRange("glowLightTime", 0, 0, 72000);
            GlowLightCoolDown = BUILDER.comment("Glow Light Spell Cooldown, Default: 10")
                    .defineInRange("glowLightCoolDown", 10, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Iceology Spell");
            IceChunkCost = BUILDER.comment("Iceology Spell Cost, Default: 16")
                    .defineInRange("iceChunkCost", 16, 0, Integer.MAX_VALUE);
            IceChunkDuration = BUILDER.comment("Time to cast Iceology Spell, Default: 0")
                    .defineInRange("iceChunkTime", 0, 0, 72000);
            IceChunkCoolDown = BUILDER.comment("Iceology Spell Cooldown, Default: 300")
                    .defineInRange("iceChunkCoolDown", 300, 0, Integer.MAX_VALUE);
            IceChunkDamage = BUILDER.comment("How much base damage Iceology deals, Default: 8.0")
                    .defineInRange("iceChunkDamage", 8.0, 1.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Hail Spell");
            HailCost = BUILDER.comment("Hail Spell Cost, Default: 8")
                    .defineInRange("hailCost", 8, 0, Integer.MAX_VALUE);
            HailDuration = BUILDER.comment("Time to cast Hail Spell, Default: 40")
                    .defineInRange("hailTime", 40, 0, 72000);
            HailCoolDown = BUILDER.comment("Hail Spell Cooldown, Default: 20")
                    .defineInRange("hailCoolDown", 20, 0, Integer.MAX_VALUE);
            HailDamage = BUILDER.comment("How much base damage Hail deals, Default: 1.0")
                    .defineInRange("hailDamage", 1.0, 1.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Frost Nova Spell");
            FrostNovaCost = BUILDER.comment("Frost Nova Spell Cost, Default: 40")
                    .defineInRange("frostNovaCost", 40, 0, Integer.MAX_VALUE);
            FrostNovaDuration = BUILDER.comment("Time to cast Frost Nova Spell, Default: 0")
                    .defineInRange("frostNovaTime", 0, 0, 72000);
            FrostNovaCoolDown = BUILDER.comment("Frost Nova Spell Cooldown, Default: 100")
                    .defineInRange("frostNovaCoolDown", 100, 0, Integer.MAX_VALUE);
            FrostNovaDamage = BUILDER.comment("How much base minimum damage Frost Nova Spell deals, Default: 3.0")
                    .defineInRange("frostNovaMinDamage", 3.0, 1.0, Double.MAX_VALUE);
            FrostNovaMaxDamage = BUILDER.comment("How much base maximum damage Frost Nova Spell deals, Default: 6.0")
                    .defineInRange("frostNovaMaxDamage", 6.0, 2.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Frostborn Spell");
            FrostbornCost = BUILDER.comment("Frostborn Spell Cost, Default: 24")
                    .defineInRange("frostbornCost", 24, 0, Integer.MAX_VALUE);
            FrostbornDuration = BUILDER.comment("Time to cast Frostborn Spell, Default: 20")
                    .defineInRange("frostbornDuration", 20, 0, 72000);
            FrostbornCoolDown = BUILDER.comment("Frostborn Spell Cooldown, Default: 1200")
                    .defineInRange("frostbornCoolDown", 1200, 0, Integer.MAX_VALUE);
            FrostbornSummonDown = BUILDER.comment("Frostborn Spell Summon Down, Default: 300")
                    .defineInRange("frostbornSummonDown", 300, 300, 72000);
            IceGolemLimit = BUILDER.comment("Number of Ice Golems that can exist around the player, Default: 2")
                    .defineInRange("iceGolemLimit", 2, 1, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Barricade Spell");
            BarricadeCost = BUILDER.comment("Barricade Spell Cost, Default: 16")
                    .defineInRange("barricadeCost", 16, 0, Integer.MAX_VALUE);
            BarricadeDuration = BUILDER.comment("Time to cast Barricade Spell, Default: 0")
                    .defineInRange("barricadeTime", 0, 0, 72000);
            BarricadeCoolDown = BUILDER.comment("Barricade Spell Cooldown, Default: 100")
                    .defineInRange("barricadeCoolDown", 100, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Quaking Spell");
            QuakingCost = BUILDER.comment("Quaking Spell Cost, Default: 8")
                    .defineInRange("quakingCost", 8, 0, Integer.MAX_VALUE);
            QuakingDuration = BUILDER.comment("Time to cast Quaking Spell, Default: 20")
                    .defineInRange("quakingTime", 20, 0, 72000);
            QuakingCoolDown = BUILDER.comment("Quaking Spell Cooldown, Default: 0")
                    .defineInRange("quakingCoolDown", 0, 0, Integer.MAX_VALUE);
            QuakingDamage = BUILDER.comment("How much base damage Quaking Spell deals, Default: 4.0")
                    .defineInRange("quakingDamage", 4.0, 1.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Pulverize Spell");
            PulverizeCost = BUILDER.comment("Pulverize Spell Cost, Default: 16")
                    .defineInRange("pulverizeCost", 16, 0, Integer.MAX_VALUE);
            PulverizeCoolDown = BUILDER.comment("Pulverize Spell Cooldown, Default: 20")
                    .defineInRange("pulverizeCoolDown", 20, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Rotation Spell");
            RotationCost = BUILDER.comment("Rotation Spell Cost, Default: 2")
                    .defineInRange("rotationCost", 2, 0, Integer.MAX_VALUE);
            RotationCoolDown = BUILDER.comment("Rotation Spell Cooldown, Default: 0")
                    .defineInRange("rotationCoolDown", 0, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Eruption Spell");
            EruptionCost = BUILDER.comment("Eruption Spell Cost, Default: 32")
                    .defineInRange("eruptionCost", 32, 0, Integer.MAX_VALUE);
            EruptionDuration = BUILDER.comment("Time to cast Eruption Spell, Default: 60")
                    .defineInRange("eruptionTime", 60, 0, 72000);
            EruptionCoolDown = BUILDER.comment("Eruption Spell Cooldown, Default: 100")
                    .defineInRange("eruptionCoolDown", 100, 0, Integer.MAX_VALUE);
            PyroclastDamage = BUILDER.comment("How much base damage Pyroclasts deal when directly hitting a mob, Default: 6.0")
                    .defineInRange("pyroclastDamage", 6.0, 1.0, Double.MAX_VALUE);
            PyroclastGriefing = BUILDER.comment("Enable Pyroclast projectile griefing, Default: true")
                    .define("pyroclastGriefing", true);
            BUILDER.pop();
            BUILDER.push("Scatter Spell");
            ScatterCost = BUILDER.comment("Scatter Spell Cost, Default: 16")
                    .defineInRange("scatterCost", 16, 0, Integer.MAX_VALUE);
            ScatterDuration = BUILDER.comment("Time to cast Scatter Spell, Default: 0")
                    .defineInRange("scatterTime", 0, 0, 72000);
            ScatterCoolDown = BUILDER.comment("Scatter Spell Cooldown, Default: 240")
                    .defineInRange("scatterCoolDown", 240, 0, Integer.MAX_VALUE);
            ScatterMineDamage = BUILDER.comment("How much base damage Scatter Mines deal when directly hitting a mob, Default: 6.0")
                    .defineInRange("scatterMinesDamage", 6.0, 1.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Launching Spell");
            LaunchCost = BUILDER.comment("Launch Spell Cost, Default: 4")
                    .defineInRange("launchCost", 4, 0, Integer.MAX_VALUE);
            LaunchDuration = BUILDER.comment("Time to cast Launching Spell, Default: 0")
                    .defineInRange("launchTime", 0, 0, 72000);
            LaunchCoolDown = BUILDER.comment("Launch Spell Cooldown, Default: 20")
                    .defineInRange("launchCoolDown", 20, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Flight Spell");
            FlyingCost = BUILDER.comment("Flight Spell Cost, Default: 4")
                    .defineInRange("flightCost", 4, 0, Integer.MAX_VALUE);
            FlyingChargeUp = BUILDER.comment("How many ticks the Flight Spell much charge before casting, Default: 0")
                    .defineInRange("flightChargeUp", 0, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Whirlwind Spell");
            WhirlwindCost = BUILDER.comment("Whirlwind Spell Cost, Default: 4")
                    .defineInRange("whirlwindCost", 4, 0, Integer.MAX_VALUE);
            WhirlwindChargeUp = BUILDER.comment("How many ticks the Whirlwind Spell much charge before casting, Default: 0")
                    .defineInRange("whirlwindChargeUp", 0, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Cushion Spell");
            CushionCost = BUILDER.comment("Cushion Spell Cost, Default: 8")
                    .defineInRange("cushionCost", 8, 0, Integer.MAX_VALUE);
            CushionDuration = BUILDER.comment("Time to cast Cushion Spell, Default: 0")
                    .defineInRange("cushionTime", 0, 0, 72000);
            CushionCoolDown = BUILDER.comment("Cushion Spell Cooldown, Default: 40")
                    .defineInRange("cushionCoolDown", 40, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Cyclone Spell");
            CycloneCost = BUILDER.comment("Cyclone Spell Cost, Default: 16")
                    .defineInRange("cycloneCost", 16, 0, Integer.MAX_VALUE);
            CycloneDuration = BUILDER.comment("Time to cast Cyclone Spell, Default: 0")
                    .defineInRange("cycloneTime", 0, 0, 72000);
            CycloneCoolDown = BUILDER.comment("Cyclone Spell Cooldown, Default: 300")
                    .defineInRange("cycloneCoolDown", 300, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Updraft Spell");
            UpdraftCost = BUILDER.comment("Updraft Spell Cost, Default: 8")
                    .defineInRange("updraftCost", 8, 0, Integer.MAX_VALUE);
            UpdraftDuration = BUILDER.comment("Time to cast Updraft Spell, Default: 0")
                    .defineInRange("updraftTime", 0, 0, 72000);
            UpdraftCoolDown = BUILDER.comment("Updraft Spell Cooldown, Default: 60")
                    .defineInRange("updraftCoolDown", 60, 0, Integer.MAX_VALUE);
            UpdraftBlastDamage = BUILDER.comment("How much base damage Updraft Blasts deals, Default: 5.0")
                    .defineInRange("updraftBlastDamage", 5.0, 1.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Wind Blast Spell");
            WindBlastCost = BUILDER.comment("Wind Blast Spell Cost, Default: 4")
                    .defineInRange("windBlastCost", 4, 0, Integer.MAX_VALUE);
            WindBlastDuration = BUILDER.comment("Time to cast Wind Blast Spell, Default: 0")
                    .defineInRange("windBlastTime", 0, 0, 72000);
            WindBlastCoolDown = BUILDER.comment("Wind Blast Spell Cooldown, Default: 60")
                    .defineInRange("windBlastCoolDown", 60, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Swarm Spell");
            SwarmCost = BUILDER.comment("Swarm Spell Cost per second, Default: 2")
                    .defineInRange("swarmCost", 2, 0, Integer.MAX_VALUE);
            SwarmChargeUp = BUILDER.comment("How many ticks the Swarm Spell much charge before casting, Default: 0")
                    .defineInRange("swarmChargeUp", 0, 0, Integer.MAX_VALUE);
            SwarmDamage = BUILDER.comment("How much base damage Swarm deals, Default: 2.0")
                    .defineInRange("swarmDamage", 2.0, 1.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Grapple Spell");
            GrappleCost = BUILDER.comment("Grapple Spell Cost, Default: 4")
                    .defineInRange("grappleCost", 4, 0, Integer.MAX_VALUE);
            GrappleDuration = BUILDER.comment("Time to cast Grapple Spell, Default: 0")
                    .defineInRange("grappleTime", 0, 0, 72000);
            GrappleCoolDown = BUILDER.comment("Grapple Spell Cooldown, Default: 20")
                    .defineInRange("grappleCoolDown", 20, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Overgrowth Spell");
            OvergrowthCost = BUILDER.comment("Overgrowth Spell Cost, Default: 8")
                    .defineInRange("overgrowthCost", 8, 0, Integer.MAX_VALUE);
            OvergrowthDuration = BUILDER.comment("Time to cast Overgrowth Spell, Default: 0")
                    .defineInRange("overgrowthTime", 0, 0, 72000);
            OvergrowthCoolDown = BUILDER.comment("Overgrowth Spell Cooldown, Default: 120")
                    .defineInRange("overgrowthCoolDown", 120, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Entangling Spell");
            EntanglingCost = BUILDER.comment("Entangling Spell Cost, Default: 8")
                    .defineInRange("entanglingCost", 8, 0, Integer.MAX_VALUE);
            EntanglingDuration = BUILDER.comment("Time to cast Entangling Spell, Default: 50")
                    .defineInRange("entanglingTime", 50, 0, 72000);
            EntanglingCoolDown = BUILDER.comment("Entangling Spell Cooldown, Default: 400")
                    .defineInRange("entanglingCoolDown", 400, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Whispering Spell");
            WhisperCost = BUILDER.comment("Whispering Spell Cost, Default: 16")
                    .defineInRange("whisperCost", 16, 0, Integer.MAX_VALUE);
            WhisperDuration = BUILDER.comment("Time to cast Whispering Spell, Default: 100")
                    .defineInRange("whisperTime", 100, 0, 72000);
            WhisperCoolDown = BUILDER.comment("Whispering Spell Cooldown, Default: 1200")
                    .defineInRange("whisperCoolDown", 1200, 0, Integer.MAX_VALUE);
            WhisperSummonDown = BUILDER.comment("Whispering Spell Summon Down, Default: 300")
                    .defineInRange("whisperSummonDown", 300, 300, 72000);
            WhisperLimit = BUILDER.comment("Number of Whisperers that can exist around the player, Default: 4")
                    .defineInRange("whisperLimit", 4, 1, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Thunderbolt Spell");
            ThunderboltCost = BUILDER.comment("Thunderbolt Spell Cost, Default: 16")
                    .defineInRange("thunderboltCost", 16, 0, Integer.MAX_VALUE);
            ThunderboltDuration = BUILDER.comment("Time to cast Thunderbolt Spell, Default: 40")
                    .defineInRange("thunderboltTime", 40, 0, 72000);
            ThunderboltCoolDown = BUILDER.comment("Thunderbolt Spell Cooldown, Default: 0")
                    .defineInRange("thunderboltCoolDown", 0, 0, Integer.MAX_VALUE);
            ThunderboltDamage = BUILDER.comment("How much base damage Thunderbolts deals, Default: 5.0")
                    .defineInRange("thunderboltDamage", 5.0, 1.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Electro Orb Spell");
            ElectroOrbCost = BUILDER.comment("Electro Orb Spell Cost, Default: 8")
                    .defineInRange("electroOrbCost", 8, 0, Integer.MAX_VALUE);
            ElectroOrbDuration = BUILDER.comment("Time to cast Electro Orb Spell, Default: 0")
                    .defineInRange("electroOrbTime", 0, 0, 72000);
            ElectroOrbCoolDown = BUILDER.comment("Electro Orb Spell Cooldown, Default: 40")
                    .defineInRange("electroOrbCoolDown", 40, 0, Integer.MAX_VALUE);
            ElectroOrbDamage = BUILDER.comment("How much base damage Electro Orbs deals, Default: 4.0")
                    .defineInRange("electroOrbDamage", 4.0, 1.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Monsoon Spell");
            MonsoonCost = BUILDER.comment("Monsoon Spell Cost, Default: 8")
                    .defineInRange("monsoonCost", 8, 0, Integer.MAX_VALUE);
            MonsoonDuration = BUILDER.comment("Time to cast Monsoon Spell, Default: 40")
                    .defineInRange("monsoonTime", 40, 0, 72000);
            MonsoonCoolDown = BUILDER.comment("Monsoon Spell Cooldown, Default: 100")
                    .defineInRange("monsoonCoolDown", 100, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Discharge Spell");
            DischargeCost = BUILDER.comment("Discharge Spell Cost, Default: 40")
                    .defineInRange("dischargeCost", 40, 0, Integer.MAX_VALUE);
            DischargeDuration = BUILDER.comment("Time to cast Discharge Spell, Default: 0")
                    .defineInRange("dischargeTime", 0, 0, 72000);
            DischargeCoolDown = BUILDER.comment("Discharge Spell Cooldown, Default: 100")
                    .defineInRange("dischargeCoolDown", 100, 0, Integer.MAX_VALUE);
            DischargeDamage = BUILDER.comment("How much base minimum damage Discharge Spell deals, Default: 6.0")
                    .defineInRange("dischargeMinDamage", 6.0, 1.0, Double.MAX_VALUE);
            DischargeMaxDamage = BUILDER.comment("How much base maximum damage Discharge Spell deals, Default: 12.0")
                    .defineInRange("dischargeMaxDamage", 12.0, 2.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Lightning Spell");
            LightningCost = BUILDER.comment("Lightning Spell Cost, Default: 16")
                    .defineInRange("lightningCost", 16, 0, Integer.MAX_VALUE);
            LightningDuration = BUILDER.comment("Time to cast Lightning Spell, Default: 0")
                    .defineInRange("lightningTime", 0, 0, 72000);
            LightningCoolDown = BUILDER.comment("Lightning Spell Cooldown, Default: 100")
                    .defineInRange("lightningCoolDown", 100, 0, Integer.MAX_VALUE);
            LightningDamage = BUILDER.comment("How much base damage Lightning from the spell deals, Default: 5.0")
                    .defineInRange("lightningDamage", 5.0, 1.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Call Spell");
            CallCost = BUILDER.comment("Call Spell Cost, Default: 16")
                    .defineInRange("callCost", 16, 0, Integer.MAX_VALUE);
            CallDuration = BUILDER.comment("Time to cast Call Spell, Default: 100")
                    .defineInRange("callTime", 100, 0, 72000);
            CallCoolDown = BUILDER.comment("Call Spell Cooldown, Default: 0")
                    .defineInRange("callCoolDown", 0, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Recall Spell");
            RecallCost = BUILDER.comment("Recall Spell Cost, Default: 1000")
                    .defineInRange("recallCost", 1000, 0, Integer.MAX_VALUE);
            RecallDuration = BUILDER.comment("Time to cast Recall Spell, Default: 160")
                    .defineInRange("recallTime", 160, 0, 72000);
            RecallCoolDown = BUILDER.comment("Recall Spell Cooldown, Default: 100")
                    .defineInRange("recallCoolDown", 100, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Ender Chest Spell");
            EnderChestCost = BUILDER.comment("Ender Chest Spell Cost, Default: 8")
                    .defineInRange("enderChestCost", 8, 0, Integer.MAX_VALUE);
            EnderChestDuration = BUILDER.comment("Time to cast Ender Chest Spell, Default: 0")
                    .defineInRange("enderChestDuration", 0, 0, 72000);
            EnderChestCoolDown = BUILDER.comment("Ender Chest Spell Cooldown, Default: 20")
                    .defineInRange("enderChestCoolDown", 20, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Banish Spell");
            BanishCost = BUILDER.comment("Banish Spell Cost, Default: 16")
                    .defineInRange("banishCost", 16, 0, Integer.MAX_VALUE);
            BanishCoolDown = BUILDER.comment("Banish Spell Cooldown, Default: 400")
                    .defineInRange("banishCoolDown", 400, 0, Integer.MAX_VALUE);
            BanishMaxHealth = BUILDER.comment("The highest maximum health an entity can possess to be affected by Banish spell, Default: 100")
                .defineInRange("banishMaxHealth", 100, 1, Integer.MAX_VALUE);
            BanishBlackList = BUILDER.comment("""
                            Add mobs that Banish Spell don't work on.\s
                            To do so, enter the namespace ID of the mob, like "minecraft:zombie, minecraft:skeleton".""")
                .defineList("banishBlackList", Lists.newArrayList(),
                        (itemRaw) -> itemRaw instanceof String);
            BUILDER.pop();
            BUILDER.push("Blink Spell");
            BlinkCost = BUILDER.comment("Blink Spell Cost, Default: 8")
                    .defineInRange("blinkCost", 8, 0, Integer.MAX_VALUE);
            BlinkDuration = BUILDER.comment("Time to cast Blink Spell, Default: 0")
                    .defineInRange("blinkDuration", 0, 0, 72000);
            BlinkCoolDown = BUILDER.comment("Blink Spell Cooldown, Default: 20")
                    .defineInRange("blinkCoolDown", 20, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("End Walker Spell");
            EndWalkCost = BUILDER.comment("End Walk Spell Cost, Default: 8")
                    .defineInRange("endWalkCost", 8, 0, Integer.MAX_VALUE);
            EndWalkDuration = BUILDER.comment("Time to cast End Walk Spell, Default: 60")
                    .defineInRange("endWalkDuration", 60, 0, 72000);
            EndWalkCoolDown = BUILDER.comment("End Walk Spell Cooldown, Default: 200")
                    .defineInRange("endWalkCoolDown", 200, 0, Integer.MAX_VALUE);
            EndWalkEffectDuration = BUILDER.comment("How long the effect is applied from the spell, Default: 200")
                    .defineInRange("endWalkEffectDuration", 200, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Tunnel Spell");
            TunnelCost = BUILDER.comment("Tunnel Spell Cost, Default: 16")
                    .defineInRange("tunnelCost", 16, 0, Integer.MAX_VALUE);
            TunnelCoolDown = BUILDER.comment("Tunnel Spell Cooldown, Default: 100")
                    .defineInRange("tunnelCoolDown", 100, 0, Integer.MAX_VALUE);
            TunnelDefaultLifespan = BUILDER.comment("The initial duration for the Tunnel lifespan in ticks, Default: 100")
                    .defineInRange("tunnelDefaultLifespan", 100, 0, Integer.MAX_VALUE);
            TunnelDefaultDistance = BUILDER.comment("The initial distance of the Tunnel, Default: 8")
                    .defineInRange("tunnelDefaultDistance", 8, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Shockwave Spell");
            ShockwaveCost = BUILDER.comment("Shockwave Spell Cost, Default: 80")
                    .defineInRange("shockwaveCost", 80, 0, Integer.MAX_VALUE);
            ShockwaveDuration = BUILDER.comment("Time to cast Shockwave Spell, Default: 0")
                    .defineInRange("shockwaveTime", 0, 0, 72000);
            ShockwaveCoolDown = BUILDER.comment("Shockwave Spell Cooldown, Default: 80")
                    .defineInRange("shockwaveCoolDown", 80, 0, Integer.MAX_VALUE);
            ShockwaveDamage = BUILDER.comment("How much base minimum damage Shockwave Spell deals, Default: 5.0")
                    .defineInRange("shockwaveMinDamage", 5.0, 1.0, Double.MAX_VALUE);
            ShockwaveMaxDamage = BUILDER.comment("How much base maximum damage Shockwave Spell deals, Default: 24.0")
                    .defineInRange("shockwaveMaxDamage", 24.0, 2.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Iron Hide Spell");
            IronHideCost = BUILDER.comment("Iron Hide Spell Cost, Default: 24")
                    .defineInRange("ironHideCost", 24, 0, Integer.MAX_VALUE);
            IronHideDuration = BUILDER.comment("Time to cast Iron Hide Spell, Default: 60")
                    .defineInRange("ironHideTime", 60, 0, 72000);
            IronHideCoolDown = BUILDER.comment("Iron Hide Spell Cooldown, Default: 500")
                    .defineInRange("ironHideCoolDown", 500, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Bulwark Spell");
            BulwarkCost = BUILDER.comment("Bulwark Spell Cost, Default: 100")
                    .defineInRange("bulwarkCost", 100, 0, Integer.MAX_VALUE);
            BulwarkDuration = BUILDER.comment("Time to cast Bulwark Spell, Default: 100")
                    .defineInRange("bulwarkDuration", 100, 0, 72000);
            BulwarkCoolDown = BUILDER.comment("Bulwark Spell Cooldown, Default: 6000")
                    .defineInRange("bulwarkCoolDown", 6000, 0, Integer.MAX_VALUE);
            BulwarkShieldAmount = BUILDER.comment("Initial amount of shields the Bulwark Spell summons, Default: 4")
                    .defineInRange("bulwarkShieldAmount", 4, 1, Integer.MAX_VALUE);
            BulwarkShieldTime = BUILDER.comment("Initial lifespan of the Bulwark Spell's shields, count in ticks, Default: 1500")
                    .defineInRange("bulwarkShieldTime", 1500, 1, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Soul Heal Spell");
            SoulHealCost = BUILDER.comment("Soul Heal Spell Cost, Default: 250")
                    .defineInRange("soulHealCost", 250, 0, Integer.MAX_VALUE);
            SoulHealDuration = BUILDER.comment("Time to cast Soul Heal Spell, Default: 0")
                    .defineInRange("soulHealDuration", 0, 0, 72000);
            SoulHealCoolDown = BUILDER.comment("Soul Heal Spell Cooldown, Default: 200")
                    .defineInRange("soulHealCoolDown", 200, 0, Integer.MAX_VALUE);
            SoulHealAmount = BUILDER.comment("Initial amount of health the Soul Heal Spell heals, Default: 8")
                    .defineInRange("soulHealAmount", 8, 2, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Telekinesis Spell");
            TelekinesisCost = BUILDER.comment("Telekinesis Spell Cost, Default: 4")
                    .defineInRange("telekinesisCost", 4, 0, Integer.MAX_VALUE);
            TelekinesisChargeUp = BUILDER.comment("How many ticks the Telekinesis Spell much charge before casting, Default: 0")
                    .defineInRange("telekinesisChargeUp", 0, 0, Integer.MAX_VALUE);
            TelekinesisMaxHealth = BUILDER.comment("The highest maximum health an entity can possess to be affected by Telekinesis spell, Default: 100")
                .defineInRange("telekinesisMaxHealth", 100, 1, Integer.MAX_VALUE);
            TelekinesisBlackList = BUILDER.comment("""
                            Add mobs that Telekinesis Spell don't work on.\s
                            To do so, enter the namespace ID of the mob, like "minecraft:zombie, minecraft:skeleton".""")
                    .defineList("telekinesisBlackList", Lists.newArrayList(),
                            (itemRaw) -> itemRaw instanceof String);
            BUILDER.pop();
            BUILDER.push("Sonic Boom Spell");
            SonicBoomCost = BUILDER.comment("Sonic Boom Spell Cost, Default: 16")
                    .defineInRange("sonicBoomCost", 16, 0, Integer.MAX_VALUE);
            SonicBoomDuration = BUILDER.comment("Time to cast Sonic Boom Spell, Default: 60")
                    .defineInRange("sonicBoomTime", 60, 0, 72000);
            SonicBoomCoolDown = BUILDER.comment("Sonic Boom Spell Cooldown, Default: 40")
                    .defineInRange("sonicBoomCoolDown", 40, 0, Integer.MAX_VALUE);
            SonicBoomDamage = BUILDER.comment("How much base damage Sonic Boom Spell deals, Default: 10.0")
                    .defineInRange("sonicBoomDamage", 10.0, 1.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Corruption Spell");
            CorruptionCost = BUILDER.comment("Corruption Spell Cost, Default: 2500")
                    .defineInRange("corruptionCost", 2500, 0, Integer.MAX_VALUE);
            CorruptionChargeUp = BUILDER.comment("How many ticks the Corruption Spell much charge before casting, Default: 0")
                    .defineInRange("corruptionChargeUp", 0, 0, Integer.MAX_VALUE);
            CorruptedBeamDamage = BUILDER.comment("How much base damage Corrupted Beam Spell deals per tick, Default: 10.0")
                    .defineInRange("corruptedBeamDamage", 10.0, 0.0, Double.MAX_VALUE);
            CorruptionImmobile = BUILDER.comment("Whether casting Corruption spell immobilizes the caster, Default: true")
                        .define("corruptionImmobile", true);
            BUILDER.pop();
        BUILDER.pop();
        BUILDER.push("Servant Limits");
        SkeletonLimit = BUILDER.comment("Number of Skeleton/Vanguard/Necromancer Servants that an individual player can have in total, Default: 32")
                .defineInRange("skeletonLimit", 32, 1, Integer.MAX_VALUE);
        BoundIllagerLimit = BUILDER.comment("Number of Bound Illager Servants that an individual player can have in total, Default: 2")
                .defineInRange("boundIllagerLimit", 2, 1, Integer.MAX_VALUE);
        RedstoneGolemLimit = BUILDER.comment("Total number of Redstone Golems an individual player can have, Default: 2")
                .defineInRange("redstoneGolemLimit", 2, 0, Integer.MAX_VALUE);
        GraveGolemLimit = BUILDER.comment("Total number of Grave Golems an individual player can have, Default: 2")
                .defineInRange("graveGolemLimit", 2, 0, Integer.MAX_VALUE);
        RedstoneMonstrosityGlobalLimit = BUILDER.comment("Total number of Redstone Monstrosities that can be summoned in total on a world, Default: 2")
                .defineInRange("redstoneMonstrosityGlobalLimit", 2, 0, Integer.MAX_VALUE);
        RedstoneMonstrosityPlayerLimit = BUILDER.comment("Total number of Redstone Monstrosities an individual player can have, Default: 1")
                .defineInRange("redstoneMonstrosityPlayerLimit", 1, 0, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Enchantments");
        MaxSoulEaterLevel = BUILDER.comment("Soul Eater Maximum Enchantment Level, Default: 5")
                .defineInRange("maxSoulEaterLevel", 5, 1, 10);
        MaxWantingLevel = BUILDER.comment("Wanting Maximum Enchantment Level, Default: 3")
                .defineInRange("maxWantingLevel", 3, 1, 10);
        MaxPotencyLevel = BUILDER.comment("Potency Maximum Enchantment Level, Default: 3")
                .defineInRange("maxPotencyLevel", 3, 1, 10);
        MaxRadiusLevel = BUILDER.comment("Radius Maximum Enchantment Level, Default: 2")
                .defineInRange("maxRadiusLevel", 2, 1, 10);
        MaxRangeLevel = BUILDER.comment("Range Maximum Enchantment Level, Default: 10")
                .defineInRange("maxRangeLevel", 10, 1, 10);
        MaxDurationLevel = BUILDER.comment("Duration Maximum Enchantment Level, Default: 3")
                .defineInRange("maxDurationLevel", 3, 1, 10);
        MaxBurningLevel = BUILDER.comment("Burning Maximum Enchantment Level, Default: 3")
                .defineInRange("maxBurningLevel", 3, 1, 10);
        MaxVelocityLevel = BUILDER.comment("Velocity Maximum Enchantment Level, Default: 5")
                .defineInRange("maxVelocityLevel", 5, 1, 10);
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
