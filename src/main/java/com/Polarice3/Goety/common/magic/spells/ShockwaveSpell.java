package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.SoulShockwaveParticleOption;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spells;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import com.Polarice3.Goety.common.network.server.SSoulExplodePacket;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ShockwaveSpell extends Spells {

    @Override
    public int SoulCost() {
        return SpellConfig.ShockwaveCost.get();
    }

    @Override
    public int CastDuration() {
        return SpellConfig.ShockwaveDuration.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff){
        int radius = 3;
        float damage = SpellConfig.ShockwaveDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
        float maxDamage = SpellConfig.ShockwaveMaxDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
        if (WandUtil.enchantedFocus(entityLiving)){
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), entityLiving);
            damage += WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
            maxDamage += WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
        }
        for (int i = -radius; i < radius; ++i){
            for (int k = -radius; k < radius; ++k){
                BlockPos blockPos = entityLiving.blockPosition().offset(i, 0, k);
                if (worldIn.random.nextFloat() <= 0.25F){
                    worldIn.sendParticles(ModParticleTypes.SOUL_EXPLODE.get(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), 0, 0, 0.04D, 0, 0.5F);
                }
            }
        }
        worldIn.sendParticles(new SoulShockwaveParticleOption(0), entityLiving.getX(), entityLiving.getY() + 0.5F, entityLiving.getZ(), 0, 0, 0, 0, 0);
        float trueDamage = Mth.clamp(damage + worldIn.random.nextInt((int) (maxDamage - damage)), damage, maxDamage);
        ModNetwork.sendToALL(new SSoulExplodePacket(entityLiving.blockPosition(), radius));
        ModNetwork.sendToALL(new SPlayWorldSoundPacket(entityLiving.blockPosition(), ModSounds.SOUL_EXPLODE.get(), 4.0F, 1.0F));
        MobUtil.explosionDamage(worldIn, entityLiving, DamageSource.indirectMagic(entityLiving, entityLiving), entityLiving.blockPosition(), radius, trueDamage);
    }
}
