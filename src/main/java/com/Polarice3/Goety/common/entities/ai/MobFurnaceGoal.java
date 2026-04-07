package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.api.entities.IMobCrafter;
import com.Polarice3.Goety.utils.BlockFinder;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class MobFurnaceGoal<T extends Mob & IMobCrafter> extends Goal {
    public static int COOK_TIME = 100;
    public T mob;
    protected int tryTicks;
    @Nullable
    public BlockPos furnace;
    public int workTick;
    public int furnaceTime;
    public float speedModifier;
    public boolean playSound = false;

    public MobFurnaceGoal(T mob, int furnaceTime, float speedModifier) {
        this.mob = mob;
        this.furnaceTime = furnaceTime;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canStartFurnaceUsing() {
        return false;
    }

    @Override
    public boolean canUse() {
        if (this.mob.level instanceof ServerLevel) {
            if (this.canStartFurnaceUsing()) {
                this.furnace = this.findFurnace();
                if (this.furnace != null) {
                    this.mob.setFurnacePos(this.furnace);
                }
                return this.furnace != null && this.mob.getTarget() == null;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.mob.level instanceof ServerLevel serverLevel) {
            if (this.canStartFurnaceUsing()) {
                if (this.furnace != null) {
                    if (this.getNearbyFurnaceUsers(serverLevel, new AABB(this.furnace).inflate(4.0D), this.furnace).isEmpty()) {
                        if (this.tryTicks <= 1200) {
                            BlockState blockState = this.mob.level.getBlockState(this.furnace);
                            return this.mob.isFurnace(blockState) && super.canContinueToUse();
                        }
                    }
                }
            }
        }
        return false;
    }

    public void start() {
        this.moveMobToBlock();
        this.tryTicks = 0;
    }

    protected void moveMobToBlock() {
        if (this.furnace == null) {
            this.stop();
            return;
        }
        this.mob.getNavigation().moveTo((double)((float)this.furnace.getX()) + 0.5D, (double)(this.furnace.getY() + 1), (double)((float)this.furnace.getZ()) + 0.5D, this.speedModifier);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void stop() {
        if (!this.mob.isFurnaceActuallyCooking()) {
            this.mob.setFurnaceLit(false);
        }
        this.furnace = null;
        this.mob.setFurnacePos(null);
        this.tryTicks = 0;
        this.workTick = 0;
        this.mob.setUsingFurnace(false);
    }

    public void tick(){
        if (this.furnace == null) {
            this.stop();
            return;
        }
        if (this.mob.level instanceof ServerLevel serverLevel) {
            if (this.mob.distanceToSqr(Vec3.atCenterOf(this.furnace)) > Mth.square(2)) {
                ++this.tryTicks;
                if (this.shouldRecalculatePath()) {
                    this.mob.getNavigation().moveTo((double)((float)this.furnace.getX()) + 0.5D, (double)this.furnace.getY(), (double)((float)this.furnace.getZ()) + 0.5D, this.speedModifier);
                }
                if (this.mob.isUsingFurnace()) {
                    this.mob.setUsingFurnace(false);
                    if (!this.mob.isFurnaceActuallyCooking()) {
                        this.mob.setFurnaceLit(false);
                    }
                }
            } else {
                this.mob.getNavigation().stop();
                this.mob.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(this.furnace));
                this.tryTicks = 0;
                ++this.workTick;
                if (!this.mob.isUsingFurnace()) {
                    this.mob.setUsingFurnace(true);
                }
                if (!this.playSound) {
                    this.mob.setFurnaceLit(true);
                    this.playSound = true;
                }
                if (this.workTick > COOK_TIME) {
                    this.onSmelt(serverLevel);
                    this.playSound = false;
                    this.workTick = 0;
                    if (!this.mob.isFurnaceActuallyCooking()) {
                        this.mob.setFurnaceLit(false);
                    }
                }
            }
        }
    }

    public void onSmelt(ServerLevel serverLevel) {
    }

    public boolean shouldRecalculatePath() {
        return this.tryTicks % 40 == 0;
    }

    public List<Mob> getNearbyFurnaceUsers(Level level, AABB aabb, BlockPos blockPos) {
        return level.getEntitiesOfClass(Mob.class, aabb, mob -> mob instanceof IMobCrafter chef && chef.getFurnacePos().isPresent()
                && BlockFinder.samePos(chef.getFurnacePos().get(), blockPos)
                && chef.isUsingFurnace()
                && chef != this.mob);
    }

    public BlockPos findFurnace(){
        int i = 8;
        int j = 1;
        for(int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
            for(int l = 0; l < i; ++l) {
                for(int i1 = 0; i1 <= l; i1 = i1 > 0 ? -i1 : 1 - i1) {
                    for(int j1 = i1 < l && i1 > -l ? l : 0; j1 <= l; j1 = j1 > 0 ? -j1 : 1 - j1) {
                        BlockPos blockPos1 = this.mob.blockPosition().offset(i1, k - 1, j1);
                        BlockState blockState = this.mob.level.getBlockState(blockPos1);
                        if (this.mob.isFurnace(blockState)){
                            if (this.getNearbyFurnaceUsers(this.mob.level, new AABB(blockPos1).inflate(4.0D), blockPos1).isEmpty()) {
                                return blockPos1;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
