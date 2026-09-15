package com.Polarice3.Goety.common.entities.ai.servant;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

import java.util.EnumSet;

public class ServantFollowOwnerGoal<T extends Mob & IServant> extends Goal {
    public final T summonedEntity;
    public LivingEntity owner;
    public final LevelReader level;
    public final double followSpeed;
    public final PathNavigation navigation;
    public int timeToRecalcPath;
    public final float stopDistance;
    public final float startDistance;
    public float oldWaterCost;

    public ServantFollowOwnerGoal(T summonedEntity, double speed, float startDistance, float stopDistance) {
        this.summonedEntity = summonedEntity;
        this.level = summonedEntity.level;
        this.followSpeed = speed;
        this.navigation = summonedEntity.getNavigation();
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        if (!(summonedEntity.getNavigation() instanceof GroundPathNavigation) && !(summonedEntity.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    public boolean canUse() {
        LivingEntity livingentity = this.summonedEntity.getTrueOwner();
        if (livingentity == null) {
            return false;
        } else if (livingentity.isSpectator()) {
            return false;
        } else if (this.summonedEntity.distanceToSqr(livingentity) < (double)(Mth.square(this.startDistance))) {
            return false;
        } else if (!this.summonedEntity.isFollowing() || this.summonedEntity.isCommanded()) {
            return false;
        } else if (this.summonedEntity.getTarget() != null) {
            return false;
        } else {
            this.owner = livingentity;
            return true;
        }
    }

    public boolean canContinueToUse() {
        if (this.navigation.isDone()) {
            return false;
        } else if (this.summonedEntity.getTarget() != null){
            return false;
        } else {
            return !(this.summonedEntity.distanceToSqr(this.owner) <= (double)(Mth.square(this.stopDistance)));
        }
    }

    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.summonedEntity.getPathfindingMalus(BlockPathTypes.WATER);
        this.summonedEntity.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.summonedEntity.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
    }

    public void tick() {
        if (this.owner != null) {
            this.summonedEntity.getLookControl().setLookAt(this.owner, 10.0F, (float) this.summonedEntity.getMaxHeadXRot());
            if (this.summonedEntity.getControlledVehicle() != null){
                this.navigation.moveTo(this.owner, this.followSpeed + 0.25D);
                if (this.summonedEntity.getControlledVehicle() instanceof Mob mob){
                    mob.getNavigation().moveTo(this.owner, this.followSpeed + 0.25D);
                }
            } else if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = 10;
                if (!this.summonedEntity.isLeashed() && !this.summonedEntity.isPassenger()) {
                    double range = this.owner instanceof Mob ? 32.0D : 16.0D;
                    boolean flag = this.summonedEntity.distanceToSqr(this.owner) >= Mth.square(range);
                    if (this.owner instanceof Mob){
                        flag |= !this.summonedEntity.hasLineOfSight(this.owner) && this.summonedEntity.distanceToSqr(this.owner) >= Mth.square(8.0D);
                    } else {
                        flag &= this.canTeleport();
                    }
                    if (flag) {
                        this.tryToTeleportNearEntity();
                    } else {
                        this.navigation.moveTo(this.owner, this.followSpeed);
                    }
                }
            }
        }
    }

    protected boolean canTeleport() {
        return MobsConfig.ServantTeleport.get();
    }

    protected void tryToTeleportNearEntity() {
        BlockPos blockpos = this.owner.blockPosition();

        for(int i = 0; i < 10; ++i) {
            int j = this.getRandomNumber(-3, 3);
            int k = this.getRandomNumber(-1, 1);
            int l = this.getRandomNumber(-3, 3);
            boolean flag = this.tryToTeleportToLocation(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
            if (flag) {
                return;
            }
        }

    }

    protected boolean tryToTeleportToLocation(int x, int y, int z) {
        if (Math.abs((double)x - this.owner.getX()) < 2.0D && Math.abs((double)z - this.owner.getZ()) < 2.0D) {
            return false;
        } else if (!this.isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
            return false;
        } else {
            this.summonedEntity.moveTo((double)x + 0.5D, (double)y, (double)z + 0.5D, this.summonedEntity.getYRot(), this.summonedEntity.getXRot());
            this.navigation.stop();
            return true;
        }
    }

    protected boolean isTeleportFriendlyBlock(BlockPos pos) {
        BlockPathTypes pathnodetype = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, pos.mutable());
        if (pathnodetype != BlockPathTypes.WALKABLE) {
            return false;
        } else {
            BlockState blockstate = this.level.getBlockState(pos.below());
            if (blockstate.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockpos = pos.subtract(this.summonedEntity.blockPosition());
                return this.level.noCollision(this.summonedEntity, this.summonedEntity.getBoundingBox().move(blockpos));
            }
        }
    }

    protected int getRandomNumber(int min, int max) {
        return this.summonedEntity.getRandom().nextInt(max - min + 1) + min;
    }
}
