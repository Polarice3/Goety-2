package com.Polarice3.Goety.common.entities.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class FloatSwimGoal extends Goal {
    public PathfinderMob mob;
    
    public FloatSwimGoal(PathfinderMob mob) {
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.mob = mob;
    }

    public boolean canUse() {
        return !this.mob.getMoveControl().hasWanted()
                && this.mob.isNoGravity()
                && this.mob.level.random.nextInt(7) == 0;
    }

    public boolean canContinueToUse() {
        return false;
    }

    public void tick() {
        BlockPos blockpos = this.mob.blockPosition();

        for(int i = 0; i < 3; ++i) {
            BlockPos blockpos1 = blockpos.offset(this.mob.level.random.nextInt(15) - 7, this.mob.level.random.nextInt(11) - 5, this.mob.level.random.nextInt(15) - 7);
            if (this.mob.level.getFluidState(blockpos1).isSource() || this.mob.level.isEmptyBlock(blockpos1)) {
                this.mob.getMoveControl().setWantedPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D);
                if (this.mob.getTarget() == null) {
                    this.mob.getLookControl().setLookAt((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                }
                break;
            }
        }

    }
}
