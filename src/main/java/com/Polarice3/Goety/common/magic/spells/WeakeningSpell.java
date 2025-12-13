package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.ShockwaveParticleOption;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
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

public class WeakeningSpell extends Spell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setDuration(5).setRadius(7.0D);
    }

    @Override
    public int defaultSoulCost() {
        return SpellConfig.WeakeningCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.WeakeningDuration.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.WEAKEN_CAST.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.WeakeningCoolDown.get();
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RADIUS.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat){
        int potency = spellStat.getPotency();
        int radius = (int) spellStat.getRadius();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)){
            potency += WandUtil.getPotencyLevel(caster);
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
        }
        ColorUtil colorUtil = new ColorUtil(ChatFormatting.LIGHT_PURPLE);
        worldIn.sendParticles(new ShockwaveParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, radius, 1, true), caster.getX(), caster.getY() + 0.25F, caster.getZ(), 0, 0, 0, 0, 0);
        for (int i = -radius; i < radius; ++i){
            for (int k = -radius; k < radius; ++k){
                BlockPos blockPos = caster.blockPosition().offset(i, 0, k);
                if (worldIn.random.nextFloat() <= 0.25F){
                    worldIn.sendParticles(ModParticleTypes.SPELL_SQUARE.get(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), 0, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 0.5F);
                }
            }
        }
        for (LivingEntity livingEntity : worldIn.getEntitiesOfClass(LivingEntity.class, caster.getBoundingBox().inflate(radius))){
            if (!MobUtil.areAllies(caster, livingEntity)) {
                livingEntity.addEffect(new MobEffectInstance(GoetyEffects.SAPPED.get(), MathHelper.secondsToTicks(duration), potency + 4));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, MathHelper.secondsToTicks(duration), potency));
                livingEntity.playSound(ModSounds.WEAKEN_CURSE.get());
            }
        }
        this.playSound(worldIn, caster, 2.0F, 1.0F);
    }
}
