package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SInstaLookPacket {
    public int looker;
    public int target;

    public SInstaLookPacket(int lookerId, int targetId){
        this.looker = lookerId;
        this.target = targetId;
    }

    public SInstaLookPacket(Mob looker, Entity target){
        this.looker = looker.getId();
        this.target = target.getId();
    }

    public static void encode(SInstaLookPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.looker);
        buffer.writeInt(packet.target);
    }

    public static SInstaLookPacket decode(FriendlyByteBuf buffer) {
        return new SInstaLookPacket(
                buffer.readInt(),
                buffer.readInt());
    }

    public static void consume(SInstaLookPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                Level level = Goety.PROXY.getLevel();
                if (level instanceof ClientLevel clientWorld) {
                    Entity looker = clientWorld.getEntity(packet.looker);
                    Entity target = clientWorld.getEntity(packet.target);
                    if (looker instanceof Mob mob && target != null) {
                        MobUtil.instaLook(mob, target);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
