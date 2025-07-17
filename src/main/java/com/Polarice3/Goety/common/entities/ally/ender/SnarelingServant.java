package com.Polarice3.Goety.common.entities.ally.ender;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.neutral.ender.AbstractSnareling;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class SnarelingServant extends AbstractSnareling {

    public SnarelingServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }
}
