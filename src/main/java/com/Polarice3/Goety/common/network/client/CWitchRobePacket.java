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

public class CWitchRobePacket {

    public static void encode(CWitchRobePacket packet, FriendlyByteBuf buffer) {
    }

    public static CWitchRobePacket decode(FriendlyByteBuf buffer) {
        return new CWitchRobePacket();
    }

    public static void consume(CWitchRobePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                ItemStack stack = CuriosFinder.findCurio(playerEntity, ModItems.WITCH_ROBE.get());

                if (stack != null){
                    WitchRobeInventory inventory = ModSaveInventory.getInstance().getWitchRobeInventory(stack.getOrCreateTag().getInt(WitchRobeItem.INVENTORY), playerEntity);
                    playerEntity.openMenu(inventory);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
