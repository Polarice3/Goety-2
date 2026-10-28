package com.Polarice3.Goety.common.entities.ai.enchanter;

import com.Polarice3.Goety.api.entities.IEnchanter;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class EnchanterFleeToAllyGoal<T extends PathfinderMob & IEnchanter> extends EnchanterGoal<T>{
    @Nullable
    private LivingEntity fleeTo;
    private int timeToRecalcPath;
    private float oldWaterCost;

    public EnchanterFleeToAllyGoal(T enchanter) {
        super(enchanter);
    }

    @Override
    public boolean canUse() {
        if (this.enchanter.getTarget() == null) {
            return false;
        } else if (!this.getAllies().isEmpty()) {
            List<LivingEntity> list = this.getAllies();
            list.sort(Comparator.comparingDouble(this.enchanter::distanceTo));
            if (!list.isEmpty()) {
                Optional<LivingEntity> optional = list.stream().findFirst();
                optional.ifPresent(livingEntity -> this.fleeTo = livingEntity);
            }
            return this.fleeTo != null;
        }
        return false;
    }

    public boolean canContinueToUse() {
        return this.fleeTo != null && !this.enchanter.getNavigation().isDone() && this.enchanter.distanceTo(this.fleeTo) > 3.0F;
    }

    public List<LivingEntity> getAllies() {
        long now = this.enchanter.level.getGameTime();
        if (now < this.nextAllyScan) {
            this.cachedAllies = this.enchanter.level.getEntitiesOfClass(
                    LivingEntity.class,
                    this.enchanter.getBoundingBox().inflate(13),
                    ally -> !(ally instanceof IEnchanter)
                            && !ally.isDeadOrDying()
                            && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(ally)
                            && ally.getAttribute(Attributes.ATTACK_DAMAGE) != null
                            && this.isAlliedTarget(ally));
        }
        this.nextAllyScan = now + 20L;
        return this.cachedAllies;
    }

    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.enchanter.getPathfindingMalus(BlockPathTypes.WATER);
        this.enchanter.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    public void stop() {
        this.fleeTo = null;
        this.enchanter.getNavigation().stop();
        this.enchanter.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
    }

    public void tick() {
        if (this.fleeTo != null && !this.enchanter.isLeashed()) {
            this.enchanter.getLookControl().setLookAt(this.fleeTo, 10.0F, (float)this.enchanter.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = this.adjustedTickDelay(10);
                double d0 = this.enchanter.getX() - this.fleeTo.getX();
                double d1 = this.enchanter.getY() - this.fleeTo.getY();
                double d2 = this.enchanter.getZ() - this.fleeTo.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if (!(d3 <= Mth.square(3.0D))) {
                    this.enchanter.getNavigation().moveTo(this.fleeTo, 1.0F);
                } else {
                    this.enchanter.getNavigation().stop();
                    boolean flag = false;
                    if (this.fleeTo instanceof Mob mob) {
                        LookControl lookcontrol = mob.getLookControl();
                        flag = lookcontrol.getWantedX() == this.enchanter.getX() && lookcontrol.getWantedY() == this.enchanter.getY() && lookcontrol.getWantedZ() == this.enchanter.getZ();
                    }
                    if (d3 <= 3.0D || flag) {
                        double d4 = this.fleeTo.getX() - this.enchanter.getX();
                        double d5 = this.fleeTo.getZ() - this.enchanter.getZ();
                        this.enchanter.getNavigation().moveTo(this.enchanter.getX() - d4, this.enchanter.getY(), this.enchanter.getZ() - d5, 1.0F);
                    }

                }
            }
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
