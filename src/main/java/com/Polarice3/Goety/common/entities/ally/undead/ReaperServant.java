package com.Polarice3.Goety.common.entities.ally.undead;

import com.Polarice3.Goety.common.entities.neutral.AbstractReaper;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class ReaperServant extends AbstractReaper {
    public ReaperServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }
}
