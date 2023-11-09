package com.Polarice3.Goety.common.magic;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public abstract class TouchSpells extends InstantCastSpells{

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
    }

    public abstract void touchResult(ServerLevel worldIn, LivingEntity caster, LivingEntity target);
}
