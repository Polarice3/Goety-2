package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.api.entities.IMobCrafter;
import com.Polarice3.Goety.utils.BlockFinder;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class MobCraftingGoal<T extends Mob & IMobCrafter> extends Goal {
    public T mob;
    public int tryTicks;
    @Nullable
    public BlockPos craftTable;
    public int workTick;
    public int craftTime;
    public int checkCooldown = 0;
    public int lastInventorySize = -1;
    public float speedModifier;
    public boolean cachedCanCraft = false;

    public MobCraftingGoal(T mob, int craftTime, float speedModifier) {
        this.mob = mob;
        this.craftTime = craftTime;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canStartCrafting() {
        return false;
    }

    @Override
    public boolean canUse() {
        if (!this.inventoryChanged() && --this.checkCooldown > 0) {
            return this.cachedCanCraft
                    && !this.mob.isUsingFurnace()
                    && this.mob.level instanceof ServerLevel;
        }
        this.checkCooldown = 40;
        this.cachedCanCraft = this.craftCheck();
        return this.cachedCanCraft;
    }

    public boolean craftCheck() {
        if (this.mob.level instanceof ServerLevel) {
            if (this.canStartCrafting()) {
                this.craftTable = this.findCraftTable();
                if (this.craftTable != null) {
                    this.mob.setCraftTablePos(this.craftTable);
                }
                return this.craftTable != null && this.mob.getTarget() == null;
            }
        }
        return false;
    }

    public boolean inventoryChanged() {
        if (this.mob instanceof InventoryCarrier carrier) {
            int currentSize = 0;
            SimpleContainer inv = carrier.getInventory();
            for (int i = 0; i < inv.getContainerSize(); i++) {
                currentSize += inv.getItem(i).getCount();
            }
            if (currentSize != this.lastInventorySize) {
                this.lastInventorySize = currentSize;
                return true;
            }
        }
        return false;
    }

    public boolean canContinueToUse() {
        if (this.mob.level instanceof ServerLevel serverLevel) {
            if (this.canStartCrafting()) {
                if (this.craftTable != null) {
                    if (this.getNearbyCraftTableUsers(serverLevel, new AABB(this.craftTable).inflate(4.0D), this.craftTable).isEmpty()) {
                        if (this.tryTicks <= 1200) {
                            BlockState blockState = this.mob.level.getBlockState(this.craftTable);
                            return this.mob.isCraftTable(blockState) && super.canContinueToUse();
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
        if (this.craftTable == null) {
            this.stop();
            return;
        }
        this.mob.getNavigation().moveTo((double)((float)this.craftTable.getX()) + 0.5D, (double)(this.craftTable.getY() + 1), (double)((float)this.craftTable.getZ()) + 0.5D, this.speedModifier);
    }

    @Override
    public void stop() {
        this.craftTable = null;
        this.mob.setCraftTablePos(null);
        this.tryTicks = 0;
        this.workTick = 0;
        this.mob.setCrafting(false);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (this.craftTable == null) {
            this.stop();
            return;
        }
        if (this.mob.level instanceof ServerLevel serverLevel) {
            if (this.mob.distanceToSqr(Vec3.atCenterOf(this.craftTable)) > Mth.square(2)) {
                ++this.tryTicks;
                if (this.shouldRecalculatePath()) {
                    this.mob.getNavigation().moveTo((double)((float)this.craftTable.getX()) + 0.5D, (double)this.craftTable.getY(), (double)((float)this.craftTable.getZ()) + 0.5D, this.speedModifier);
                }
                if (this.mob.isCrafting()) {
                    this.mob.setCrafting(false);
                }
            } else {
                this.mob.getNavigation().stop();
                this.mob.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(this.craftTable));
                this.tryTicks = 0;
                ++this.workTick;
                if (!this.mob.isCrafting()) {
                    this.mob.setCrafting(true);
                }

                if (this.workTick >= this.craftTime) {
                    this.workTick = 0;
                    this.onCraft(serverLevel);
                }
            }
        }
    }

    public void onCraft(ServerLevel serverLevel) {
    }

    public boolean shouldRecalculatePath() {
        return this.tryTicks % 40 == 0;
    }

    public List<Mob> getNearbyCraftTableUsers(Level level, AABB aabb, BlockPos blockPos) {
        return level.getEntitiesOfClass(Mob.class, aabb, mob -> mob instanceof IMobCrafter chef && chef.getCraftTablePos().isPresent()
                && BlockFinder.samePos(chef.getCraftTablePos().get(), blockPos)
                && chef.isCrafting()
                && chef != this.mob);
    }

    public BlockPos findCraftTable(){
        int i = 8;
        int j = 1;
        for(int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
            for(int l = 0; l < i; ++l) {
                for(int i1 = 0; i1 <= l; i1 = i1 > 0 ? -i1 : 1 - i1) {
                    for(int j1 = i1 < l && i1 > -l ? l : 0; j1 <= l; j1 = j1 > 0 ? -j1 : 1 - j1) {
                        BlockPos blockPos1 = this.mob.blockPosition().offset(i1, k - 1, j1);
                        BlockState blockState = this.mob.level.getBlockState(blockPos1);
                        if (this.mob.isCraftTable(blockState)){
                            if (this.getNearbyCraftTableUsers(this.mob.level, new AABB(blockPos1).inflate(4.0D), blockPos1).isEmpty()) {
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
