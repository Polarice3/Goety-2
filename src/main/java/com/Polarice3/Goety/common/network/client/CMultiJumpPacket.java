package com.Polarice3.Goety.common.network.client;

import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Based on DoubleJumpPacket from Aether-Redux codes: <a href="https://github.com/Zepalesque/The-Aether-Redux/blob/1.20.1/src/main/java/net/zepalesque/redux/network/packet/DoubleJumpPacket.java">...</a>
 */
public class CMultiJumpPacket {

    public static void encode(CMultiJumpPacket packet, FriendlyByteBuf buffer) {
    }

    public static CMultiJumpPacket decode(FriendlyByteBuf buffer) {
        return new CMultiJumpPacket();
    }

    public static void consume(CMultiJumpPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();
            if (playerEntity != null) {
                SEHelper.doubleJump(playerEntity);
            }

        });
        ctx.get().setPacketHandled(true);
    }
}
