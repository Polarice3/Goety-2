package com.Polarice3.Goety.common.network.client;

import com.Polarice3.Goety.client.inventory.container.WandandBagContainer;
import com.Polarice3.Goety.common.items.handler.FocusBagItemHandler;
import com.Polarice3.Goety.common.items.handler.SoulUsingItemHandler;
import com.Polarice3.Goety.common.items.magic.DarkWand;
import com.Polarice3.Goety.utils.TotemFinder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public class CWandAndBagKeyPacket {

    public static void encode(CWandAndBagKeyPacket packet, FriendlyByteBuf buffer) {
    }

    public static CWandAndBagKeyPacket decode(FriendlyByteBuf buffer) {
        return new CWandAndBagKeyPacket();
    }

    public static void consume(CWandAndBagKeyPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                ItemStack bag = TotemFinder.findBag(playerEntity);
                ItemStack stack = playerEntity.getMainHandItem();
                ItemStack stack2 = playerEntity.getOffhandItem();

                if (!bag.isEmpty()) {
                    if (!stack.isEmpty() && stack.getItem() instanceof DarkWand) {
                        SimpleMenuProvider provider = new SimpleMenuProvider(
                                (id, inventory, player) -> new WandandBagContainer(id, SoulUsingItemHandler.get(playerEntity.getMainHandItem()), FocusBagItemHandler.get(bag), playerEntity.getMainHandItem()), stack.getDisplayName());
                        NetworkHooks.openScreen(playerEntity, provider, (buffer) -> buffer.writeBoolean(playerEntity.getUsedItemHand() == InteractionHand.MAIN_HAND));
                    } else if (!stack2.isEmpty() && stack2.getItem() instanceof DarkWand) {
                        SimpleMenuProvider provider = new SimpleMenuProvider(
                                (id, inventory, player) -> new WandandBagContainer(id, SoulUsingItemHandler.get(playerEntity.getOffhandItem()), FocusBagItemHandler.get(bag), playerEntity.getOffhandItem()), stack2.getDisplayName());
                        NetworkHooks.openScreen(playerEntity, provider, (buffer) -> buffer.writeBoolean(playerEntity.getUsedItemHand() == InteractionHand.OFF_HAND));
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
