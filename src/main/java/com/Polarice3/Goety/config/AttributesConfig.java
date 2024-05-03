package com.Polarice3.Goety.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class AttributesConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Double> SkeletonVillagerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> SkeletonVillagerServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ZPiglinServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ZPiglinServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> ZPiglinServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ZPiglinBruteServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ZPiglinBruteServantArmor;
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
    public static final ForgeConfigSpec.ConfigValue<Double> TramplerHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> TramplerDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> CrusherHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> CrusherDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> StormCasterHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> StormCasterDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> CryologerHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> CryologerDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> PreacherHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> PreacherHeal;
    public static final ForgeConfigSpec.ConfigValue<Double> ConquillagerHealth;

    public static final ForgeConfigSpec.ConfigValue<Double> WraithHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> WraithDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> NecromancerHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> NecromancerDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> HauntedArmorHealth;

    public static final ForgeConfigSpec.ConfigValue<Double> ZombieServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ZombieServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> ZombieServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> HuskServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> HuskServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> HuskServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> DrownedServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> DrownedServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> DrownedServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> FrozenZombieServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> FrozenZombieServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> FrozenZombieServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> JungleZombieServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> JungleZombieServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> JungleZombieServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> SkeletonServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> SkeletonServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> StrayServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> StrayServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> MossySkeletonServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> MossySkeletonServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> SunkenSkeletonServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> SunkenSkeletonServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> VanguardServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> VanguardServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> VanguardServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> SkeletonPillagerHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> SkeletonPillagerDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ZombieVindicatorHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ZombieVindicatorArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> ZombieVindicatorDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> BoundEvokerHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> SummonedVexHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> SummonedVexDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> MiniGhastHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> MiniGhastDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> WhispererHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> WhispererDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> LeapleafHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> LeapleafDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> QuickGrowingVineHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> PoisonQuillVineHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> PoisonQuillVineDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> IceGolemHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> IceGolemArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> IceGolemDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> IceGolemFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> SquallGolemHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> SquallGolemArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> SquallGolemDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> SquallGolemFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> RedstoneGolemHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> RedstoneGolemArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> RedstoneGolemDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> RedstoneGolemFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> GraveGolemHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> GraveGolemArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> GraveGolemDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> GraveGolemFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> HauntHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> HauntArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> HauntDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> HauntFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> RedstoneMonstrosityHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> RedstoneMonstrosityArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> RedstoneMonstrosityDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> RedstoneMonstrosityFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> RedstoneMonstrosityHPPercentDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> RedstoneMonstrosityDamageCap;
    public static final ForgeConfigSpec.ConfigValue<Double> RedstoneCubeHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> RedstoneCubeArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> RedstoneCubeDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> RedstoneCubeFollowRange;

    public static final ForgeConfigSpec.ConfigValue<Double> SkullLordHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> SkullLordDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> BoneLordHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> BoneLordDamage;

    public static final ForgeConfigSpec.ConfigValue<Double> ApostleHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ApostleArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> ApostleToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> ApostleMagicDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> VizierHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> CroneHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> MinisterHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> MinisterDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> ApostleDamageCap;
    public static final ForgeConfigSpec.ConfigValue<Integer> ApostleBowDamage;
    public static final ForgeConfigSpec.ConfigValue<Integer> VizierDamageCap;

    static {
        BUILDER.push("Attributes");
            BUILDER.push("Cultists");
            MalghastHealth = BUILDER.comment("How much Max Health Malghast have, Default: 20.0")
                    .defineInRange("malghastHealth", 20.0, 1.0, Double.MAX_VALUE);
                BUILDER.push("Skeleton Villager Servant");
                SkeletonVillagerServantHealth = BUILDER.comment("How much Max Health Skeleton Villager Servants have, Default: 20.0")
                        .defineInRange("skeletonVillagerServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                SkeletonVillagerServantDamage = BUILDER.comment("How much damage Skeleton Villager Servants deals, Default: 2.0")
                        .defineInRange("skeletonVillagerServantDamage", 2.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Zombified Piglin Servant");
                ZPiglinServantHealth = BUILDER.comment("How much Max Health Zombified Piglin Servants have, Default: 20.0")
                        .defineInRange("zombifiedPiglinServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                ZPiglinServantArmor = BUILDER.comment("How much natural armor points Zombified Piglin Servants have, Default: 2.0")
                        .defineInRange("zombifiedPiglinServantArmor", 2.0, 1.0, Double.MAX_VALUE);
                ZPiglinServantDamage = BUILDER.comment("How much damage Zombified Piglin Servants deals, Default: 5.0")
                        .defineInRange("zombifiedPiglinServantDamage", 5.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Zombified Piglin Brute Servant");
                ZPiglinBruteServantHealth = BUILDER.comment("How much Max Health Zombified Piglin Brute Servants have, Default: 50.0")
                        .defineInRange("zombifiedPiglinBruteServantHealth", 50.0, 1.0, Double.MAX_VALUE);
                ZPiglinBruteServantArmor = BUILDER.comment("How much natural armor points Zombified Piglin Brute Servants have, Default: 2.0")
                        .defineInRange("zombifiedPiglinBruteServantArmor", 2.0, 1.0, Double.MAX_VALUE);
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
                BUILDER.push("Trampler");
                TramplerHealth = BUILDER.comment("How much Max Health Tramplers have, Default: 30.0")
                        .defineInRange("tramplerHealth", 30.0, 1.0, Double.MAX_VALUE);
                TramplerDamage = BUILDER.comment("How much damage Tramplers deals, Default: 6.0")
                        .defineInRange("tramplerDamage", 6.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Crusher");
                CrusherHealth = BUILDER.comment("How much Max Health Crushers have, Default: 24.0")
                        .defineInRange("crusherHealth", 24.0, 1.0, Double.MAX_VALUE);
                CrusherDamage = BUILDER.comment("How much damage Crushers deals, Default: 10.0")
                        .defineInRange("crusherDamage", 10.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Storm Caster");
                StormCasterHealth = BUILDER.comment("How much Max Health Storm Casters have, Default: 24.0")
                        .defineInRange("stormCasterHealth", 24.0, 1.0, Double.MAX_VALUE);
                StormCasterDamage = BUILDER.comment("How much damage Storm Casters deals, Default: 5.0")
                        .defineInRange("stormCasterDamage", 5.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Cryologer");
                CryologerHealth = BUILDER.comment("How much Max Health Cryologers have, Default: 24.0")
                        .defineInRange("cryologerHealth", 24.0, 1.0, Double.MAX_VALUE);
                CryologerDamage = BUILDER.comment("How much damage Cryologers deals, Default: 5.0")
                        .defineInRange("cryologerDamage", 5.0, 1.0, Double.MAX_VALUE);
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
                ZombieServantHealth = BUILDER.comment("How much Max Health Zombie Servants have, Default: 20.0")
                        .defineInRange("zombieServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                ZombieServantArmor = BUILDER.comment("How much natural armor points Zombie Servants have, Default: 2.0")
                        .defineInRange("zombieServantArmor", 2.0, 1.0, Double.MAX_VALUE);
                ZombieServantDamage = BUILDER.comment("How much damage Zombie Servants deals, Default: 3.0")
                        .defineInRange("zombieServantDamage", 3.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Husk Servant");
                HuskServantHealth = BUILDER.comment("How much Max Health Husk Servants have, Default: 20.0")
                        .defineInRange("huskServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                HuskServantArmor = BUILDER.comment("How much natural armor points Husk Servants have, Default: 2.0")
                        .defineInRange("huskServantArmor", 2.0, 1.0, Double.MAX_VALUE);
                HuskServantDamage = BUILDER.comment("How much damage Husk Servants deals, Default: 3.0")
                        .defineInRange("huskServantDamage", 3.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Drowned Servant");
                DrownedServantHealth = BUILDER.comment("How much Max Health Drowned Servants have, Default: 20.0")
                        .defineInRange("drownedServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                DrownedServantArmor = BUILDER.comment("How much natural armor points Drowned Servants have, Default: 2.0")
                        .defineInRange("drownedServantArmor", 2.0, 1.0, Double.MAX_VALUE);
                DrownedServantDamage = BUILDER.comment("How much damage Drowned Servants deals, Default: 3.0")
                        .defineInRange("drownedServantDamage", 3.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Frozen Zombie Servant");
                FrozenZombieServantHealth = BUILDER.comment("How much Max Health Frozen Zombie Servants have, Default: 20.0")
                        .defineInRange("frozenZombieServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                FrozenZombieServantArmor = BUILDER.comment("How much natural armor points Frozen Zombie Servants have, Default: 2.0")
                        .defineInRange("frozenZombieServantArmor", 2.0, 1.0, Double.MAX_VALUE);
                FrozenZombieServantDamage = BUILDER.comment("How much damage Frozen Zombie Servants deals, Default: 3.0")
                        .defineInRange("frozenZombieServantDamage", 3.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Jungle Zombie Servant");
                JungleZombieServantHealth = BUILDER.comment("How much Max Health Jungle Zombie Servants have, Default: 20.0")
                        .defineInRange("jungleZombieServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                JungleZombieServantArmor = BUILDER.comment("How much natural armor points Jungle Zombie Servants have, Default: 2.0")
                        .defineInRange("jungleZombieServantArmor", 2.0, 1.0, Double.MAX_VALUE);
                JungleZombieServantDamage = BUILDER.comment("How much damage Jungle Zombie Servants deals, Default: 3.0")
                        .defineInRange("jungleZombieServantDamage", 3.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Skeleton Servant");
                SkeletonServantHealth = BUILDER.comment("How much Max Health Skeleton Servants have, Default: 20.0")
                        .defineInRange("skeletonServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                SkeletonServantDamage = BUILDER.comment("How much damage Skeleton Servants deals, Default: 2.0")
                        .defineInRange("skeletonServantDamage", 2.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Stray Servant");
                StrayServantHealth = BUILDER.comment("How much Max Health Stray Servants have, Default: 20.0")
                        .defineInRange("strayServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                StrayServantDamage = BUILDER.comment("How much damage Stray Servants deals, Default: 2.0")
                        .defineInRange("strayServantDamage", 2.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Mossy Skeleton Servant");
                MossySkeletonServantHealth = BUILDER.comment("How much Max Health Mossy Skeleton Servants have, Default: 20.0")
                        .defineInRange("mossySkeletonServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                MossySkeletonServantDamage = BUILDER.comment("How much damage Mossy Skeleton Servants deals, Default: 2.0")
                        .defineInRange("mossySkeletonServantDamage", 2.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Sunken Skeleton Servant");
                SunkenSkeletonServantHealth = BUILDER.comment("How much Max Health Sunken Skeleton Servants have, Default: 20.0")
                        .defineInRange("sunkenSkeletonServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                SunkenSkeletonServantDamage = BUILDER.comment("How much damage Sunken Skeleton Servants deals, Default: 2.0")
                        .defineInRange("sunkenSkeletonServantDamage", 2.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Vanguard Servant");
                VanguardServantHealth = BUILDER.comment("How much Max Health Vanguard Servants have, Default: 20.0")
                        .defineInRange("vanguardServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                VanguardServantArmor = BUILDER.comment("How much Armor Vanguard Servants have, Default: 9.0")
                        .defineInRange("vanguardServantArmor", 9.0, 1.0, Double.MAX_VALUE);
                VanguardServantDamage = BUILDER.comment("How much damage Vanguard Servants deals, Default: 7.0")
                        .defineInRange("vanguardServantDamage", 7.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Skeleton Pillager");
                SkeletonPillagerHealth = BUILDER.comment("How much Max Health Skeleton Pillagers have, Default: 24.0")
                        .defineInRange("skeletonPillagerHealth", 24.0, 1.0, Double.MAX_VALUE);
                SkeletonPillagerDamage = BUILDER.comment("How much damage Skeleton Pillagers deals, Default: 5.0")
                        .defineInRange("skeletonPillagerDamage", 5.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Zombie Vindicator");
                ZombieVindicatorHealth = BUILDER.comment("How much Max Health Zombie Vindicators have, Default: 24.0")
                        .defineInRange("zombieVindicatorHealth", 24.0, 1.0, Double.MAX_VALUE);
                ZombieVindicatorArmor = BUILDER.comment("How much natural armor points Zombie Vindicators have, Default: 2.0")
                        .defineInRange("zombieVindicatorArmor", 2.0, 1.0, Double.MAX_VALUE);
                ZombieVindicatorDamage = BUILDER.comment("How much damage Zombie Vindicators deals, Default: 5.0")
                        .defineInRange("zombieVindicatorDamage", 5.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Bound Evoker");
                BoundEvokerHealth = BUILDER.comment("How much Max Health Bound Evokers have, Default: 24.0")
                        .defineInRange("boundEvokerHealth", 24.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Vex");
                SummonedVexHealth = BUILDER.comment("How much Max Health Summoned Vexes have, Default: 14.0")
                        .defineInRange("summonedVexHealth", 14.0, 1.0, Double.MAX_VALUE);
                SummonedVexDamage = BUILDER.comment("How much damage Summoned Vexes deals, Default: 4.0")
                        .defineInRange("summonedVexDamage", 4.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Mini-Ghast");
                MiniGhastHealth = BUILDER.comment("How much Max Health Mini-Ghasts have, Default: 5.0")
                        .defineInRange("miniGhastHealth", 5.0, 1.0, Double.MAX_VALUE);
                MiniGhastDamage = BUILDER.comment("How much damage Mini-Ghasts' fireballs deals, Default: 5.0")
                        .defineInRange("miniGhastDamage", 5.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Whisperer");
                WhispererHealth = BUILDER.comment("How much Max Health Whisperers have, Default: 20.0")
                        .defineInRange("whispererHealth", 20.0, 1.0, Double.MAX_VALUE);
                WhispererDamage = BUILDER.comment("How much damage Whisperer melee attack deals, Default: 2.5")
                        .defineInRange("whispererDamage", 2.5, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Leapleaf");
                LeapleafHealth = BUILDER.comment("How much Max Health Leapleaves have, Default: 70.0")
                        .defineInRange("leapleafHealth", 70.0, 1.0, Double.MAX_VALUE);
                LeapleafDamage = BUILDER.comment("How much damage Leapleaves deals, Default: 10.0")
                        .defineInRange("leapleafDamage", 10.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Quick Growing Vine");
                QuickGrowingVineHealth = BUILDER.comment("How much Max Health Quick Growing Vines have, Default: 10.0")
                        .defineInRange("quickGrowingVineHealth", 10.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Poison-Quill Vine");
                PoisonQuillVineHealth = BUILDER.comment("How much Max Health Poison-Quill Vines have, Default: 40.0")
                        .defineInRange("poisonQuillVineHealth", 40.0, 1.0, Double.MAX_VALUE);
                PoisonQuillVineDamage = BUILDER.comment("How much damage Poison-Quill Vines' quills deals, Default: 2.5")
                        .defineInRange("poisonQuillVineDamage", 2.5, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Ice Golem");
                IceGolemHealth = BUILDER.comment("How much Max Health Ice Golems have, Default: 60.0")
                        .defineInRange("iceGolemHealth", 60.0, 1.0, Double.MAX_VALUE);
                IceGolemArmor = BUILDER.comment("How much natural armor points Ice Golems have, Default: 0.0")
                        .defineInRange("iceGolemArmor", 0.0, 0.0, Double.MAX_VALUE);
                IceGolemDamage = BUILDER.comment("How much damage Ice Golems deals, Default: 7.5")
                        .defineInRange("iceGolemDamage", 7.5, 1.0, Double.MAX_VALUE);
                IceGolemFollowRange = BUILDER.comment("How much following/detection range Ice Golems have, Default: 16.0")
                        .defineInRange("iceGolemFollowRange", 16.0, 1.0, 2048.0D);
                BUILDER.pop();
                BUILDER.push("Squall Golem");
                SquallGolemHealth = BUILDER.comment("How much Max Health Squall Golems have, Default: 88.0")
                        .defineInRange("squallGolemHealth", 88.0, 1.0, Double.MAX_VALUE);
                SquallGolemArmor = BUILDER.comment("How much natural armor points Squall Golems have, Default: 0.0")
                        .defineInRange("squallGolemArmor", 0.0, 0.0, Double.MAX_VALUE);
                SquallGolemDamage = BUILDER.comment("How much damage Squall Golems deals, Default: 16.0")
                        .defineInRange("squallGolemDamage", 16.0, 1.0, Double.MAX_VALUE);
                SquallGolemFollowRange = BUILDER.comment("How much following/detection range Squall Golems have, Default: 32.0")
                        .defineInRange("squallGolemFollowRange", 32.0, 1.0, 2048.0D);
                BUILDER.pop();
                BUILDER.push("Redstone Golem");
                RedstoneGolemHealth = BUILDER.comment("How much Max Health Redstone Golems have, Default: 240.0")
                        .defineInRange("redstoneGolemHealth", 240.0, 1.0, Double.MAX_VALUE);
                RedstoneGolemArmor = BUILDER.comment("How much natural armor points Redstone Golems have, Default: 0.0")
                        .defineInRange("redstoneGolemArmor", 0.0, 0.0, Double.MAX_VALUE);
                RedstoneGolemDamage = BUILDER.comment("How much damage Redstone Golems deals, Default: 20.0")
                        .defineInRange("redstoneGolemDamage", 20.0, 1.0, Double.MAX_VALUE);
                RedstoneGolemFollowRange = BUILDER.comment("How much following/detection range Redstone Golems have, Default: 32.0")
                        .defineInRange("redstoneGolemFollowRange", 32.0, 1.0, 2048.0D);
                BUILDER.pop();
                BUILDER.push("Grave Golem");
                GraveGolemHealth = BUILDER.comment("How much Max Health Grave Golems have, Default: 180.0")
                        .defineInRange("graveGolemHealth", 180.0, 1.0, Double.MAX_VALUE);
                GraveGolemArmor = BUILDER.comment("How much natural armor points Grave Golems have, Default: 0.0")
                        .defineInRange("graveGolemArmor", 0.0, 0.0, Double.MAX_VALUE);
                GraveGolemDamage = BUILDER.comment("How much damage Grave Golems deals, Default: 16.0")
                        .defineInRange("graveGolemDamage", 16.0, 1.0, Double.MAX_VALUE);
                GraveGolemFollowRange = BUILDER.comment("How much following/detection range Grave Golems have, Default: 32.0")
                        .defineInRange("graveGolemFollowRange", 32.0, 1.0, 2048.0D);
                HauntHealth = BUILDER.comment("How much Max Health Haunts have, Default: 6.0")
                        .defineInRange("hauntHealth", 6.0, 1.0, Double.MAX_VALUE);
                HauntArmor = BUILDER.comment("How much natural armor points Haunts have, Default: 0.0")
                        .defineInRange("hauntArmor", 0.0, 0.0, Double.MAX_VALUE);
                HauntDamage = BUILDER.comment("How much damage Haunts deals, Default: 2.0")
                        .defineInRange("hauntDamage", 2.0, 1.0, Double.MAX_VALUE);
                HauntFollowRange = BUILDER.comment("How much following/detection range Haunts have, Default: 32.0")
                        .defineInRange("hauntFollowRange", 32.0, 1.0, 2048.0D);
                BUILDER.pop();
                BUILDER.push("Redstone Monstrosity");
                RedstoneMonstrosityHealth = BUILDER.comment("How much Max Health Redstone Monstrosities have, Default: 600.0")
                        .defineInRange("redstoneMonstrosityHealth", 600.0, 1.0, Double.MAX_VALUE);
                RedstoneMonstrosityArmor = BUILDER.comment("How much natural armor points Redstone Monstrosities have, Default: 0.0")
                        .defineInRange("redstoneMonstrosityArmor", 0.0, 0.0, Double.MAX_VALUE);
                RedstoneMonstrosityDamage = BUILDER.comment("How much damage Redstone Monstrosities deals, Default: 21.0")
                        .defineInRange("redstoneMonstrosityDamage", 21.0, 1.0, Double.MAX_VALUE);
                RedstoneMonstrosityFollowRange = BUILDER.comment("How much following/detection range Redstone Monstrosities have, Default: 32.0")
                        .defineInRange("redstoneMonstrosityFollowRange", 32.0, 1.0, 2048.0D);
                RedstoneMonstrosityHPPercentDamage = BUILDER.comment("Redstone Monstrosities attack HP percent damage, Default: 0.08")
                        .defineInRange("redstoneMonstrosityHPPercentDamage", 0.08, 0.0, 1.0);
                RedstoneMonstrosityDamageCap = BUILDER.comment("The maximum amount of damage an Redstone Monstrosities can attain per hit, Default: 25.0")
                        .defineInRange("redstoneMonstrosityDamageCap", 25.0D, 1.0D, Double.MAX_VALUE);
                RedstoneCubeHealth = BUILDER.comment("How much Max Health Redstone Cubes have, Default: 10.0")
                        .defineInRange("redstoneCubeHealth", 10.0, 1.0, Double.MAX_VALUE);
                RedstoneCubeArmor = BUILDER.comment("How much natural armor points Redstone Cubes have, Default: 0.0")
                        .defineInRange("redstoneCubeArmor", 0.0, 0.0, Double.MAX_VALUE);
                RedstoneCubeDamage = BUILDER.comment("How much damage Redstone Cubes deals, Default: 4.0")
                        .defineInRange("redstoneCubeDamage", 4.0, 1.0, Double.MAX_VALUE);
                RedstoneCubeFollowRange = BUILDER.comment("How much following/detection range Redstone Cubes have, Default: 16.0")
                        .defineInRange("redstoneCubeFollowRange", 16.0, 1.0, 2048.0D);
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
                ApostleArmor = BUILDER.comment("How much natural Armor Points Apostles have, Default: 12.0")
                        .defineInRange("apostleArmor", 12.0, 0.0, Double.MAX_VALUE);
                ApostleToughness = BUILDER.comment("How much natural Toughness Points Apostles have, Default: 6.0")
                        .defineInRange("apostleToughness", 6.0, 0.0, Double.MAX_VALUE);
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
