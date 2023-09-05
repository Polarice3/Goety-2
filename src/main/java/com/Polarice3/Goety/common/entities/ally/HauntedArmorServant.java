package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.common.entities.neutral.AbstractHauntedArmor;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class HauntedArmorServant extends AbstractHauntedArmor {
    public HauntedArmorServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }
}
