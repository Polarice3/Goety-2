package com.Polarice3.Goety.api.entities.ally;

import com.Polarice3.Goety.api.entities.IOwned;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public interface IServant extends IOwned {
    boolean isWandering();

    void setWandering(boolean wandering);

    boolean isStaying();

    void setStaying(boolean staying);

    default boolean isFollowing(){
        return !this.isWandering() && !this.isStaying();
    }

    boolean canUpdateMove();

    void updateMoveMode(Player player);

    void setCommandPos(BlockPos blockPos);

    void setCommandPos(BlockPos blockPos, boolean removeEntity);

    void setCommandPosEntity(LivingEntity living);
}
