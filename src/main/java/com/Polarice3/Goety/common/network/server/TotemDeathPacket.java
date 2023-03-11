package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.utils.TotemFinder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class TotemDeathPacket {
    private final UUID LivingEntityUUID;

    public TotemDeathPacket(UUID uuid) {
        this.LivingEntityUUID = uuid;
    }

    public static void encode(TotemDeathPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.LivingEntityUUID);
    }

    public static TotemDeathPacket decode(FriendlyByteBuf buffer) {
        return new TotemDeathPacket(buffer.readUUID());
    }

    public static void consume(TotemDeathPacket packet, Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {
            Player playerEntity = Goety.PROXY.getPlayer();

            if (playerEntity != null) {
                Minecraft.getInstance().particleEngine.createTrackingEmitter(playerEntity, ParticleTypes.TOTEM_OF_UNDYING, 30);
                playerEntity.level.playLocalSound(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.TOTEM_USE, playerEntity.getSoundSource(), 1.0F, 1.0F, false);
                Minecraft.getInstance().gameRenderer.displayItemActivation(TotemFinder.FindTotem(playerEntity));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
