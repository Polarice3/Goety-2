package com.Polarice3.Goety.common.magic;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.magic.ISummonSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.function.Predicate;

public abstract class SummonSpell extends Spell implements ISummonSpell {
    public int enchantment = 0;
    public int duration = 1;

    public abstract int SummonDownDuration();

    public boolean NecroPower(LivingEntity entityLiving){
        return CuriosFinder.hasUndeadCape(entityLiving);
    }

    public int summonLimit(){
        return 64;
    }

    public Predicate<LivingEntity> summonPredicate(){
        return livingEntity -> livingEntity instanceof IOwned;
    }

    @Override
    public boolean conditionsMet(ServerLevel worldIn, LivingEntity entityLiving) {
        int count = 0;
        for (Entity entity : worldIn.getAllEntities()) {
            if (entity instanceof LivingEntity livingEntity && entity instanceof IOwned owned) {
                if (this.summonPredicate().test(livingEntity)) {
                    if (owned.getTrueOwner() == entityLiving && livingEntity.isAlive()) {
                        ++count;
                    }
                }
            }
        }
        if (count >= this.summonLimit() && !this.isShifting(entityLiving)){
            if (entityLiving instanceof Player player) {
                player.displayClientMessage(Component.translatable("info.goety.summon.limit"), true);
            }
            return false;
        } else {
            return super.conditionsMet(worldIn, entityLiving);
        }
    }

    public abstract void commonResult(ServerLevel worldIn, LivingEntity entityLiving);

    public void summonAdvancement(LivingEntity summoner, LivingEntity summoned){
        if(summoner instanceof ServerPlayer serverPlayer){
            CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, summoned);
        }
    }

    public void SummonDown(LivingEntity entityLiving){
        if (SpellConfig.SummonDown.get()){
            ISummonSpell.super.SummonDown(entityLiving);
        }
    }
}
