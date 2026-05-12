package com.Polarice3.Goety.api.entities;

import com.Polarice3.Goety.utils.ModTicketTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ChunkPos;

public interface IChunkLoader {

    default long getTicketTime() {
        return 0;
    }

    default void setTicketTime(long time) {

    }

    default long decreaseTicketTime() {
        long ticket = this.getTicketTime() - 1L;
        this.setTicketTime(ticket);
        return ticket;
    }

    default boolean shouldChunkLoad(){
        return false;
    }

    default void chunkLoad() {
        if (this instanceof Mob mob){
            if (mob.level instanceof ServerLevel serverLevel) {
                if (this.shouldChunkLoad()) {
                    int i = SectionPos.blockToSectionCoord(mob.position().x());
                    int j = SectionPos.blockToSectionCoord(mob.position().z());
                    BlockPos blockPos = BlockPos.containing(mob.position());
                    if (this.decreaseTicketTime() <= 0L || i != SectionPos.blockToSectionCoord(blockPos.getX()) || j != SectionPos.blockToSectionCoord(blockPos.getZ())) {
                        serverLevel.getChunkSource().addRegionTicket(ModTicketTypes.SERVANT, mob.chunkPosition(), 5, mob.blockPosition());
                        serverLevel.resetEmptyTime();
                        this.setTicketTime(ModTicketTypes.SERVANT.timeout() - 1L);
                    }
                } else if (this.getTicketTime() > 0) {
                    this.setTicketTime(0);
                }
            }
        }
    }

    default void forceChunkLoadSelf() {
        if (this instanceof Mob mob){
            if (mob.level instanceof ServerLevel serverLevel) {
                serverLevel.getChunkSource().addRegionTicket(ModTicketTypes.SERVANT, mob.chunkPosition(), 5, mob.blockPosition());
                serverLevel.resetEmptyTime();
            }
        }
    }

    default void chunkLoadTarget(BlockPos blockPos) {
        if (blockPos != null) {
            if (this instanceof Mob mob) {
                if (mob.level instanceof ServerLevel serverLevel) {
                    if (this.shouldChunkLoad()) {
                        int i = SectionPos.blockToSectionCoord(blockPos.getX());
                        int j = SectionPos.blockToSectionCoord(blockPos.getZ());
                        if (this.getTicketTime() <= 0 || i != SectionPos.blockToSectionCoord(blockPos.getX()) || j != SectionPos.blockToSectionCoord(blockPos.getZ())) {
                            serverLevel.getChunkSource().addRegionTicket(ModTicketTypes.SERVANT, new ChunkPos(blockPos), 9, blockPos);
                            serverLevel.resetEmptyTime();
                        }
                    }
                }
            }
        }
    }
}
