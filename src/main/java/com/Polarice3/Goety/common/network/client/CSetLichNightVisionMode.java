package com.Polarice3.Goety.common.network.client;

import com.Polarice3.Goety.utils.LichdomHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CSetLichNightVisionMode {

    public static void encode(CSetLichNightVisionMode packet, FriendlyByteBuf buffer) {
    }

    public static CSetLichNightVisionMode decode(FriendlyByteBuf buffer) {
        return new CSetLichNightVisionMode();
    }

    public static void consume(CSetLichNightVisionMode packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();
            if (playerEntity != null && LichdomHelper.isLich(playerEntity)) {
                LichdomHelper.setNightVision(playerEntity, !LichdomHelper.nightVision(playerEntity));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
