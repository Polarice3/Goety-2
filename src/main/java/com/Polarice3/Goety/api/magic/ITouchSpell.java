package com.Polarice3.Goety.api.magic;

import com.Polarice3.Goety.common.magic.SpellStat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public interface ITouchSpell extends ISpell{
    default int defaultCastDuration() {
        return 0;
    }

    @Deprecated(forRemoval = true)
    default void touchResult(ServerLevel worldIn, LivingEntity caster, LivingEntity target){
        this.touchResult(worldIn, caster, target, this.defaultStats());
    }

    default void touchResult(ServerLevel worldIn, LivingEntity caster, LivingEntity target, SpellStat spellStat){
    }
}
