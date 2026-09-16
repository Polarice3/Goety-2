package com.Polarice3.Goety.common.entities.ai.servant;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.utils.BlockFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;

public class ServantReturnToGuardPos<T extends PathfinderMob & IServant> extends MoveToBlockGoal {
    public final T servant;
    public int range;

    public ServantReturnToGuardPos(T servant, double speed, int range) {
        super(servant, speed, range);
        this.servant = servant;
        this.range = range;
    }

    @Override
    public boolean canUse() {
        if (this.servant.isGuardingArea()) {
            if (super.canUse()) {
                return this.servant.distanceToSqr(this.servant.vec3BoundPos()) > Mth.square(this.range);
            }
        }
        return false;
    }

    protected boolean findNearestBlock() {
        if (this.servant.isGuardingArea()) {
            this.blockPos = this.servant.getBoundPos();
            return this.blockPos != null;
        }
        return false;
    }

    @Override
    protected boolean isValidTarget(LevelReader p_25619_, BlockPos p_25620_) {
        if (this.servant.isGuardingArea()) {
            return BlockFinder.samePos(this.servant.getBoundPos(), p_25620_);
        }
        return false;
    }
}
