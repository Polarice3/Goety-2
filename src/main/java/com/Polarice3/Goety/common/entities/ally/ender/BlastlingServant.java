package com.Polarice3.Goety.common.entities.ally.ender;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.neutral.ender.AbstractBlastling;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class BlastlingServant extends AbstractBlastling {

    public BlastlingServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }
}
