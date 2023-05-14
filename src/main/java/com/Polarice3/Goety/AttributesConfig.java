package com.Polarice3.Goety;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class AttributesConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> OverrideAttributes;

    public static final ForgeConfigSpec.ConfigValue<Double> ZombieVillagerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ZombieVillagerServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> SkeletonVillagerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> SkeletonVillagerServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ZPiglinServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ZPiglinServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ZPiglinBruteServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ZPiglinBruteServantDamage;

    public static final ForgeConfigSpec.ConfigValue<Double> MalghastHealth;

    public static final ForgeConfigSpec.ConfigValue<Double> EnviokerHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> EnviokerDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> TormentorHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> TormentorDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> InquillagerHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> InquillagerDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ConquillagerHealth;

    public static final ForgeConfigSpec.ConfigValue<Double> WraithHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> WraithDamage;

    public static final ForgeConfigSpec.ConfigValue<Double> ZombieServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ZombieServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> SkeletonServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> SkeletonServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> SummonedVexHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> SummonedVexDamage;

    public static final ForgeConfigSpec.ConfigValue<Double> ApostleHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ApostleMagicDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> VizierHealth;

    public static final ForgeConfigSpec.ConfigValue<Integer> ApostleDamageCap;
    public static final ForgeConfigSpec.ConfigValue<Integer> ApostleBowDamage;
    public static final ForgeConfigSpec.ConfigValue<Integer> VizierDamageCap;

    static {
        BUILDER.push("General");
        OverrideAttributes = BUILDER.comment("Allows newly Configured Attributes to override pre-existing ones, Default: true")
                        .define("overrideAttributes", true);
        BUILDER.pop();
        BUILDER.push("Attributes");
            BUILDER.push("Cultists");
            MalghastHealth = BUILDER.comment("How much Max Health Malghast have, Default: 20.0")
                    .defineInRange("malghastHealth", 20.0, 1.0, Double.MAX_VALUE);
                BUILDER.push("Zombie Villager Servant");
                ZombieVillagerServantHealth = BUILDER.comment("How much Max Health Zombie Villager Servants have, Default: 20.0")
                        .defineInRange("zombieVillagerServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                ZombieVillagerServantDamage = BUILDER.comment("How much damage Zombie Villager Servants deals, Default: 3.0")
                        .defineInRange("zombieVillagerServantDamage", 3.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Skeleton Villager Servant");
                SkeletonVillagerServantHealth = BUILDER.comment("How much Max Health Skeleton Villager Servants have, Default: 20.0")
                        .defineInRange("skeletonVillagerServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                SkeletonVillagerServantDamage = BUILDER.comment("How much damage Skeleton Villager Servants deals, Default: 2.0")
                        .defineInRange("skeletonVillagerServantDamage", 2.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Zombified Piglin Servant");
                ZPiglinServantHealth = BUILDER.comment("How much Max Health Zombified Piglin Servants have, Default: 20.0")
                        .defineInRange("zombifiedPiglinServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                ZPiglinServantDamage = BUILDER.comment("How much damage Zombified Piglin Servants deals, Default: 5.0")
                        .defineInRange("zombifiedPiglinServantDamage", 5.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Zombified Piglin Brute Servant");
                ZPiglinBruteServantHealth = BUILDER.comment("How much Max Health Zombified Piglin Brute Servants have, Default: 50.0")
                        .defineInRange("zombifiedPiglinBruteServantHealth", 50.0, 1.0, Double.MAX_VALUE);
                ZPiglinBruteServantDamage = BUILDER.comment("How much damage Zombified Piglin Brute Servants deals, Default: 7.0")
                        .defineInRange("zombifiedPiglinBruteServantDamage", 7.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
            BUILDER.pop();
            BUILDER.push("Illagers");
                BUILDER.push("Envioker");
                EnviokerHealth = BUILDER.comment("How much Max Health Envioker have, Default: 24.0")
                        .defineInRange("enviokerHealth", 24.0, 1.0, Double.MAX_VALUE);
                EnviokerDamage = BUILDER.comment("How much damage Envioker deals, Default: 5.0")
                        .defineInRange("enviokerDamage", 5.0, 1.0, Double.MAX_VALUE);
                TormentorHealth = BUILDER.comment("How much Max Health Tormentor have, Default: 24.0")
                        .defineInRange("tormentorHealth", 24.0, 1.0, Double.MAX_VALUE);
                TormentorDamage = BUILDER.comment("How much damage Tormentor deals, Default: 4.0")
                        .defineInRange("tormentorDamage", 4.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Inquillager");
                InquillagerHealth = BUILDER.comment("How much Max Health Inquillager have, Default: 24.0")
                        .defineInRange("inquillagerHealth", 24.0, 1.0, Double.MAX_VALUE);
                InquillagerDamage = BUILDER.comment("How much damage Inquillager deals, Default: 5.0")
                        .defineInRange("inquillagerDamage", 5.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
            ConquillagerHealth = BUILDER.comment("How much Max Health Conquillager have, Default: 24.0")
                    .defineInRange("conquillagerHealth", 24.0, 1.0, Double.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("General");
                BUILDER.push("Wraith");
                WraithHealth = BUILDER.comment("How much Max Health Wraiths have, Default: 25.0")
                        .defineInRange("wraithHealth", 25.0, 1.0, Double.MAX_VALUE);
                WraithDamage = BUILDER.comment("How much damage Wraith deals, Default: 4.0")
                        .defineInRange("wraithDamage", 4.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
            BUILDER.pop();
            BUILDER.push("Summoned Mobs");
                BUILDER.push("Zombie Servant");
                ZombieServantHealth = BUILDER.comment("How much Max Health Zombie Servants and variants have, Default: 20.0")
                        .defineInRange("zombieServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                ZombieServantDamage = BUILDER.comment("How much damage Zombie Servants and variants deals, Default: 3.0")
                        .defineInRange("zombieServantDamage", 3.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Skeleton Servant");
                SkeletonServantHealth = BUILDER.comment("How much Max Health Skeleton Servants and variants have, Default: 20.0")
                        .defineInRange("skeletonServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                SkeletonServantDamage = BUILDER.comment("How much damage Skeleton Servants and variants deals, Default: 3.0")
                        .defineInRange("skeletonServantDamage", 3.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Vex");
                SummonedVexHealth = BUILDER.comment("How much Max Health Summoned Vexes have, Default: 14.0")
                        .defineInRange("summonedVexHealth", 14.0, 1.0, Double.MAX_VALUE);
                SummonedVexDamage = BUILDER.comment("How much damage Summoned Vexes deals, Default: 4.0")
                        .defineInRange("summonedVexDamage", 4.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
            BUILDER.pop();
            BUILDER.push("Bosses");
                BUILDER.push("Vizier");
                VizierHealth = BUILDER.comment("How much Max Health Viziers have, Default: 200.0")
                        .defineInRange("vizierHealth", 200.0, 100.0, Double.MAX_VALUE);
                VizierDamageCap = BUILDER.comment("The maximum amount of damage a Vizier can attain per hit, Default: 20")
                        .defineInRange("vizierDamageCap", 20, 1, Integer.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Apostle");
                ApostleHealth = BUILDER.comment("How much Max Health Apostles have, Default: 320.0")
                        .defineInRange("apostleHealth", 320.0, 100.0, Double.MAX_VALUE);
                ApostleDamageCap = BUILDER.comment("The maximum amount of damage an Apostle can attain per hit, Default: 20")
                        .defineInRange("apostleDamageCap", 20, 1, Integer.MAX_VALUE);
                ApostleBowDamage = BUILDER.comment("Multiplies Apostle's Bow damage, Default: 4")
                        .defineInRange("apostleBowDamage", 4, 2, Integer.MAX_VALUE);
                ApostleMagicDamage = BUILDER.comment("Set Apostle's spell damage (ie, Fire Tornadoes, Fire Blasts, Roars), Default: 6.0")
                        .defineInRange("apostleMagicDamage", 6.0, 6.0, Double.MAX_VALUE);
                BUILDER.pop();
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
