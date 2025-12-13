package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class IronHideSpell extends Spell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setDuration(1);
    }

    @Override
    public int defaultSoulCost() {
        return SpellConfig.IronHideCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.IronHideDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.IronHideCoolDown.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public boolean conditionsMet(ServerLevel worldIn, LivingEntity caster) {
        if (caster instanceof Mob mob){
            return !mob.hasEffect(GoetyEffects.IRON_HIDE.get());
        }
        return super.conditionsMet(worldIn, caster);
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat){
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            potency = WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
        }
        caster.addEffect(new MobEffectInstance(GoetyEffects.IRON_HIDE.get(), MathHelper.minutesToTicks(duration), potency, false, false, true));
        this.playSound(worldIn, caster, ModSounds.IRON_HIDE.get());
    }
}
