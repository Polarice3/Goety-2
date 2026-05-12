package com.Polarice3.Goety.common.effects;

import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class VenomEffect extends GoetyBaseEffect {
    public VenomEffect() {
        super(MobEffectCategory.HARMFUL, 0xa746c2);
    }

    public void applyEffectTick(LivingEntity p_19467_, int p_19468_) {
        if (p_19467_.hasEffect(MobEffects.POISON)) {
            p_19467_.removeEffect(MobEffects.POISON);
        }
        if (p_19467_.getHealth() > 1.0F) {
            p_19467_.hurt(ModDamageSource.getDamageSource(p_19467_.level, ModDamageSource.VENOM), 1.0F);
        }
    }

    public boolean isDurationEffectTick(int p_19455_, int p_19456_) {
        int j = 25 >> p_19456_;
        if (j > 0) {
            return p_19455_ % j == 0;
        } else {
            return true;
        }
    }
}
