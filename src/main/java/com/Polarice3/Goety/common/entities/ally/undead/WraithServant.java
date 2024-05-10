package com.Polarice3.Goety.common.entities.ally.undead;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.AbstractWraith;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class WraithServant extends AbstractWraith {

    public WraithServant(EntityType<? extends Summoned> p_i48553_1_, Level p_i48553_2_) {
        super(p_i48553_1_, p_i48553_2_);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(8, new WanderGoal(this, 1.0D, 10));
    }
}
