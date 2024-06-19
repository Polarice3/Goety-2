package com.Polarice3.Goety.common.entities.ally.undead.skeleton;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.neutral.AbstractCairnNecromancer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class CairnNecromancerServant extends AbstractCairnNecromancer {
    public CairnNecromancerServant(EntityType<? extends AbstractCairnNecromancer> type, Level level) {
        super(type, level);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(8, new WanderGoal(this, 1.0D, 10));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isHostile()) {
            if (this.tickCount % 20 == 0) {
                this.convertTo(ModEntityType.CAIRN_NECROMANCER.get(), false);
            }
        }
    }
}
