package com.Polarice3.Goety.common.network.client.focus;

import com.Polarice3.Goety.common.items.handler.FocusBagItemHandler;
import com.Polarice3.Goety.common.items.handler.SoulUsingItemHandler;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.TotemFinder;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CSwapFocusPacket {
    public int swapWith;

    public CSwapFocusPacket(int swapWith){
        this.swapWith = swapWith;
    }

    public static void encode(CSwapFocusPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.swapWith);
    }

    public static CSwapFocusPacket decode(FriendlyByteBuf buffer) {
        return new CSwapFocusPacket(buffer.readInt());
    }

    public static void consume(CSwapFocusPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> swapFocus(packet.swapWith, ctx.get().getSender()));
    }

    public static void swapFocus(int swapSlot, Player player) {
        ItemStack stack = TotemFinder.findBag(player);
        if (stack.getCount() <= 0) {
            return;
        }

        ItemStack wand = WandUtil.findWand(player);

        FocusBagItemHandler bagHandler = FocusBagItemHandler.get(stack);
        SoulUsingItemHandler wandHandler = SoulUsingItemHandler.get(wand);

        ItemStack wandFocus = wandHandler.getSlot();

        ItemStack bagFocus = bagHandler.getStackInSlot(swapSlot);
        bagHandler.setStackInSlot(swapSlot, wandFocus);
        wandHandler.extractItem();
        wandHandler.insertItem(bagFocus);
        if (player instanceof ServerPlayer serverPlayer){
            serverPlayer.connection.send(new ClientboundCustomSoundPacket(ModSounds.FOCUS_PICK.getId(), SoundSource.PLAYERS, serverPlayer.position(), 1.0F, 1.0F, serverPlayer.getLevel().getRandom().nextLong()));
        }
    }
}
