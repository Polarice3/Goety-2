package com.Polarice3.Goety.common.network.server;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SPurifyEffectPacket {
    private final int mob;
    private final boolean removeDebuff;

    public SPurifyEffectPacket(int mob, boolean removeDebuff){
        this.mob = mob;
        this.removeDebuff = removeDebuff;
    }

    public static void encode(SPurifyEffectPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.mob);
        buffer.writeBoolean(packet.removeDebuff);
    }

    public static SPurifyEffectPacket decode(FriendlyByteBuf buffer) {
        return new SPurifyEffectPacket(buffer.readInt(), buffer.readBoolean());
    }

    public static void consume(SPurifyEffectPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientLevel clientWorld = Minecraft.getInstance().level;
            if (clientWorld != null) {
                Entity entity = clientWorld.getEntity(packet.mob);
                if (entity instanceof LivingEntity livingEntity) {
                    for (MobEffectInstance mobEffectInstance : livingEntity.getActiveEffects()){
                        boolean flag;
                        if (packet.removeDebuff) {
                            flag = !mobEffectInstance.getEffect().isBeneficial();
                        } else {
                            flag = mobEffectInstance.getEffect().isBeneficial();
                        }
                        if (flag && !mobEffectInstance.getEffect().getCurativeItems().isEmpty()){
                            livingEntity.removeEffect(mobEffectInstance.getEffect());
                        }
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
