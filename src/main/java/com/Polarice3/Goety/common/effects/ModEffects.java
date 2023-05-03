package com.Polarice3.Goety.common.effects;

import com.Polarice3.Goety.Goety;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Goety.MOD_ID);

    public static void init(){
        EFFECTS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<MobEffect> ILLAGUE = EFFECTS.register("illague",
            IllagueEffect::new);

    public static final RegistryObject<MobEffect> SUMMON_DOWN = EFFECTS.register("summon_down",
            () -> new ModEffect(MobEffectCategory.HARMFUL, 0));

    public static final RegistryObject<MobEffect> GOLD_TOUCHED = EFFECTS.register("gold_touched",
            () -> new ModEffect(MobEffectCategory.HARMFUL, 4866583));

    public static final RegistryObject<MobEffect> BURN_HEX = EFFECTS.register("burn_hex",
            () -> new ModEffect(MobEffectCategory.HARMFUL, 2236962));

    public static final RegistryObject<MobEffect> SAPPED = EFFECTS.register("sapped",
            () -> new ModEffect(MobEffectCategory.HARMFUL, 197379));

    public static final RegistryObject<MobEffect> CLIMBING = EFFECTS.register("climbing",
            () -> new ModEffect(MobEffectCategory.BENEFICIAL, 0xf5e895));

    public static final RegistryObject<MobEffect> BUFF = EFFECTS.register("buff",
            () -> new ModEffect(MobEffectCategory.BENEFICIAL, 9643043)
                    .addAttributeModifier(Attributes.ATTACK_DAMAGE, "f033b086-8a5e-44f2-8655-888dd700691c",
                            1.0D, AttributeModifier.Operation.ADDITION));

    public static final RegistryObject<MobEffect> RAMPAGE = EFFECTS.register("rampage",
            () -> new ModEffect(MobEffectCategory.BENEFICIAL, 0x6a0000)
                    .addAttributeModifier(Attributes.ATTACK_DAMAGE, "f51b56c4-bab1-43f4-8607-4db1b50061ef",
                            1.0D, AttributeModifier.Operation.ADDITION)
                    .addAttributeModifier(Attributes.ATTACK_SPEED, "da7d3d70-88a2-43c7-8924-4b4fbaf3b6aa",
                            (double)0.1F, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<MobEffect> SOUL_HUNGER = EFFECTS.register("soul_hunger",
            SoulHungerEffect::new);
}
