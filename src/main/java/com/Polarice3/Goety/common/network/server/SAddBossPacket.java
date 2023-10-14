package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.common.entities.hostile.IBoss;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SAddBossPacket {
    private final ClientboundAddEntityPacket entityPacket;
    private final UUID bossID;

    public SAddBossPacket(ClientboundAddEntityPacket entityPacket, UUID bossID){
        this.entityPacket = entityPacket;
        this.bossID = bossID;
    }

    public static void encode(SAddBossPacket packet, FriendlyByteBuf buffer) {
        packet.entityPacket.write(buffer);
        buffer.writeUUID(packet.bossID);
    }

    public static SAddBossPacket decode(FriendlyByteBuf buffer) {
        return new SAddBossPacket(new ClientboundAddEntityPacket(buffer), buffer.readUUID());
    }

    public static void consume(SAddBossPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft.getInstance().execute(() -> {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player != null) {
                    player.connection.handleAddEntity(packet.entityPacket);
                    Entity entity = player.level.getEntity(packet.entityPacket.getId());
                    if (entity instanceof IBoss boss) {
                        boss.setBossInfoUUID(packet.bossID);
                    }
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
