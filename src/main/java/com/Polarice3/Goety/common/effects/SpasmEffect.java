package com.Polarice3.Goety.common.effects;

import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class SpasmEffect extends GoetyBaseEffect{
    public SpasmEffect() {
        super(MobEffectCategory.HARMFUL, 0xfffcb8);
    }

    public void applyEffectTick(LivingEntity living, int amplify) {
        if (living.getHealth() > 1.0F) {
            if (living.hurt(ModDamageSource.SHOCK, 1.0F)) {
                MobUtil.push(living, living.level.random.nextDouble(), living.level.random.nextDouble() / 2.0D, living.level.random.nextDouble());
            }
        }
    }

    public boolean isDurationEffectTick(int tick, int amplify) {
        int j = 40 >> amplify;
        if (j > 0) {
            return tick % j == 0;
        } else {
            return true;
        }
    }
}
