package com.Polarice3.Goety.common.magic.spells.geomancy;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.ShockwaveParticleOption;
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

public class WarStompSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.DischargeCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.DischargeDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.DischargeCoolDown.get();
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
    public boolean conditionsMet(ServerLevel worldIn, LivingEntity caster, SpellStat spellStat) {
        return caster.onGround();
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RADIUS.get());
        return list;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat){
        int radius = (int) spellStat.getRadius();
        float potency = spellStat.getPotency();
        float damage = SpellConfig.DischargeDamage.get().floatValue() * WandUtil.damageMultiply();
        float maxDamage = SpellConfig.DischargeMaxDamage.get().floatValue() * WandUtil.damageMultiply();
        if (WandUtil.enchantedFocus(caster)){
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster);
            potency += WandUtil.getPotencyLevel(caster) / 2.0F;
        }
        damage += potency;
        maxDamage += potency;
        double groundY = BlockFinder.findGroundY(worldIn, caster.position());
        BlockPos blockPos = BlockPos.containing(caster.position().x, groundY - 1.0D, caster.position().z);

        ColorUtil colorUtil = new ColorUtil(worldIn.getBlockState(blockPos).getMapColor(worldIn, blockPos).col);
        worldIn.sendParticles(new ShockwaveParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue()), caster.getX(), caster.getY() + 0.5F, caster.getZ(), 0, 0, 0, 0, 0);
        worldIn.sendParticles(new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), radius, 1), caster.getX(), caster.getY() + 0.5F, caster.getZ(), 1, 0, 0, 0, 0);
        float trueDamage = Mth.clamp(damage + RandomUtil.nextInt(worldIn.getRandom(), (int) (maxDamage - damage)), damage, maxDamage);
        new SpellExplosion(worldIn, caster, caster.damageSources().mobAttack(caster), caster.blockPosition(), radius, trueDamage){
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
