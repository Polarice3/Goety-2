package com.Polarice3.Goety.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class BrewConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> AbsorptionCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> BlindnessCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> DarknessCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FireResistanceCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> GlowingCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> HasteCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> HungerCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> HarmingCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> HealingCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> HealthBoostCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> InvisibilityCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> JumpBoostCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> LevitationCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> LuckCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> MiningFatigueCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> NauseaCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> NightVisionCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> PoisonCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> RegenerationCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ResistanceCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SaturationCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SlowFallingCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SlownessCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SpeedCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> StrengthCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> UnluckCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> WaterBreathingCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> WeaknessCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> WitherCost;

    public static final ForgeConfigSpec.ConfigValue<Integer> AbsorptionCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> HarmingCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> HealingCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> HealthBoostCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> LevitationCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> ResistanceCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> WitherCapacity;

    public static final ForgeConfigSpec.ConfigValue<Integer> ClimbingCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> CorpseEaterCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> PressureCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> EnderGroundCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> EnderFluxCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FlameHandsCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> VenomousHandsCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> VoidTouchedCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FreezingCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> GoldTouchedCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> GravityPulseCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> NyctophobiaCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SnowSkinCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SappedCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> StormsWrathCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SunAllergyCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> EvilEyeCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> TrippingCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> RepulsiveCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> PhotosynthesisCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SwiftSwimCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SwirlingCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ArrowmanticCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> BottlingCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FlammableCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FlimsyCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FortunateCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> InsightCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> LeechingCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> RallyingCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> RadianceCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ExplosiveCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FieryAuraCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FrostyAuraCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> WildRageCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FireTrailCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> PlungeCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> CursedCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> DeflectiveCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ShieldingCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SaveEffectsCost;

    public static final ForgeConfigSpec.ConfigValue<Integer> BottlingCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> CorpseEaterCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> CursedCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> DeflectiveCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> EvilEyeCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> ExplosiveCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> FieryAuraCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> FireTrailCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> FortunateCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> FrostyAuraCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> GravityPulseCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> NyctophobiaCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> PlungeCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> RadianceCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> RallyingCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> SaveEffectsCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> ShieldingCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> SnowSkinCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> StormsWrathCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> SunAllergyCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> SwirlingCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> WildRageCapacity;

    public static final ForgeConfigSpec.ConfigValue<Integer> BatBurstCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> BeesCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> BlindJumpCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ChopTreeCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> CombustCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> CorrosionCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> DroughtCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ExplodeCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ExtinguishCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FertilityCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FlayingCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FloodingCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FreezeCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> GrowthCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> GrowCactusCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> GrowCaveVinesCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> GrowTreeCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> HarvestCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> InfestCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> LaunchCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> LeafShellCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> LoveCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> MossifyCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> PartLavaCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> PartWaterCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> PulverizeCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> PurifyDebuffsCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> PurifyBuffsCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> PruningCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> RaiseDeadCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ShearCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SnowyCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> StripArmorCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SweetThornsCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ThornTrapCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> TransposeCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> WebbedCost;

    public static final ForgeConfigSpec.ConfigValue<Integer> BatBurstCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> BeesCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> CombustCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> ExplodeCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> FloodingCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> PurifyDebuffsCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> PurifyBuffsCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> RaiseDeadCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> StripArmorCapacity;
    public static final ForgeConfigSpec.ConfigValue<Integer> WebbedCapacity;

    public static final ForgeConfigSpec.ConfigValue<Integer> BottlingLevelReq;
    public static final ForgeConfigSpec.ConfigValue<Integer> MaxBottlingLevel;

    public static final ForgeConfigSpec.ConfigValue<Boolean> PressureCurable;
    public static final ForgeConfigSpec.ConfigValue<Boolean> NyctophobiaCurable;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SunAllergyCurable;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SnowSkinCurable;
    public static final ForgeConfigSpec.ConfigValue<Boolean> EvilEyeCurable;

    static {
        BUILDER.push("Brew Bottling");
        BottlingLevelReq = BUILDER.comment("How many times the player must bottle a brew to increase Bottling level, Default: 20")
                .defineInRange("bottlingLevelReq", 20, 1, Integer.MAX_VALUE);
        MaxBottlingLevel = BUILDER.comment("Maximum Bottling Level the player can obtain, set to 0 to disable Bottling Levels, Default: 5")
                .defineInRange("maxBottlingLevel", 5, 0, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Vanilla Soul Cost");
        AbsorptionCost = BUILDER.comment("Absorption Soul Cost, Default: 50")
                .defineInRange("absorptionCost", 50, 1, Integer.MAX_VALUE);
        BlindnessCost = BUILDER.comment("Blindness Soul Cost, Default: 10")
                .defineInRange("blindnessCost", 10, 1, Integer.MAX_VALUE);
        DarknessCost = BUILDER.comment("Darkness Soul Cost, Default: 10")
                .defineInRange("darknessCost", 10, 1, Integer.MAX_VALUE);
        FireResistanceCost = BUILDER.comment("Fire Resistance Soul Cost, Default: 25")
                .defineInRange("fireResistanceCost", 25, 1, Integer.MAX_VALUE);
        GlowingCost = BUILDER.comment("Glowing Soul Cost, Default: 10")
                .defineInRange("glowingCost", 10, 1, Integer.MAX_VALUE);
        HasteCost = BUILDER.comment("Haste Soul Cost, Default: 25")
                .defineInRange("hasteCost", 25, 1, Integer.MAX_VALUE);
        HungerCost = BUILDER.comment("Hunger Soul Cost, Default: 10")
                .defineInRange("hungerCost", 10, 1, Integer.MAX_VALUE);
        HarmingCost = BUILDER.comment("Harming Soul Cost, Default: 25")
                .defineInRange("harmingCost", 25, 1, Integer.MAX_VALUE);
        HealingCost = BUILDER.comment("Healing Soul Cost, Default: 25")
                .defineInRange("healingCost", 25, 1, Integer.MAX_VALUE);
        HealthBoostCost = BUILDER.comment("Health Boost Soul Cost, Default: 100")
                .defineInRange("healthBoostCost", 100, 1, Integer.MAX_VALUE);
        InvisibilityCost = BUILDER.comment("Invisibility Soul Cost, Default: 25")
                .defineInRange("invisibilityCost", 25, 1, Integer.MAX_VALUE);
        JumpBoostCost = BUILDER.comment("Jump Boost Soul Cost, Default: 25")
                .defineInRange("jumpBoostCost", 25, 1, Integer.MAX_VALUE);
        LevitationCost = BUILDER.comment("Levitation Soul Cost, Default: 25")
                .defineInRange("levitationCost", 25, 1, Integer.MAX_VALUE);
        LuckCost = BUILDER.comment("Luck Soul Cost, Default: 25")
                .defineInRange("luckCost", 25, 1, Integer.MAX_VALUE);
        MiningFatigueCost = BUILDER.comment("Mining Fatigue Soul Cost, Default: 10")
                .defineInRange("miningFatigueCost", 10, 1, Integer.MAX_VALUE);
        NauseaCost = BUILDER.comment("Nausea Soul Cost, Default: 10")
                .defineInRange("nauseaCost", 10, 1, Integer.MAX_VALUE);
        NightVisionCost = BUILDER.comment("Night Vision Soul Cost, Default: 25")
                .defineInRange("nightVisionCost", 25, 1, Integer.MAX_VALUE);
        PoisonCost = BUILDER.comment("Poison Soul Cost, Default: 10")
                .defineInRange("poisonCost", 10, 1, Integer.MAX_VALUE);
        RegenerationCost = BUILDER.comment("Regeneration Soul Cost, Default: 25")
                .defineInRange("regenerationCost", 25, 1, Integer.MAX_VALUE);
        ResistanceCost = BUILDER.comment("Resistance Soul Cost, Default: 100")
                .defineInRange("resistanceCost", 100, 1, Integer.MAX_VALUE);
        SaturationCost = BUILDER.comment("Saturation Soul Cost, Default: 25")
                .defineInRange("saturationCost", 25, 1, Integer.MAX_VALUE);
        SlowFallingCost = BUILDER.comment("Slow Falling Soul Cost, Default: 25")
                .defineInRange("slowFallingCost", 25, 1, Integer.MAX_VALUE);
        SlownessCost = BUILDER.comment("Slowness Soul Cost, Default: 10")
                .defineInRange("slownessCost", 10, 1, Integer.MAX_VALUE);
        SpeedCost = BUILDER.comment("Speed Soul Cost, Default: 25")
                .defineInRange("speedCost", 25, 1, Integer.MAX_VALUE);
        StrengthCost = BUILDER.comment("Strength Soul Cost, Default: 25")
                .defineInRange("strengthCost", 25, 1, Integer.MAX_VALUE);
        UnluckCost = BUILDER.comment("Unluck Soul Cost, Default: 25")
                .defineInRange("unluckCost", 25, 1, Integer.MAX_VALUE);
        WaterBreathingCost = BUILDER.comment("Water Breathing Soul Cost, Default: 25")
                .defineInRange("waterBreathingCost", 25, 1, Integer.MAX_VALUE);
        WeaknessCost = BUILDER.comment("Weakness Soul Cost, Default: 25")
                .defineInRange("weaknessCost", 25, 1, Integer.MAX_VALUE);
        WitherCost = BUILDER.comment("Wither Soul Cost, Default: 25")
                .defineInRange("witherCost", 25, 1, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Vanilla Extra Capacity");
        AbsorptionCapacity = BUILDER.comment("Absorption Extra Capacity, Default: 1")
                .defineInRange("absorptionCapacity", 1, 0, Integer.MAX_VALUE);
        HarmingCapacity = BUILDER.comment("Harming Extra Capacity, Default: 1")
                .defineInRange("harmingCapacity", 1, 0, Integer.MAX_VALUE);
        HealingCapacity = BUILDER.comment("Healing Extra Capacity, Default: 1")
                .defineInRange("healingCapacity", 1, 0, Integer.MAX_VALUE);
        HealthBoostCapacity = BUILDER.comment("Health Boost Extra Capacity, Default: 2")
                .defineInRange("healthBoostCapacity", 2, 0, Integer.MAX_VALUE);
        LevitationCapacity = BUILDER.comment("Levitation Extra Capacity, Default: 2")
                .defineInRange("levitationCapacity", 2, 0, Integer.MAX_VALUE);
        ResistanceCapacity = BUILDER.comment("Resistance Extra Capacity, Default: 6")
                .defineInRange("resistanceCapacity", 6, 0, Integer.MAX_VALUE);
        WitherCapacity = BUILDER.comment("Wither Extra Capacity, Default: 1")
                .defineInRange("witherCapacity", 1, 0, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Modded Soul Cost");
        BUILDER.push("Status Effects");
        ClimbingCost = BUILDER.comment("Climbing Soul Cost, Default: 25")
                .defineInRange("climbingCost", 25, 1, Integer.MAX_VALUE);
        CorpseEaterCost = BUILDER.comment("Corpse Eater Soul Cost, Default: 100")
                .defineInRange("corpseEaterCost", 100, 1, Integer.MAX_VALUE);
        PressureCost = BUILDER.comment("Pressure Soul Cost, Default: 10")
                .defineInRange("pressureCost", 10, 1, Integer.MAX_VALUE);
        EnderGroundCost = BUILDER.comment("Ender Ground Soul Cost, Default: 10")
                .defineInRange("enderGroundCost", 10, 1, Integer.MAX_VALUE);
        EnderFluxCost = BUILDER.comment("Ender Flux Soul Cost, Default: 25")
                .defineInRange("enderFluxCost", 25, 1, Integer.MAX_VALUE);
        FlameHandsCost = BUILDER.comment("Flame Hands Soul Cost, Default: 25")
                .defineInRange("flameHandsCost", 25, 1, Integer.MAX_VALUE);
        VenomousHandsCost = BUILDER.comment("Venomous Hands Soul Cost, Default: 25")
                .defineInRange("venomousHandsCost", 25, 1, Integer.MAX_VALUE);
        VoidTouchedCost = BUILDER.comment("Void Touched Soul Cost, Default: 25")
                .defineInRange("voidTouchedCost", 25, 1, Integer.MAX_VALUE);
        FreezingCost = BUILDER.comment("Freezing Soul Cost, Default: 25")
                .defineInRange("freezingCost", 25, 1, Integer.MAX_VALUE);
        GoldTouchedCost = BUILDER.comment("Gold Touched Soul Cost, Default: 10")
                .defineInRange("goldTouchedCost", 10, 1, Integer.MAX_VALUE);
        GravityPulseCost = BUILDER.comment("Gravity Pulse Soul Cost, Default: 50")
                .defineInRange("gravityPulseCost", 50, 1, Integer.MAX_VALUE);
        NyctophobiaCost = BUILDER.comment("Nyctophobia Soul Cost, Default: 50")
                .defineInRange("nyctophobiaCost", 50, 1, Integer.MAX_VALUE);
        SnowSkinCost = BUILDER.comment("Snow Skin Soul Cost, Default: 50")
                .defineInRange("snowSkinCost", 50, 1, Integer.MAX_VALUE);
        SappedCost = BUILDER.comment("Sapped Soul Cost, Default: 25")
                .defineInRange("sappedCost", 25, 1, Integer.MAX_VALUE);
        StormsWrathCost = BUILDER.comment("Storm's Wrath Soul Cost, Default: 50")
                .defineInRange("stormsWrathCost", 50, 1, Integer.MAX_VALUE);
        SunAllergyCost = BUILDER.comment("Sun Allergy Soul Cost, Default: 50")
                .defineInRange("sunAllergyCost", 50, 1, Integer.MAX_VALUE);
        EvilEyeCost = BUILDER.comment("Evil Eye Soul Cost, Default: 50")
                .defineInRange("evilEyeCost", 50, 1, Integer.MAX_VALUE);
        TrippingCost = BUILDER.comment("Tripping Soul Cost, Default: 25")
                .defineInRange("trippingCost", 25, 1, Integer.MAX_VALUE);
        RepulsiveCost = BUILDER.comment("Repulsive Soul Cost, Default: 25")
                .defineInRange("repulsiveCost", 25, 1, Integer.MAX_VALUE);
        PhotosynthesisCost = BUILDER.comment("Photosynthesis Soul Cost, Default: 25")
                .defineInRange("photosynthesisCost", 25, 1, Integer.MAX_VALUE);
        SwiftSwimCost = BUILDER.comment("Swift Swim Soul Cost, Default: 25")
                .defineInRange("swiftSwimCost", 25, 1, Integer.MAX_VALUE);
        SwirlingCost = BUILDER.comment("Swirling Soul Cost, Default: 50")
                .defineInRange("swirlingCost", 50, 1, Integer.MAX_VALUE);
        ArrowmanticCost = BUILDER.comment("Arrowmantic Soul Cost, Default: 50")
                .defineInRange("arrowmanticCost", 50, 1, Integer.MAX_VALUE);
        BottlingCost = BUILDER.comment("Bottling Soul Cost, Default: 50")
                .defineInRange("bottlingCost", 50, 1, Integer.MAX_VALUE);
        FlammableCost = BUILDER.comment("Flammable Soul Cost, Default: 50")
                .defineInRange("flammableCost", 50, 1, Integer.MAX_VALUE);
        FlimsyCost = BUILDER.comment("Flimsy Soul Cost, Default: 50")
                .defineInRange("flimsyCost", 50, 1, Integer.MAX_VALUE);
        FortunateCost = BUILDER.comment("Fortunate Soul Cost, Default: 50")
                .defineInRange("fortunateCost", 100, 1, Integer.MAX_VALUE);
        InsightCost = BUILDER.comment("Insight Soul Cost, Default: 50")
                .defineInRange("insightCost", 50, 1, Integer.MAX_VALUE);
        LeechingCost = BUILDER.comment("Leeching Soul Cost, Default: 50")
                .defineInRange("leechingCost", 50, 1, Integer.MAX_VALUE);
        RallyingCost = BUILDER.comment("Rallying Soul Cost, Default: 50")
                .defineInRange("rallyingCost", 50, 1, Integer.MAX_VALUE);
        RadianceCost = BUILDER.comment("Radiance Soul Cost, Default: 50")
                .defineInRange("radianceCost", 50, 1, Integer.MAX_VALUE);
        ExplosiveCost = BUILDER.comment("Explosive Soul Cost, Default: 50")
                .defineInRange("explosiveCost", 50, 1, Integer.MAX_VALUE);
        FieryAuraCost = BUILDER.comment("Fiery Aura Soul Cost, Default: 50")
                .defineInRange("fieryAuraCost", 50, 1, Integer.MAX_VALUE);
        FrostyAuraCost = BUILDER.comment("Frosty Aura Soul Cost, Default: 100")
                .defineInRange("frostyAuraCost", 100, 1, Integer.MAX_VALUE);
        WildRageCost = BUILDER.comment("Wild Rage Soul Cost, Default: 50")
                .defineInRange("wildRageCost", 50, 1, Integer.MAX_VALUE);
        FireTrailCost = BUILDER.comment("Fire Trail Soul Cost, Default: 50")
                .defineInRange("fireTrailCost", 50, 1, Integer.MAX_VALUE);
        PlungeCost = BUILDER.comment("Plunge Soul Cost, Default: 50")
                .defineInRange("plungeCost", 50, 1, Integer.MAX_VALUE);
        CursedCost = BUILDER.comment("Cursed Soul Cost, Default: 50")
                .defineInRange("cursedCost", 50, 1, Integer.MAX_VALUE);
        DeflectiveCost = BUILDER.comment("Deflective Soul Cost, Default: 50")
                .defineInRange("deflectiveCost", 50, 1, Integer.MAX_VALUE);
        ShieldingCost = BUILDER.comment("Shielding Soul Cost, Default: 50")
                .defineInRange("shieldingCost", 50, 1, Integer.MAX_VALUE);
        SaveEffectsCost = BUILDER.comment("Save Effects Soul Cost, Default: 50")
                .defineInRange("saveEffectsCost", 50, 1, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Block/Other Effects");
        BatBurstCost = BUILDER.comment("Bat Burst Soul Cost, Default: 100")
                .defineInRange("batBurstCost", 100, 1, Integer.MAX_VALUE);
        BeesCost = BUILDER.comment("BEEEEES Soul Cost, Default: 50")
                .defineInRange("beesCost", 50, 1, Integer.MAX_VALUE);
        BlindJumpCost = BUILDER.comment("Blind Jump Soul Cost, Default: 10")
                .defineInRange("blindJumpCost", 10, 1, Integer.MAX_VALUE);
        ChopTreeCost = BUILDER.comment("Chop Tree Soul Cost, Default: 25")
                .defineInRange("chopTreeCost", 25, 1, Integer.MAX_VALUE);
        CombustCost = BUILDER.comment("Combust Soul Cost, Default: 100")
                .defineInRange("combustCost", 100, 1, Integer.MAX_VALUE);
        CorrosionCost = BUILDER.comment("Corrosion Soul Cost, Default: 25")
                .defineInRange("corrosionCost", 25, 1, Integer.MAX_VALUE);
        DroughtCost = BUILDER.comment("Drought Soul Cost, Default: 25")
                .defineInRange("droughtCost", 25, 1, Integer.MAX_VALUE);
        ExplodeCost = BUILDER.comment("Explode Soul Cost, Default: 100")
                .defineInRange("explodeCost", 100, 1, Integer.MAX_VALUE);
        ExtinguishCost = BUILDER.comment("Extinguish Soul Cost, Default: 25")
                .defineInRange("extinguishCost", 25, 1, Integer.MAX_VALUE);
        FertilityCost = BUILDER.comment("Fertility Soul Cost, Default: 25")
                .defineInRange("fertilityCost", 25, 1, Integer.MAX_VALUE);
        FlayingCost = BUILDER.comment("Flaying Soul Cost, Default: 25")
                .defineInRange("flayingCost", 25, 1, Integer.MAX_VALUE);
        FloodingCost = BUILDER.comment("Flooding Soul Cost, Default: 100")
                .defineInRange("floodingCost", 100, 1, Integer.MAX_VALUE);
        FreezeCost = BUILDER.comment("Freeze Soul Cost, Default: 10")
                .defineInRange("freezeCost", 10, 1, Integer.MAX_VALUE);
        GrowthCost = BUILDER.comment("Growth Soul Cost, Default: 50")
                .defineInRange("growthCost", 50, 1, Integer.MAX_VALUE);
        GrowCactusCost = BUILDER.comment("Grow Cactus Soul Cost, Default: 10")
                .defineInRange("growCactusCost", 10, 1, Integer.MAX_VALUE);
        GrowCaveVinesCost = BUILDER.comment("Grow Cave Vines Soul Cost, Default: 10")
                .defineInRange("growCaveVinesCost", 10, 1, Integer.MAX_VALUE);
        GrowTreeCost = BUILDER.comment("Grow Tree Soul Cost, Default: 25")
                .defineInRange("growTreeCost", 25, 1, Integer.MAX_VALUE);
        HarvestCost = BUILDER.comment("Harvest Soul Cost, Default: 25")
                .defineInRange("harvestCost", 25, 1, Integer.MAX_VALUE);
        InfestCost = BUILDER.comment("Infest Soul Cost, Default: 25")
                .defineInRange("infestCost", 25, 1, Integer.MAX_VALUE);
        LaunchCost = BUILDER.comment("Launch Soul Cost, Default: 25")
                .defineInRange("launchCost", 25, 1, Integer.MAX_VALUE);
        LeafShellCost = BUILDER.comment("Leaf Shell Soul Cost, Default: 25")
                .defineInRange("leafShellCost", 25, 1, Integer.MAX_VALUE);
        LoveCost = BUILDER.comment("Love Soul Cost, Default: 10")
                .defineInRange("loveCost", 10, 1, Integer.MAX_VALUE);
        MossifyCost = BUILDER.comment("Mossify Soul Cost, Default: 25")
                .defineInRange("mossifyCost", 25, 1, Integer.MAX_VALUE);
        PartLavaCost = BUILDER.comment("Part Lava Soul Cost, Default: 25")
                .defineInRange("partLavaCost", 25, 1, Integer.MAX_VALUE);
        PartWaterCost = BUILDER.comment("Part Water Soul Cost, Default: 25")
                .defineInRange("partWaterCost", 25, 1, Integer.MAX_VALUE);
        PulverizeCost = BUILDER.comment("Pulverize Soul Cost, Default: 25")
                .defineInRange("pulverizeCost", 25, 1, Integer.MAX_VALUE);
        PurifyDebuffsCost = BUILDER.comment("Purify Debuffs Soul Cost, Default: 50")
                .defineInRange("purifyDebuffsCost", 50, 1, Integer.MAX_VALUE);
        PurifyBuffsCost = BUILDER.comment("Purify Buffs Soul Cost, Default: 50")
                .defineInRange("purifyBuffsCost", 50, 1, Integer.MAX_VALUE);
        PruningCost = BUILDER.comment("Pruning Soul Cost, Default: 10")
                .defineInRange("pruningCost", 10, 1, Integer.MAX_VALUE);
        RaiseDeadCost = BUILDER.comment("Raise Dead Soul Cost, Default: 50")
                .defineInRange("raiseDeadCost", 50, 1, Integer.MAX_VALUE);
        ShearCost = BUILDER.comment("Shear Soul Cost, Default: 10")
                .defineInRange("shearCost", 10, 1, Integer.MAX_VALUE);
        SnowyCost = BUILDER.comment("Snowy Soul Cost, Default: 25")
                .defineInRange("snowyCost", 25, 1, Integer.MAX_VALUE);
        StripArmorCost = BUILDER.comment("Strip Armor Soul Cost, Default: 100")
                .defineInRange("stripArmorCost", 100, 1, Integer.MAX_VALUE);
        SweetThornsCost = BUILDER.comment("Sweet Thorns Soul Cost, Default: 25")
                .defineInRange("sweetThornsCost", 25, 1, Integer.MAX_VALUE);
        ThornTrapCost = BUILDER.comment("Thorn Trap Soul Cost, Default: 50")
                .defineInRange("thornTrapCost", 50, 1, Integer.MAX_VALUE);
        TransposeCost = BUILDER.comment("Transpose Soul Cost, Default: 25")
                .defineInRange("transposeCost", 25, 1, Integer.MAX_VALUE);
        WebbedCost = BUILDER.comment("Webbed Soul Cost, Default: 50")
                .defineInRange("webbedCost", 50, 1, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.pop();
        BUILDER.push("Modded Capacity");
        BUILDER.push("Status Effects");
        BottlingCapacity = BUILDER.comment("Bottling Extra Capacity, Default: 4")
                .defineInRange("bottlingCapacity", 4, 0, Integer.MAX_VALUE);
        CorpseEaterCapacity = BUILDER.comment("Corpse Eater Extra Capacity, Default: 4")
                .defineInRange("corpseEaterCapacity", 4, 0, Integer.MAX_VALUE);
        CursedCapacity = BUILDER.comment("Cursed Extra Capacity, Default: 4")
                .defineInRange("cursedCapacity", 4, 0, Integer.MAX_VALUE);
        DeflectiveCapacity = BUILDER.comment("Deflective Extra Capacity, Default: 4")
                .defineInRange("deflectiveCapacity", 4, 0, Integer.MAX_VALUE);
        EvilEyeCapacity = BUILDER.comment("Evil Eye Extra Capacity, Default: 4")
                .defineInRange("evilEyeCapacity", 4, 0, Integer.MAX_VALUE);
        ExplosiveCapacity = BUILDER.comment("Explosive Extra Capacity, Default: 2")
                .defineInRange("explosiveCapacity", 2, 0, Integer.MAX_VALUE);
        FieryAuraCapacity = BUILDER.comment("Fiery Aura Extra Capacity, Default: 2")
                .defineInRange("fieryAuraCapacity", 2, 0, Integer.MAX_VALUE);
        FireTrailCapacity = BUILDER.comment("Fire Trail Extra Capacity, Default: 4")
                .defineInRange("fireTrailCapacity", 4, 0, Integer.MAX_VALUE);
        FortunateCapacity = BUILDER.comment("Fortunate Extra Capacity, Default: 6")
                .defineInRange("fortunateCapacity", 6, 0, Integer.MAX_VALUE);
        FrostyAuraCapacity = BUILDER.comment("Frosty Aura Extra Capacity, Default: 2")
                .defineInRange("frostyAuraCapacity", 2, 0, Integer.MAX_VALUE);
        GravityPulseCapacity = BUILDER.comment("Gravity Pulse Extra Capacity, Default: 2")
                .defineInRange("gravityPulseCapacity", 2, 0, Integer.MAX_VALUE);
        NyctophobiaCapacity = BUILDER.comment("Nyctophobia Extra Capacity, Default: 4")
                .defineInRange("nyctophobiaCapacity", 4, 0, Integer.MAX_VALUE);
        PlungeCapacity = BUILDER.comment("Plunge Extra Capacity, Default: 4")
                .defineInRange("plungeCapacity", 4, 0, Integer.MAX_VALUE);
        RadianceCapacity = BUILDER.comment("Radiance Extra Capacity, Default: 2")
                .defineInRange("radianceCapacity", 2, 0, Integer.MAX_VALUE);
        RallyingCapacity = BUILDER.comment("Rallying Extra Capacity, Default: 2")
                .defineInRange("rallyingCapacity", 2, 0, Integer.MAX_VALUE);
        SaveEffectsCapacity = BUILDER.comment("Save Effects Extra Capacity, Default: 2")
                .defineInRange("saveEffectsCapacity", 8, 0, Integer.MAX_VALUE);
        ShieldingCapacity = BUILDER.comment("Shielding Extra Capacity, Default: 2")
                .defineInRange("shieldingCapacity", 2, 0, Integer.MAX_VALUE);
        SnowSkinCapacity = BUILDER.comment("Snow Skin Extra Capacity, Default: 4")
                .defineInRange("snowSkinCapacity", 4, 0, Integer.MAX_VALUE);
        StormsWrathCapacity = BUILDER.comment("Storm's Wrath Extra Capacity, Default: 4")
                .defineInRange("stormsWrathCapacity", 4, 0, Integer.MAX_VALUE);
        SunAllergyCapacity = BUILDER.comment("Sun Allergy Extra Capacity, Default: 4")
                .defineInRange("sunAllergyCapacity", 4, 0, Integer.MAX_VALUE);
        SwirlingCapacity = BUILDER.comment("Swirling Extra Capacity, Default: 2")
                .defineInRange("swirlingCapacity", 2, 0, Integer.MAX_VALUE);
        WildRageCapacity = BUILDER.comment("Wild Rage Extra Capacity, Default: 2")
                .defineInRange("wildRageCapacity", 2, 0, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Block/Other Effects");
        BatBurstCapacity = BUILDER.comment("Bat Burst Extra Capacity, Default: 2")
                .defineInRange("batBurstCapacity", 2, 0, Integer.MAX_VALUE);
        BeesCapacity = BUILDER.comment("BEEEEES Extra Capacity, Default: 1")
                .defineInRange("beesCapacity", 1, 0, Integer.MAX_VALUE);
        CombustCapacity = BUILDER.comment("Combust Extra Capacity, Default: 1")
                .defineInRange("combustCapacity", 1, 0, Integer.MAX_VALUE);
        ExplodeCapacity = BUILDER.comment("Explode Extra Capacity, Default: 4")
                .defineInRange("explodeCapacity", 4, 0, Integer.MAX_VALUE);
        FloodingCapacity = BUILDER.comment("Flooding Extra Capacity, Default: 2")
                .defineInRange("floodingCapacity", 2, 0, Integer.MAX_VALUE);
        PurifyDebuffsCapacity = BUILDER.comment("Purify Debuffs Extra Capacity, Default: 2")
                .defineInRange("purifyDebuffsCapacity", 2, 0, Integer.MAX_VALUE);
        PurifyBuffsCapacity = BUILDER.comment("Purify Buffs Extra Capacity, Default: 2")
                .defineInRange("purifyBuffsCapacity", 2, 0, Integer.MAX_VALUE);
        RaiseDeadCapacity = BUILDER.comment("Raise Dead Extra Capacity, Default: 2")
                .defineInRange("raiseDeadCapacity", 2, 0, Integer.MAX_VALUE);
        StripArmorCapacity = BUILDER.comment("Strip Armor Extra Capacity, Default: 2")
                .defineInRange("stripArmorCapacity", 2, 0, Integer.MAX_VALUE);
        WebbedCapacity = BUILDER.comment("Webbed Extra Capacity, Default: 1")
                .defineInRange("webbedCapacity", 1, 0, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.pop();
        BUILDER.push("Curable");
        PressureCurable = BUILDER.comment("Whether Pressure is curable via Milk or Wartful Egg, Default: false")
                .define("pressureCurable", false);
        NyctophobiaCurable = BUILDER.comment("Whether Nyctophobia is curable via Milk or Wartful Egg, Default: false")
                .define("nyctophobiaCurable", false);
        SunAllergyCurable = BUILDER.comment("Whether Sun Allergy is curable via Milk or Wartful Egg, Default: false")
                .define("sunAllergyCurable", false);
        SnowSkinCurable = BUILDER.comment("Whether Snow Skin is curable via Milk or Wartful Egg, Default: false")
                .define("snowSkinCurable", false);
        EvilEyeCurable = BUILDER.comment("Whether Evil Eye is curable via Milk or Wartful Egg, Default: false")
                .define("evilEyeCurable", false);
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
