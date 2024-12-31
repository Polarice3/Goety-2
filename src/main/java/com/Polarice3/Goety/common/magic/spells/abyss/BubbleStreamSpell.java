package com.Polarice3.Goety.common.magic.spells.abyss;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.BreathingSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class BubbleStreamSpell extends BreathingSpell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setRange(8);
    }

    @Override
    public int defaultSoulCost() {
        return SpellConfig.BubbleStreamCost.get();
    }

    @Override
    public int defaultCastUp() {
        return SpellConfig.BubbleStreamChargeUp.get();
    }

    @Override
    public int shotsNumber() {
        return SpellConfig.BubbleStreamDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.BubbleStreamCoolDown.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.BUBBLE_STREAM.get();
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        return list;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ABYSS;
    }

    @Override
    public boolean conditionsMet(ServerLevel worldIn, LivingEntity caster, SpellStat spellStat) {
        if (caster instanceof Mob mob){
            if (mob.getTarget() != null){
                int range = spellStat.getRange();
                if (WandUtil.enchantedFocus(caster)){
                    range += WandUtil.getLevels(ModEnchantments.RANGE.get(), caster);
                }
                return mob.hasLineOfSight(mob.getTarget()) && mob.distanceTo(mob.getTarget()) <= range + 4.0D;
            }
        }
        return super.conditionsMet(worldIn, caster, spellStat);
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat){
        float potency = spellStat.getPotency();
        int range = spellStat.getRange();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), caster);
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), caster);
        }
        float damage = SpellConfig.BubbleStreamDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
        damage += potency;
        if (!worldIn.isClientSide) {
            for (Entity target : getBreathTarget(caster, range)) {
                if (target != null) {
                    DamageSource damageSource = ModDamageSource.bubbleStream(caster, caster);
                    if (target.hurt(damageSource, damage)){
                        int air = Math.min(target.getAirSupply() + 1, target.getMaxAirSupply());
                        target.setAirSupply(air);
                    }
                }
            }
        }
        worldIn.playSound(null, caster.getX(), caster.getY(), caster.getZ(), ModSounds.BUBBLE_STREAM.get(), this.getSoundSource(), worldIn.random.nextFloat() * 0.5F, caster.getVoicePitch());
    }

    @Override
    public void showWandBreath(LivingEntity entityLiving) {
        int range = 0;
        if (entityLiving instanceof Player player){
            if (WandUtil.enchantedFocus(player)){
                range += WandUtil.getLevels(ModEnchantments.RANGE.get(), player);
            }
        }
        this.breathAttack(ModParticleTypes.BUBBLE_STREAM.get(), entityLiving, true, 0.3F + ((double) range / 10), 0);
    }
}
