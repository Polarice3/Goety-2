package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.utils.EntityFinder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SPlayEntitySoundPacket {
    private UUID entity;
    private SoundEvent soundEvent;
    private float volume;
    private float pitch;

    public SPlayEntitySoundPacket(UUID uuid, SoundEvent soundEvent, float volume, float pitch){
        this.entity = uuid;
        this.soundEvent = soundEvent;
        this.volume = volume;
        this.pitch = pitch;
    }

    public static void encode(SPlayEntitySoundPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.entity);
        buffer.writeResourceLocation(packet.soundEvent.getLocation());
        buffer.writeFloat(packet.volume);
        buffer.writeFloat(packet.pitch);
    }

    public static SPlayEntitySoundPacket decode(FriendlyByteBuf buffer) {
        return new SPlayEntitySoundPacket(buffer.readUUID(), new SoundEvent(buffer.readResourceLocation()), buffer.readFloat(), buffer.readFloat());
    }

    public static void consume(SPlayEntitySoundPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT;

            ClientLevel clientWorld = Minecraft.getInstance().level;
            if (clientWorld != null){
                Entity entity = EntityFinder.getEntityByUuiDGlobal(packet.entity).get();
                if (entity != null){
                    clientWorld.playLocalSound(entity.blockPosition(), packet.soundEvent, entity.getSoundSource(), packet.volume, packet.pitch, false);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
