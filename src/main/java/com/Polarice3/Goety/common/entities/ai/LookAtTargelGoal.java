package com.Polarice3.Goety.common.entities.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class LookAtTargelGoal extends Goal {
    protected final Mob mob;
    protected LivingEntity target;
    protected final float lookDistance;

    public LookAtTargelGoal(Mob looker, float distance) {
        this.mob = looker;
        this.target = looker.getTarget();
        this.lookDistance = distance;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    public boolean canUse() {
        this.target = this.mob.getTarget();
        return this.target != null;
    }

    public boolean canContinueToUse() {
        if (!this.target.isAlive()) {
            return false;
        } else return !(this.mob.distanceToSqr(this.target) > (double) (this.lookDistance * this.lookDistance));
    }

    public void stop() {
        this.target = null;
        this.mob.getLookControl().setLookAt(this.mob);
    }

    public void tick() {
        if (this.target.isAlive()) {
            this.mob.getLookControl().setLookAt(this.target.getX(), this.mob.getEyeY(), this.target.getZ());
        }
    }
}
