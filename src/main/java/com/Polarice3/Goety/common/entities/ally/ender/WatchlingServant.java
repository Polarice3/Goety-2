package com.Polarice3.Goety.common.entities.ally.ender;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.neutral.ender.AbstractWatchling;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class WatchlingServant extends AbstractWatchling {

    public WatchlingServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }
}
