package com.Polarice3.Goety.common.capabilities.witchbarter;

import com.Polarice3.Goety.common.capabilities.lichdom.LichProvider;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.LichdomHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class WBUpdatePacket {
    private final UUID WitchUUID;
    private CompoundTag tag;

    public WBUpdatePacket(UUID uuid, CompoundTag tag) {
        this.WitchUUID = uuid;
        this.tag = tag;
    }

    public WBUpdatePacket(LivingEntity player) {
        this.WitchUUID = player.getUUID();
        player.getCapability(LichProvider.CAPABILITY, null).ifPresent((lichdom) -> {
            this.tag = (CompoundTag) LichdomHelper.save(new CompoundTag(), lichdom);
        });
    }

    public static void encode(WBUpdatePacket packet, FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.WitchUUID);
        buffer.writeNbt(packet.tag);
    }

    public static WBUpdatePacket decode(FriendlyByteBuf buffer) {
        return new WBUpdatePacket(buffer.readUUID(), buffer.readNbt());
    }

    public static void consume(WBUpdatePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT;

            Entity entity = EntityFinder.getEntityByUuiDGlobal(packet.WitchUUID).get();
            entity.getCapability(WitchBarterProvider.CAPABILITY).ifPresent((infamy) -> {
                WitchBarterProvider.load(packet.tag, infamy);
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
