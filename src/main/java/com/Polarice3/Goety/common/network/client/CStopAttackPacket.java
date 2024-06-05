package com.Polarice3.Goety.common.network.client;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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
                        if (entity instanceof IOwned owned && entity instanceof Mob mob && owned.getTrueOwner() == playerEntity){
                            Entity pick = MobUtil.getSingleTarget(serverLevel, playerEntity, 16, 3);
                            if (pick instanceof LivingEntity target
                                    && target != mob
                                    && target != owned.getTrueOwner()
                                    && !MobUtil.areAllies(playerEntity, target)
                                    && mob.distanceTo(playerEntity) <= 32){
                                mob.setTarget(target);
                                if (mob.getLastHurtByMob() != target){
                                    mob.setLastHurtByMob(target);
                                }
                                entity.playSound(ModSounds.ROAR_SPELL.get(), 1.0F, 2.0F);
                                serverLevel.broadcastEntityEvent(entity, (byte) 20);
                            } else {
                                if (mob.getTarget() != null) {
                                    mob.setTarget(null);
                                    if (mob.getLastHurtByMob() != null){
                                        mob.setLastHurtByMob(null);
                                    }
                                    entity.playSound(ModSounds.CAST_SPELL.get(), 1.0F, 1.0F);
                                    serverLevel.broadcastEntityEvent(entity, (byte) 20);
                                }
                            }
                        }
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
