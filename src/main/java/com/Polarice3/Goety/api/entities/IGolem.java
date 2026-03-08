package com.Polarice3.Goety.api.entities;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public interface IGolem {

    default boolean canAnimateMove(){
        return false;
    }

    default double getAttackReachSqr(LivingEntity enemy) {
        if (this instanceof Mob mob) {
            return mob.getMeleeAttackRangeSqr(enemy);
        }
        return 0.0D;
    }

    default boolean targetClose(LivingEntity enemy, double distToEnemySqr){
        if (this instanceof LivingEntity self) {
            double reach = this.getAttackReachSqr(enemy);
            return distToEnemySqr <= reach || self.getBoundingBox().intersects(enemy.getBoundingBox());
        } else {
            return false;
        }
    }
}
