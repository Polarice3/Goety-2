package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.List;

public class DefendVillagerGoal extends TargetGoal {
    private final Mob protector;
    private LivingEntity pendingTarget;

    public DefendVillagerGoal(Mob guardIn) {
        super(guardIn, true, true);
        this.protector = guardIn;
        this.setFlags(EnumSet.of(Flag.TARGET, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        AABB aabb = this.protector.getBoundingBox().inflate(10.0D, 8.0D, 10.0D);
        List<Mob> list = this.protector.level.getEntitiesOfClass(Mob.class, aabb);
        for (Mob mob1 : list) {
            if (mob1 instanceof Villager villager) {
                if (villager.getLastHurtByMob() instanceof IOwned) {
                    this.pendingTarget = villager.getLastHurtByMob();
                }
            } else if (mob1 instanceof IOwned) {
                if (mob1.getTarget() instanceof Villager) {
                    this.pendingTarget = mob1;
                }
            }
        }
        if (this.protector instanceof IronGolem golem) {
            if (golem.isPlayerCreated()) {
                if (MobUtil.getOwner(this.pendingTarget) instanceof Player || this.pendingTarget instanceof Player) {
                    return false;
                }
            }
        }
        return this.pendingTarget != null && !MobUtil.areAllies(this.pendingTarget, this.protector) && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(this.pendingTarget);
    }

    @Override
    public void start() {
        this.protector.setTarget(this.pendingTarget);
        super.start();
    }
}
