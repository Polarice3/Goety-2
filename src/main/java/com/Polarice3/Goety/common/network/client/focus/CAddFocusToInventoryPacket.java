package com.Polarice3.Goety.common.network.client.focus;

import com.Polarice3.Goety.common.items.handler.SoulUsingItemHandler;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CAddFocusToInventoryPacket {
    public CAddFocusToInventoryPacket(){
    }

    public static void encode(CAddFocusToInventoryPacket packet, FriendlyByteBuf buffer) {
    }

    public static CAddFocusToInventoryPacket decode(FriendlyByteBuf buffer) {
        return new CAddFocusToInventoryPacket();
    }

    public static void consume(CAddFocusToInventoryPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (player != null) {
                ItemStack stack = WandUtil.findFocus(player);
                if (stack.getCount() <= 0) {
                    return;
                }

                ItemStack wand = WandUtil.findWand(player);

                SoulUsingItemHandler wandHandler = SoulUsingItemHandler.get(wand);

                ItemStack wandFocus = wandHandler.getSlot();

                for (int i = 0; i < player.getInventory().items.size(); ++i) {
                    ItemStack itemStack = player.getInventory().getItem(i);
                    if (itemStack.isEmpty()) {
                        player.getInventory().setItem(i, wandFocus);
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
