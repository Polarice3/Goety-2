package com.Polarice3.Goety.common.network.client;

import com.Polarice3.Goety.client.inventory.container.SoulItemContainer;
import com.Polarice3.Goety.common.items.handler.SoulUsingItemHandler;
import com.Polarice3.Goety.common.items.magic.DarkWand;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public class CWandKeyPacket {

    public static void encode(CWandKeyPacket packet, FriendlyByteBuf buffer) {
    }

    public static CWandKeyPacket decode(FriendlyByteBuf buffer) {
        return new CWandKeyPacket();
    }

    public static void consume(CWandKeyPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                ItemStack stack = playerEntity.getMainHandItem();
                ItemStack stack2 = playerEntity.getOffhandItem();

                if (!stack.isEmpty() && stack.getItem() instanceof DarkWand) {
                    SimpleMenuProvider provider = new SimpleMenuProvider(
                            (id, inventory, player) -> new SoulItemContainer(id, inventory, SoulUsingItemHandler.get(stack), stack, playerEntity.getUsedItemHand()), Component.translatable(stack.getDescriptionId()));
                    NetworkHooks.openScreen(playerEntity, provider, (buffer) -> buffer.writeBoolean(playerEntity.getUsedItemHand() == InteractionHand.MAIN_HAND));
                } else if (!stack2.isEmpty() && stack2.getItem() instanceof DarkWand){
                    SimpleMenuProvider provider = new SimpleMenuProvider(
                            (id, inventory, player) -> new SoulItemContainer(id, inventory, SoulUsingItemHandler.get(stack2), stack2, playerEntity.getUsedItemHand()), Component.translatable(stack2.getDescriptionId()));
                    NetworkHooks.openScreen(playerEntity, provider, (buffer) -> buffer.writeBoolean(playerEntity.getUsedItemHand() == InteractionHand.OFF_HAND));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
