package com.Polarice3.Goety.api.blocks.entities;

import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.UUID;

public interface IOwnedBlock {

    default boolean screenView(){
        return true;
    }

    @Nullable
    default UUID getOwnerUUID() {
        return null;
    }

    default void setOwnerUUID(@Nullable UUID p_184754_1_) {
    }

    default int getOwnerId() {
        return -1;
    }

    default void setOwnerId(int p_184754_1_) {
    }

    Player getPlayer();

    default CompoundTag getAttitudeLists() {
        return new CompoundTag();
    }

    default boolean isGrudgedTowards(LivingEntity target) {
        if (this.getPlayer() != null) {
            return SEHelper.isGrudged(this.getPlayer(), target);
        } else if (this.getOwnerUUID() != null && target.level instanceof ServerLevel serverLevel) {
            return SEHelper.isSavedGrudge(serverLevel, this.getOwnerUUID(), target);
        }
        return false;
    }

    default boolean isAllyWith(LivingEntity target) {
        if (this.getPlayer() != null) {
            return SEHelper.isAlly(this.getPlayer(), target);
        } else if (this.getOwnerUUID() != null && target.level instanceof ServerLevel serverLevel) {
            return SEHelper.isSavedAlly(serverLevel, this.getOwnerUUID(), target);
        }
        return false;
    }
}
