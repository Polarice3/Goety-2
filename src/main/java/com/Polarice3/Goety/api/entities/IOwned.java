package com.Polarice3.Goety.api.entities;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.UUID;

public interface IOwned {
    LivingEntity getTrueOwner();

    UUID getOwnerId();

    void setOwnerId(@Nullable UUID p_184754_1_);

    void setTrueOwner(LivingEntity livingEntity);

    void setHostile(boolean hostile);

    boolean isHostile();

    default LivingEntity getMasterOwner(){
        if (this.getTrueOwner() instanceof IOwned owned){
            return owned.getTrueOwner();
        } else {
            return null;
        }
    }

    default void convertNewEquipment(Entity entity) {
    }
}
