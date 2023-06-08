package com.Polarice3.Goety.common.network;

import com.Polarice3.Goety.utils.EntityFinder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class EntityUpdatePacket {
    private final UUID LivingEntityUUID;
    private CompoundTag tag;

    public EntityUpdatePacket(UUID uuid, CompoundTag tag) {
        this.LivingEntityUUID = uuid;
        this.tag = tag;
    }

    public static void encode(EntityUpdatePacket packet, FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.LivingEntityUUID);
        buffer.writeNbt(packet.tag);
    }

    public static EntityUpdatePacket decode(FriendlyByteBuf buffer) {
        return new EntityUpdatePacket(buffer.readUUID(), buffer.readNbt());
    }

    public static void consume(EntityUpdatePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT;

            Entity entity = EntityFinder.getEntityByUuiDGlobal(packet.LivingEntityUUID).get();
            if (entity instanceof LivingEntity livingEntity){
                livingEntity.readAdditionalSaveData(packet.tag);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
