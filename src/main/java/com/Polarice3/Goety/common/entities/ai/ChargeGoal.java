package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.api.entities.ICharger;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Based on ChargeAttackGoal codes made by @TeamTwilight: <a href="https://github.com/TeamTwilight/twilightforest/blob/1.19.x/src/main/java/twilightforest/entity/ai/goal/ChargeAttackGoal.java">...</a>
 */
public class ChargeGoal extends Goal {
    protected final PathfinderMob charger;
    protected LivingEntity chargeTarget;
    protected Vec3 chargePos;
    protected final float speed;
    protected double minRange;
    protected double maxRange;
    protected int chance;
    protected int windup;
    protected int coolDown;
    protected final int coolDownTotal;
    protected boolean hasAttacked;

    public ChargeGoal(PathfinderMob mob, float speed, int coolDownTotal) {
        this(mob, speed, 10, coolDownTotal);
    }

    public ChargeGoal(PathfinderMob mob, float speed, int chance, int coolDownTotal) {
        this(mob, speed, 4.0D, 8.0D, chance, coolDownTotal);
    }

    public ChargeGoal(PathfinderMob mob, float speed, double minRange, double maxRange, int chance, int coolDownTotal) {
        this.charger = mob;
        this.speed = speed;
        this.chance = chance;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.windup = 0;
        this.coolDownTotal = coolDownTotal;
        this.hasAttacked = false;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        this.chargeTarget = this.charger.getTarget();

        if (this.charger.tickCount < this.coolDown){
            return false;
        } else if (this.chargeTarget == null) {
            return false;
        } else {
            double distance = this.charger.distanceToSqr(this.chargeTarget);
            if (distance < this.minRange || distance > this.maxRange) {
                return false;
            } else if (!this.charger.isOnGround()) {
                return false;
            } else {
                Vec3 chargePos = findChargePoint(this.charger, this.chargeTarget);
                boolean canSeeTarget = this.charger.getSensing().hasLineOfSight(this.chargeTarget);
                if (!canSeeTarget) {
                    return false;
                } else {
                    this.chargePos = chargePos;

                    return this.charger.getRandom().nextInt(this.chance + 1) == 0;
                }
            }
        }
    }

    @Override
    public void start() {
        this.windup = MathHelper.secondsToTicks(1) + this.charger.getRandom().nextInt(MathHelper.secondsToTicks(1));
        this.charger.setSprinting(true);
    }

    @Override
    public boolean canContinueToUse() {
        return this.windup > 0 || !this.charger.getNavigation().isDone();
    }

    @Override
    public void tick() {
        this.charger.getLookControl().setLookAt(this.chargePos.x(), this.chargePos.y() - 1, this.chargePos.z(), 10.0F, this.charger.getMaxHeadXRot());

        if (this.windup > 0) {
            if (--this.windup == 0) {
                this.charger.getNavigation().moveTo(this.chargePos.x(), this.chargePos.y(), this.chargePos.z(), this.speed);
            } else {
                if (this.charger instanceof ICharger chargeMob) {
                    chargeMob.setCharging(true);
                }
            }
        }
        if (this.charger.distanceToSqr(this.chargeTarget.getX(), this.chargeTarget.getY(), this.chargeTarget.getZ()) <= this.getAttackReachSqr(this.chargeTarget)) {
            if (!this.hasAttacked) {
                this.hasAttacked = true;
                this.charger.doHurtTarget(this.chargeTarget);
            }
        }
    }

    @Override
    public void stop() {
        this.windup = 0;
        this.chargeTarget = null;
        this.hasAttacked = false;
        this.charger.setSprinting(false);
        this.coolDown = this.charger.tickCount + this.coolDownTotal;
        if (this.charger instanceof ICharger chargeMob) {
            chargeMob.setCharging(false);
        }
    }

    public double getAttackReachSqr(LivingEntity target) {
        return this.charger.getBbWidth() * 2.0F * this.charger.getBbWidth() * 2.0F + target.getBbWidth();
    }

    protected Vec3 findChargePoint(Entity attacker, Entity target) {
        double vecx = target.getX() - attacker.getX();
        double vecz = target.getZ() - attacker.getZ();
        float rangle = (float) (Math.atan2(vecz, vecx));

        double distance = Mth.sqrt((float) (vecx * vecx + vecz * vecz));
        double overshoot = 2.1D;

        double dx = Mth.cos(rangle) * (distance + overshoot);
        double dz = Mth.sin(rangle) * (distance + overshoot);

        return new Vec3(attacker.getX() + dx, target.getY(), attacker.getZ() + dz);
    }
}
