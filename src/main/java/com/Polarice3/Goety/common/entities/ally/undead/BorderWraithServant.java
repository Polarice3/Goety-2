package com.Polarice3.Goety.common.entities.ally.undead;

import com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal;
import com.Polarice3.Goety.common.entities.neutral.AbstractBorderWraith;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class BorderWraithServant extends AbstractBorderWraith {

    public BorderWraithServant(EntityType<? extends AbstractBorderWraith> p_i48553_1_, Level p_i48553_2_) {
        super(p_i48553_1_, p_i48553_2_);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, AvoidTargetGoal.AvoidRadiusGoal.newGoal(this, 2, 4, 1.2D, 1.4D));
        this.goalSelector.addGoal(8, new WanderGoal(this, 1.0D, 10));
    }
}
