package com.Polarice3.Goety.common.entities.ai.servant;

import com.Polarice3.Goety.api.entities.ally.IServant;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class ServantGoToWaterGoal<T extends PathfinderMob & IServant> extends Goal {
    private final T mob;
    private double wantedX;
    private double wantedY;
    private double wantedZ;
    private final double speedModifier;
    private final Level level;

    public ServantGoToWaterGoal(T p_i48910_1_, double p_i48910_2_) {
        this.mob = p_i48910_1_;
        this.speedModifier = p_i48910_2_;
        this.level = p_i48910_1_.level;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        if (this.mob.getTrueOwner() != null){
            if (!this.mob.getTrueOwner().isInWater()){
                return false;
            }
        } else if (!this.level.isDay()) {
            return false;
        }
        if (this.mob.isInWater()) {
            return false;
        }
        Vec3 vector3d = this.getWaterPos();
        if (vector3d == null) {
            return false;
        } else {
            this.wantedX = vector3d.x;
            this.wantedY = vector3d.y;
            this.wantedZ = vector3d.z;
            return true;
        }
    }

    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone();
    }

    public void start() {
        this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
    }

    @Nullable
    private Vec3 getWaterPos() {
        RandomSource random = this.mob.getRandom();
        BlockPos blockpos = this.mob.blockPosition();

        for(int i = 0; i < 10; ++i) {
            BlockPos blockpos1 = blockpos.offset(random.nextInt(20) - 10, 2 - random.nextInt(8), random.nextInt(20) - 10);
            if (this.level.getBlockState(blockpos1).is(Blocks.WATER)) {
                return Vec3.atBottomCenterOf(blockpos1);
            }
        }

        return null;
    }
}
