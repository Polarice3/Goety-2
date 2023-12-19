package com.Polarice3.Goety.common.magic;

import com.Polarice3.Goety.api.magic.ISummonSpell;
import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public abstract class SummonSpells extends Spells implements ISummonSpell {
    public int enchantment = 0;
    public int duration = 1;

    public abstract int SummonDownDuration();

    public boolean NecroPower(LivingEntity entityLiving){
        return CuriosFinder.hasUndeadCape(entityLiving);
    }

    public abstract void commonResult(ServerLevel worldIn, LivingEntity entityLiving);

    public void summonAdvancement(LivingEntity summoner, LivingEntity summoned){
        if(summoner instanceof ServerPlayer serverPlayer){
            CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, summoned);
        }
    }
}
