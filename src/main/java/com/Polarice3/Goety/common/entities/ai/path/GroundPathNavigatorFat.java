package com.Polarice3.Goety.common.entities.ai.path;

import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

//Based on @l_ender's codes:https://github.com/lender544/new1.20.1/blob/master/src/main/java/com/github/L_Ender/cataclysm/entity/etc/path/GroundPathNavigatorWide.java
public class GroundPathNavigatorFat extends GroundPathNavigation {
    private float distanceModifier = 0.75F;

    public GroundPathNavigatorFat(Mob mob, Level worldIn) {
        super(mob, worldIn);
    }

    public GroundPathNavigatorFat(Mob mob, Level worldIn, float distanceModifier) {
        super(mob, worldIn);
        this.distanceModifier = distanceModifier;
    }

    protected void followThePath() {
        Vec3 vector3d = this.getTempMobPos();
        this.maxDistanceToWaypoint = this.mob.getBbWidth() * this.distanceModifier;
        if (this.path != null) {
            Vec3i vector3i = this.path.getNextNodePos();
            double d0 = Math.abs(this.mob.getX() - ((double) vector3i.getX() + 0.5));
            double d1 = Math.abs(this.mob.getY() - (double) vector3i.getY());
            double d2 = Math.abs(this.mob.getZ() - ((double) vector3i.getZ() + 0.5));
            boolean flag = d0 < (double) this.maxDistanceToWaypoint && d2 < (double) this.maxDistanceToWaypoint && d1 < 1.0;
            if (flag || this.canCutCorner(this.path.getNextNode().type) && this.shouldTargetNextNodeInDirection(vector3d)) {
                this.path.advance();
            }
        }

        this.doStuckDetection(vector3d);
    }

    private boolean shouldTargetNextNodeInDirection(Vec3 currentPosition) {
        if (this.path == null){
            return false;
        } else if (this.path.getNextNodeIndex() + 1 >= this.path.getNodeCount()) {
            return false;
        } else {
            Vec3 vector3d = Vec3.atBottomCenterOf(this.path.getNextNodePos());
            if (!currentPosition.closerThan(vector3d, 2.0)) {
                return false;
            } else {
                Vec3 vector3d1 = Vec3.atBottomCenterOf(this.path.getNodePos(this.path.getNextNodeIndex() + 1));
                Vec3 vector3d2 = vector3d1.subtract(vector3d);
                Vec3 vector3d3 = currentPosition.subtract(vector3d);
                return vector3d2.dot(vector3d3) > 0.0;
            }
        }
    }
}
