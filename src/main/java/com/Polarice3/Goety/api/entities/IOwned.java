package com.Polarice3.Goety.api.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.UUID;

public interface IOwned {
    LivingEntity getTrueOwner();

    UUID getOwnerId();

    void setOwnerId(@Nullable UUID p_184754_1_);

    void setTrueOwner(LivingEntity livingEntity);

    void setHostile(boolean hostile);

    boolean isHostile();

    @Nullable
    default EntityType<?> getVariant(Level level, BlockPos blockPos){
        return null;
    };

    default LivingEntity getMasterOwner(){
        if (this.getTrueOwner() instanceof IOwned owned){
            return owned.getTrueOwner();
        } else {
            return this.getTrueOwner();
        }
    }

    default void convertNewEquipment(Entity entity) {
    }

    default boolean isFamiliar(){
        return false;
    }
}
