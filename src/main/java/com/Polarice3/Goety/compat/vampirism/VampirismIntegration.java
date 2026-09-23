package com.Polarice3.Goety.compat.vampirism;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

public class VampirismIntegration {

    public static MobEffect getSunscreen() {
        return ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("vampirism", "sunscreen"));
    }

    public static boolean hasSunscreen(LivingEntity livingEntity) {
        if (getSunscreen() != null) {
            try {
                return livingEntity.hasEffect(getSunscreen());
            } catch (NullPointerException exception) {
                return false;
            }
        }
        return false;
    }
}
