package com.Polarice3.Goety.common.network.client;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CStopAttackPacket {
    public static void encode(CStopAttackPacket packet, FriendlyByteBuf buffer) {
    }

    public static CStopAttackPacket decode(FriendlyByteBuf buffer) {
        return new CStopAttackPacket();
    }

    public static void consume(CStopAttackPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                if (playerEntity.level instanceof ServerLevel serverLevel){
                    for (Entity entity : serverLevel.getAllEntities()){
                        if (entity instanceof Owned && ((Owned) entity).getTrueOwner() == playerEntity){
                            if (((Owned) entity).getTarget() != null) {
                                ((Owned) entity).setTarget(null);
                                entity.playSound(ModSounds.CAST_SPELL.get(), 1.0F, 1.0F);
                                serverLevel.broadcastEntityEvent(entity, (byte) 20);
                            }
                        }
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
