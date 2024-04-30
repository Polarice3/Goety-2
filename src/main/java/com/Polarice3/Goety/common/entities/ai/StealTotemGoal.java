package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.api.items.magic.ITotem;
import com.Polarice3.Goety.common.entities.hostile.illagers.HuntingIllagerEntity;
import com.Polarice3.Goety.config.MobsConfig;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class StealTotemGoal<T extends HuntingIllagerEntity> extends Goal {
    private static final Predicate<ItemEntity> ALLOWED_ITEMS = (itemEntity) -> !itemEntity.hasPickUpDelay()
            && itemEntity.isAlive()
            && (itemEntity.getItem().getItem() instanceof ITotem || itemEntity.getItem().getItem() == Items.TOTEM_OF_UNDYING);
    private final T mob;

    public StealTotemGoal(T p_i50572_2_) {
        this.mob = p_i50572_2_;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public boolean canUse() {
        List<ItemEntity> list = this.mob.level.getEntitiesOfClass(ItemEntity.class, this.mob.getBoundingBox().inflate(16.0D, 8.0D, 16.0D), ALLOWED_ITEMS);
        if (!list.isEmpty() && this.mob.inventory.canAddItem(list.get(0).getItem()) && MobsConfig.IllagerSteal.get()) {
            return this.mob.getNavigation().moveTo(list.get(0), 1.15F);
        } else {
            return false;
        }
    }

    public void tick() {
        if (this.mob.getNavigation().getTargetPos().closerToCenterThan(this.mob.position(), 1.414D)) {
            List<ItemEntity> list = this.mob.level.getEntitiesOfClass(ItemEntity.class, this.mob.getBoundingBox().inflate(4.0D, 4.0D, 4.0D), ALLOWED_ITEMS);
            if (!list.isEmpty()) {
                this.mob.pickUpItem(list.get(0));
            }
        }

    }
}
