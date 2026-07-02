package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SInstaLookAtPacket {
    public int looker;
    public Vec3 target;

    public SInstaLookAtPacket(int lookerId, Vec3 vec3){
        this.looker = lookerId;
        this.target = vec3;
    }

    public SInstaLookAtPacket(Mob looker, Vec3 vec3){
        this.looker = looker.getId();
        this.target = vec3;
    }

    public static void encode(SInstaLookAtPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.looker);
        buffer.writeVector3f(packet.target.toVector3f());
    }

    public static SInstaLookAtPacket decode(FriendlyByteBuf buffer) {
        return new SInstaLookAtPacket(
                buffer.readInt(),
                new Vec3(buffer.readVector3f()));
    }

    public static void consume(SInstaLookAtPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                Level level = Goety.PROXY.getLevel();
                if (level instanceof ClientLevel clientWorld) {
                    Entity looker = clientWorld.getEntity(packet.looker);
                    if (looker instanceof Mob mob && packet.target != null) {
                        MobUtil.instaLook(mob, packet.target);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
