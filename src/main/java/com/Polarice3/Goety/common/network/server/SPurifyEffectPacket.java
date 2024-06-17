package com.Polarice3.Goety.common.network.server;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

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
                    for (MobEffect mobEffect : ForgeRegistries.MOB_EFFECTS){
                        boolean flag;
                        if (packet.removeDebuff) {
                            flag = !mobEffect.isBeneficial();
                        } else {
                            flag = mobEffect.isBeneficial();
                        }
                        if (flag && !mobEffect.getCurativeItems().isEmpty()){
                            livingEntity.removeEffect(mobEffect);
                        }
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
