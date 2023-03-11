package com.Polarice3.Goety.common.network.client;

import com.Polarice3.Goety.client.inventory.container.FocusBagContainer;
import com.Polarice3.Goety.common.items.handler.FocusBagItemHandler;
import com.Polarice3.Goety.utils.TotemFinder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public class CBagKeyPacket {

    public static void encode(CBagKeyPacket packet, FriendlyByteBuf buffer) {
    }

    public static CBagKeyPacket decode(FriendlyByteBuf buffer) {
        return new CBagKeyPacket();
    }

    public static void consume(CBagKeyPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                ItemStack stack = TotemFinder.findBag(playerEntity);

                if (!stack.isEmpty()){
                    SimpleMenuProvider provider = new SimpleMenuProvider(
                            (id, inventory, player) -> new FocusBagContainer(id, inventory, FocusBagItemHandler.get(stack), stack), stack.getDisplayName());
                    NetworkHooks.openScreen(playerEntity, provider, (buffer) -> {});
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
