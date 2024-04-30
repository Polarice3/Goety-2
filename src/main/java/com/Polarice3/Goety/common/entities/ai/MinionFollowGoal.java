package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.common.entities.neutral.Minion;
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

public class MinionFollowGoal extends Goal {
    private final Minion summonedEntity;
    private LivingEntity owner;
    private final LevelReader level;
    private final double followSpeed;
    private final PathNavigation navigation;
    private int timeToRecalcPath;
    private final float maxDist;
    private final float minDist;
    private float oldWaterCost;
    private final boolean teleportToLeaves;

    public MinionFollowGoal(Minion summonedEntity, double speed, float minDist, float maxDist, boolean teleportToLeaves) {
        this.summonedEntity = summonedEntity;
        this.level = summonedEntity.level;
        this.followSpeed = speed;
        this.navigation = summonedEntity.getNavigation();
        this.minDist = minDist;
        this.maxDist = maxDist;
        this.teleportToLeaves = teleportToLeaves;
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
        } else if (livingentity instanceof Mob) {
            return false;
        } else if (this.summonedEntity.distanceToSqr(livingentity) < (double)(this.minDist * this.minDist)) {
            return false;
        } else if (this.summonedEntity.getTarget() != null) {
            return false;
        } else if (this.summonedEntity.isCharging()) {
            return false;
        } else {
            this.owner = livingentity;
            return true;
        }
    }

    public boolean canContinueToUse() {
        if (this.summonedEntity.getTarget() != null) {
            return false;
        } else if (this.summonedEntity.isCharging()) {
            return false;
        } else if (this.navigation.isDone()){
            return false;
        } else {
            return !(this.summonedEntity.distanceToSqr(this.owner) <= (double)(this.maxDist * this.maxDist));
        }
    }

    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.summonedEntity.getPathfindingMalus(BlockPathTypes.WATER);
        this.summonedEntity.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    public void stop() {
        this.navigation.stop();
        this.summonedEntity.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
    }

    public void tick() {
        this.summonedEntity.getLookControl().setLookAt(this.owner, 10.0F, (float)this.summonedEntity.getMaxHeadXRot());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            if (this.summonedEntity.distanceTo(this.owner) > 8.0D) {
                double x = Mth.floor(this.owner.getX()) - 2;
                double y = Mth.floor(this.owner.getBoundingBox().minY);
                double z = Mth.floor(this.owner.getZ()) - 2;
                for(int l = 0; l <= 4; ++l) {
                    for(int i1 = 0; i1 <= 4; ++i1) {
                        if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.ValidPosition(BlockPos.containing(x + l, y + 2, z + i1))){
                            float a = (float) ((x + l) + 0.5F);
                            float b = (float) ((z + i1) + 0.5F);
                            this.summonedEntity.getMoveControl().setWantedPosition(a, y, b, this.followSpeed);
                            this.navigation.stop();
                        }
                    }
                }
            }
            if (this.summonedEntity.distanceToSqr(this.owner) > 144.0 && MobsConfig.VexTeleport.get()){
                this.tryToTeleportNearEntity();
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
            if (!this.teleportToLeaves && blockstate.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockpos = pos.subtract(this.summonedEntity.blockPosition());
                return this.level.noCollision(this.summonedEntity, this.summonedEntity.getBoundingBox().move(blockpos));
            }
        }
    }

    protected boolean ValidPosition(BlockPos pos) {
        BlockState blockstate = this.level.getBlockState(pos);
        return (blockstate.canSurvive(this.level, pos) && this.level.isEmptyBlock(pos.above()) && this.level.isEmptyBlock(pos.above(2)));
    }

    private int getRandomNumber(int min, int max) {
        return this.summonedEntity.getRandom().nextInt(max - min + 1) + min;
    }
}
