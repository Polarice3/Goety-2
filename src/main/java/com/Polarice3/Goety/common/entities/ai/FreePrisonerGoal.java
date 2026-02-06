package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.common.entities.ally.illager.raider.Prisoner;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class FreePrisonerGoal extends Goal {
    private final Mob liberator;
    private Prisoner prisoner;
    private int freeTime;

    public FreePrisonerGoal(Mob mob) {
        this.liberator = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.liberator instanceof IronGolem golem) {
            if (golem.isPlayerCreated()) {
                return false;
            }
        }
        if (this.liberator.getTarget() == null) {
            double range = 16.0D;
            if (this.liberator.getAttribute(Attributes.FOLLOW_RANGE) != null) {
                range = this.liberator.getAttributeValue(Attributes.FOLLOW_RANGE);
            }
            AABB aabb = this.liberator.getBoundingBox().inflate(range, range / 2.0D, range);
            List<Prisoner> list = this.liberator.level.getEntitiesOfClass(Prisoner.class, aabb, this.liberator::hasLineOfSight);
            if (!list.isEmpty()) {
                list.sort(Comparator.comparingDouble(this.liberator::distanceTo));
                if (list.stream().findFirst().isPresent()) {
                    this.prisoner = list.stream().findFirst().get();
                }
            }
        }
        return this.prisoner != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.liberator.getTarget() == null && this.prisoner != null && this.prisoner.isAlive();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void stop() {
        this.liberator.getNavigation().stop();
        this.prisoner = null;
        this.freeTime = 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.prisoner != null) {
            if (this.liberator.getBoundingBox().inflate(0.5D).intersects(this.prisoner.getBoundingBox())) {
                this.liberator.getNavigation().stop();
                this.liberator.getMoveControl().strafe(0.0F, 0.0F);
                this.liberator.getLookControl().setLookAt(this.prisoner);
                this.prisoner.getNavigation().stop();
                this.prisoner.getMoveControl().strafe(0.0F, 0.0F);
                this.prisoner.getLookControl().setLookAt(this.liberator);
                ++this.freeTime;
                if (this.freeTime % 4 == 0 && !(this.liberator instanceof IronGolem)) {
                    this.liberator.swing(InteractionHand.MAIN_HAND);
                    SoundType soundType = SoundType.CHAIN;
                    this.liberator.playSound(soundType.getHitSound(), soundType.getVolume(), soundType.getPitch());
                }
                if (this.freeTime >= 80 || this.liberator instanceof IronGolem) {
                    this.prisoner.unshackle(null);
                }
            } else {
                if (this.freeTime > 0) {
                    --this.freeTime;
                }
                this.liberator.getNavigation().moveTo(this.prisoner, 0.75F);
            }
        }
    }
}
