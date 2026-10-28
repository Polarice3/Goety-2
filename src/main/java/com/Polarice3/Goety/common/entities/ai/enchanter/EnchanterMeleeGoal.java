package com.Polarice3.Goety.common.entities.ai.enchanter;

import com.Polarice3.Goety.api.entities.IEnchanter;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class EnchanterMeleeGoal<T extends PathfinderMob & IEnchanter> extends EnchanterGoal<T> {
    public int meleeTick;

    public EnchanterMeleeGoal(T enchanter) {
        super(enchanter);
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.enchanter.getTarget();
        if (target == null) {
            return false;
        } else if (!target.isAlive()) {
            return false;
        } else {
            return this.enchanter.distanceTo(target) <= 2.2F;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return (super.canContinueToUse() || this.meleeTick > 0) && this.meleeTick < 23;
    }

    @Override
    public void start() {
        super.start();
        this.meleeTick = 0;
    }

    @Override
    public void stop() {
        super.stop();
        this.meleeTick = 0;
        this.enchanter.setAnimationState(IEnchanter.IDLE);
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity target = this.enchanter.getTarget();

        boolean flag = this.meleeTick > 0;
        if (target != null && target.isAlive()) {
            flag |= this.enchanter.isWithinMeleeAttackRange(target);
            if (!this.enchanter.isWithinMeleeAttackRange(target)) {
                this.enchanter.getNavigation().moveTo(target, 1.0D);
            }
        }
        if (flag) {
            if (target != null && !target.isDeadOrDying()) {
                MobUtil.instaLook(this.enchanter, target);
            }
            if (!this.enchanter.isCurrentAnimation(IEnchanter.ATTACK)) {
                this.enchanter.setAnimationState(IEnchanter.ATTACK);
            }
            ++this.meleeTick;
            if (this.meleeTick == 11) {
                this.enchanter.playSound(ModSounds.ENCHANTER_ATTACK.get());
                this.enchanter.playSound(ModSounds.BOOK_CLOSE.get());
                if (target != null && !target.isDeadOrDying() && (this.enchanter.isWithinMeleeAttackRange(target) || this.enchanter.getBoundingBox().inflate(1.0D).intersects(target.getBoundingBox()))) {
                    this.enchanter.doHurtTarget(target);
                }
            }
            this.enchanter.getNavigation().stop();
            this.enchanter.getMoveControl().strafe(0, 0);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
