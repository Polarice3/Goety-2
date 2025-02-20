package com.Polarice3.Goety.common.entities.hostile;

import com.Polarice3.Goety.common.entities.neutral.AbstractReaper;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;

public class Reaper extends AbstractReaper implements Enemy {
    public Reaper(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setHostile(true);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }
}
