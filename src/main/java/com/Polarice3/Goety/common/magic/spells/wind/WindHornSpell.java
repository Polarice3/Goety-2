package com.Polarice3.Goety.common.magic.spells.wind;

import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
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
        return 0;
    }

    @Override
    public int defaultCastDuration() {
        return 0;
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return null;
    }

    @Override
    public int defaultSpellCooldown() {
        return 0;
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
        new SpellExplosion(worldIn, entityLiving, entityLiving.damageSources().magic(), entityLiving.blockPosition(), radius, 0.0F) {
            public void explodeHurt(Entity target, DamageSource damageSource, double x, double y, double z, double seen, float actualDamage) {
                if (target instanceof LivingEntity livingEntity) {
                    MobUtil.push(target, x * seen, y * seen, z * seen);
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, MathHelper.secondsToTicks(3 + WandUtil.getLevels(ModEnchantments.DURATION.get(), entityLiving)), WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving)));
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, MathHelper.secondsToTicks(3 + WandUtil.getLevels(ModEnchantments.DURATION.get(), entityLiving)), WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving)));
                }
            }
        };
    }
}
