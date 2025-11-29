package com.Polarice3.Goety.common.entities.ally.undead.skeleton;

import com.Polarice3.Goety.common.entities.neutral.AbstractMossyNecromancer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class MossyNecromancerServant extends AbstractMossyNecromancer {
    public MossyNecromancerServant(EntityType<? extends AbstractMossyNecromancer> type, Level level) {
        super(type, level);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(8, new WanderGoal<>(this, 1.0D, 10));
    }
}
