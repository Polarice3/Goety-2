package com.Polarice3.Goety.common.magic.spells.frost;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChillHideSpell extends Spell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setDuration(1);
    }

    @Override
    public int defaultSoulCost() {
        return SpellConfig.ChillingCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.ChillingDuration.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.FROST_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.ChillingCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.FROST;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)){
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
            potency += WandUtil.getPotencyLevel(caster);
        }
        LivingEntity target = this.getTarget(caster);
        AABB aabb = caster.getBoundingBox().inflate(4.0D);
        if (isShifting(caster) && target != null){
            if (MobUtil.areAllies(target, caster)){
                target.addEffect(new MobEffectInstance(GoetyEffects.CHILL_HIDE.get(), MathHelper.secondsToTicks(45 * duration), potency));
                aabb = target.getBoundingBox().inflate(4.0D);
            }
        } else {
            caster.addEffect(new MobEffectInstance(GoetyEffects.CHILL_HIDE.get(), MathHelper.secondsToTicks(45 * duration), potency));
        }
        if (this.rightStaff(staff)){
            for (LivingEntity livingEntity : worldIn.getEntitiesOfClass(LivingEntity.class, aabb)) {
                if (MobUtil.areAllies(livingEntity, caster) && livingEntity != caster) {
                    livingEntity.addEffect(new MobEffectInstance(GoetyEffects.CHILL_HIDE.get(), MathHelper.secondsToTicks(45 * duration), potency));
                }
            }
        }
        this.playSound(worldIn, caster, ModSounds.ICE_SPIKE_HIT.get(), 1.0F, 0.5F);
    }
}
