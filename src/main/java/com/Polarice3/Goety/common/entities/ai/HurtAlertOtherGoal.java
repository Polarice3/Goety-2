package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class HurtAlertOtherGoal extends TargetGoal {
    public static final TargetingConditions HURT_BY_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
    public static final int ALERT_RANGE_Y = 10;
    public int timestamp;
    public Predicate<Mob> mobPredicate;

    public HurtAlertOtherGoal(PathfinderMob p_26039_, Predicate<Mob> predicate) {
        super(p_26039_, true);
        this.mobPredicate = predicate;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    public boolean canUse() {
        int i = this.mob.getLastHurtByMobTimestamp();
        LivingEntity livingentity = this.mob.getLastHurtByMob();
        if (i != this.timestamp && livingentity != null) {
            if (livingentity.getType() == EntityType.PLAYER && this.mob.level().getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                return false;
            } else {
                return this.canAttack(livingentity, HURT_BY_TARGETING);
            }
        } else {
            return false;
        }
    }

    public void start() {
        this.mob.setTarget(this.mob.getLastHurtByMob());
        this.targetMob = this.mob.getTarget();
        this.timestamp = this.mob.getLastHurtByMobTimestamp();
        this.unseenMemoryTicks = 300;
        this.alertOthers();

        super.start();
    }

    protected void alertOthers() {
        double d0 = this.getFollowDistance();
        AABB aabb = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(d0, ALERT_RANGE_Y, d0);
        List<? extends Mob> list = this.mob.level().getEntitiesOfClass(Mob.class, aabb, this.mobPredicate);

        for (Mob mob1 : list) {
            if (this.mob != mob1 && mob1.getTarget() == null && this.mob.getLastHurtByMob() != null && !MobUtil.areAllies(mob1, this.mob.getLastHurtByMob())) {
                this.alertOther(mob1, this.mob.getLastHurtByMob());
            }
        }
    }

    protected void alertOther(Mob p_26042_, LivingEntity p_26043_) {
        p_26042_.setTarget(p_26043_);
    }
}
