package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.utils.EntityFinder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
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
        return new SPlayEntitySoundPacket(buffer.readUUID(), SoundEvent.createVariableRangeEvent(buffer.readResourceLocation()), buffer.readFloat(), buffer.readFloat());
    }

    public static void consume(SPlayEntitySoundPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level level = Goety.PROXY.getLevel();
            if (level instanceof ClientLevel clientWorld) {
                Optional<? extends Entity> optionalEntity = EntityFinder.getEntityByUuiDGlobal(packet.entity);
                if (optionalEntity.isPresent()){
                    Entity entity = optionalEntity.get();
                    clientWorld.playLocalSound(entity.blockPosition(), packet.soundEvent, entity.getSoundSource(), packet.volume, packet.pitch, false);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
