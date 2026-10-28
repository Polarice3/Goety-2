package com.Polarice3.Goety.common.entities.ai.path;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;

/**
 * Based on @lgh877's codes:<a href="https://github.com/lgh877/CrimsonStevesMoreMobs/blob/57b2dda1f29392d516fcdd1deaec3a2e77b76369/src/main/java/net/mcreator/crimson_steves_mobs/SmartBodyHelper2.java">...</a>
 */
public class MonstrosityBodyRotControl extends BodyRotationControl {
    private final Mob mob;
    private static final float MAX_ROTATE = 75;
    private int headStableTime;
    private float lastStableYHeadRot;

    public MonstrosityBodyRotControl(Mob mob) {
        super(mob);
        this.mob = mob;
    }

    @Override
    public void clientTick() {
        if (this.isMoving()) {
            this.mob.yBodyRot = this.mob.getYRot();
            this.rotateHeadIfNecessary();
            this.lastStableYHeadRot = this.mob.yHeadRot;
            this.headStableTime = 0;
        } else {
            if (this.notCarryingMobPassengers()) {
                if (Math.abs(this.mob.yHeadRot - this.lastStableYHeadRot) > 15.0F) {
                    this.headStableTime = 0;
                    this.lastStableYHeadRot = this.mob.yHeadRot;
                    this.rotateBodyIfNecessary();
                } else {
                    float limit = MAX_ROTATE;
                    ++this.headStableTime;
                    int speed = 10;
                    if (this.headStableTime > speed) {
                        limit = Math.max(1 - (this.headStableTime - speed) / speed, 0) * MAX_ROTATE;
                    }

                    this.mob.yBodyRot = approach(this.mob.yHeadRot, this.mob.yBodyRot, limit);
                }
            }
        }
    }

    private void rotateBodyIfNecessary() {
        this.mob.yBodyRot = Mth.rotateIfNecessary(this.mob.yBodyRot, this.mob.yHeadRot, (float)this.mob.getMaxHeadYRot());
    }

    private void rotateHeadIfNecessary() {
        this.mob.yHeadRot = Mth.rotateIfNecessary(this.mob.yHeadRot, this.mob.yBodyRot, (float)this.mob.getMaxHeadYRot());
    }

    private boolean notCarryingMobPassengers() {
        return this.mob.getPassengers().isEmpty() || !(this.mob.getPassengers().get(0) instanceof Mob);
    }

    private boolean isMoving() {
        double d0 = this.mob.getX() - this.mob.xo;
        double d1 = this.mob.getZ() - this.mob.zo;
        return d0 * d0 + d1 * d1 > (double)2.5000003E-7F;
    }

    public static float approach(float target, float current, float limit) {
        float delta = Mth.wrapDegrees(current - target);
        if (delta < -limit) {
            delta = -limit;
        } else if (delta >= limit) {
            delta = limit;
        }
        return target + delta * 0.55F;
    }
}
