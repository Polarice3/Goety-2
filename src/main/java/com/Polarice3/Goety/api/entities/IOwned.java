package com.Polarice3.Goety.api.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.UUID;

public interface IOwned {

    UUID SPEED_MODIFIER_UUID = UUID.fromString("9c47949c-b896-4802-8e8a-f08c50791a8a");

    AttributeModifier SPEED_MODIFIER = new AttributeModifier(SPEED_MODIFIER_UUID, "Staying speed penalty", -1.0D, AttributeModifier.Operation.ADDITION);

    LivingEntity getTrueOwner();

    UUID getOwnerId();

    void setOwnerId(@Nullable UUID p_184754_1_);

    default void setOwnerClientId(int id) {
    }

    void setTrueOwner(@Nullable LivingEntity livingEntity);

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

    default void setLimitedLife(int limitedLifeTicksIn) {
        this.setHasLifespan(true);
        this.setLifespan(limitedLifeTicksIn);
    }

    default void setHasLifespan(boolean lifespan){
    }

    default boolean hasLifespan(){
        return false;
    }

    default void setLifespan(int lifespan){
    }

    default int getLifespan(){
        return 0;
    }

    default boolean isFamiliar(){
        return false;
    }
}
