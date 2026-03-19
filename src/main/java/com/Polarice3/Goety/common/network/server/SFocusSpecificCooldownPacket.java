package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.capabilities.soulenergy.FocusCooldown;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SFocusSpecificCooldownPacket {
    private final ItemStack itemStack;
    private final String key;
    private final int duration;

    public SFocusSpecificCooldownPacket(ItemStack itemStack, int duration) {
        this.itemStack = itemStack;
        this.key = FocusCooldown.keyOf(itemStack);
        this.duration = duration;
    }

    public SFocusSpecificCooldownPacket(String key, int duration) {
        this.itemStack = ItemStack.EMPTY;
        this.key = key;
        this.duration = duration;
    }

    public static void encode(SFocusSpecificCooldownPacket packet, FriendlyByteBuf buffer) {
        buffer.writeItemStack(packet.itemStack, false);
        buffer.writeUtf(packet.key);
        buffer.writeVarInt(packet.duration);
    }

    public static SFocusSpecificCooldownPacket decode(FriendlyByteBuf buffer) {
        ItemStack stack = buffer.readItem();
        String key = buffer.readUtf();
        int duration = buffer.readVarInt();
        return new SFocusSpecificCooldownPacket(stack, key, duration);
    }

    private SFocusSpecificCooldownPacket(ItemStack itemStack, String key, int duration) {
        this.itemStack = itemStack;
        this.key = key;
        this.duration = duration;
    }

    public static void consume(SFocusSpecificCooldownPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = Goety.PROXY.getPlayer();
            if (player != null) {
                if (packet.duration == 0) {
                    SEHelper.getFocusCoolDown(player).removeSpecificCooldown(player, player.level, packet.key);
                } else {
                    SEHelper.addSpecificCooldown(player, packet.itemStack, packet.duration);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
