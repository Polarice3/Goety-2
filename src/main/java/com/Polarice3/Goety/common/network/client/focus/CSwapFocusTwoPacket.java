package com.Polarice3.Goety.common.network.client.focus;

import com.Polarice3.Goety.common.items.handler.SoulUsingItemHandler;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CSwapFocusTwoPacket {
    public int swapWith;

    public CSwapFocusTwoPacket(int swapWith){
        this.swapWith = swapWith;
    }

    public static void encode(CSwapFocusTwoPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.swapWith);
    }

    public static CSwapFocusTwoPacket decode(FriendlyByteBuf buffer) {
        return new CSwapFocusTwoPacket(buffer.readInt());
    }

    public static void consume(CSwapFocusTwoPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> swapFocus(packet.swapWith, ctx.get().getSender()));
    }

    public static void swapFocus(int swapSlot, Player player) {
        ItemStack wand = WandUtil.findWand(player);

        SoulUsingItemHandler wandHandler = SoulUsingItemHandler.get(wand);

        ItemStack wandFocus = wandHandler.getSlot();

        ItemStack invFocus = player.getInventory().getItem(swapSlot);
        player.getInventory().setItem(swapSlot, wandFocus);
        wandHandler.extractItem();
        wandHandler.insertItem(invFocus);
        if (player instanceof ServerPlayer serverPlayer){
            serverPlayer.connection.send(new ClientboundSoundPacket(ModSounds.FOCUS_PICK.getHolder().get(), SoundSource.PLAYERS, serverPlayer.position().x, serverPlayer.position().y, serverPlayer.position().z, 1.0F, 1.0F, serverPlayer.level().getRandom().nextLong()));
        }
    }
}
