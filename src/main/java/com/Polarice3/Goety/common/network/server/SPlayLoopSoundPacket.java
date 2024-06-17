package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.client.audio.LoopSoundPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SPlayLoopSoundPacket {
    private final SoundEvent soundEvent;
    private final int entity;
    private final float volume;

    public SPlayLoopSoundPacket(Entity entity, SoundEvent soundEvent, float volume){
        this.entity = entity.getId();
        this.soundEvent = soundEvent;
        this.volume = volume;
    }

    public SPlayLoopSoundPacket(int entity, SoundEvent soundEvent, float volume){
        this.entity = entity;
        this.soundEvent = soundEvent;
        this.volume = volume;
    }

    public static void encode(SPlayLoopSoundPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.entity);
        buffer.writeResourceLocation(packet.soundEvent.getLocation());
        buffer.writeFloat(packet.volume);
    }

    public static SPlayLoopSoundPacket decode(FriendlyByteBuf buffer) {
        return new SPlayLoopSoundPacket(
                buffer.readInt(),
                SoundEvent.createVariableRangeEvent(buffer.readResourceLocation()),
                buffer.readFloat());
    }

    public static void consume(SPlayLoopSoundPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT;

            ClientLevel clientWorld = Minecraft.getInstance().level;
            if (clientWorld != null){
                if (packet.entity >= 0) {
                    Entity entity = clientWorld.getEntity(packet.entity);
                    if (entity != null) {
                        LoopSoundPlayer.playSound(entity, packet.soundEvent, packet.volume);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
