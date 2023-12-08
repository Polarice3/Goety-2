package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SFocusCooldownPacket {
    private final Item item;
    private final int duration;

    public SFocusCooldownPacket(Item p_132000_, int p_132001_) {
        this.item = p_132000_;
        this.duration = p_132001_;
    }

    public static void encode(SFocusCooldownPacket packet, FriendlyByteBuf buffer) {
        buffer.writeId(Registry.ITEM, packet.item);
        buffer.writeVarInt(packet.duration);
    }

    public static SFocusCooldownPacket decode(FriendlyByteBuf buffer) {
        return new SFocusCooldownPacket(buffer.readById(Registry.ITEM), buffer.readVarInt());
    }

    public static void consume(SFocusCooldownPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                LocalPlayer localPlayer = Minecraft.getInstance().player;
                if (localPlayer != null) {
                    if (packet.duration == 0) {
                        SEHelper.getFocusCoolDown(localPlayer).removeCooldown(localPlayer.level, packet.item);
                    } else {
                        SEHelper.addCooldown(localPlayer, packet.item, packet.duration);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
