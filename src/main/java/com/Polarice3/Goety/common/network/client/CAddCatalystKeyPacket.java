package com.Polarice3.Goety.common.network.client;

import com.Polarice3.Goety.common.inventory.ModSaveInventory;
import com.Polarice3.Goety.common.inventory.WitchRobeInventory;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.curios.WitchRobeItem;
import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CAddCatalystKeyPacket {
    public static void encode(CAddCatalystKeyPacket packet, FriendlyByteBuf buffer) {
    }

    public static CAddCatalystKeyPacket decode(FriendlyByteBuf buffer) {
        return new CAddCatalystKeyPacket();
    }

    public static void consume(CAddCatalystKeyPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                ItemStack stack = CuriosFinder.findCurio(playerEntity, ModItems.WITCH_ROBE.get());
                ItemStack mainHandItem = playerEntity.getMainHandItem();
                ItemStack offhandItem = playerEntity.getOffhandItem();

                if (!stack.isEmpty()){
                    WitchRobeInventory inventory = ModSaveInventory.getInstance().getWitchRobeInventory(stack.getOrCreateTag().getInt(WitchRobeItem.INVENTORY), playerEntity);
                    if (!mainHandItem.isEmpty()){
                        inventory.addBottlesOrCatalyst(mainHandItem);
                    } else if (!offhandItem.isEmpty()){
                        inventory.addBottlesOrCatalyst(offhandItem);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
