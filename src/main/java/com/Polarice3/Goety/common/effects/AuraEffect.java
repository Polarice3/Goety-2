package com.Polarice3.Goety.common.effects;

import com.Polarice3.Goety.client.particles.GroundAuraParticleOption;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class AuraEffect extends GoetyBaseEffect {
    @Nullable
    public MobEffect giveEffect;
    public boolean ampRadius;
    public float radius;

    public AuraEffect(MobEffectCategory p_19451_, int p_19452_, MobEffect giveEffect, float radius, boolean ampRadius) {
        super(p_19451_, p_19452_);
        this.giveEffect = giveEffect;
        this.radius = radius;
        this.ampRadius = ampRadius;
    }

    public AuraEffect(MobEffectCategory p_19451_, int p_19452_, MobEffect giveEffect, float radius) {
        this(p_19451_, p_19452_, giveEffect, radius, false);
    }

    public AuraEffect(MobEffectCategory p_19451_, int p_19452_, MobEffect giveEffect) {
        this(p_19451_, p_19452_, giveEffect, 8.0F, false);
    }

    public AuraEffect(MobEffectCategory p_19451_, int p_19452_) {
        this(p_19451_, p_19452_, null);
    }

    public void applyEffectTick(LivingEntity livingEntity, int amplify) {
        MobEffect effect = this.giveEffect;
        if (this == GoetyEffects.SHIELDING.get()) {
            effect = GoetyEffects.SHIELDED.get();
        } else if (this == GoetyEffects.RALLYING.get()) {
            effect = GoetyEffects.RALLIED.get();
        }
        if (effect != null) {
            float radius = this.radius;
            if (this.ampRadius) {
                radius += amplify;
            }
            for (LivingEntity ally : livingEntity.level.getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(radius))) {
                if (ally != livingEntity && MobUtil.areAllies(livingEntity, ally)) {
                    ally.addEffect(new MobEffectInstance(effect, 5, amplify, false, false));
                }
            }
            if (livingEntity.tickCount % 20 == 0) {
                if (livingEntity.level instanceof ServerLevel serverLevel) {
                    radius /= 2.0F;
                    serverLevel.sendParticles(new GroundAuraParticleOption(livingEntity.getId(), radius, new ColorUtil(this.getColor())), livingEntity.getX(), livingEntity.getY() + 0.25F, livingEntity.getZ(), 1, 0.0F, 0.0F, 0.0F, 0.0F);
                }
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int tick, int amplify) {
        return true;
    }
}
