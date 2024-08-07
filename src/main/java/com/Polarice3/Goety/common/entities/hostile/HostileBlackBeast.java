package com.Polarice3.Goety.common.entities.hostile;

import com.Polarice3.Goety.common.entities.ally.BlackBeast;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;

public class HostileBlackBeast extends BlackBeast implements Enemy {

    public HostileBlackBeast(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setHostile(true);
    }
}
