package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.client.audio.GuardianAttackSound;
import com.Polarice3.Goety.common.entities.ally.GuardianServant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SGuardianAttackSoundPacket {
    private final int entity;

    public SGuardianAttackSoundPacket(Entity entity){
        this.entity = entity.getId();
    }

    public SGuardianAttackSoundPacket(int entity){
        this.entity = entity;
    }

    public static void encode(SGuardianAttackSoundPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.entity);
    }

    public static SGuardianAttackSoundPacket decode(FriendlyByteBuf buffer) {
        return new SGuardianAttackSoundPacket(
                buffer.readInt());
    }

    public static void consume(SGuardianAttackSoundPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            /*if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                Minecraft minecraft = Minecraft.getInstance();
                ClientLevel clientWorld = minecraft.level;
                if (clientWorld != null) {
                    if (packet.entity >= 0) {
                        Entity entity = clientWorld.getEntity(packet.entity);
                        if (entity instanceof GuardianServant guardianServant) {
                            minecraft.getSoundManager().play(new GuardianAttackSound(guardianServant));
                        }
                    }
                }
            }*/
        });
        ctx.get().setPacketHandled(true);
    }
}
