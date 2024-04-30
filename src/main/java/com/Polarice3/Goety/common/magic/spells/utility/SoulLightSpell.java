package com.Polarice3.Goety.common.magic.spells.utility;

import com.Polarice3.Goety.common.entities.projectiles.SoulLight;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.config.SpellConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class SoulLightSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.SoulLightCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.SoulLightDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.SoulLightCoolDown.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return SoundEvents.EGG_THROW;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff){
        SoulLight soulLightEntity = new SoulLight(worldIn, entityLiving);
        soulLightEntity.setOwner(entityLiving);
        soulLightEntity.shootFromRotation(entityLiving, entityLiving.getXRot(), entityLiving.getYRot(), 0.0F, 1.5F, 1.0F);
        worldIn.addFreshEntity(soulLightEntity);
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), CastingSound(), this.getSoundSource(), 1.0F, 1.0F);
    }
}
