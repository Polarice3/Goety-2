package com.Polarice3.Goety.common.world.data;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;

public class ChunkLoadData extends SavedData {
    private static final String DATA_NAME = "goety_chunk_load_data";
    private final Map<BlockPos, Integer> ticketPositions = new HashMap<>();

    public static ChunkLoadData create() {
        return new ChunkLoadData();
    }

    public static ChunkLoadData load(CompoundTag tag) {
        ChunkLoadData data = create();
        ListTag list = tag.getList("Positions", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            BlockPos pos = NbtUtils.readBlockPos(entry);
            int radius = entry.getInt("Radius");
            data.ticketPositions.put(pos, radius);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        this.ticketPositions.forEach((pos, radius) -> {
            CompoundTag entry = NbtUtils.writeBlockPos(pos);
            entry.putInt("Radius", radius);
            list.add(entry);
        });
        tag.put("Positions", list);
        return tag;
    }

    public void addPosition(BlockPos pos, int radius) {
        this.ticketPositions.put(pos.immutable(), radius);
        this.setDirty();
    }

    public void removePosition(BlockPos pos) {
        this.ticketPositions.remove(pos);
        this.setDirty();
    }

    public Map<BlockPos, Integer> getPositions() {
        return this.ticketPositions;
    }

    public boolean hasPosition(BlockPos pos) {
        return this.ticketPositions.containsKey(pos);
    }

    public static ChunkLoadData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                ChunkLoadData::load,
                ChunkLoadData::create,
                DATA_NAME
        );
    }
}
