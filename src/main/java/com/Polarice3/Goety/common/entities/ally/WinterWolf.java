package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class WinterWolf extends BlackWolf{

    public WinterWolf(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            if (entityIn instanceof LivingEntity livingEntity) {
                MobEffect effect = MobEffects.MOVEMENT_SLOWDOWN;
                if (CuriosFinder.hasFrostRobes(this.getMasterOwner())){
                    effect = GoetyEffects.FREEZING.get();
                }
                livingEntity.addEffect(new MobEffectInstance(effect, MathHelper.secondsToTicks(5), 0), this);
            }
        }
        return flag;
    }
}
