package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.utils.EntityFinder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SSetPlayerOwnerPacket {
    private final UUID summoned;

    public SSetPlayerOwnerPacket(Entity summoned){
        this.summoned = summoned.getUUID();
    }

    public SSetPlayerOwnerPacket(UUID summoned){
        this.summoned = summoned;
    }

    public static void encode(SSetPlayerOwnerPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.summoned);
    }

    public static SSetPlayerOwnerPacket decode(FriendlyByteBuf buffer) {
        return new SSetPlayerOwnerPacket(buffer.readUUID());
    }

    public static void consume(SSetPlayerOwnerPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT;

            ClientLevel clientWorld = Minecraft.getInstance().level;
            if (clientWorld != null){
                Entity entity = EntityFinder.getEntityByUuiDGlobal(packet.summoned).isPresent() ? EntityFinder.getEntityByUuiDGlobal(packet.summoned).get() : null;
                Player playerEntity = Goety.PROXY.getPlayer();
                if (entity != null && playerEntity != null){
                    if (entity instanceof IOwned owned){
                        owned.setTrueOwner(playerEntity);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
