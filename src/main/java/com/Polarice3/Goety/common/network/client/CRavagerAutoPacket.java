package com.Polarice3.Goety.common.network.client;

import com.Polarice3.Goety.common.entities.ally.ModRavager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CRavagerAutoPacket {
    public static void encode(CRavagerAutoPacket packet, FriendlyByteBuf buffer) {
    }

    public static CRavagerAutoPacket decode(FriendlyByteBuf buffer) {
        return new CRavagerAutoPacket();
    }

    public static void consume(CRavagerAutoPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                if (playerEntity.getVehicle() instanceof ModRavager ravager){
                    ravager.setAutonomous(!ravager.isAutonomous());
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
