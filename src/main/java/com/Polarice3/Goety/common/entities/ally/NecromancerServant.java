package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class NecromancerServant extends AbstractNecromancer {
    public NecromancerServant(EntityType<? extends AbstractNecromancer> type, Level level) {
        super(type, level);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(8, new WanderGoal(this, 1.0D, 10));
    }
}
