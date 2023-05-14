package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.utils.EntityFinder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SApostleSmitePacket {
    private final UUID apostle;
    private final int antiRegen;

    public SApostleSmitePacket(UUID apostle, int antiRegen){
        this.apostle = apostle;
        this.antiRegen = antiRegen;
    }

    public static void encode(SApostleSmitePacket packet, FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.apostle);
        buffer.writeInt(packet.antiRegen);
    }

    public static SApostleSmitePacket decode(FriendlyByteBuf buffer) {
        return new SApostleSmitePacket(buffer.readUUID(), buffer.readInt());
    }

    public static void consume(SApostleSmitePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT;

            Entity entity = EntityFinder.getEntityByUuiDGlobal(packet.apostle).get();
            if (entity instanceof Apostle apostle){
                apostle.antiRegenTotal = packet.antiRegen;
                apostle.antiRegen = packet.antiRegen;
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
