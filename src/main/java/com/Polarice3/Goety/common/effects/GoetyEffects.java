package com.Polarice3.Goety.common.effects;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.config.BrewConfig;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GoetyEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Goety.MOD_ID);

    public static void init(){
        EFFECTS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<MobEffect> ILLAGUE = EFFECTS.register("illague",
            IllagueEffect::new);

    public static final RegistryObject<MobEffect> SUMMON_DOWN = EFFECTS.register("summon_down",
            () -> new GoetyBaseEffect(MobEffectCategory.HARMFUL, 0));

    public static final RegistryObject<MobEffect> GOLD_TOUCHED = EFFECTS.register("gold_touched",
            () -> new GoetyBaseEffect(MobEffectCategory.HARMFUL, 4866583));

    public static final RegistryObject<MobEffect> BURN_HEX = EFFECTS.register("burn_hex",
            () -> new GoetyBaseEffect(MobEffectCategory.HARMFUL, 2236962));

    public static final RegistryObject<MobEffect> SAPPED = EFFECTS.register("sapped",
            () -> new GoetyBaseEffect(MobEffectCategory.HARMFUL, 0x3f395f));

    public static final RegistryObject<MobEffect> CLIMBING = EFFECTS.register("climbing",
            () -> new GoetyBaseEffect(MobEffectCategory.BENEFICIAL, 0xf5e895));

    public static final RegistryObject<MobEffect> CHARGED = EFFECTS.register("charged",
            () -> new GoetyBaseEffect(MobEffectCategory.NEUTRAL, 0xd67b5b));

    public static final RegistryObject<MobEffect> BUFF = EFFECTS.register("buff",
            () -> new GoetyBaseEffect(MobEffectCategory.BENEFICIAL, 0)
                    .addAttributeModifier(Attributes.ATTACK_DAMAGE, "f033b086-8a5e-44f2-8655-888dd700691c",
                            1.0D, AttributeModifier.Operation.ADDITION));

    public static final RegistryObject<MobEffect> RAMPAGE = EFFECTS.register("rampage",
            () -> new GoetyBaseEffect(MobEffectCategory.BENEFICIAL, 0x6a0000)
                    .addAttributeModifier(Attributes.ATTACK_DAMAGE, "f51b56c4-bab1-43f4-8607-4db1b50061ef",
                            1.0D, AttributeModifier.Operation.ADDITION)
                    .addAttributeModifier(Attributes.ATTACK_SPEED, "da7d3d70-88a2-43c7-8924-4b4fbaf3b6aa",
                            (double)0.1F, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<MobEffect> WANE = EFFECTS.register("wane",
            () -> new GoetyBaseEffect(MobEffectCategory.HARMFUL, 0x425b64)
                    .addAttributeModifier(Attributes.ATTACK_DAMAGE, "85c0b45f-d88b-4752-9aa0-0460a5125860",
                            -4.0D, AttributeModifier.Operation.ADDITION)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, "9a4d6273-711a-4f6d-87d2-23e91e190ab8",
                            -0.15D, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<MobEffect> BUSTED = EFFECTS.register("busted",
            () -> new GoetyBaseEffect(MobEffectCategory.HARMFUL, 0x232f58)
                    .addAttributeModifier(Attributes.ARMOR, "bf1df32b-2ee2-4fb9-9b96-e1765202bca3",
                            -0.5D, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<MobEffect> SOUL_ARMOR = EFFECTS.register("soul_armor",
            () -> new BrewMobEffect(MobEffectCategory.BENEFICIAL, 0x668785, false)
                    .addAttributeModifier(Attributes.ARMOR, "e4b8d878-0c5b-43b2-bd94-8c021d231f1e",
                            2.0D, AttributeModifier.Operation.ADDITION));

    public static final RegistryObject<MobEffect> IRON_HIDE = EFFECTS.register("iron_hide",
            () -> new BrewMobEffect(MobEffectCategory.BENEFICIAL, 0x585858, false)
                    .addAttributeModifier(Attributes.ARMOR, "7487ebfe-56fb-4e83-b804-3f337b2a7814",
                            4.0D, AttributeModifier.Operation.ADDITION));

    public static final RegistryObject<MobEffect> SHADOW_WALK = EFFECTS.register("shadow_walk",
            () -> new GoetyBaseEffect(MobEffectCategory.BENEFICIAL, 0));

    public static final RegistryObject<MobEffect> SOUL_HUNGER = EFFECTS.register("soul_hunger",
            SoulHungerEffect::new);

    public static final RegistryObject<MobEffect> CURSED = EFFECTS.register("cursed",
            () -> new GoetyBaseEffect(MobEffectCategory.HARMFUL, 0x1e1f24));

    public static final RegistryObject<MobEffect> FREEZING = EFFECTS.register("freezing",
            () -> new GoetyBaseEffect(MobEffectCategory.HARMFUL, 0xf4fcfc));

    public static final RegistryObject<MobEffect> DOOM = EFFECTS.register("doom",
            DoomEffect::new);

    public static final RegistryObject<MobEffect> ACID_VENOM = EFFECTS.register("acid_venom",
            VenomEffect::new);

    public static final RegistryObject<MobEffect> SPASMS = EFFECTS.register("spasms",
            SpasmEffect::new);

    public static final RegistryObject<MobEffect> STUNNED = EFFECTS.register("stunned",
            () -> new GoetyBaseEffect(MobEffectCategory.HARMFUL, 0xffbc2e)
                    .addAttributeModifier(ForgeMod.SWIM_SPEED.get(), "e4669259-9b6f-40d2-b253-46e65b1f3363",
                            -1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, "963d8748-941f-4f75-b4a6-a9c85013f27f",
                            -1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<MobEffect> TANGLED = EFFECTS.register("tangled",
            () -> new GoetyBaseEffect(MobEffectCategory.HARMFUL, 0xffffff)
                    .addAttributeModifier(ForgeMod.SWIM_SPEED.get(), "862219f1-18f4-483a-94db-0d4c6c4fdef2",
                            -1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, "8246d3de-e765-487d-adda-18a1deb3e4a9",
                            -1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL));

    //Brew Exclusive
    public static final RegistryObject<MobEffect> PRESSURE = EFFECTS.register("pressure",
            () -> new BrewMobEffect(MobEffectCategory.HARMFUL, 0x007200, BrewConfig.PressureCurable.get()));

    public static final RegistryObject<MobEffect> ENDER_GROUND = EFFECTS.register("ender_ground",
            () -> new GoetyBaseEffect(MobEffectCategory.HARMFUL, 0x258474));

    public static final RegistryObject<MobEffect> ENDER_FLUX = EFFECTS.register("ender_flux",
            () -> new GoetyBaseEffect(MobEffectCategory.HARMFUL, 0x441d5a));

    public static final RegistryObject<MobEffect> NYCTOPHOBIA = EFFECTS.register("nyctophobia",
            () -> new BrewMobEffect(MobEffectCategory.HARMFUL, 0x0d1305, BrewConfig.NyctophobiaCurable.get()));

    public static final RegistryObject<MobEffect> SUN_ALLERGY = EFFECTS.register("sun_allergy",
            () -> new BrewMobEffect(MobEffectCategory.HARMFUL, 0x1f1421, BrewConfig.SunAllergyCurable.get()));

    public static final RegistryObject<MobEffect> SNOW_SKIN = EFFECTS.register("snow_skin",
            () -> new BrewMobEffect(MobEffectCategory.HARMFUL, 0xe3f3f3, BrewConfig.SnowSkinCurable.get()));

    public static final RegistryObject<MobEffect> EVIL_EYE = EFFECTS.register("evil_eye",
            () -> new EvilEyeEffect(MobEffectCategory.HARMFUL, 0x560269, BrewConfig.EvilEyeCurable.get()));

    public static final RegistryObject<MobEffect> TRIPPING = EFFECTS.register("tripping",
            () -> new GoetyBaseEffect(MobEffectCategory.HARMFUL, 0x101636));

    public static final RegistryObject<MobEffect> ARROWMANTIC = EFFECTS.register("arrowmantic",
            () -> new GoetyBaseEffect(MobEffectCategory.HARMFUL, 0x969696));

    public static final RegistryObject<MobEffect> PLUNGE = EFFECTS.register("plunge",
            () -> new GoetyBaseEffect(MobEffectCategory.HARMFUL, 0x8d989a));

    public static final RegistryObject<MobEffect> FLIMSY = EFFECTS.register("flimsy",
            () -> new GoetyBaseEffect(MobEffectCategory.HARMFUL, 0xf5f5f5));

    public static final RegistryObject<MobEffect> SENSE_LOSS = EFFECTS.register("sense_loss",
            () -> new GoetyBaseEffect(MobEffectCategory.HARMFUL, 2039587));

    public static final RegistryObject<MobEffect> FLAMMABLE = EFFECTS.register("flammable",
            () -> new GoetyBaseEffect(MobEffectCategory.HARMFUL, 0x1e0f07));

    public static final RegistryObject<MobEffect> STORMS_WRATH = EFFECTS.register("storms_wrath",
            () -> new GoetyBaseEffect(MobEffectCategory.HARMFUL, 0xe77c56));

    public static final RegistryObject<MobEffect> EXPLOSIVE = EFFECTS.register("explosive",
            () -> new GoetyBaseEffect(MobEffectCategory.BENEFICIAL, 0x912d11));

    public static final RegistryObject<MobEffect> SWIFT_SWIM = EFFECTS.register("swift_swim",
            () -> new GoetyBaseEffect(MobEffectCategory.BENEFICIAL, 0xbead6a)
                    .addAttributeModifier(ForgeMod.SWIM_SPEED.get(), "15c1a19c-b4f8-4d84-ab37-a9036ac1885f",
                            1.0D, AttributeModifier.Operation.ADDITION));

    public static final RegistryObject<MobEffect> FROG_LEG = EFFECTS.register("frog_leg",
            () -> new GoetyBaseEffect(MobEffectCategory.BENEFICIAL, 0x6abe30));

    public static final RegistryObject<MobEffect> FLAME_HANDS = EFFECTS.register("flame_hands",
            () -> new GoetyBaseEffect(MobEffectCategory.BENEFICIAL, 0xff3d29));

    public static final RegistryObject<MobEffect> VENOMOUS_HANDS = EFFECTS.register("venomous_hands",
            () -> new GoetyBaseEffect(MobEffectCategory.BENEFICIAL, 0x122620));

    public static final RegistryObject<MobEffect> REPULSIVE = EFFECTS.register("repulsive",
            () -> new GoetyBaseEffect(MobEffectCategory.BENEFICIAL, 0x67502c));

    public static final RegistryObject<MobEffect> FIRE_TRAIL = EFFECTS.register("fire_trail",
            () -> new GoetyBaseEffect(MobEffectCategory.BENEFICIAL, 0xffc800));

    public static final RegistryObject<MobEffect> FIERY_AURA = EFFECTS.register("fiery_aura",
            () -> new GoetyBaseEffect(MobEffectCategory.BENEFICIAL, 0xff0000));

    public static final RegistryObject<MobEffect> FROSTY_AURA = EFFECTS.register("frosty_aura",
            () -> new GoetyBaseEffect(MobEffectCategory.BENEFICIAL, 0x212d5f));

    public static final RegistryObject<MobEffect> PHOTOSYNTHESIS = EFFECTS.register("photosynthesis",
            () -> new GoetyBaseEffect(MobEffectCategory.BENEFICIAL, 0xffec4f));

    public static final RegistryObject<MobEffect> INSIGHT = EFFECTS.register("insight",
            () -> new GoetyBaseEffect(MobEffectCategory.BENEFICIAL, 0x59b057));

    public static final RegistryObject<MobEffect> BOTTLING = EFFECTS.register("bottling",
            () -> new GoetyBaseEffect(MobEffectCategory.BENEFICIAL, 0x3e250f));

    public static final RegistryObject<MobEffect> CORPSE_EATER = EFFECTS.register("corpse_eater",
            () -> new GoetyBaseEffect(MobEffectCategory.BENEFICIAL, 0x48565e));

    public static final RegistryObject<MobEffect> FORTUNATE = EFFECTS.register("fortunate",
            () -> new GoetyBaseEffect(MobEffectCategory.BENEFICIAL, 0x4aedd9));

    public static final RegistryObject<MobEffect> SAVE_EFFECTS = EFFECTS.register("save_effects",
            () -> new GoetyBaseEffect(MobEffectCategory.NEUTRAL, 0x4f446b));

    public static final RegistryObject<MobEffect> WILD_RAGE = EFFECTS.register("wild_rage",
            () -> new GoetyBaseEffect(MobEffectCategory.NEUTRAL, 0xa8311c));
}
