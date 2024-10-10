package com.Polarice3.Goety.common.capabilities.witchbarter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class WBUpdatePacket {
    private final int witchId;
    private CompoundTag tag;

    public WBUpdatePacket(int witchId, CompoundTag tag) {
        this.witchId = witchId;
        this.tag = tag;
    }

    public WBUpdatePacket(LivingEntity livingEntity) {
        this.witchId = livingEntity.getId();
        livingEntity.getCapability(WitchBarterProvider.CAPABILITY, null).ifPresent((barter) -> {
            this.tag = WitchBarterProvider.save(new CompoundTag(), barter);
        });
    }

    public static void encode(WBUpdatePacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.witchId);
        buffer.writeNbt(packet.tag);
    }

    public static WBUpdatePacket decode(FriendlyByteBuf buffer) {
        return new WBUpdatePacket(buffer.readInt(), buffer.readNbt());
    }

    public static void consume(WBUpdatePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT;

            ClientLevel clientLevel = Minecraft.getInstance().level;
            if (clientLevel != null){
                Entity entity = clientLevel.getEntity(packet.witchId);
                if (entity != null) {
                    entity.getCapability(WitchBarterProvider.CAPABILITY).ifPresent((barter) -> {
                        WitchBarterProvider.load(packet.tag, barter);
                    });
                }
            }

        });
        ctx.get().setPacketHandled(true);
    }
}
