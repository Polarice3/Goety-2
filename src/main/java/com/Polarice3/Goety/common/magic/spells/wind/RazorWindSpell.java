package com.Polarice3.Goety.common.magic.spells.wind;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.RazorWind;
import com.Polarice3.Goety.common.entities.projectiles.SlashProjectile;
import com.Polarice3.Goety.common.entities.projectiles.VoidSlash;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class RazorWindSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.RazorWindCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.RazorWindDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.RazorWindCoolDown.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WIND;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RADIUS.get());
        return list;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat){
        float radius = (float) spellStat.getRadius();
        float damage = SpellConfig.RazorWindDamage.get().floatValue() * WandUtil.damageMultiply();
        if (rightStaff(staff)){
            radius += 0.5F;
        }
        if (WandUtil.enchantedFocus(caster)) {
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster) / 4.0F;
            damage += WandUtil.getPotencyLevel(caster);
        }
        damage += spellStat.getPotency();
        SlashProjectile razorWind = new RazorWind(worldIn, caster);
        if (this.typeStaff(staff, SpellType.VOID)) {
            razorWind = new VoidSlash(worldIn, caster);
        }
        razorWind.setPos(caster.getEyePosition());
        razorWind.slash(caster.getLookAngle(), 2.0F);
        razorWind.setRadius(0.3F + radius);
        razorWind.setMaxRadius(razorWind.getMaxRadius() + radius);
        razorWind.setDamage(damage);
        razorWind.setMaxLifeSpan(40);
        worldIn.addFreshEntity(razorWind);
        this.playSound(worldIn, caster, ModSounds.WIND.get(), 2.0F, 1.5F);
    }
}
