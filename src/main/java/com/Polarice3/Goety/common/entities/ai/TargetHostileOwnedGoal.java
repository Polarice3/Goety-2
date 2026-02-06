package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ally.illager.raider.Prisoner;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;

import java.util.function.Predicate;

public class TargetHostileOwnedGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

    public TargetHostileOwnedGoal(Mob mob, Class<T> pClass) {
        super(mob, pClass, 5, false, false, predicate());
        if (mob.getType().is(ModTags.EntityTypes.VILLAGE_GUARDS)){
            this.targetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(villageGuardPredicate(mob));
        }
    }

    public static Predicate<LivingEntity> predicate(){
        return (entity) ->
                entity instanceof IOwned owned
                        && !(owned instanceof Prisoner)
                        && owned.isHostile();
    }

    public static Predicate<LivingEntity> villageGuardPredicate(Mob mob){
        return (entity) ->
                entity instanceof IOwned owned
                        && !(owned instanceof Prisoner)
                        && (owned.isHostile() || (!(mob instanceof IronGolem golem && golem.isPlayerCreated()) && owned instanceof RaiderServant raider && raider.isRaiding()));
    }
}
