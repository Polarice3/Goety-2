package com.Polarice3.Goety.common.capabilities.misc;

import com.Polarice3.Goety.utils.MiscCapHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MiscCapUpdatePacket {
    private final int entityID;
    private CompoundTag tag;

    public MiscCapUpdatePacket(int id, CompoundTag tag) {
        this.entityID = id;
        this.tag = tag;
    }

    public MiscCapUpdatePacket(LivingEntity living) {
        this.entityID = living.getId();
        living.getCapability(MiscProvider.CAPABILITY, null).ifPresent((misc) -> {
            this.tag = (CompoundTag) MiscCapHelper.save(new CompoundTag(), misc);
        });
    }

    public static void encode(MiscCapUpdatePacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.entityID);
        buffer.writeNbt(packet.tag);
    }

    public static MiscCapUpdatePacket decode(FriendlyByteBuf buffer) {
        return new MiscCapUpdatePacket(buffer.readInt(), buffer.readNbt());
    }

    public static void consume(MiscCapUpdatePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT;

            ClientLevel clientLevel = Minecraft.getInstance().level;
            if (clientLevel != null){
                Entity entity = clientLevel.getEntity(packet.entityID);
                if (entity != null) {
                    entity.getCapability(MiscProvider.CAPABILITY).ifPresent((misc) -> {
                        MiscCapHelper.load(packet.tag, misc);
                    });
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
