package com.Polarice3.Goety.common.entities.ally.undead.skeleton;

import com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
import com.Polarice3.Goety.common.entities.neutral.AbstractWitherNecromancer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class WitherNecromancerServant extends AbstractWitherNecromancer {

    public WitherNecromancerServant(EntityType<? extends AbstractNecromancer> type, Level level) {
        super(type, level);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(5, new WanderGoal<>(this, 1.0D));
    }
}
