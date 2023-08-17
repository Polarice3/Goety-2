package com.Polarice3.Goety;

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
    public static final ForgeConfigSpec.ConfigValue<Integer> InvisibilityCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> JumpBoostCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> LevitationCost;
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
    public static final ForgeConfigSpec.ConfigValue<Integer> WaterBreathingCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> WeaknessCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> WitherCost;

    public static final ForgeConfigSpec.ConfigValue<Integer> ClimbingCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> PressureCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> EnderGroundCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FlameHandsCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> VenomousHandsCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FreezingCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> GoldTouchedCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> NyctophobiaCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SappedCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SunAllergyCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> TrippingCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> RepulsiveCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> PhotosynthesisCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SwiftSwimCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ArrowmanticCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FlammableCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ExplosiveCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FieryAuraCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> WildRageCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FireTrailCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> PlungeCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> CursedCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SaveEffectsCost;

    public static final ForgeConfigSpec.ConfigValue<Boolean> NyctophobiaCurable;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SunAllergyCurable;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SnowSkinCurable;

    static {
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
        InvisibilityCost = BUILDER.comment("Invisibility Soul Cost, Default: 25")
                .defineInRange("invisibilityCost", 25, 1, Integer.MAX_VALUE);
        JumpBoostCost = BUILDER.comment("Jump Boost Soul Cost, Default: 25")
                .defineInRange("jumpBoostCost", 25, 1, Integer.MAX_VALUE);
        LevitationCost = BUILDER.comment("Levitation Soul Cost, Default: 25")
                .defineInRange("levitationCost", 25, 1, Integer.MAX_VALUE);
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
        WaterBreathingCost = BUILDER.comment("Water Breathing Soul Cost, Default: 25")
                .defineInRange("waterBreathingCost", 25, 1, Integer.MAX_VALUE);
        WeaknessCost = BUILDER.comment("Weakness Soul Cost, Default: 25")
                .defineInRange("weaknessCost", 25, 1, Integer.MAX_VALUE);
        WitherCost = BUILDER.comment("Wither Soul Cost, Default: 25")
                .defineInRange("witherCost", 25, 1, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Modded Soul Cost");
        ClimbingCost = BUILDER.comment("Climbing Soul Cost, Default: 25")
                .defineInRange("climbingCost", 25, 1, Integer.MAX_VALUE);
        PressureCost = BUILDER.comment("Pressure Soul Cost, Default: 10")
                .defineInRange("pressureCost", 10, 1, Integer.MAX_VALUE);
        EnderGroundCost = BUILDER.comment("Ender Ground Soul Cost, Default: 10")
                .defineInRange("enderGroundCost", 10, 1, Integer.MAX_VALUE);
        FlameHandsCost = BUILDER.comment("Flame Hands Soul Cost, Default: 25")
                .defineInRange("flameHandsCost", 25, 1, Integer.MAX_VALUE);
        VenomousHandsCost = BUILDER.comment("Venomous Hands Soul Cost, Default: 25")
                .defineInRange("venomousHandsCost", 25, 1, Integer.MAX_VALUE);
        FreezingCost = BUILDER.comment("Freezing Soul Cost, Default: 25")
                .defineInRange("freezingCost", 25, 1, Integer.MAX_VALUE);
        GoldTouchedCost = BUILDER.comment("Gold Touched Soul Cost, Default: 10")
                .defineInRange("goldTouchedCost", 10, 1, Integer.MAX_VALUE);
        NyctophobiaCost = BUILDER.comment("Nyctophobia Soul Cost, Default: 50")
                .defineInRange("nyctophobiaCost", 50, 1, Integer.MAX_VALUE);
        SappedCost = BUILDER.comment("Sapped Soul Cost, Default: 25")
                .defineInRange("sappedCost", 25, 1, Integer.MAX_VALUE);
        SunAllergyCost = BUILDER.comment("Sun Allergy Soul Cost, Default: 50")
                .defineInRange("sunAllergyCost", 50, 1, Integer.MAX_VALUE);
        TrippingCost = BUILDER.comment("Tripping Soul Cost, Default: 25")
                .defineInRange("trippingCost", 25, 1, Integer.MAX_VALUE);
        RepulsiveCost = BUILDER.comment("Repulsive Soul Cost, Default: 25")
                .defineInRange("repulsiveCost", 25, 1, Integer.MAX_VALUE);
        PhotosynthesisCost = BUILDER.comment("Photosynthesis Soul Cost, Default: 25")
                .defineInRange("photosynthesisCost", 25, 1, Integer.MAX_VALUE);
        SwiftSwimCost = BUILDER.comment("Swift Swim Soul Cost, Default: 25")
                .defineInRange("swiftSwimCost", 25, 1, Integer.MAX_VALUE);
        ArrowmanticCost = BUILDER.comment("Arrowmantic Soul Cost, Default: 50")
                .defineInRange("arrowmanticCost", 50, 1, Integer.MAX_VALUE);
        FlammableCost = BUILDER.comment("Flammable Soul Cost, Default: 50")
                .defineInRange("flammableCost", 50, 1, Integer.MAX_VALUE);
        ExplosiveCost = BUILDER.comment("Explosive Soul Cost, Default: 50")
                .defineInRange("explosiveCost", 50, 1, Integer.MAX_VALUE);
        FieryAuraCost = BUILDER.comment("Fiery Aura Soul Cost, Default: 50")
                .defineInRange("fieryAuraCost", 50, 1, Integer.MAX_VALUE);
        WildRageCost = BUILDER.comment("Wild Rage Soul Cost, Default: 50")
                .defineInRange("wildRageCost", 50, 1, Integer.MAX_VALUE);
        FireTrailCost = BUILDER.comment("Fire Trail Soul Cost, Default: 50")
                .defineInRange("fireTrailCost", 50, 1, Integer.MAX_VALUE);
        PlungeCost = BUILDER.comment("Plunge Soul Cost, Default: 50")
                .defineInRange("plungeCost", 50, 1, Integer.MAX_VALUE);
        CursedCost = BUILDER.comment("Cursed Soul Cost, Default: 50")
                .defineInRange("cursedCost", 50, 1, Integer.MAX_VALUE);
        SaveEffectsCost = BUILDER.comment("Save Effects Soul Cost, Default: 50")
                .defineInRange("saveEffectsCost", 50, 1, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Curable");
        NyctophobiaCurable = BUILDER.comment("Whether Nyctophobia is curable via Milk or Wartful Egg, Default: false")
                .define("nyctophobiaCurable", false);
        SunAllergyCurable = BUILDER.comment("Whether Sun Allergy is curable via Milk or Wartful Egg, Default: false")
                .define("sunAllergyCurable", false);
        SnowSkinCurable = BUILDER.comment("Whether Snow Skin is curable via Milk or Wartful Egg, Default: false")
                .define("snowSkinCurable", false);
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
