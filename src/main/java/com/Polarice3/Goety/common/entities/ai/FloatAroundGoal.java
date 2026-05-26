package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.api.entities.ally.IServant;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FloatAroundGoal<T extends Mob & IServant> extends Goal {
    private final T mob;
    public float distance;
    public int height;
    public double speed;

    public FloatAroundGoal(T mob, float distance, int height, double speed) {
        this.mob = mob;
        this.distance = distance;
        this.height = height;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        MoveControl moveControl = this.mob.getMoveControl();
        if (this.mob.isCommanded() || this.mob.isStaying()){
            return false;
        } else if (!moveControl.hasWanted()) {
            return true;
        } else {
            double d0 = moveControl.getWantedX() - this.mob.getX();
            double d1 = moveControl.getWantedY() - this.mob.getY();
            double d2 = moveControl.getWantedZ() - this.mob.getZ();
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            return d3 < 1.0D || d3 > 3600.0D;
        }
    }

    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void start() {
        RandomSource random = this.mob.getRandom();
        BlockPos blockPos = null;
        if (this.mob.getBoundPos() != null){
            blockPos = this.mob.getBoundPos();
            if (this.mob.getTarget() != null){
                BlockPos blockPos1 = this.mob.getTarget().blockPosition().above(this.height);
                if (this.mob.isWithinGuard(blockPos1)) {
                    blockPos = blockPos1;
                }
            }
        } else if (this.mob.getTarget() != null){
            blockPos = this.mob.getTarget().blockPosition().above(this.height);
        } else if (this.mob.getTrueOwner() != null && this.mob.isFollowing()){
            blockPos = this.mob.getTrueOwner().blockPosition().above(this.height);
        }

        if (blockPos != null) {
            if (this.mob.distanceToSqr(Vec3.atCenterOf(blockPos)) < Mth.square(this.distance)) {
                Vec3 vector3d = Vec3.atCenterOf(blockPos).subtract(this.mob.position()).normalize();
                double X = this.mob.getX() + vector3d.x * this.distance + (random.nextFloat() * 2.0F - 1.0F) * this.distance;
                double Y = this.mob.getY() + vector3d.y * this.distance + (random.nextFloat() * 2.0F - 1.0F) * this.distance;
                double Z = this.mob.getZ() + vector3d.z * this.distance + (random.nextFloat() * 2.0F - 1.0F) * this.distance;

                this.mob.getMoveControl().setWantedPosition(X, Y, Z, this.speed);
            } else {
                this.mob.getMoveControl().setWantedPosition(blockPos.getX() + 0.5D, blockPos.getY(), blockPos.getZ() + 0.5D, this.speed);
            }
        } else {
            double d0 = this.mob.getX() + (random.nextFloat() * 2.0F - 1.0F) * this.distance;
            double d1 = this.mob.getY() + (random.nextFloat() * 2.0F - 1.0F) * this.distance;
            double d2 = this.mob.getZ() + (random.nextFloat() * 2.0F - 1.0F) * this.distance;
            this.mob.getMoveControl().setWantedPosition(d0, d1, d2, this.speed);
        }
    }
}
