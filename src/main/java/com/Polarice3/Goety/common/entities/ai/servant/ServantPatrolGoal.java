package com.Polarice3.Goety.common.entities.ai.servant;

import com.Polarice3.Goety.api.entities.ally.IServant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;

public class ServantPatrolGoal<T extends PathfinderMob & IServant> extends Goal {
    public final T mob;
    private final double speed;
    private int waitTicks;

    public ServantPatrolGoal(T mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.mob.isPatrolling()
                && this.mob.getTarget() == null
                && this.mob.getCurrentWaypoint() != null;
    }

    @Override
    public void start() {
        super.start();
        this.waitTicks = 40;
    }

    @Override
    public void tick() {
        GlobalPos waypoint = this.mob.getCurrentWaypoint();
        if (waypoint == null) {
            return;
        }
        if (!waypoint.dimension().equals(this.mob.level.dimension())) {
            return;
        }

        BlockPos pos = waypoint.pos();
        AABB aabb = new AABB(pos).inflate(1.0D);
        if (this.mob.getBoundingBox().inflate(1.0D).intersects(aabb)) {
            if (--this.waitTicks <= 0) {
                this.mob.advancePatrol();
                this.waitTicks = 40;
            }
        } else if (this.mob.getNavigation().isDone()) {
            this.mob.getNavigation().moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, this.speed);
        }
    }
}
