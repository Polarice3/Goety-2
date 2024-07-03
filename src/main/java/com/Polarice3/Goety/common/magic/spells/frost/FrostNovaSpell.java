package com.Polarice3.Goety.common.magic.spells.frost;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.ShockwaveParticleOption;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FrostNovaSpell extends Spell {
    @Override
    public int defaultSoulCost() {
        return SpellConfig.FrostNovaCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.FrostNovaDuration.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.FROST_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.FrostNovaCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.FROST;
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
        float radius = 2.5F;
        int duration = 1;
        float damage = SpellConfig.FrostNovaDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
        float maxDamage = SpellConfig.FrostNovaMaxDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
        if (WandUtil.enchantedFocus(entityLiving)){
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), entityLiving);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), entityLiving);
            damage += WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving) / 2.0F;
            maxDamage += WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving) / 2.0F;
        }
        LivingEntity spellTarget = entityLiving;
        LivingEntity target = this.getTarget(entityLiving);
        if (isShifting(entityLiving) && target != null){
            spellTarget = target;
        }
        this.createParticleBall(worldIn, spellTarget, (int) radius);
        worldIn.sendParticles(new ShockwaveParticleOption(0, radius * 2, 1), spellTarget.getX(), spellTarget.getY() + 0.5F, spellTarget.getZ(), 0, 0, 0, 0, 0);
        float trueDamage = Mth.clamp(damage + worldIn.random.nextInt((int) (maxDamage - damage)), damage, maxDamage);
        int finalDuration = duration;
        new SpellExplosion(worldIn, entityLiving, ModDamageSource.directFreeze(entityLiving), spellTarget.blockPosition(), radius, trueDamage){
            @Override
            public void explodeHurt(Entity target, DamageSource damageSource, double x, double y, double z, double seen, float actualDamage) {
                if (target instanceof LivingEntity target1 && !MobUtil.areAllies(entityLiving, target1) && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target1)){
                    super.explodeHurt(target, damageSource, x, y, z, seen, actualDamage);
                    target1.addEffect(new MobEffectInstance(GoetyEffects.FREEZING.get(), MathHelper.secondsToTicks(5) * finalDuration));
                }
            }
        };
        worldIn.playSound(null, spellTarget.getX(), spellTarget.getY(), spellTarget.getZ(), ModSounds.ICE_CHUNK_HIT.get(), this.getSoundSource(), 1.0F, 0.5F);
    }

    private void createParticleBall(ServerLevel serverLevel, LivingEntity livingEntity, int radius) {
        double d0 = livingEntity.getX();
        double d1 = livingEntity.getY();
        double d2 = livingEntity.getZ();

        for(int i = -radius; i <= radius; ++i) {
            for(int j = -radius; j <= radius; ++j) {
                for(int k = -radius; k <= radius; ++k) {
                    double d3 = (double)j + (livingEntity.getRandom().nextDouble() - livingEntity.getRandom().nextDouble()) * 0.5D;
                    double d4 = (double)i + (livingEntity.getRandom().nextDouble() - livingEntity.getRandom().nextDouble()) * 0.5D;
                    double d5 = (double)k + (livingEntity.getRandom().nextDouble() - livingEntity.getRandom().nextDouble()) * 0.5D;
                    double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5) / 0.5 + livingEntity.getRandom().nextGaussian() * 0.05D;
                    serverLevel.sendParticles(ModParticleTypes.FROST_NOVA.get(), d0, d1, d2, 0, d3 / d6, d4 / d6, d5 / d6, 0.5F);
                    if (i != -radius && i != radius && j != -radius && j != radius) {
                        k += radius * 2 - 1;
                    }
                }
            }
        }
    }
}
