package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.common.entities.ally.golem.RedstoneCube;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SRCGlowPacket {
    private final int redstoneCube;
    private final float bigGlow;

    public SRCGlowPacket(int redstoneCube, float bigGlow){
        this.redstoneCube = redstoneCube;
        this.bigGlow = bigGlow;
    }

    public static void encode(SRCGlowPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.redstoneCube);
        buffer.writeFloat(packet.bigGlow);
    }

    public static SRCGlowPacket decode(FriendlyByteBuf buffer) {
        return new SRCGlowPacket(buffer.readInt(), buffer.readFloat());
    }

    public static void consume(SRCGlowPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientLevel clientWorld = Minecraft.getInstance().level;
            if (clientWorld != null) {
                Entity entity = clientWorld.getEntity(packet.redstoneCube);
                if (entity instanceof RedstoneCube redstoneCube) {
                    redstoneCube.bigGlow = packet.bigGlow;
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
