package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spells;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MiscCapHelper;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class BulwarkSpell extends Spells {
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
    public boolean conditionsMet(ServerLevel worldIn, LivingEntity entityLiving) {
        return MiscCapHelper.getShields(entityLiving) <= 0;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        int amount = SpellConfig.BulwarkShieldAmount.get();
        int duration = SpellConfig.BulwarkShieldTime.get();
        if (WandUtil.enchantedFocus(entityLiving)) {
            amount += WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
            duration *= Math.min(4, WandUtil.getLevels(ModEnchantments.DURATION.get(), entityLiving) + 1);
        }
        MiscCapHelper.setShields(entityLiving, amount);
        MiscCapHelper.setShieldTime(entityLiving, duration);
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.SHIELD_UP.get(), this.getSoundSource(), 3.0F, entityLiving.getVoicePitch());
    }
}
