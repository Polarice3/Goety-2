package com.Polarice3.Goety.common.effects;

import com.Polarice3.Goety.client.particles.GroundAuraParticle;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class AuraEffect extends GoetyBaseEffect {

    public AuraEffect(MobEffectCategory p_19451_, int p_19452_) {
        super(p_19451_, p_19452_);
    }

    public void applyEffectTick(LivingEntity livingEntity, int amplify) {
        MobEffect effect = null;
        if (this == GoetyEffects.SHIELDING.get()) {
            effect = GoetyEffects.SHIELDED.get();
        } else if (this == GoetyEffects.RALLYING.get()) {
            effect = GoetyEffects.RALLIED.get();
        }
        if (effect != null) {
            for (LivingEntity ally : livingEntity.level.getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(8.0D))) {
                if (ally != livingEntity && MobUtil.areAllies(livingEntity, ally)) {
                    ally.addEffect(new MobEffectInstance(effect, 5, amplify, false, false));
                }
            }
            if (livingEntity.tickCount % 20 == 0) {
                if (livingEntity.level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(new GroundAuraParticle.Option(livingEntity.getId(), 4.0F, new ColorUtil(this.getColor())), livingEntity.getX(), livingEntity.getY() + 0.25F, livingEntity.getZ(), 1, 0.0F, 0.0F, 0.0F, 0.0F);
                }
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int tick, int amplify) {
        return true;
    }
}
