package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class EffectsUtil {

    public static void amplifyEffect (LivingEntity infected, MobEffect effect, int duration){
        amplifyEffect(infected, effect, duration, 4, false, true);
    }

    public static void amplifyEffect(LivingEntity infected, MobEffect effect, int duration, int maxAmp, boolean pAmbient, boolean pVisible){
        MobEffectInstance MobEffectInstance1 = infected.getEffect(effect);
        int i = 1;
        if (MobEffectInstance1 != null) {
            i += MobEffectInstance1.getAmplifier();
            infected.removeEffect(effect);
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
            infected.removeEffect(effect);
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
            infected.removeEffect(effect);
        }
        MobEffectInstance MobEffectInstance = new MobEffectInstance(effect, i, a);
        infected.addEffect(MobEffectInstance);
    }

    public static void decreaseDuration(LivingEntity infected, MobEffect effect, float duration, boolean pAmbient, boolean pVisible){
        MobEffectInstance MobEffectInstance1 = infected.getEffect(effect);
        float i = duration;
        int a = 0;
        if (MobEffectInstance1 != null) {
            a = MobEffectInstance1.getAmplifier();
            i = MobEffectInstance1.getDuration() - duration;
            infected.removeEffect(effect);
        }
        MobEffectInstance MobEffectInstance = new MobEffectInstance(effect, (int) i, a, pAmbient, pVisible);
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
            infected.removeEffect(effect);
        }

        i = Math.max(0, i);
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
            infected.removeEffect(effect);
        }
        MobEffectInstance MobEffectInstance = new MobEffectInstance(effect, duration/2, a, pAmbient, pVisible);
        infected.addEffect(MobEffectInstance);
    }

    public static int getAmplifier(LivingEntity infected, MobEffect effect){
        MobEffectInstance MobEffectInstance1 = infected.getEffect(effect);
        if (MobEffectInstance1 != null){
            return MobEffectInstance1.getAmplifier();
        }
        return 0;
    }

    public static int getAmplifierPlus(LivingEntity infected, MobEffect effect){
        MobEffectInstance MobEffectInstance1 = infected.getEffect(effect);
        if (MobEffectInstance1 != null){
            return MobEffectInstance1.getAmplifier() + 1;
        }
        return 0;
    }

    public static boolean canAffectLich(MobEffectInstance effectInstance, Level world) {
        return effectInstance.getEffect() != MobEffects.BLINDNESS
                && effectInstance.getEffect() != MobEffects.CONFUSION
                && effectInstance.getEffect() != MobEffects.HUNGER
                && effectInstance.getEffect() != MobEffects.SATURATION
                && new Zombie(world).canBeAffected(effectInstance);
    }

    public static int getFortuneEffectLevel(LootContext lootContext) {
        Entity entity = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (entity instanceof LivingEntity livingEntity) {
            MobEffectInstance mobEffectInstance = livingEntity.getEffect(GoetyEffects.FORTUNATE.get());
            if (mobEffectInstance != null){
                int level = mobEffectInstance.getAmplifier();
                return level + 1;
            }
        }
        return 0;
    }

    public static int infiniteEffect(){
        return -1;
    }

    public static List<LivingEntity> addEffectToAllAround(ServerLevel p_216947_, @org.jetbrains.annotations.Nullable Entity p_216948_, Vec3 p_216949_, double p_216950_, MobEffectInstance p_216951_, int p_216952_) {
        MobEffect mobeffect = p_216951_.getEffect();
        List<LivingEntity> list = new ArrayList<>();
        for (Entity entity : p_216947_.getAllEntities()){
            if (entity instanceof LivingEntity livingEntity){
                if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity) && !MobUtil.areAllies(p_216948_, livingEntity) && p_216949_.closerThan(livingEntity.position(), p_216950_) && (!livingEntity.hasEffect(mobeffect) || livingEntity.getEffect(mobeffect).getAmplifier() < p_216951_.getAmplifier() || livingEntity.getEffect(mobeffect).endsWithin(p_216952_ - 1))){
                    list.add(livingEntity);
                }
            }
        }
        list.forEach((p_238232_) -> {
            p_238232_.addEffect(new MobEffectInstance(p_216951_), p_216948_);
        });
        return list;
    }
}
