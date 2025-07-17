package com.Polarice3.Goety.api.entities.ally;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.pathfinder.Path;

public interface IAquaServant extends IServant {

    default boolean wantsToSwim() {
        if (this instanceof Mob mob) {
            if (this.isSearchingForLand()) {
                return true;
            } else if (mob.getTarget() != null && mob.getTarget().isInWater()) {
                return true;
            } else {
                return this.getTrueOwner() != null && this.isFollowing() && (this.getTrueOwner().isInWater() || (mob.isInWater() && this.getTrueOwner().getY() > mob.getY()));
            }
        }
        return false;
    }

    default boolean closeToNextPos() {
        if (this instanceof Mob mob) {
            Path path = mob.getNavigation().getPath();
            if (path != null) {
                BlockPos blockpos = path.getTarget();
                if (blockpos != null) {
                    double d0 = mob.distanceToSqr((double) blockpos.getX(), (double) blockpos.getY(), (double) blockpos.getZ());
                    return d0 < 4.0D;
                }
            }
        }

        return false;
    }

    default boolean isSearchingForLand(){
        return false;
    }

    default void setSearchingForLand(boolean searching) {
    }
}
