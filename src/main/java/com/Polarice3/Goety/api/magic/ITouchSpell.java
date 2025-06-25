package com.Polarice3.Goety.api.magic;

import com.Polarice3.Goety.common.magic.SpellStat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public interface ITouchSpell extends ISpell{
    default int defaultCastDuration() {
        return 0;
    }

    default void touchResult(ServerLevel worldIn, LivingEntity caster, LivingEntity target, SpellStat spellStat){
    }
}
