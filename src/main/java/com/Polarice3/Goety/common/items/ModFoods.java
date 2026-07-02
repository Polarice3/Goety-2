package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static final FoodProperties HENBANE = (new FoodProperties.Builder())
            .nutrition(1)
            .saturationMod(0.1F)
            .effect(() -> new MobEffectInstance(MobEffects.POISON, 900, 0), 1.0F)
            .effect(() -> new MobEffectInstance(GoetyEffects.SUN_ALLERGY.get(), 900, 0), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 300, 0), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 0), 1.0F)
            .build();
    public static final FoodProperties NIGHTSHADE = (new FoodProperties.Builder())
            .nutrition(1)
            .saturationMod(0.1F)
            .effect(() -> new MobEffectInstance(MobEffects.POISON, 1200, 1), 1.0F)
            .effect(() -> new MobEffectInstance(GoetyEffects.TRIPPING.get(), 1200, 0), 1.0F)
            .effect(() -> new MobEffectInstance(GoetyEffects.FLAMMABLE.get(), 1200, 0), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 300, 2), 1.0F)
            .build();
}
