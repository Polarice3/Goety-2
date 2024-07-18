package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.common.network.EntityUpdatePacket;
import com.Polarice3.Goety.common.network.ModNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class EntityFinder {
    public static Optional<ServerPlayer> getPlayerByUuiDGlobal(UUID uuid) {
        for (ServerLevel world : ServerLifecycleHooks.getCurrentServer().getAllLevels()) {
            ServerPlayer player = (ServerPlayer) world.getPlayerByUUID(uuid);
            if (player != null)
                return Optional.of(player);
        }
        return Optional.empty();
    }

    public static Optional<? extends Entity> getEntityByUuiDGlobal(UUID uuid) {
        return getEntityByUuiDGlobal(ServerLifecycleHooks.getCurrentServer(), uuid);
    }

    public static Optional<? extends Entity> getEntityByUuiDGlobal(MinecraftServer server, UUID uuid) {
        if (uuid != null && server != null) {
            for (ServerLevel world : server.getAllLevels()) {
                Entity entity = world.getEntity(uuid);
                if (entity != null)
                    return Optional.of(entity);
            }
        }
        return Optional.empty();
    }

    public static Player getServerPlayersByUuiD(UUID uuid) {
        return getServerPlayersByUuiD(ServerLifecycleHooks.getCurrentServer(), uuid);
    }

    public static Player getServerPlayersByUuiD(MinecraftServer server, UUID uuid){
        if (uuid != null && server != null) {
            for (ServerLevel world : server.getAllLevels()) {
                return world.getPlayerByUUID(uuid);
            }
        }
        return null;
    }

    @Nullable
    public static Entity getEntityByUuiD(UUID uuid) {
        return getEntityByUuiD(ServerLifecycleHooks.getCurrentServer(), uuid);
    }

    @Nullable
    public static Entity getEntityByUuiD(MinecraftServer server, UUID uuid){
        if (uuid != null && server != null) {
            for (ServerLevel world : server.getAllLevels()) {
                return world.getEntity(uuid);
            }
        }
        return null;
    }

    public static LivingEntity getLivingEntityByUuiD(UUID uuid) {
        return getLivingEntityByUuiD(ServerLifecycleHooks.getCurrentServer(), uuid);
    }

    public static LivingEntity getLivingEntityByUuiD(MinecraftServer server, UUID uuid){
        if (uuid != null && server != null) {
            for (ServerLevel world : server.getAllLevels()) {
                Entity entity = world.getEntity(uuid);
                if (entity instanceof LivingEntity){
                    return (LivingEntity) entity;
                }
            }
        }
        return null;
    }

    public static Player getNearbyPlayer(Level world, BlockPos blockPos){
        for (Player player : world.getEntitiesOfClass(Player.class, new AABB(blockPos).inflate(256))){
            return player;
        }
        return null;
    }

    public static void sendEntityUpdatePacket(Player player, LivingEntity livingEntity) {
        ModNetwork.sendTo(player, new EntityUpdatePacket(livingEntity.getUUID(), livingEntity.getPersistentData()));
    }

    public static void sendEntityUpdatePacket(LivingEntity livingEntity) {
        ModNetwork.sendToALL(new EntityUpdatePacket(livingEntity.getUUID(), livingEntity.getPersistentData()));
    }
}
