package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.neutral.GulfTentacle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class STentacleRangePacket {
    private final int mob;
    private final float range;

    public STentacleRangePacket(int mob, float range){
        this.mob = mob;
        this.range = range;
    }

    public static void encode(STentacleRangePacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.mob);
        buffer.writeFloat(packet.range);
    }

    public static STentacleRangePacket decode(FriendlyByteBuf buffer) {
        return new STentacleRangePacket(buffer.readInt(), buffer.readFloat());
    }

    public static void consume(STentacleRangePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level level = Goety.PROXY.getLevel();
            if (level instanceof ClientLevel clientWorld) {
                Entity entity = clientWorld.getEntity(packet.mob);
                if (entity instanceof GulfTentacle tentacle) {
                    tentacle.setRange(packet.range);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
