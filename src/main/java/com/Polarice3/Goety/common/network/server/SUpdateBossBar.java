package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.Goety;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SUpdateBossBar {
    private final UUID bar;
    private final int boss;
    private final boolean remove;

    public SUpdateBossBar(UUID bar, int boss, boolean remove) {
        this.bar = bar;
        this.boss = boss;
        this.remove = remove;
    }

    public SUpdateBossBar(UUID bar, Mob boss, boolean remove) {
        this.bar = bar;
        this.boss = boss.getId();
        this.remove = remove;
    }

    public static void encode(SUpdateBossBar packet, FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.bar);
        buffer.writeInt(packet.boss);
        buffer.writeBoolean(packet.remove);
    }

    public static SUpdateBossBar decode(FriendlyByteBuf buffer) {
        return new SUpdateBossBar(buffer.readUUID(), buffer.readInt(), buffer.readBoolean());
    }

    public static void consume(SUpdateBossBar packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (ctx.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                player = Goety.PROXY.getPlayer();
            }
            if (player != null) {
                Entity entity = player.level.getEntity(packet.boss);
                if (entity instanceof Mob mob) {
                    if (packet.remove) {
                        Goety.PROXY.removeBossBar(packet.bar, mob);
                    } else {
                        Goety.PROXY.addBossBar(packet.bar, mob);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
