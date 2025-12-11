package com.Polarice3.Goety.common.entities.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class GoToTargetGoal extends Goal {
    public Mob mob;
    public float approachRange;
    public float speedModifier;

    public GoToTargetGoal(Mob mob, float approachRange, float speedModifier) {
        this.mob = mob;
        this.approachRange = approachRange;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK, Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (this.mob.getTarget() != null) {
            LivingEntity target = this.mob.getTarget();
            if (target.distanceTo(this.mob) > this.approachRange) {
                this.mob.getNavigation().moveTo(target, this.speedModifier);
                return true;
            }
        }
        return false;
    }
}
