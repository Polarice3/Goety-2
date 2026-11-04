package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.common.blocks.entities.PlinthBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.PlinthHooks;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

//Backported from Goety 3, made by Vivideru: https://github.com/Vivideru/Goety-3/blob/main/src/main/java/com/Vivideru/Goety/common/entities/ai/PlinthReturnGoal.java
public class PlinthReturnGoal extends Goal {
    private static final double FINAL_APPROACH = 2.0D;
    private static final double FINAL_APPROACH_SPEED = 0.8D;
    private final Mob mob;
    private int idleTicks;
    private int repathTicks;

    public PlinthReturnGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.mob.getTarget() != null || PlinthHooks.stopResting(this.mob) || PlinthHooks.isResting(this.mob)) {
            this.idleTicks = 0;
            return false;
        }
        PlinthBlockEntity plinth = this.plinth();
        if (plinth == null) {
            return false;
        }
        return ++this.idleTicks >= PlinthHooks.RETURN_DELAY;
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.getTarget() == null && !PlinthHooks.isResting(this.mob) && !PlinthHooks.recentlyWoke(this.mob) && this.plinth() != null;
    }

    @Override
    public void start() {
        this.repathTicks = 0;
        this.moveToPlinth();
    }

    @Override
    public void stop() {
        this.idleTicks = 0;
        this.mob.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        PlinthBlockEntity plinth = this.plinth();
        if (plinth == null || this.mob.getTarget() != null || PlinthHooks.isResting(this.mob) || PlinthHooks.recentlyWoke(this.mob)) {
            return;
        }
        if (PlinthHooks.isOverCenter(this.mob, plinth)) {
            if (!PlinthHooks.rest(this.mob, plinth)) {
                this.idleTicks = -200;
            }
            return;
        }
        Vec3 center = plinth.restPosition();
        double dx = center.x - this.mob.getX();
        double dz = center.z - this.mob.getZ();
        double approach = Math.max(FINAL_APPROACH, this.mob.getBbWidth() * 0.5D + 1.0D);
        if (dx * dx + dz * dz <= approach * approach) {
            this.mob.getNavigation().stop();
            this.mob.getMoveControl().setWantedPosition(center.x, center.y, center.z, FINAL_APPROACH_SPEED);
            return;
        }
        if (--this.repathTicks <= 0 || this.mob.getNavigation().isDone() && this.repathTicks < 30) {
            this.moveToPlinth();
        }
    }

    private void moveToPlinth() {
        PlinthBlockEntity plinth = this.plinth();
        if (plinth != null) {
            Vec3 center = plinth.restPosition();
            double shift = (int) (this.mob.getBbWidth() + 1.0F) * 0.5D - 0.5D;
            this.mob.getNavigation().moveTo(center.x - shift, center.y, center.z - shift, 1.0D);
            this.repathTicks = 40;
        }
    }

    private PlinthBlockEntity plinth() {
        GlobalPos globalPos = PlinthHooks.getPlinth(this.mob);
        if (globalPos == null || globalPos.dimension() != this.mob.level.dimension()) {
            return null;
        }
        PlinthBlockEntity plinth = PlinthHooks.plinthAt(this.mob.level, globalPos.pos());
        if (plinth == null && this.mob.level.isLoaded(globalPos.pos())) {
            PlinthHooks.clearPlinth(this.mob);
        }
        return plinth;
    }
}
