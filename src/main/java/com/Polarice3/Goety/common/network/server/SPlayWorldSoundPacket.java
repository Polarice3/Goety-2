package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.Goety;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SPlayWorldSoundPacket {
    private BlockPos blockPos;
    private SoundEvent soundEvent;
    private float volume;
    private float pitch;

    public SPlayWorldSoundPacket(BlockPos blockPos, SoundEvent soundEvent, float volume, float pitch){
        this.blockPos = blockPos;
        this.soundEvent = soundEvent;
        this.volume = volume;
        this.pitch = pitch;
    }

    public static void encode(SPlayWorldSoundPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.blockPos);
        buffer.writeResourceLocation(packet.soundEvent.getLocation());
        buffer.writeFloat(packet.volume);
        buffer.writeFloat(packet.pitch);
    }

    public static SPlayWorldSoundPacket decode(FriendlyByteBuf buffer) {
        return new SPlayWorldSoundPacket(buffer.readBlockPos(), SoundEvent.createVariableRangeEvent(buffer.readResourceLocation()), buffer.readFloat(), buffer.readFloat());
    }

    public static void consume(SPlayWorldSoundPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level level = Goety.PROXY.getLevel();
            if (level instanceof ClientLevel clientWorld) {
                clientWorld.playLocalSound(packet.blockPos, packet.soundEvent, SoundSource.NEUTRAL, packet.volume, packet.pitch, false);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
