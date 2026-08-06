package com.Polarice3.Goety.api.magic;

import com.Polarice3.Goety.common.magic.SpellStat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ITouchSpell extends ISpell{
    default int defaultCastDuration() {
        return 0;
    }

    default boolean targetConditions(ServerLevel worldIn, LivingEntity caster, @Nullable LivingEntity target, ItemStack staff, SpellStat spellStat) {
        return true;
    }

    default void touchResult(ServerLevel worldIn, LivingEntity caster, LivingEntity target, ItemStack staff, SpellStat spellStat){
    }
}
