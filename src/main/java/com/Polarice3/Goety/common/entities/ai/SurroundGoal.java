package com.Polarice3.Goety.common.entities.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class SurroundGoal<T extends Mob> extends Goal {
   private final T mob;
   private final double speedModifier;
   private final double attackRadiusSqr;
   private boolean strafingClockwise;
   private boolean strafingBackwards;
   private boolean chanceBack;
   private int strafingTime = -1;

   public SurroundGoal(T mob, double speedModifier, float radius, boolean chanceBack) {
      this.mob = mob;
      this.speedModifier = speedModifier;
      this.attackRadiusSqr = radius * radius;
      this.chanceBack = chanceBack;
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public SurroundGoal(T mob, double speedModifier, float radius) {
      this(mob, speedModifier, radius, true);
   }

   public boolean canUse() {
      return this.mob.getTarget() != null && this.mob.hasLineOfSight(this.mob.getTarget());
   }

   public void start() {
      super.start();
      this.mob.setAggressive(true);
      this.strafingTime = 0;
   }

   public void stop() {
      super.stop();
      this.mob.setAggressive(false);
      this.mob.getMoveControl().strafe(0.0F, 0.0F);
   }

   public boolean requiresUpdateEveryTick() {
      return true;
   }

   public void tick() {
      LivingEntity livingentity = this.mob.getTarget();
      if (livingentity != null) {
         double d0 = this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());

         if (d0 <= this.attackRadiusSqr) {
            this.mob.getNavigation().stop();
            ++this.strafingTime;
         } else {
            this.mob.getNavigation().moveTo(livingentity, this.speedModifier);
            this.strafingTime = -1;
         }

         if (this.strafingTime >= 20) {
            if ((double)this.mob.getRandom().nextFloat() < 0.3D) {
               this.strafingClockwise = !this.strafingClockwise;
            }

            if (this.chanceBack) {
               if ((double) this.mob.getRandom().nextFloat() < 0.3D) {
                  this.strafingBackwards = !this.strafingBackwards;
               }
            }

            this.strafingTime = 0;
         }

         if (this.strafingTime > -1) {
            this.strafingBackwards = !(d0 > this.attackRadiusSqr);

            this.mob.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
            this.mob.lookAt(livingentity, 30.0F, 30.0F);
         } else {
            this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
         }
      }
   }
}
