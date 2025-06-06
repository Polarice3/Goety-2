package com.Polarice3.Goety.api.entities;

import net.minecraft.world.entity.LivingEntity;

public interface IGolem {

    default boolean canAnimateMove(){
        return false;
    }

    double getAttackReachSqr(LivingEntity enemy);

    default boolean targetClose(LivingEntity enemy, double distToEnemySqr){
        if (this instanceof LivingEntity self) {
            double reach = this.getAttackReachSqr(enemy);
            return distToEnemySqr <= reach || self.getBoundingBox().intersects(enemy.getBoundingBox());
        } else {
            return false;
        }
    }
}
