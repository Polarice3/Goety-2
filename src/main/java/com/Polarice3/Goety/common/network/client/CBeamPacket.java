package com.Polarice3.Goety.common.network.client;

import com.Polarice3.Goety.common.entities.projectiles.AbstractBeam;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CBeamPacket {
    private final int beamEntityID;
    private final double positionX;
    private final double positionY;
    private final double positionZ;
    private final float xRot;
    private final float yRot;
    private final float xRotO;
    private final float yRotO;

    public CBeamPacket(AbstractBeam corruptedBeam) {
        this.beamEntityID = corruptedBeam.getId();
        this.positionX = corruptedBeam.position().x;
        this.positionY = corruptedBeam.position().y;
        this.positionZ = corruptedBeam.position().z;
        this.xRot = corruptedBeam.getXRot();
        this.yRot = corruptedBeam.getYRot();
        this.xRotO = corruptedBeam.xRotO;
        this.yRotO = corruptedBeam.yRotO;
    }

    public CBeamPacket(int beamEntityID, double positionX, double positionY, double positionZ, float xRot, float yRot, float xRotO, float yRotO) {
        this.beamEntityID = beamEntityID;
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
        this.xRot = xRot;
        this.yRot = yRot;
        this.xRotO = xRotO;
        this.yRotO = yRotO;
    }

    public static void encode(CBeamPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.beamEntityID);
        buf.writeDouble(packet.positionX);
        buf.writeDouble(packet.positionY);
        buf.writeDouble(packet.positionZ);
        buf.writeFloat(packet.xRot);
        buf.writeFloat(packet.yRot);
        buf.writeFloat(packet.xRotO);
        buf.writeFloat(packet.yRotO);
    }

    public static CBeamPacket decode(FriendlyByteBuf buf) {
        return new CBeamPacket(
                buf.readInt(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat()
        );
    }

    public static void consume(CBeamPacket packet, Supplier<NetworkEvent.Context> ctx) {
        if (packet != null) {
            ctx.get().setPacketHandled(true);
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player != null) {
                    Entity entity = player.level.getEntity(packet.beamEntityID);
                    if (entity instanceof AbstractBeam corruptedBeam) {
                        if (corruptedBeam.getOwner() != player) {
                            return;
                        }
                        corruptedBeam.setPos(packet.positionX, packet.positionY, packet.positionZ);
                        corruptedBeam.setXRot(packet.xRot);
                        corruptedBeam.setYRot(packet.yRot);
                        corruptedBeam.xRotO = packet.xRotO;
                        corruptedBeam.yRotO = packet.yRotO;
                    }
                }
            });
        }
    }
}
