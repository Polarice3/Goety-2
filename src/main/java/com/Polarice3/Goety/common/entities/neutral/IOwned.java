package com.Polarice3.Goety.common.entities.neutral;

import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.UUID;

public interface IOwned {
    LivingEntity getTrueOwner();
    UUID getOwnerId();
    void setOwnerId(@Nullable UUID p_184754_1_);
    void setTrueOwner(LivingEntity livingEntity);
}
