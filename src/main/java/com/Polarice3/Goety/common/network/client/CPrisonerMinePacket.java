package com.Polarice3.Goety.common.network.client;

import com.Polarice3.Goety.common.entities.ally.illager.raider.Prisoner;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.ItemHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CPrisonerMinePacket {
    public int mob;

    public CPrisonerMinePacket(int id) {
        this.mob = id;
    }

    public static void encode(CPrisonerMinePacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.mob);
    }

    public static CPrisonerMinePacket decode(FriendlyByteBuf buffer) {
        return new CPrisonerMinePacket(buffer.readInt());
    }

    public static void consume(CPrisonerMinePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                Entity entity = playerEntity.level.getEntity(packet.mob);
                if (entity instanceof Prisoner prisoner) {
                    ItemHelper.hurtAndBreak(prisoner.getMainHandItem(), MobsConfig.PrisonerMiningDurability.get(), prisoner);
                    prisoner.mineTimes += 1;
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
