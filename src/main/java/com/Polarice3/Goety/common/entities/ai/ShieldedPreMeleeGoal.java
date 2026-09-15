package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.api.entities.IShielded;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;

public class ShieldedPreMeleeGoal<T extends PathfinderMob & IShielded> extends ModMeleeAttackGoal {
    public T mob;
    private int delayCounter;
    private static final double SPEED = 1.0D;
    
    public ShieldedPreMeleeGoal(T mob) {
        this(mob, SPEED);
    }

    public ShieldedPreMeleeGoal(T mob, double speed) {
        super(mob, speed, true);
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        return this.mob.getTarget() != null
                && this.mob.getTarget().isAlive();
    }

    @Override
    public void start() {
        this.mob.setAggressive(true);
        this.delayCounter = 0;
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
        if (this.mob.getTarget() == null) {
            this.mob.setAggressive(false);
        }
    }

    @Override
    public void tick() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity == null) {
            return;
        }

        this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
        double d0 = this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());

        if (--this.delayCounter <= 0 && !this.mob.targetClose(livingentity, d0)) {
            this.delayCounter = 10;
            this.mob.getNavigation().moveTo(livingentity, this.speedModifier);
        }

        this.checkAndPerformAttack(livingentity, this.mob.distanceToSqr(livingentity.getX(), livingentity.getBoundingBox().minY, livingentity.getZ()));
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
        if (this.mob.targetClose(enemy, distToEnemySqr)) {
            if (!this.mob.isMeleeAttacking()) {
                this.mob.setMeleeAttacking(true);
            }
        }
    }
}
