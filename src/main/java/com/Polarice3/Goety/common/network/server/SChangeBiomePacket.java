package com.Polarice3.Goety.common.network.server;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SChangeBiomePacket {
    private final BlockPos pos;
    private final ResourceKey<Biome> biomeId;

    public SChangeBiomePacket(BlockPos pos, ResourceKey<Biome> id) {
        this.pos = pos;
        this.biomeId = id;
    }

    public SChangeBiomePacket(FriendlyByteBuf buf) {
        this.pos = new BlockPos(buf.readInt(), 0, buf.readInt());
        this.biomeId = ResourceKey.create(Registry.BIOME_REGISTRY, buf.readResourceLocation());
    }

    public static void encode(SChangeBiomePacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.pos.getX());
        buf.writeInt(packet.pos.getZ());
        buf.writeResourceLocation(packet.biomeId.location());
    }

    public static SChangeBiomePacket decode(FriendlyByteBuf buffer) {
        return new SChangeBiomePacket(buffer);
    }

    public static void consume(SChangeBiomePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                LevelChunk chunkAt = (LevelChunk) level.getChunk(packet.pos);

                Holder<Biome> biome = level.registryAccess().ownedRegistryOrThrow(Registry.BIOME_REGISTRY).getHolderOrThrow(packet.biomeId);

                int minY = QuartPos.fromBlock(level.getMinBuildHeight());
                int maxY = minY + QuartPos.fromBlock(level.getHeight()) - 1;

                int x = QuartPos.fromBlock(packet.pos.getX());
                int z = QuartPos.fromBlock(packet.pos.getZ());

                for (LevelChunkSection section : chunkAt.getSections()) {
                    for (int sy = 0; sy < 16; sy += 4) {
                        int y = Mth.clamp(QuartPos.fromBlock(section.bottomBlockY() + sy), minY, maxY);
                        if (section.getBiomes() instanceof PalettedContainer<Holder<Biome>> container)
                            container.set(x & 3, y & 3, z & 3, biome);
                        SectionPos pos = SectionPos.of(packet.pos.getX() >> 4, (section.bottomBlockY() >> 4) + sy, packet.pos.getZ() >> 4);
                        level.setSectionDirtyWithNeighbors(pos.x(), pos.y(), pos.z());
                    }
                }
                level.onChunkLoaded(new ChunkPos(packet.pos));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
