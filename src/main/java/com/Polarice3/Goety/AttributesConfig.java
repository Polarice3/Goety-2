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
    public static final ForgeConfigSpec.ConfigValue<Double> PikerHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> PikerArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> PikerDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> PreacherHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> PreacherHeal;
    public static final ForgeConfigSpec.ConfigValue<Double> ConquillagerHealth;

    public static final ForgeConfigSpec.ConfigValue<Double> WraithHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> WraithDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> NecromancerHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> NecromancerDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> HauntedArmorHealth;

    public static final ForgeConfigSpec.ConfigValue<Double> ZombieServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ZombieServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> SkeletonServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> SkeletonServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> VanguardServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> VanguardServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> VanguardServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> SummonedVexHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> SummonedVexDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> RedstoneGolemHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> RedstoneGolemDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> GraveGolemHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> GraveGolemDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> HauntHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> HauntDamage;

    public static final ForgeConfigSpec.ConfigValue<Double> SkullLordHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> SkullLordDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> BoneLordHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> BoneLordDamage;

    public static final ForgeConfigSpec.ConfigValue<Double> ApostleHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ApostleMagicDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> VizierHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> CroneHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> MinisterHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> MinisterDamage;

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
                BUILDER.push("Piker");
                PikerHealth = BUILDER.comment("How much Max Health Pikers have, Default: 24.0")
                        .defineInRange("pikerHealth", 24.0, 1.0, Double.MAX_VALUE);
                PikerArmor = BUILDER.comment("How much Armor Pikers have, Default: 4.0")
                        .defineInRange("pikerArmor", 4.0, 1.0, Double.MAX_VALUE);
                PikerDamage = BUILDER.comment("How much damage Pikers deals, Default: 9.0")
                        .defineInRange("pikerDamage", 9.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Preacher");
                PreacherHealth = BUILDER.comment("How much Max Health Preachers have, Default: 24.0")
                        .defineInRange("preacherHealth", 24.0, 1.0, Double.MAX_VALUE);
                PreacherHeal = BUILDER.comment("How much health Preachers heal, Default: 6.0")
                        .defineInRange("preacherHeal", 6.0, 1.0, Double.MAX_VALUE);
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
                BUILDER.push("Necromancer");
                NecromancerHealth = BUILDER.comment("How much Max Health Necromancers have, Default: 50.0")
                        .defineInRange("necromancerHealth", 50.0, 1.0, Double.MAX_VALUE);
                NecromancerDamage = BUILDER.comment("How much damage Necromancers deals, Default: 4.0")
                        .defineInRange("necromancerDamage", 4.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Haunted Armor");
                HauntedArmorHealth = BUILDER.comment("How much Max Health Haunted Armor have, Default: 25.0")
                        .defineInRange("hauntedArmorHealth", 25.0, 1.0, Double.MAX_VALUE);
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
                BUILDER.push("Vanguard Servant");
                VanguardServantHealth = BUILDER.comment("How much Max Health Vanguard Servants have, Default: 20.0")
                        .defineInRange("vanguardServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                VanguardServantArmor = BUILDER.comment("How much Armor Vanguard Servants have, Default: 9.0")
                        .defineInRange("vanguardServantArmor", 9.0, 1.0, Double.MAX_VALUE);
                VanguardServantDamage = BUILDER.comment("How much damage Vanguard Servants deals, Default: 7.0")
                        .defineInRange("vanguardServantDamage", 7.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Vex");
                SummonedVexHealth = BUILDER.comment("How much Max Health Summoned Vexes have, Default: 14.0")
                        .defineInRange("summonedVexHealth", 14.0, 1.0, Double.MAX_VALUE);
                SummonedVexDamage = BUILDER.comment("How much damage Summoned Vexes deals, Default: 4.0")
                        .defineInRange("summonedVexDamage", 4.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Redstone Golem");
                RedstoneGolemHealth = BUILDER.comment("How much Max Health Redstone Golems have, Default: 240.0")
                        .defineInRange("redstoneGolemHealth", 240.0, 1.0, Double.MAX_VALUE);
                RedstoneGolemDamage = BUILDER.comment("How much damage Redstone Golems deals, Default: 20.0")
                        .defineInRange("redstoneGolemDamage", 20.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Grave Golem");
                GraveGolemHealth = BUILDER.comment("How much Max Health Grave Golems have, Default: 180.0")
                        .defineInRange("graveGolemHealth", 180.0, 1.0, Double.MAX_VALUE);
                GraveGolemDamage = BUILDER.comment("How much damage Grave Golems deals, Default: 16.0")
                        .defineInRange("graveGolemDamage", 16.0, 1.0, Double.MAX_VALUE);
                HauntHealth = BUILDER.comment("How much Max Health Haunts have, Default: 6.0")
                        .defineInRange("hauntHealth", 6.0, 1.0, Double.MAX_VALUE);
                HauntDamage = BUILDER.comment("How much damage Haunts deals, Default: 2.0")
                        .defineInRange("hauntDamage", 2.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
            BUILDER.pop();
            BUILDER.push("Mini-Bosses");
                BUILDER.push("Crone");
                CroneHealth = BUILDER.comment("How much Max Health Crones have, Default: 130.0")
                        .defineInRange("croneHealth", 130.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Minister");
                MinisterHealth = BUILDER.comment("How much Max Health Ministers have, Default: 128.0")
                        .defineInRange("ministerHealth", 128.0, 1.0, Double.MAX_VALUE);
                MinisterDamage = BUILDER.comment("How much damage Ministers deals, Default: 5.0")
                        .defineInRange("ministerDamage", 5.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Skull Lord");
                SkullLordHealth = BUILDER.comment("How much Max Health Skull Lord have, Default: 100.0")
                        .defineInRange("skullLordHealth", 100.0, 1.0, Double.MAX_VALUE);
                SkullLordDamage = BUILDER.comment("How much damage Skull Lord deals, Default: 6.0")
                        .defineInRange("skullLordDamage", 6.0, 1.0, Double.MAX_VALUE);
                BoneLordHealth = BUILDER.comment("How much Max Health Bone Lord have, Default: 20.0")
                        .defineInRange("boneLordHealth", 20.0, 1.0, Double.MAX_VALUE);
                BoneLordDamage = BUILDER.comment("How much damage Bone Lord deals, Default: 3.0")
                        .defineInRange("boneLordDamage", 3.0, 1.0, Double.MAX_VALUE);
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
