package com.Polarice3.Goety.common.magic.spells.wind;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WindHornSpell extends Spell {

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
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        int radius = 3;
        if (WandUtil.enchantedFocus(entityLiving)){
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), entityLiving);
        }
        if (rightStaff(staff)){
            radius *= 2;
        }
        ColorUtil colorUtil = new ColorUtil(0xffffff);
        worldIn.sendParticles(new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), radius * 2, radius), entityLiving.getX(), entityLiving.getY() + 0.5F, entityLiving.getZ(), 0, 0, 0, 0, 0);
        ServerParticleUtil.windShockwaveParticle(worldIn, colorUtil, radius, 0, -1, entityLiving.position().add(0.0D, 1.0D, 0.0D));
        for (LivingEntity livingEntity : worldIn.getEntitiesOfClass(LivingEntity.class, entityLiving.getBoundingBox().inflate(radius))){
            if (!MobUtil.areAllies(entityLiving, livingEntity)) {
                double d0 = livingEntity.getX() - entityLiving.getX();
                double d1 = livingEntity.getZ() - entityLiving.getZ();
                double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
                MobUtil.push(livingEntity, d0 / d2 * 4.0D, 0.2D, d1 / d2 * 4.0D);
                livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, MathHelper.secondsToTicks(3 + WandUtil.getLevels(ModEnchantments.DURATION.get(), entityLiving)), WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving)));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, MathHelper.secondsToTicks(3 + WandUtil.getLevels(ModEnchantments.DURATION.get(), entityLiving)), WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving)));
            }
        }
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.WIND_HORN.get(), this.getSoundSource(), 3.0F, 1.0F);
    }
}
