package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;

import java.util.function.Predicate;

public class AvoidTargetGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {

    public AvoidTargetGoal(PathfinderMob mob, Class<T> tClass, Predicate<LivingEntity> predicate, float radius, double minSpeed, double maxSpeed) {
        super(mob, tClass, predicate, radius, minSpeed, maxSpeed, livingEntity -> mob.getTarget() == livingEntity);
    }

    public AvoidTargetGoal(PathfinderMob mob, Class<T> tClass, float radius, double minSpeed, double maxSpeed) {
        super(mob, tClass, radius, minSpeed, maxSpeed, livingEntity -> mob.getTarget() == livingEntity);
    }

    public static AvoidTargetGoal<LivingEntity> newGoal(PathfinderMob pathfinderMob, float radius, double minSpeed, double maxSpeed){
        return new AvoidTargetGoal<>(pathfinderMob, LivingEntity.class, radius, minSpeed, maxSpeed);
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.toAvoid != null && this.toAvoid.distanceTo(this.mob) < this.maxDist;
    }

    public void stop() {
        super.stop();
        this.pathNav.stop();
    }

    public static class AvoidRadiusGoal<T extends LivingEntity> extends ModAvoidGoal<T>{
        public AvoidRadiusGoal(PathfinderMob mob, Class<T> tClass, Predicate<LivingEntity> predicate, float avoidStart, int maxDist, int minDist, double walkSpeed, double runSpeed, Predicate<LivingEntity> target) {
            super(mob, tClass, predicate, avoidStart, maxDist, minDist, walkSpeed, runSpeed, target);
        }

        public AvoidRadiusGoal(PathfinderMob mob, Class<T> tClass, Predicate<LivingEntity> predicate, float avoidStart, int maxDist, int minDist, double walkSpeed, double runSpeed) {
            this(mob, tClass, predicate, avoidStart, maxDist, minDist, walkSpeed, runSpeed, livingEntity -> mob.getTarget() == livingEntity);
        }

        public AvoidRadiusGoal(PathfinderMob mob, Class<T> tClass, float avoidStart, int maxDist, int minDist, double walkSpeed, double runSpeed) {
            super(mob, tClass, avoidStart, maxDist, minDist, walkSpeed, runSpeed, livingEntity -> mob.getTarget() == livingEntity);
        }

        public static AvoidRadiusGoal<LivingEntity> newGoal(PathfinderMob pathfinderMob, float avoidStart, int maxDist, double minSpeed, double maxSpeed){
            return new AvoidRadiusGoal<>(pathfinderMob, LivingEntity.class, avoidStart, maxDist, maxDist, minSpeed, maxSpeed);
        }

        public static AvoidRadiusGoal<LivingEntity> newGoalTwo(PathfinderMob pathfinderMob, float avoidStart, int maxDist, double minSpeed, double maxSpeed){
            return new AvoidRadiusGoal<>(pathfinderMob, LivingEntity.class, livingEntity -> true, avoidStart, maxDist, maxDist, minSpeed, maxSpeed, livingEntity -> pathfinderMob.getTarget() == livingEntity || MobUtil.isOwnedTargetable(pathfinderMob, livingEntity));
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.toAvoid != null && this.toAvoid.distanceTo(this.mob) < this.maxDist;
        }

        public void stop() {
            super.stop();
            this.pathNav.stop();
        }
    }
}