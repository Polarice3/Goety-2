package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.client.particles.RisingCircleParticleOption;
import com.Polarice3.Goety.client.particles.SoulShockwaveParticleOption;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class SoulHealSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.SoulHealCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.SoulHealDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.SoulHealCoolDown.get();
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

    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff){
        int enchantment = 1;
        double radius = 0;
        if (WandUtil.enchantedFocus(entityLiving)) {
            enchantment += WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), entityLiving);
        }
        float heal = worldIn.random.nextInt(SpellConfig.SoulHealAmount.get() * enchantment) + 1.0F;
        entityLiving.heal(heal);
        if (radius > 0) {
            for (LivingEntity livingEntity : worldIn.getEntitiesOfClass(LivingEntity.class, entityLiving.getBoundingBox().inflate(8.0D * radius))) {
                if (livingEntity instanceof OwnableEntity owned) {
                    if (owned.getOwner() == entityLiving) {
                        livingEntity.heal(heal);
                        healParticles(livingEntity, worldIn);
                    }
                }
            }
        }
        worldIn.sendParticles(new SoulShockwaveParticleOption(), entityLiving.getX(), entityLiving.getY() + 0.5F, entityLiving.getZ(), 0, 0, 0, 0, 0);
        healParticles(entityLiving, worldIn);
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.SOUL_HEAL.get(), this.getSoundSource(), 1.0F, 1.0F);
    }

    public void healParticles(LivingEntity livingEntity, ServerLevel worldIn){
        worldIn.sendParticles(new RisingCircleParticleOption(0), livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 0, 0, 0, 0, 0);
        worldIn.sendParticles(new RisingCircleParticleOption(5), livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 0, 0, 0, 0, 0);
        worldIn.sendParticles(new RisingCircleParticleOption(10), livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 0, 0, 0, 0, 0);
    }
}
