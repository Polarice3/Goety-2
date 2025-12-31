package com.Polarice3.Goety.common.magic.spells.wild;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.neutral.InsectSwarm;
import com.Polarice3.Goety.common.magic.BreathingSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SwarmSpell extends BreathingSpell {
    public float damage = SpellConfig.SwarmDamage.get().floatValue() * WandUtil.damageMultiply();

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setDuration(1).setRange(8);
    }

    @Override
    public int defaultSoulCost() {
        return SpellConfig.SwarmCost.get();
    }

    @Override
    public int defaultCastUp() {
        return SpellConfig.SwarmChargeUp.get();
    }

    @Override
    public int shotsNumber() {
        return SpellConfig.SwarmDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.SwarmCoolDown.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return null;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WILD;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        list.add(ModEnchantments.RANGE.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        int range = spellStat.getRange();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
            range += WandUtil.getRangeLevel(caster);
        }
        float damage = this.damage + potency;
        if (!worldIn.isClientSide) {
            for (Entity target : getBreathTarget(caster, range)) {
                if (target != null) {
                    if (target.hurt(ModDamageSource.swarm(caster, caster), damage)){
                        if (rightStaff(staff)) {
                            if (target instanceof LivingEntity livingTarget) {
                                MobEffect mobEffect = MobEffects.POISON;
                                if (CuriosFinder.hasWildRobe(caster)) {
                                    mobEffect = GoetyEffects.ACID_VENOM.get();
                                }
                                livingTarget.addEffect(new MobEffectInstance(mobEffect, MathHelper.secondsToTicks(5) * duration));
                            }
                        }
                        if (!target.isAlive()){
                            InsectSwarm insectSwarm = new InsectSwarm(worldIn, caster, target.position());
                            insectSwarm.setLimitedLife(200 * duration);
                            if (potency > 0){
                                insectSwarm.addEffect(new MobEffectInstance(GoetyEffects.BUFF.get(), EffectsUtil.infiniteEffect(), potency - 1, false, false));
                            }
                            worldIn.addFreshEntity(insectSwarm);
                        }
                    }
                }
            }
        }
        this.playSound(worldIn, caster, ModSounds.INSECT_SWARM.get(), worldIn.random.nextFloat() * 0.5F, worldIn.random.nextFloat() * 0.5F);
    }

    @Override
    public void showWandBreath(LivingEntity entityLiving, ItemStack staff, SpellStat spellStat) {
        int range = 0;
        if (entityLiving instanceof Player player){
            if (WandUtil.enchantedFocus(player)){
                range += WandUtil.getRangeLevel(entityLiving);
            }
        }
        this.breathAttack(ModParticleTypes.FLY.get(), entityLiving, true, 0.3F + ((double) range / 10), 5);
    }
}
