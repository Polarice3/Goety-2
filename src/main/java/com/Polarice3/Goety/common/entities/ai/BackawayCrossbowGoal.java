package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.utils.CrossbowHelper;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.NodeEvaluator;

import java.util.EnumSet;

//Improved codes based on @TeamAbnormal's codes: https://github.com/team-abnormals/savage-and-ravage/blob/1.20.x/src/main/java/com/teamabnormals/savage_and_ravage/common/entity/ai/goal/ImprovedCrossbowGoal.java
public class BackawayCrossbowGoal<T extends PathfinderMob & RangedAttackMob & CrossbowAttackMob> extends Goal {
    private final T mob;
    private CrossbowState crossbowState = CrossbowState.UNCHARGED;
    private final double speedModifier;
    private final float attackRadiusSqr;
    private int seeTime;
    private int attackDelay;

    public BackawayCrossbowGoal(T p_i50322_1_, double p_i50322_2_, float p_i50322_4_) {
        this.mob = p_i50322_1_;
        this.speedModifier = p_i50322_2_;
        this.attackRadiusSqr = p_i50322_4_ * p_i50322_4_;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        return this.isValidTarget() && this.isHoldingCrossbow();
    }

    private boolean isHoldingCrossbow() {
        return this.mob.isHolding(is -> is.getItem() instanceof CrossbowItem);
    }

    public boolean canContinueToUse() {
        return this.isValidTarget() && (this.canUse() || !this.mob.getNavigation().isDone()) && this.isHoldingCrossbow();
    }

    private boolean isValidTarget() {
        return this.mob.getTarget() != null && this.mob.getTarget().isAlive();
    }

    public void start() {
        super.start();
        this.mob.setAggressive(true);
    }

    public void stop() {
        super.stop();
        this.mob.setAggressive(false);
        this.mob.setTarget((LivingEntity)null);
        this.seeTime = 0;
        if (this.mob.isUsingItem()) {
            this.mob.stopUsingItem();
            this.mob.setChargingCrossbow(false);
            CrossbowItem.setCharged(this.mob.getUseItem(), false);
        }

    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity != null) {
            boolean canSeeEnemy = this.mob.getSensing().hasLineOfSight(livingentity);

            if (canSeeEnemy) {
                ++this.seeTime;
            } else {
                this.seeTime = 0;
            }

            double distanceSq = this.mob.distanceToSqr(livingentity);
            double distance = Mth.sqrt((float) distanceSq);
            if (distance <= 14.0F) {
                if (this.mob.getBlockStateOn().isFaceSturdy(this.mob.level, this.mob.blockPosition(), Direction.UP, SupportType.CENTER)) {
                    this.mob.getMoveControl().strafe(mob.isUsingItem() ? -0.5F : -3.0F, 0);
                }
            }

            ItemStack activeStack = this.mob.getUseItem();
            boolean isFarAway = distanceSq > (double) this.attackRadiusSqr;
            boolean shouldMoveTowardsEnemy = (isFarAway || this.seeTime < 5);
            if (shouldMoveTowardsEnemy) {
                double speedChange = this.crossbowState != CrossbowState.CHARGING ? this.speedModifier : this.speedModifier * 0.5D;
                this.mob.getNavigation().moveTo(livingentity, speedChange);
            } else {
                this.mob.getNavigation().stop();
            }

            this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);

            if (this.crossbowState == CrossbowState.UNCHARGED && !CrossbowItem.isCharged(activeStack)) {
                if (canSeeEnemy) {
                    this.mob.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.mob, item -> item instanceof CrossbowItem));
                    this.crossbowState = CrossbowState.CHARGING;
                    this.mob.setChargingCrossbow(true);
                }
            } else if (this.crossbowState == CrossbowState.CHARGING) {
                if (!this.mob.isUsingItem()) {
                    this.crossbowState = CrossbowState.UNCHARGED;
                }

                int i = this.mob.getTicksUsingItem();
                if (i >= CrossbowItem.getChargeDuration(activeStack) || CrossbowItem.isCharged(activeStack)) {
                    this.mob.releaseUsingItem();
                    this.crossbowState = CrossbowState.CHARGED;
                    this.attackDelay = 20 + this.mob.getRandom().nextInt(20);
                    if (this.mob.getOffhandItem().getItem() instanceof FireworkRocketItem) {
                        this.mob.startUsingItem(InteractionHand.OFF_HAND);
                    }
                    this.mob.setChargingCrossbow(false);
                }
            } else if (this.crossbowState == CrossbowState.CHARGED) {
                --this.attackDelay;
                if (this.attackDelay == 0) {
                    this.crossbowState = CrossbowState.READY_TO_ATTACK;
                }
            } else if (this.crossbowState == CrossbowState.READY_TO_ATTACK && livingentity.distanceTo(this.mob) <= 20.0F && canSeeEnemy) {
                this.mob.performRangedAttack(livingentity, 1.0F);
                CrossbowItem.setCharged(this.mob.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this.mob, item -> item instanceof CrossbowItem)), false);
                this.crossbowState = CrossbowState.UNCHARGED;
            }
        }
    }

    private boolean isWalkable() {
        PathNavigation pathnavigator = this.mob.getNavigation();
        NodeEvaluator nodeprocessor = pathnavigator.getNodeEvaluator();
        return nodeprocessor.getBlockPathType(this.mob.level(), Mth.floor(this.mob.getX() + 1.0D), Mth.floor(this.mob.getY()), Mth.floor(this.mob.getZ() + 1.0D)) == BlockPathTypes.WALKABLE;
    }

    private boolean isCrossbowUncharged() {
        return this.crossbowState == CrossbowState.UNCHARGED;
    }

    private boolean hasFirework() {
        if (this.mob.getProjectile(this.mob.getUseItem()).getItem() == Items.FIREWORK_ROCKET){
            return true;
        } else if (this.mob.getOffhandItem().getItem() == Items.FIREWORK_ROCKET) {
            return true;
        } else {
            for (ItemStack projectileStack : CrossbowHelper.getChargedProjectiles(this.mob.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this.mob, item -> item instanceof CrossbowItem)))) {
                if (projectileStack.getItem() == Items.FIREWORK_ROCKET) {
                    return true;
                }
            }
        }
        return false;
    }

    enum CrossbowState {
        UNCHARGED,
        CHARGING,
        CHARGED,
        READY_TO_ATTACK;
    }
}
