package com.Polarice3.Goety.api.magic;

import com.Polarice3.Goety.common.magic.SpellStat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ITouchSpell extends ISpell{
    default int defaultCastDuration() {
        return 0;
    }

    @Deprecated(forRemoval = true)
    default void touchResult(ServerLevel worldIn, LivingEntity caster, LivingEntity target, SpellStat spellStat){
        this.touchResult(worldIn, caster, target, ItemStack.EMPTY, spellStat);
    }

    default void touchResult(ServerLevel worldIn, LivingEntity caster, LivingEntity target, ItemStack staff, SpellStat spellStat){
    }
}
