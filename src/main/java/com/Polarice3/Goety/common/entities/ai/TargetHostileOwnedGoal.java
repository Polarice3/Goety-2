package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

import java.util.function.Predicate;

public class TargetHostileOwnedGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

    public TargetHostileOwnedGoal(Mob golem, Class<T> pClass) {
        super(golem, pClass, 5, false, false, predicate());
    }

    public static Predicate<LivingEntity> predicate(){
        return (entity) ->
                entity instanceof IOwned
                        && ((Owned) entity).isHostile();
    }
}
