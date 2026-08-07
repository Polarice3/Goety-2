package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(method = "getEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/entity/LivingEntity;)I", at = @At("RETURN"), cancellable = true)
    private static void goety$virtualEnchantFromEffect(Enchantment enchantment, LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        int original = cir.getReturnValueI();
        int effectLevel = 0;

        if (enchantment == Enchantments.SMITE && entity.hasEffect(GoetyEffects.SMITING.get())) {
            MobEffectInstance inst = entity.getEffect(GoetyEffects.SMITING.get());
            if (inst != null) {
                effectLevel = inst.getAmplifier() + 1;
            }
        } else if (enchantment == Enchantments.BANE_OF_ARTHROPODS && entity.hasEffect(GoetyEffects.INSECT_BANE.get())) {
            MobEffectInstance inst = entity.getEffect(GoetyEffects.INSECT_BANE.get());
            if (inst != null) {
                effectLevel = inst.getAmplifier() + 1;
            }
        }

        if (effectLevel > 0) {
            cir.setReturnValue(Math.max(original, effectLevel));
        }
    }
}
