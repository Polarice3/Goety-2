package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MiscCapHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class BulwarkSpell extends Spell {
    @Override
    public int defaultSoulCost() {
        return SpellConfig.BulwarkCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.BulwarkDuration.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.BulwarkCoolDown.get();
    }

    @Override
    public boolean conditionsMet(ServerLevel worldIn, LivingEntity caster) {
        return MiscCapHelper.getShields(caster) <= 0;
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
        int amount = SpellConfig.BulwarkShieldAmount.get();
        int duration = SpellConfig.BulwarkShieldTime.get();
        if (WandUtil.enchantedFocus(caster)) {
            amount += WandUtil.getPotencyLevel(caster);
            duration *= Math.min(4, WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1);
        }
        amount += spellStat.getPotency();
        if (spellStat.getDuration() > 0){
            duration = spellStat.getDuration();
        }
        LivingEntity target = this.getTarget(caster);
        if (isShifting(caster) && target != null){
            if (MobUtil.areAllies(target, caster)){
                MiscCapHelper.setShields(target, amount);
                MiscCapHelper.setShieldTime(target, duration);
            }
        } else {
            MiscCapHelper.setShields(caster, amount);
            MiscCapHelper.setShieldTime(caster, duration);
        }
        this.playSound(worldIn, caster, ModSounds.SHIELD_UP.get(), 3.0F, caster.getVoicePitch());
    }
}
