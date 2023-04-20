package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.entities.projectiles.GlowLight;
import com.Polarice3.Goety.common.magic.InstantCastSpells;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

public class GlowLightSpell extends InstantCastSpells {

    @Override
    public int SoulCost() {
        return SpellConfig.GlowLightCost.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return SoundEvents.EGG_THROW;
    }

    @Override
    public void RegularResult(ServerLevel worldIn, LivingEntity entityLiving) {
        GlowLight soulLightEntity = new GlowLight(worldIn, entityLiving);
        soulLightEntity.setOwner(entityLiving);
        soulLightEntity.shootFromRotation(entityLiving, entityLiving.getXRot(), entityLiving.getYRot(), 0.0F, 1.5F, 1.0F);
        worldIn.addFreshEntity(soulLightEntity);
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), CastingSound(), SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public void StaffResult(ServerLevel worldIn, LivingEntity entityLiving) {
        GlowLight soulLightEntity = new GlowLight(worldIn, entityLiving);
        soulLightEntity.setOwner(entityLiving);
        soulLightEntity.shootFromRotation(entityLiving, entityLiving.getXRot(), entityLiving.getYRot(), 0.0F, 1.5F, 1.0F);
        worldIn.addFreshEntity(soulLightEntity);
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), CastingSound(), SoundSource.PLAYERS, 1.0F, 1.0F);
    }
}
