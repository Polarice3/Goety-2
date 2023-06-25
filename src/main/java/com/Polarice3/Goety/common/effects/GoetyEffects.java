package com.Polarice3.Goety.common.effects;

import com.Polarice3.Goety.Goety;
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
            () -> new GoetyBaseEffect(MobEffectCategory.BENEFICIAL, 9643043)
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

    public static final RegistryObject<MobEffect> SOUL_HUNGER = EFFECTS.register("soul_hunger",
            SoulHungerEffect::new);

    //Brew Exclusive
    public static final RegistryObject<MobEffect> PRESSURE = EFFECTS.register("pressure",
            () -> new GoetyBaseEffect(MobEffectCategory.HARMFUL, 0x007200));

    public static final RegistryObject<MobEffect> ENDER_GROUND = EFFECTS.register("ender_ground",
            () -> new GoetyBaseEffect(MobEffectCategory.HARMFUL, 0x258474));

    public static final RegistryObject<MobEffect> FREEZING = EFFECTS.register("freezing",
            () -> new GoetyBaseEffect(MobEffectCategory.HARMFUL, 0xf4fcfc));

    public static final RegistryObject<MobEffect> NYCTOPHOBIA = EFFECTS.register("nyctophobia",
            () -> new GoetyBaseEffect(MobEffectCategory.HARMFUL, 0x0d1305));

    public static final RegistryObject<MobEffect> SUN_ALLERGY = EFFECTS.register("sun_allergy",
            () -> new GoetyBaseEffect(MobEffectCategory.HARMFUL, 0x1f1421));

    public static final RegistryObject<MobEffect> TRIPPING = EFFECTS.register("tripping",
            () -> new GoetyBaseEffect(MobEffectCategory.HARMFUL, 0x101636));

    public static final RegistryObject<MobEffect> ARROWMANTIC = EFFECTS.register("arrowmantic",
            () -> new GoetyBaseEffect(MobEffectCategory.HARMFUL, 0x969696));

    public static final RegistryObject<MobEffect> EXPLOSIVE = EFFECTS.register("explosive",
            () -> new GoetyBaseEffect(MobEffectCategory.BENEFICIAL, 0x912d11));

    public static final RegistryObject<MobEffect> SWIFT_SWIM = EFFECTS.register("swift_swim",
            () -> new GoetyBaseEffect(MobEffectCategory.BENEFICIAL, 0xbead6a)
                    .addAttributeModifier(ForgeMod.SWIM_SPEED.get(), "15c1a19c-b4f8-4d84-ab37-a9036ac1885f",
                            1.0D, AttributeModifier.Operation.ADDITION));

    public static final RegistryObject<MobEffect> FLAME_HANDS = EFFECTS.register("flame_hands",
            () -> new GoetyBaseEffect(MobEffectCategory.BENEFICIAL, 0xff3d29));

    public static final RegistryObject<MobEffect> VENOMOUS_HANDS = EFFECTS.register("venomous_hands",
            () -> new GoetyBaseEffect(MobEffectCategory.BENEFICIAL, 0x122620));

    public static final RegistryObject<MobEffect> REPULSIVE = EFFECTS.register("repulsive",
            () -> new GoetyBaseEffect(MobEffectCategory.BENEFICIAL, 0x67502c));

    public static final RegistryObject<MobEffect> FIERY_AURA = EFFECTS.register("fiery_aura",
            () -> new GoetyBaseEffect(MobEffectCategory.BENEFICIAL, 0xff0000));

}
