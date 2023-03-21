package com.Polarice3.Goety.common.network.client;

import com.Polarice3.Goety.common.items.equipment.DeathScytheItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CScytheStrikePacket {
    public static void encode(CScytheStrikePacket packet, FriendlyByteBuf buffer) {
    }

    public static CScytheStrikePacket decode(FriendlyByteBuf buffer) {
        return new CScytheStrikePacket();
    }

    public static void consume(CScytheStrikePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                DeathScytheItem.strike(playerEntity.level, playerEntity);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
