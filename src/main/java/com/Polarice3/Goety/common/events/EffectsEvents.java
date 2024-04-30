package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.EffectsUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public class EffectsEvents {

    public static void Illague(ServerLevel level, LivingEntity infected){
        int duration = Objects.requireNonNull(infected.getEffect(GoetyEffects.ILLAGUE.get())).getDuration() + 1;
        int amplifier = Objects.requireNonNull(infected.getEffect(GoetyEffects.ILLAGUE.get())).getAmplifier();
        if (MobsConfig.IllagueSpread.get()) {
            for (LivingEntity livingEntity : level.getEntitiesOfClass(LivingEntity.class, infected.getBoundingBox().inflate(8.0D))) {
                if (!(livingEntity instanceof PatrollingMonster) && livingEntity.getMobType() != MobType.UNDEAD && !livingEntity.hasEffect(GoetyEffects.ILLAGUE.get())) {
                    if (livingEntity.tickCount % 100 == 0 && livingEntity.getRandom().nextInt(20) == 0){
                        if (livingEntity instanceof Player) {
                            if (!((Player) livingEntity).isCreative()) {
                                livingEntity.addEffect(new MobEffectInstance(GoetyEffects.ILLAGUE.get(), duration / 2, amplifier, false, false));
                            }
                        } else {
                            livingEntity.addEffect(new MobEffectInstance(GoetyEffects.ILLAGUE.get(), duration / 2, amplifier, false, false));
                        }
                    }
                }
            }
        }
        if (infected.tickCount % 20 == 0){
            for(int i = 0; i < 8; ++i) {
                level.sendParticles(ModParticleTypes.PLAGUE_EFFECT.get(), infected.getRandomX(0.5D), infected.getRandomY(), infected.getRandomZ(0.5D), 1, 0.0D, 0.5D, 0.0D, 0);
            }
        }
        int i1 = Mth.clamp((amplifier * 50) * 10, 0, 2500);
        int i2 = amplifier + 1;
        int k = 600 >> amplifier;
        if (k > 0) {
            if ((infected.tickCount % k == 0) && level.getDifficulty() != Difficulty.PEACEFUL) {
                int r = level.random.nextInt(8);
                int r2 = level.random.nextInt(6000 - i1);
                int r3 = level.random.nextInt(i2);
                int r4 = r3 + 1;
                if (r2 == 0) {
                    EffectsUtil.amplifyEffect(infected, GoetyEffects.ILLAGUE.get(), 6000);
                }
                if (!infected.getType().is(EntityTypeTags.RAIDERS)) {
                    switch (r) {
                        case 0 ->
                                infected.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 400 * r4, r3, false, false));
                        case 1 ->
                                infected.addEffect(new MobEffectInstance(MobEffects.HUNGER, 400 * r4, r3, false, false));
                        case 2 ->
                                infected.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 400 * r4, 0, false, false));
                        case 3 ->
                                infected.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400 * r4, r3, false, false));
                        case 4 ->
                                infected.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 400 * r4, r3, false, false));
                        case 5 ->
                                infected.addEffect(new MobEffectInstance(MobEffects.POISON, 400 * r4, r3, false, false));
                        case 6 ->
                                infected.addEffect(new MobEffectInstance(GoetyEffects.SAPPED.get(), 400 * r4, 0, false, false));
                        case 7 ->
                                infected.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, r3, false, false));
                    }
                }
            }
        }
    }
}
