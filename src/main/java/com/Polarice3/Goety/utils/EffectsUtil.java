package com.Polarice3.Goety.utils;

import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class EffectsUtil {

    public static void amplifyEffect (LivingEntity infected, MobEffect effect, int duration){
        amplifyEffect(infected, effect, duration, 4, false, true);
    }

    public static void amplifyEffect(LivingEntity infected, MobEffect effect, int duration, int maxAmp, boolean pAmbient, boolean pVisible){
        MobEffectInstance MobEffectInstance1 = infected.getEffect(effect);
        int i = 1;
        if (MobEffectInstance1 != null) {
            i += MobEffectInstance1.getAmplifier();
            infected.removeEffectNoUpdate(effect);
        } else {
            --i;
        }

        i = Mth.clamp(i, 0, maxAmp);
        MobEffectInstance MobEffectInstance = new MobEffectInstance(effect, duration, i, pAmbient, pVisible);
        infected.addEffect(MobEffectInstance);
    }

    public static void resetDuration(LivingEntity infected, MobEffect effect, int duration){
        resetDuration(infected, effect, duration, false, true);
    }

    public static void resetDuration(LivingEntity infected, MobEffect effect, int duration, boolean pAmbient, boolean pVisible){
        MobEffectInstance MobEffectInstance1 = infected.getEffect(effect);
        int a = 0;
        if (MobEffectInstance1 != null) {
            a = MobEffectInstance1.getAmplifier();
            infected.removeEffectNoUpdate(effect);
        }
        MobEffectInstance MobEffectInstance = new MobEffectInstance(effect, duration, a, pAmbient, pVisible);
        infected.addEffect(MobEffectInstance);
    }

    public static void increaseDuration(LivingEntity infected, MobEffect effect, int duration){
        MobEffectInstance MobEffectInstance1 = infected.getEffect(effect);
        int i = duration;
        int a = 0;
        if (MobEffectInstance1 != null) {
            a = MobEffectInstance1.getAmplifier();
            i = MobEffectInstance1.getDuration() + duration;
            infected.removeEffectNoUpdate(effect);
        }
        MobEffectInstance MobEffectInstance = new MobEffectInstance(effect, i, a);
        infected.addEffect(MobEffectInstance);
    }

    public static void deamplifyEffect(LivingEntity infected, MobEffect effect, int duration){
        deamplifyEffect(infected, effect, duration, false, true);
    }

    public static void deamplifyEffect(LivingEntity infected, MobEffect effect, int duration, boolean pAmbient, boolean pVisible){
        MobEffectInstance MobEffectInstance1 = infected.getEffect(effect);
        int i = 0;
        if (MobEffectInstance1 != null) {
            int amp = MobEffectInstance1.getAmplifier();
            i = amp - 1;
            infected.removeEffectNoUpdate(effect);
        }

        i = Mth.clamp(i, 0, 4);
        MobEffectInstance MobEffectInstance = new MobEffectInstance(effect, duration, i, pAmbient, pVisible);
        infected.addEffect(MobEffectInstance);
    }

    public static void halveDuration(LivingEntity infected, MobEffect effect, int duration){
        halveDuration(infected, effect, duration, false, true);
    }

    public static void halveDuration(LivingEntity infected, MobEffect effect, int duration, boolean pAmbient, boolean pVisible){
        MobEffectInstance MobEffectInstance1 = infected.getEffect(effect);
        int a = 0;
        if (MobEffectInstance1 != null) {
            a = MobEffectInstance1.getAmplifier();
            infected.removeEffectNoUpdate(effect);
        }
        MobEffectInstance MobEffectInstance = new MobEffectInstance(effect, duration/2, a, pAmbient, pVisible);
        infected.addEffect(MobEffectInstance);
    }
}
