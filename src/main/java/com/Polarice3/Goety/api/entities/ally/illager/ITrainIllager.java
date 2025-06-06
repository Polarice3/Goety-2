package com.Polarice3.Goety.api.entities.ally.illager;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

public interface ITrainIllager {

    default boolean canSpawn(Level level, BlockPos blockPos, int range){
        return false;
    }

    default boolean mobCanTrainTo(Mob mob, Level level, BlockPos blockPos, int range) {
        return true;
    }

    EntityType<? extends Mob> getIllager(Level level, BlockPos blockPos, int range);
}
