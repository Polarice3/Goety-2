package com.Polarice3.Goety.api.magic;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ITouchSpell extends ISpell{
    default int CastDuration() {
        return 0;
    }

    default void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
    }

    void touchResult(ServerLevel worldIn, LivingEntity caster, LivingEntity target);
}
