package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.api.entities.ally.IServant;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

//Based on @l_ender's codes:https://github.com/lender544/new1.20.1/blob/master/src/main/java/com/github/L_Ender/cataclysm/entity/AI/MobAILeaveWater.java
public class ModLeaveWaterGoal<T extends PathfinderMob & IServant> extends Goal {
    private final T mob;
    private BlockPos targetPos;

    public ModLeaveWaterGoal(T mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        if (this.mob.level.getFluidState(this.mob.blockPosition()).is(FluidTags.WATER) && (this.mob.getTarget() != null || this.mob.getTrueOwner() != null || this.mob.getRandom().nextInt(30) == 0) && this.shouldLeaveWater()) {
            this.targetPos = this.generateTarget();
            return this.targetPos != null;
        } else {
            return false;
        }
    }

    public boolean canContinueToUse() {
        if (this.shouldLeaveWater()) {
            this.mob.getNavigation().stop();
            return false;
        } else {
            return !this.mob.getNavigation().isDone() && this.targetPos != null && !this.mob.level.getFluidState(this.targetPos).is(FluidTags.WATER);
        }
    }

    public void start() {
        if (this.targetPos != null) {
            this.mob.getNavigation().moveTo(this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), 1.0F);
        }

    }

    public void tick() {
        if (this.targetPos != null) {
            this.mob.getNavigation().moveTo(this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), 1.0F);
        }

        if (this.mob.horizontalCollision && this.mob.isInWater()) {
            float f1 = (float) (this.mob.getYRot() * (Math.PI / 180.0F));
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(-Mth.sin(f1) * 0.2F, 0.1F, Mth.cos(f1) * 0.2F));
        }

    }

    public BlockPos generateTarget() {
        Vec3 vector3d = LandRandomPos.getPos(this.mob, 23, 7);

        for(int tries = 0; vector3d != null && tries < 8; ++tries) {
            boolean waterDetected = false;
            for (BlockPos blockpos1 : BlockPos.betweenClosed(Mth.floor(vector3d.x - 2.0), Mth.floor(vector3d.y - 1.0), Mth.floor(vector3d.z - 2.0), Mth.floor(vector3d.x + 2.0), Mth.floor(vector3d.y), Mth.floor(vector3d.z + 2.0))) {
                if (this.mob.level.getFluidState(blockpos1).is(FluidTags.WATER)) {
                    waterDetected = true;
                    break;
                }
            }

            if (!waterDetected) {
                return BlockPos.containing(vector3d);
            }

            vector3d = LandRandomPos.getPos(this.mob, 23, 7);
        }

        return null;
    }

    public boolean shouldLeaveWater() {
        if (this.mob.getTrueOwner() != null && !this.mob.getTrueOwner().isInWater()){
            return true;
        }
        return this.mob.getTarget() != null && !this.mob.getTarget().isInWater();
    }
}
