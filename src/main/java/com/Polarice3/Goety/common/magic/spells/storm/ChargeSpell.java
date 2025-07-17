package com.Polarice3.Goety.common.magic.spells.storm;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.TouchSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.EffectsUtil;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ChargeSpell extends TouchSpell {
    @Override
    public int defaultSoulCost() {
        return SpellConfig.ChargeCost.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.ZAP.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.ChargeCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.STORM;
    }

    @Override
    public void touchResult(ServerLevel worldIn, LivingEntity caster, LivingEntity target, ItemStack staff, SpellStat spellStat) {
        if (!target.hasEffect(GoetyEffects.CHARGED.get())) {
            this.playSound(worldIn, target, 1.0F, 0.5F);
            target.addEffect(new MobEffectInstance(GoetyEffects.CHARGED.get(), MathHelper.secondsToTicks(30), 0, false, false));
        } else {
            MobEffectInstance instance = target.getEffect(GoetyEffects.CHARGED.get());
            if (instance != null) {
                if (instance.getAmplifier() >= 1) {
                    target.hurt(ModDamageSource.directShock(caster), SpellConfig.ChargeDamage.get().floatValue());
                } else {
                    this.playSound(worldIn, target, 1.0F, 0.75F);
                }
                EffectsUtil.amplifyEffect(target, GoetyEffects.CHARGED.get(), MathHelper.secondsToTicks(30), 3, false, false);
            }
        }
    }
}
