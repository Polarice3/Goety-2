package com.Polarice3.Goety.common.network.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CTargetPlayerPacket {
    private final int aggressor;

    public CTargetPlayerPacket(Mob aggressor) {
        this.aggressor = aggressor.getId();
    }

    public CTargetPlayerPacket(int target) {
        this.aggressor = target;
    }

    public static void encode(CTargetPlayerPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.aggressor);
    }

    public static CTargetPlayerPacket decode(FriendlyByteBuf buffer) {
        return new CTargetPlayerPacket(buffer.readInt());
    }

    public static void consume(CTargetPlayerPacket packet, Supplier<NetworkEvent.Context> ctx) {
        if (packet != null) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();

                if (player != null) {
                    Entity entity = player.level.getEntity(packet.aggressor);
                    if (entity instanceof Mob mob){
                        mob.setTarget(player);
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
