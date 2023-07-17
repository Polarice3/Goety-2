package com.Polarice3.Goety.common.network.client;

import com.Polarice3.Goety.common.entities.ally.ModRavager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CRavagerRoarPacket {
    public static void encode(CRavagerRoarPacket packet, FriendlyByteBuf buffer) {
    }

    public static CRavagerRoarPacket decode(FriendlyByteBuf buffer) {
        return new CRavagerRoarPacket();
    }

    public static void consume(CRavagerRoarPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                if (playerEntity.getVehicle() instanceof ModRavager ravager){
                    ravager.forceRoar();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
