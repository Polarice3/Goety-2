package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import com.Polarice3.Goety.common.network.server.SSoulExplodePacket;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class ShockwaveSpell extends Spell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setRadius(3.0D);
    }

    @Override
    public int defaultSoulCost() {
        return SpellConfig.ShockwaveCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.ShockwaveDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.ShockwaveCoolDown.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
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
        int potency = spellStat.getPotency();
        float damage = SpellConfig.ShockwaveDamage.get().floatValue() * WandUtil.damageMultiply();
        float maxDamage = SpellConfig.ShockwaveMaxDamage.get().floatValue() * WandUtil.damageMultiply();
        if (WandUtil.enchantedFocus(caster)){
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster);
            potency += WandUtil.getPotencyLevel(caster);
        }
        damage += potency;
        maxDamage += potency;
        for (int i = -radius; i < radius; ++i){
            for (int k = -radius; k < radius; ++k){
                BlockPos blockPos = caster.blockPosition().offset(i, 0, k);
                if (worldIn.random.nextFloat() <= 0.25F){
                    worldIn.sendParticles(ModParticleTypes.SOUL_EXPLODE.get(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), 0, 0, 0.04D, 0, 0.5F);
                }
            }
        }
        ColorUtil colorUtil = new ColorUtil(0x2ac9cf);
        worldIn.sendParticles(new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), radius * 2, radius), caster.getX(), caster.getY() + 0.5F, caster.getZ(), 0, 0, 0, 0, 0);
        ServerParticleUtil.windShockwaveParticle(worldIn, colorUtil, radius, 0, -1, caster.position().add(0.0D, 0.5D, 0.0D));
        float trueDamage = Mth.clamp(damage + RandomUtil.nextInt(worldIn.getRandom(), (int) (maxDamage - damage)), damage, maxDamage);
        ModNetwork.sendToALL(new SSoulExplodePacket(caster.blockPosition(), radius));
        ModNetwork.sendToALL(new SPlayWorldSoundPacket(caster.blockPosition(), ModSounds.SOUL_EXPLODE.get(), 4.0F, 1.0F));
        MobUtil.explosionDamage(worldIn, caster, worldIn.damageSources().indirectMagic(caster, caster), caster.blockPosition(), radius, trueDamage);
    }
}
