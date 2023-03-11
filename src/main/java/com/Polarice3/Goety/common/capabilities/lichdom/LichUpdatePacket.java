package com.Polarice3.Goety.common.capabilities.lichdom;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.utils.LichdomHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class LichUpdatePacket {
    private final UUID PlayerUUID;
    private CompoundTag tag;

    public LichUpdatePacket(UUID uuid, CompoundTag tag) {
        this.PlayerUUID = uuid;
        this.tag = tag;
    }

    public LichUpdatePacket(Player player) {
        this.PlayerUUID = player.getUUID();
        player.getCapability(LichProvider.CAPABILITY, null).ifPresent((lichdom) -> {
            this.tag = (CompoundTag) LichdomHelper.save(new CompoundTag(), lichdom);
        });
    }

    public static void encode(LichUpdatePacket packet, FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.PlayerUUID);
        buffer.writeNbt(packet.tag);
    }

    public static LichUpdatePacket decode(FriendlyByteBuf buffer) {
        return new LichUpdatePacket(buffer.readUUID(), buffer.readNbt());
    }

    public static void consume(LichUpdatePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT;

            Player player = Goety.PROXY.getPlayer();
            if (player != null) {
                player.getCapability(LichProvider.CAPABILITY).ifPresent((lichdom) -> {
                    LichdomHelper.load(packet.tag, lichdom);
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
