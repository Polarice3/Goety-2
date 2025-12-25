package com.Polarice3.Goety.common.network.client;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CDismissServantsPacket {
    public static void encode(CDismissServantsPacket packet, FriendlyByteBuf buffer) {
    }

    public static CDismissServantsPacket decode(FriendlyByteBuf buffer) {
        return new CDismissServantsPacket();
    }

    public static void consume(CDismissServantsPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                if (playerEntity.level instanceof ServerLevel serverLevel){
                    for (Entity entity : serverLevel.getAllEntities()){
                        if (entity instanceof IOwned owned && owned instanceof LivingEntity livingEntity && owned.getTrueOwner() == playerEntity){
                            if (owned.isLimitedLife() && !SEHelper.getGroundedEntities(playerEntity).contains(livingEntity) && !SEHelper.getGroundedEntityTypes(playerEntity).contains(entity.getType())) {
                                entity.hurt(ModDamageSource.getDamageSource(serverLevel, ModDamageSource.DISMISSED), Float.MAX_VALUE);
                                entity.playSound(ModSounds.ROAR_SPELL.get(), 0.5F, 2.0F);
                            }
                        }
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
