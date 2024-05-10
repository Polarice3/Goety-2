package com.Polarice3.Goety.common.entities.ally.spider;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class IcySpiderServant extends SpiderServant{
    public IcySpiderServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    public boolean doHurtTarget(Entity target) {
        if (super.doHurtTarget(target)) {
            if (target instanceof LivingEntity livingEntity) {
                int i = this.getMasterOwner() instanceof Player ? 7 : 0;
                if (this.level.getDifficulty() == Difficulty.NORMAL) {
                    i = 7;
                } else if (this.level.getDifficulty() == Difficulty.HARD) {
                    i = 15;
                }

                if (i > 0) {
                    MobEffect effect = MobEffects.MOVEMENT_SLOWDOWN;
                    if (CuriosFinder.hasFrostRobes(this.getMasterOwner())){
                        effect = GoetyEffects.FREEZING.get();
                    }
                    livingEntity.addEffect(new MobEffectInstance(effect, i * 20, 0), this);
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
