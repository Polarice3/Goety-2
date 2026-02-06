package com.Polarice3.Goety.common.network.client;

import com.Polarice3.Goety.common.entities.ally.illager.raider.AllyTrampler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CTramplerPacket {
    public int mob;
    public int mode;

    public CTramplerPacket(int id, int mode) {
        this.mob = id;
        this.mode = mode;
    }

    public static void encode(CTramplerPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.mob);
        buffer.writeInt(packet.mode);
    }

    public static CTramplerPacket decode(FriendlyByteBuf buffer) {
        return new CTramplerPacket(buffer.readInt(), buffer.readInt());
    }

    public static void consume(CTramplerPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                Entity entity = playerEntity.level.getEntity(packet.mob);
                if (entity instanceof AllyTrampler trampler) {
                    if (packet.mode == 0) {
                        ++trampler.running;
                    } else if (packet.mode == 1){
                        trampler.setDashing(true);
                    } else if (packet.mode == 2){
                        trampler.setDashing(false);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
