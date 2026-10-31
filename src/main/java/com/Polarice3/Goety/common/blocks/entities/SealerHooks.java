package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.common.blocks.ApparitionDoorBlock;
import com.Polarice3.Goety.common.blocks.CryptChestBlock;
import com.Polarice3.Goety.mixin.BaseContainerBlockEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.LockCode;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//Backported from Goety 3, made by Vivideru: https://github.com/Vivideru/Goety-3/blob/main/src/main/java/com/Vivideru/Goety/common/blocks/entities/SealerHooks.java
public final class SealerHooks {
    public static final LockCode SEAL = new LockCode("goety:sealer");
    public static final int MAX_CONNECTED_DOORS = 256;
    private static final Map<ResourceKey<Level>, Map<BlockPos, Set<BlockPos>>> SEALED = new ConcurrentHashMap<>();

    private SealerHooks() {
    }

    public static boolean isSealable(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof ApparitionDoorBlock || state.hasProperty(CryptChestBlock.LOCKED) && state.getBlock() instanceof CryptChestBlock) {
            return true;
        }
        return level.getBlockEntity(pos) instanceof BaseContainerBlockEntity;
    }

    public static Set<BlockPos> connectedDoors(BlockGetter level, BlockPos start) {
        Set<BlockPos> doors = new LinkedHashSet<>();
        if (!(level.getBlockState(start).getBlock() instanceof ApparitionDoorBlock)) {
            return doors;
        }
        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(start.immutable());
        doors.add(start.immutable());
        while (!queue.isEmpty() && doors.size() < MAX_CONNECTED_DOORS) {
            BlockPos pos = queue.poll();
            for (Direction direction : Direction.values()) {
                BlockPos next = pos.relative(direction);
                if (!doors.contains(next) && level.getBlockState(next).getBlock() instanceof ApparitionDoorBlock) {
                    doors.add(next);
                    queue.add(next);
                }
            }
        }
        return doors;
    }

    public static void seal(Level level, BlockPos target, BlockPos sealer) {
        BlockState state = level.getBlockState(target);
        if (state.getBlock() instanceof ApparitionDoorBlock) {
            for (BlockPos door : connectedDoors(level, target)) {
                hold(level, door, sealer);
            }
            return;
        }
        hold(level, target, sealer);
        if (state.getBlock() instanceof CryptChestBlock && state.hasProperty(CryptChestBlock.LOCKED)) {
            if (!state.getValue(CryptChestBlock.LOCKED)) {
                level.setBlock(target, state.setValue(CryptChestBlock.LOCKED, true), 3);
            }
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(target);
        if (blockEntity instanceof BaseContainerBlockEntity container && !SEAL.equals(((BaseContainerBlockEntityAccessor) container).goety$getLockKey())) {
            ((BaseContainerBlockEntityAccessor) container).goety$setLockKey(SEAL);
            container.setChanged();
        }
    }

    public static void unseal(Level level, BlockPos target, BlockPos sealer) {
        if (!level.isLoaded(target)) {
            release(level, target, sealer);
            return;
        }
        BlockState state = level.getBlockState(target);
        if (state.getBlock() instanceof ApparitionDoorBlock) {
            for (BlockPos door : connectedDoors(level, target)) {
                release(level, door, sealer);
            }
            return;
        }
        if (!release(level, target, sealer)) {
            return;
        }
        if (state.getBlock() instanceof CryptChestBlock && state.hasProperty(CryptChestBlock.LOCKED)) {
            if (state.getValue(CryptChestBlock.LOCKED)) {
                level.setBlock(target, state.setValue(CryptChestBlock.LOCKED, false), 3);
            }
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(target);
        if (blockEntity instanceof BaseContainerBlockEntity container && SEAL.equals(((BaseContainerBlockEntityAccessor) container).goety$getLockKey())) {
            ((BaseContainerBlockEntityAccessor) container).goety$setLockKey(LockCode.NO_LOCK);
            container.setChanged();
        }
    }

    public static void clientMirror(Level level, BlockPos sealer, BlockPos target, boolean sealing) {
        if (target == null) {
            return;
        }
        Set<BlockPos> targets = level.isLoaded(target) && level.getBlockState(target).getBlock() instanceof ApparitionDoorBlock ? connectedDoors(level, target) : Set.of(target.immutable());
        for (BlockPos pos : targets) {
            if (sealing) {
                hold(level, pos, sealer);
            } else {
                release(level, pos, sealer);
            }
        }
    }

    private static void hold(Level level, BlockPos pos, BlockPos sealer) {
        SEALED.computeIfAbsent(level.dimension(), key -> new ConcurrentHashMap<>())
                .computeIfAbsent(pos.immutable(), key -> ConcurrentHashMap.newKeySet())
                .add(sealer.immutable());
    }

    private static boolean release(Level level, BlockPos pos, BlockPos sealer) {
        Map<BlockPos, Set<BlockPos>> sealed = SEALED.get(level.dimension());
        if (sealed == null) {
            return true;
        }
        Set<BlockPos> holders = sealed.get(pos);
        if (holders == null) {
            return true;
        }
        holders.remove(sealer);
        if (holders.isEmpty()) {
            sealed.remove(pos);
            return true;
        }
        return false;
    }

    public static boolean isSealed(LevelAccessor level, BlockPos pos) {
        if (!(level instanceof Level realLevel)) {
            return false;
        }
        Map<BlockPos, Set<BlockPos>> sealed = SEALED.get(realLevel.dimension());
        return sealed != null && sealed.containsKey(pos);
    }

    public static void clear(Level level) {
        SEALED.remove(level.dimension());
    }
}
