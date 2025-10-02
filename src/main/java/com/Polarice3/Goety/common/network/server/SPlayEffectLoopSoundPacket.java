package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.audio.LoopSoundPlayer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SPlayEffectLoopSoundPacket {
    private final SoundEvent soundEvent;
    private final int entity;
    private final MobEffect effect;
    private final float volume;
    private final float pitch;

    public SPlayEffectLoopSoundPacket(Entity entity, SoundEvent soundEvent, MobEffect mobEffect, float volume, float pitch){
        this.entity = entity.getId();
        this.soundEvent = soundEvent;
        this.effect = mobEffect;
        this.volume = volume;
        this.pitch = pitch;
    }

    public SPlayEffectLoopSoundPacket(int entity, SoundEvent soundEvent, MobEffect mobEffect, float volume, float pitch){
        this.entity = entity;
        this.soundEvent = soundEvent;
        this.effect = mobEffect;
        this.volume = volume;
        this.pitch = pitch;
    }

    public static void encode(SPlayEffectLoopSoundPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.entity);
        buffer.writeResourceLocation(packet.soundEvent.getLocation());
        buffer.writeInt(MobEffect.getId(packet.effect));
        buffer.writeFloat(packet.volume);
        buffer.writeFloat(packet.pitch);
    }

    public static SPlayEffectLoopSoundPacket decode(FriendlyByteBuf buffer) {
        return new SPlayEffectLoopSoundPacket(
                buffer.readInt(),
                SoundEvent.createVariableRangeEvent(buffer.readResourceLocation()),
                MobEffect.byId(buffer.readInt()),
                buffer.readFloat(),
                buffer.readFloat());
    }

    public static void consume(SPlayEffectLoopSoundPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                Level level = Goety.PROXY.getLevel();
                if (level instanceof ClientLevel clientWorld) {
                    if (packet.entity >= 0) {
                        Entity entity = clientWorld.getEntity(packet.entity);
                        if (entity instanceof LivingEntity livingEntity) {
                            LoopSoundPlayer.playEffectSound(livingEntity, packet.soundEvent, packet.effect, packet.volume, packet.pitch);
                        }
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
