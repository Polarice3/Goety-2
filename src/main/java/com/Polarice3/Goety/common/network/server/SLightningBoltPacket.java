package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.Goety;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SLightningBoltPacket {
    private final double x;
    private final double y;
    private final double z;
    private final double x2;
    private final double y2;
    private final double z2;
    private final int lifespan;

    public SLightningBoltPacket(double x, double y, double z, double x2, double y2, double z2, int lifespan){
        this.x = x;
        this.y = y;
        this.z = z;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.lifespan = lifespan;
    }

    public SLightningBoltPacket(Vec3 start, Vec3 end, int lifespan){
        this.x = start.x;
        this.y = start.y;
        this.z = start.z;
        this.x2 = end.x;
        this.y2 = end.y;
        this.z2 = end.z;
        this.lifespan = lifespan;
    }

    public static void encode(SLightningBoltPacket packet, FriendlyByteBuf buffer) {
        buffer.writeFloat((float)packet.x);
        buffer.writeFloat((float)packet.y);
        buffer.writeFloat((float)packet.z);
        buffer.writeFloat((float)packet.x2);
        buffer.writeFloat((float)packet.y2);
        buffer.writeFloat((float)packet.z2);
        buffer.writeInt(packet.lifespan);
    }

    public static SLightningBoltPacket decode(FriendlyByteBuf buffer) {
        return new SLightningBoltPacket(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(),
                buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readInt());
    }

    public static void consume(SLightningBoltPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Vec3 start = new Vec3(packet.x, packet.y, packet.z);
            Vec3 end = new Vec3(packet.x2, packet.y2, packet.z2);
            Goety.PROXY.lightningBolt(start, end, packet.lifespan);
        });
        ctx.get().setPacketHandled(true);
    }
}
