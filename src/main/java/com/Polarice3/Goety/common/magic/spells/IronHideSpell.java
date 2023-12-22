package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spells;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class IronHideSpell extends Spells {

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

    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff){
        int enchantment = 0;
        int duration = 1;
        if (WandUtil.enchantedFocus(entityLiving)) {
            enchantment = WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), entityLiving);
        }
        entityLiving.addEffect(new MobEffectInstance(GoetyEffects.IRON_HIDE.get(), MathHelper.minutesToTicks(duration), enchantment, false, false, true));
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.IRON_HIDE.get(), this.getSoundSource(), 1.0F, 1.0F);
    }
}
