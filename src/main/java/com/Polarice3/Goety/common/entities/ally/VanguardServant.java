package com.Polarice3.Goety.common.entities.ally;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class VanguardServant extends AbstractSkeletonServant {
    public VanguardServant(EntityType<? extends Summoned> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected SoundEvent getStepSound() {
        return null;
    }
}
