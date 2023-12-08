package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.magic.TouchSpells;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.EffectsUtil;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class ChargeSpell extends TouchSpells {
    @Override
    public int SoulCost() {
        return SpellConfig.ChargeCost.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.ZAP.get();
    }

    @Override
    public int SpellCooldown() {
        return SpellConfig.ChargeCoolDown.get();
    }

    @Override
    public void touchResult(ServerLevel worldIn, LivingEntity caster, LivingEntity target) {
        if (!target.hasEffect(GoetyEffects.CHARGED.get())) {
            worldIn.playSound(null, target.getX(), target.getY(), target.getZ(), CastingSound(), this.getSoundSource(), 1.0F, 0.5F);
            target.addEffect(new MobEffectInstance(GoetyEffects.CHARGED.get(), MathHelper.secondsToTicks(30), 0, false, false));
        } else {
            MobEffectInstance instance = target.getEffect(GoetyEffects.CHARGED.get());
            if (instance != null) {
                if (instance.getAmplifier() >= 1) {
                    target.hurt(ModDamageSource.directShock(caster), SpellConfig.ChargeDamage.get().floatValue());
                } else {
                    worldIn.playSound(null, target.getX(), target.getY(), target.getZ(), CastingSound(), this.getSoundSource(), 1.0F, 0.75F);
                }
                EffectsUtil.amplifyEffect(target, GoetyEffects.CHARGED.get(), MathHelper.secondsToTicks(30), 3, false, false);
            }
        }
    }
}
