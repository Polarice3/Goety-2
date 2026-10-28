package com.Polarice3.Goety.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Invoker(value = "getDamageAfterArmorAbsorb")
    float goety$invokeGetDamageAfterArmorAbsorb(DamageSource source, float amount);

    @Invoker(value = "getDamageAfterMagicAbsorb")
    float goety$invokeGetDamageAfterMagicAbsorb(DamageSource source, float amount);
}
