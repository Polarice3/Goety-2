package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.Goety;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SPlayPlayerSoundPacket {
    private SoundEvent soundEvent;
    private float volume;
    private float pitch;

    public SPlayPlayerSoundPacket(SoundEvent soundEvent, float volume, float pitch){
        this.soundEvent = soundEvent;
        this.volume = volume;
        this.pitch = pitch;
    }

    public static void encode(SPlayPlayerSoundPacket packet, FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(packet.soundEvent.getLocation());
        buffer.writeFloat(packet.volume);
        buffer.writeFloat(packet.pitch);
    }

    public static SPlayPlayerSoundPacket decode(FriendlyByteBuf buffer) {
        return new SPlayPlayerSoundPacket(new SoundEvent(buffer.readResourceLocation()), buffer.readFloat(), buffer.readFloat());
    }

    public static void consume(SPlayPlayerSoundPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = Goety.PROXY.getPlayer();
            if (player != null){
                player.playSound(packet.soundEvent, packet.volume, packet.pitch);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
