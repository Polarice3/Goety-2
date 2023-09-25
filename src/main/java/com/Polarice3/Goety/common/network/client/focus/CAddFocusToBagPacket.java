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

public class CAddFocusToBagPacket {
    public CAddFocusToBagPacket(){
    }

    public static void encode(CAddFocusToBagPacket packet, FriendlyByteBuf buffer) {
    }

    public static CAddFocusToBagPacket decode(FriendlyByteBuf buffer) {
        return new CAddFocusToBagPacket();
    }

    public static void consume(CAddFocusToBagPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (player != null) {
                ItemStack stack = TotemFinder.findBag(player);
                if (stack.getCount() <= 0) {
                    return;
                }

                ItemStack wand = WandUtil.findWand(player);

                FocusBagItemHandler bagHandler = FocusBagItemHandler.get(stack);
                SoulUsingItemHandler wandHandler = SoulUsingItemHandler.get(wand);

                ItemStack wandFocus = wandHandler.getSlot();

                for (int i = 1; i < bagHandler.getSlots(); ++i) {
                    ItemStack itemStack = bagHandler.getStackInSlot(i);
                    if (itemStack.isEmpty()) {
                        bagHandler.setStackInSlot(i, wandFocus);
                        wandHandler.extractItem();
                        break;
                    }
                }
                if (player instanceof ServerPlayer serverPlayer){
                    serverPlayer.connection.send(new ClientboundCustomSoundPacket(ModSounds.FOCUS_PICK.getId(), SoundSource.PLAYERS, serverPlayer.position(), 1.0F, 1.0F, serverPlayer.getLevel().getRandom().nextLong()));
                }
            }
        });
    }
}
