package com.Polarice3.Goety.common.magic.spells.geomancy;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.SmashParticleOption;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class TemblorClapSpell extends Spell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setRadius(3.0D);
    }

    @Override
    public int defaultSoulCost() {
        return SpellConfig.TemblorClapCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.TemblorClapDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.TemblorClapCoolDown.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.GEOMANCY;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RADIUS.get());
        return list;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat){
        double radius = spellStat.getRadius();
        float potency = spellStat.getPotency();
        float damage = SpellConfig.TemblorClapDamage.get().floatValue() * WandUtil.damageMultiply();
        float maxDamage = SpellConfig.TemblorClapMaxDamage.get().floatValue() * WandUtil.damageMultiply();
        if (WandUtil.enchantedFocus(caster)){
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster);
            potency += WandUtil.getPotencyLevel(caster) / 2.0F;
        }
        damage += potency;
        maxDamage += potency;
        double increase = 0.0D;
        if (caster.onGround()) {
            increase += 0.5D;
        }
        if (rightStaff(staff)) {
            increase += 0.5D;
        }
        radius += increase;
        double groundY = BlockFinder.findGroundY(worldIn, caster.position());
        BlockPos blockPos = BlockPos.containing(caster.position().x, groundY - 1.0D, caster.position().z);

        int color = worldIn.getBlockState(blockPos).getMapColor(worldIn, blockPos).col;
        if (worldIn.getBlockState(blockPos).isAir()) {
            color = 0xfefaa3;
        }
        ColorUtil colorUtil = new ColorUtil(color);
        worldIn.sendParticles(new SmashParticleOption(colorUtil, (float) radius, 10), caster.getX(), caster.getY() + 0.5F, caster.getZ(), 0, 0, 0, 0, 0);
        float trueDamage = Mth.clamp(damage + RandomUtil.nextInt(worldIn.getRandom(), (int) (maxDamage - damage)), damage, maxDamage);
        new SpellExplosion(worldIn, caster, caster.damageSources().mobAttack(caster), caster.blockPosition(), (float) radius, trueDamage){
            @Override
            public void explodeHurt(Entity target, DamageSource damageSource, double x, double y, double z, double seen, float actualDamage) {
                if (target instanceof LivingEntity target1){
                    super.explodeHurt(target, damageSource, x, y, z, seen, actualDamage);
                    target1.addEffect(new MobEffectInstance(GoetyEffects.STUNNED.get(), MathHelper.secondsToTicks(3)));
                }
            }
        };
        this.playSound(worldIn, caster, ModSounds.REDSTONE_EXPLODE.get(), 2.0F, 1.0F);
    }
}
