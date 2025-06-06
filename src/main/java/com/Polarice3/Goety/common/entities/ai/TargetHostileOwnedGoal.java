package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ally.illager.RaiderServant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;

import java.util.function.Predicate;

public class TargetHostileOwnedGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

    public TargetHostileOwnedGoal(Mob golem, Class<T> pClass) {
        super(golem, pClass, 5, false, false, predicate());
        if (golem instanceof IronGolem ironGolem){
            this.targetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(ironGolemPredicate(ironGolem));
        }
    }

    public static Predicate<LivingEntity> predicate(){
        return (entity) ->
                entity instanceof IOwned owned
                        && owned.isHostile();
    }

    public static Predicate<LivingEntity> ironGolemPredicate(IronGolem golem){
        return (entity) ->
                entity instanceof IOwned owned
                        && (owned.isHostile() || (!golem.isPlayerCreated() && owned instanceof RaiderServant raider && raider.isRaiding()));
    }
}
