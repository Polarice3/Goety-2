package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.common.entities.boss.Apostle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SApostleSmitePacket {
    private final int apostle;
    private final int antiRegen;

    public SApostleSmitePacket(int apostle, int antiRegen){
        this.apostle = apostle;
        this.antiRegen = antiRegen;
    }

    public static void encode(SApostleSmitePacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.apostle);
        buffer.writeInt(packet.antiRegen);
    }

    public static SApostleSmitePacket decode(FriendlyByteBuf buffer) {
        return new SApostleSmitePacket(buffer.readInt(), buffer.readInt());
    }

    public static void consume(SApostleSmitePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientLevel clientWorld = Minecraft.getInstance().level;
            if (clientWorld != null) {
                Entity entity = clientWorld.getEntity(packet.apostle);
                if (entity instanceof Apostle apostle) {
                    apostle.antiRegenTotal = packet.antiRegen;
                    apostle.antiRegen = packet.antiRegen;
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
