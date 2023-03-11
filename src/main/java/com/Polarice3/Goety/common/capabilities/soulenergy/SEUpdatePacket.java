package com.Polarice3.Goety.common.capabilities.soulenergy;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SEUpdatePacket {
    private final UUID PlayerUUID;
    private CompoundTag tag;

    public SEUpdatePacket(UUID uuid, CompoundTag tag) {
        this.PlayerUUID = uuid;
        this.tag = tag;
    }

    public SEUpdatePacket(Player player) {
        this.PlayerUUID = player.getUUID();
        player.getCapability(SEProvider.CAPABILITY, null).ifPresent((soulEnergy) -> {
            this.tag = (CompoundTag) SEHelper.save(new CompoundTag(), soulEnergy);
        });
    }

    public static void encode(SEUpdatePacket packet, FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.PlayerUUID);
        buffer.writeNbt(packet.tag);
    }

    public static SEUpdatePacket decode(FriendlyByteBuf buffer) {
        return new SEUpdatePacket(buffer.readUUID(), buffer.readNbt());
    }

    public static void consume(SEUpdatePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT;

            Player player = Goety.PROXY.getPlayer();
            if (player != null) {
                player.getCapability(SEProvider.CAPABILITY).ifPresent((soulEnergy) -> {
                    SEHelper.load(packet.tag, soulEnergy);
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
