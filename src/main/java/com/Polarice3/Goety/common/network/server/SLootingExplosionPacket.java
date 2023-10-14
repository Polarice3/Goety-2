package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.utils.LootingExplosion;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class SLootingExplosionPacket {
    private final double x;
    private final double y;
    private final double z;
    private final float power;
    private final float knockbackX;
    private final float knockbackY;
    private final float knockbackZ;

    public SLootingExplosionPacket(double p_132115_, double p_132116_, double p_132117_, float p_132118_, @Nullable Vec3 p_132120_) {
        this.x = p_132115_;
        this.y = p_132116_;
        this.z = p_132117_;
        this.power = p_132118_;
        if (p_132120_ != null) {
            this.knockbackX = (float)p_132120_.x;
            this.knockbackY = (float)p_132120_.y;
            this.knockbackZ = (float)p_132120_.z;
        } else {
            this.knockbackX = 0.0F;
            this.knockbackY = 0.0F;
            this.knockbackZ = 0.0F;
        }
    }

    public static void encode(SLootingExplosionPacket packet, FriendlyByteBuf buffer) {
        buffer.writeFloat((float)packet.x);
        buffer.writeFloat((float)packet.y);
        buffer.writeFloat((float)packet.z);
        buffer.writeFloat(packet.power);
        buffer.writeFloat(packet.knockbackX);
        buffer.writeFloat(packet.knockbackY);
        buffer.writeFloat(packet.knockbackZ);
    }

    public static SLootingExplosionPacket decode(FriendlyByteBuf buffer) {
        return new SLootingExplosionPacket(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), new Vec3(buffer.readFloat(), buffer.readFloat(), buffer.readFloat()));
    }

    public static void consume(SLootingExplosionPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = Goety.PROXY.getPlayer();
            if (player != null){
                LootingExplosion explosion = new LootingExplosion(player.level, null, packet.x, packet.y, packet.z, packet.power, false, Explosion.BlockInteraction.NONE, LootingExplosion.Mode.REGULAR);
                explosion.finalizeExplosion(true);
                player.setDeltaMovement(player.getDeltaMovement().add(packet.knockbackX, packet.knockbackY, packet.knockbackZ));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
