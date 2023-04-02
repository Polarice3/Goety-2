package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.ModEffects;
import com.Polarice3.Goety.utils.EffectsUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public class EffectsEvents {

    public void tick(ServerLevel level) {
        for (Entity entity: level.getAllEntities()){
            if (entity instanceof LivingEntity infected){
                if (infected.hasEffect(ModEffects.ILLAGUE.get())){
                    this.Illague(level, infected);
                }
            }
        }
    }

    public void Illague(ServerLevel level, LivingEntity infected){
        int duration = Objects.requireNonNull(infected.getEffect(ModEffects.ILLAGUE.get())).getDuration() + 1;
        int amplifier = Objects.requireNonNull(infected.getEffect(ModEffects.ILLAGUE.get())).getAmplifier();
        if (MainConfig.IllagueSpread.get()) {
            for (LivingEntity livingEntity : level.getEntitiesOfClass(LivingEntity.class, infected.getBoundingBox().inflate(8.0D))) {
                if (!(livingEntity instanceof PatrollingMonster) && livingEntity.getMobType() != MobType.UNDEAD && !livingEntity.hasEffect(ModEffects.ILLAGUE.get())) {
                    if (livingEntity instanceof Player) {
                        if (!((Player) livingEntity).isCreative()) {
                            livingEntity.addEffect(new MobEffectInstance(ModEffects.ILLAGUE.get(), duration / 2, amplifier, false, false));
                        }
                    } else {
                        livingEntity.addEffect(new MobEffectInstance(ModEffects.ILLAGUE.get(), duration / 2, amplifier, false, false));
                    }
                }
            }
        }
        if (infected.tickCount % 20 == 0){
            for(int i = 0; i < 8; ++i) {
                level.sendParticles(ModParticleTypes.PLAGUE_EFFECT.get(), infected.getRandomX(0.5D), infected.getRandomY(), infected.getRandomZ(0.5D), 1, 0.0D, 0.5D, 0.0D, 0);
            }
        }
        int i1 = Mth.clamp(amplifier * 50, 0, 250);
        int i2 = amplifier + 1;
        int i3 = i1 * 10;
        int c = switch (level.getDifficulty()) {
            case PEACEFUL -> 6000;
            case EASY -> 4500;
            case NORMAL -> 3000;
            case HARD -> 1500;
        };
        int k = 600 >> amplifier;
        if (k > 0) {
            if ((infected.tickCount % k == 0) && level.getDifficulty() != Difficulty.PEACEFUL) {
                int r = level.random.nextInt(8);
                int r2 = level.random.nextInt(c - i3);
                int r3 = level.random.nextInt(i2);
                int r4 = r3 + 1;
                if (r2 == 0) {
                    EffectsUtil.amplifyEffect(infected, ModEffects.ILLAGUE.get(), 6000);
                }
                if (infected instanceof Player) {
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
                                infected.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 400 * r4, 0, false, false));
                        case 7 -> infected.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, r3, false, false));
                    }
                } else {
                    switch (r) {
                        case 0 ->
                                infected.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 400 * r4, r3, false, false));
                        case 1, 2, 3 ->
                                infected.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400 * r4, r3, false, false));
                        case 4, 5, 6 ->
                                infected.addEffect(new MobEffectInstance(MobEffects.POISON, 400 * r4, r3, false, false));
                        case 7 -> infected.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, r3, false, false));
                    }
                }
            }
        }
    }
}
