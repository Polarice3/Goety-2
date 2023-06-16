package com.Polarice3.Goety.common.magic;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public abstract class TouchSpells extends InstantCastSpells{
    public abstract void touchResult(ServerLevel worldIn, LivingEntity caster, LivingEntity target);
}
