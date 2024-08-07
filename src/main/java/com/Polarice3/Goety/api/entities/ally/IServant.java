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

    default boolean isPatrolling(){
        return this.getBoundPos() != null;
    }

    default BlockPos getBoundPos(){
        return null;
    }

    default void setBoundPos(BlockPos blockPos){
    }

    default boolean isFollowing(){
        return !this.isWandering() && !this.isStaying() && !this.isPatrolling();
    }

    boolean canUpdateMove();

    void updateMoveMode(Player player);

    boolean isCommanded();

    void setCommandPos(BlockPos blockPos);

    void setCommandPos(BlockPos blockPos, boolean removeEntity);

    void setCommandPosEntity(LivingEntity living);

    void tryKill(Player player);
}
