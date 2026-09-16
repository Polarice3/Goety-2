package com.Polarice3.Goety.common.entities.ai.servant;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

import java.util.EnumSet;

public class ServantFollowOwnerWaterGoal<T extends PathfinderMob & IServant> extends Goal {
    public final T summonedEntity;
    public LivingEntity owner;
    public final LevelReader level;
    public final double followSpeed;
    public Path path;
    public double pathedTargetX;
    public double pathedTargetY;
    public double pathedTargetZ;
    public int ticksUntilNextPathRecalculation;
    public final PathNavigation navigation;
    public final float maxDist;
    public final float minDist;

    public ServantFollowOwnerWaterGoal(T summonedEntity, double speed, float minDist, float maxDist) {
        this.summonedEntity = summonedEntity;
        this.level = summonedEntity.level;
        this.followSpeed = speed;
        this.navigation = summonedEntity.getNavigation();
        this.minDist = minDist;
        this.maxDist = maxDist;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        LivingEntity livingentity = this.summonedEntity.getTrueOwner();
        if (livingentity == null) {
            return false;
        } else if (livingentity.isSpectator()) {
            return false;
        } else if (this.summonedEntity.distanceToSqr(livingentity) < (double)(this.minDist * this.minDist)) {
            return false;
        } else if (this.summonedEntity.distanceTo(livingentity) >= 1024.0F) {
            return false;
        } else if (!this.summonedEntity.isFollowing()) {
            return false;
        } else if (this.summonedEntity.getTarget() != null) {
            return false;
        } else {
            this.owner = livingentity;
            if (!livingentity.isAlive()) {
                return false;
            } else {
                this.path = this.summonedEntity.getNavigation().createPath(livingentity, 0);
                if (this.path != null) {
                    return true;
                }
            }
            return true;
        }
    }

    public boolean canContinueToUse() {
        if (this.navigation.isDone()) {
            return false;
        } else if (this.summonedEntity.getTarget() != null){
            return false;
        } else {
            return !(this.summonedEntity.distanceToSqr(this.owner) <= (double)(this.maxDist * this.maxDist));
        }
    }

    public void start() {
        this.summonedEntity.getNavigation().moveTo(this.path, this.followSpeed);
        this.ticksUntilNextPathRecalculation = 0;
    }

    public void stop() {
        this.owner = null;
        this.navigation.stop();
    }

    public void tick() {
        this.summonedEntity.getLookControl().setLookAt(this.owner, 30.0F, 30.0F);
        double d0 = this.summonedEntity.distanceToSqr(this.owner.getX(), this.owner.getY(), this.owner.getZ());
        this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
        if (this.ticksUntilNextPathRecalculation <= 0 && (this.pathedTargetX == 0.0D && this.pathedTargetY == 0.0D && this.pathedTargetZ == 0.0D || this.owner.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0D || this.summonedEntity.getRandom().nextFloat() < 0.05F)) {
            this.pathedTargetX = this.owner.getX();
            this.pathedTargetY = this.owner.getY();
            this.pathedTargetZ = this.owner.getZ();
            this.ticksUntilNextPathRecalculation = 4 + this.summonedEntity.getRandom().nextInt(7);
            double range = this.owner instanceof Mob ? 32.0D : 16.0D;
            boolean flag = d0 > Mth.square(range);
            if (this.owner instanceof Mob){
                flag |= !this.summonedEntity.hasLineOfSight(this.owner) && d0 >= Mth.square(8.0D);
            } else {
                flag &= MobsConfig.ServantTeleport.get();
            }
            if (flag){
                this.tryToTeleportNearEntity();
            }
            if (d0 > 1024.0D) {
                this.ticksUntilNextPathRecalculation += 10;
            } else if (d0 > 256.0D) {
                this.ticksUntilNextPathRecalculation += 5;
            }

            if (!this.summonedEntity.getNavigation().moveTo(this.owner, this.followSpeed)) {
                this.ticksUntilNextPathRecalculation += 15;
            }
        }
    }

    private void tryToTeleportNearEntity() {
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

    private boolean tryToTeleportToLocation(int x, int y, int z) {
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

    private boolean isTeleportFriendlyBlock(BlockPos pos) {
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

    private int getRandomNumber(int min, int max) {
        return this.summonedEntity.getRandom().nextInt(max - min + 1) + min;
    }
}
