package com.Polarice3.Goety.common.magic.spells.wind;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WindHornSpell extends Spell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setDuration(3).setRadius(3.0D);
    }

    @Override
    public int defaultSoulCost() {
        return SpellConfig.TremblingCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.TremblingDuration.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.TremblingCoolDown.get();
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
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        int radius = (int) spellStat.getRadius();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)){
            potency += WandUtil.getPotencyLevel(caster);
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
        }
        if (rightStaff(staff)){
            radius *= 2;
        }
        ColorUtil colorUtil = new ColorUtil(0xffffff);
        worldIn.sendParticles(new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), (float) (radius * 2), radius), caster.getX(), caster.getY() + 0.5F, caster.getZ(), 0, 0, 0, 0, 0);
        ServerParticleUtil.windShockwaveParticle(worldIn, colorUtil, (float) radius, 0, -1, caster.position().add(0.0D, 1.0D, 0.0D));
        for (LivingEntity livingEntity : worldIn.getEntitiesOfClass(LivingEntity.class, caster.getBoundingBox().inflate(radius))){
            if (!MobUtil.areAllies(caster, livingEntity)) {
                double power = 4.0D + potency;
                double d0 = livingEntity.getX() - caster.getX();
                double d1 = livingEntity.getZ() - caster.getZ();
                double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
                MobUtil.push(livingEntity, d0 / d2 * power, 0.2D, d1 / d2 * power);
                livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, MathHelper.secondsToTicks(duration), potency));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, MathHelper.secondsToTicks(duration), potency));
            }
        }
        this.playSound(worldIn, caster, ModSounds.WIND_HORN.get(), 3.0F, 1.0F);
    }
}
