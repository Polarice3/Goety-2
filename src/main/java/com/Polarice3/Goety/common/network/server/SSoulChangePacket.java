package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.client.gui.overlay.SoulChangePopup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SSoulChangePacket {
    private final int amount;

    public SSoulChangePacket(int amount) {
        this.amount = amount;
    }

    public static void encode(SSoulChangePacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.amount);
    }

    public static SSoulChangePacket decode(FriendlyByteBuf buffer) {
        return new SSoulChangePacket(buffer.readVarInt());
    }

    public static void consume(SSoulChangePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                SoulChangePopup.push(packet.amount);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
