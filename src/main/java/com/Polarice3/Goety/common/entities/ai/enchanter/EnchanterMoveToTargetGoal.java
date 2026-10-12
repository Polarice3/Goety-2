package com.Polarice3.Goety.common.entities.ai.enchanter;

import com.Polarice3.Goety.api.entities.IEnchanter;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class EnchanterMoveToTargetGoal<T extends PathfinderMob & IEnchanter> extends Goal {
    public T enchanter;
    @Nullable
    private LivingEntity target;

    public EnchanterMoveToTargetGoal(T enchanter) {
        this.enchanter = enchanter;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity livingentity = this.enchanter.getTarget();
        if (livingentity != null && livingentity.isAlive()) {
            this.target = livingentity;
            return this.target.distanceTo(this.enchanter) > 6.0D;
        } else {
            return false;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.target != null && this.target.isAlive() && this.target.distanceTo(this.enchanter) > 6.0D;
    }

    @Override
    public void stop() {
        this.enchanter.getNavigation().stop();
    }

    public void tick() {
        if (this.target != null) {
            this.enchanter.getNavigation().moveTo(this.target, 1.0F);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
