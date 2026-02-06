package com.Polarice3.Goety.common.network.client;

import com.Polarice3.Goety.common.items.equipment.BladeOfEnderItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CBoEStrikePacket {
    public static void encode(CBoEStrikePacket packet, FriendlyByteBuf buffer) {
    }

    public static CBoEStrikePacket decode(FriendlyByteBuf buffer) {
        return new CBoEStrikePacket();
    }

    public static void consume(CBoEStrikePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                BladeOfEnderItem.strike(playerEntity.level, playerEntity);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
