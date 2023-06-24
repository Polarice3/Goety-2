package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.utils.BrewUtils;
import com.Polarice3.Goety.utils.ParticleUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SAddBrewParticlesPacket {
    private final ItemStack itemStack;
    private final BlockPos blockPos;
    private final boolean instant;
    private final int color;

    public SAddBrewParticlesPacket(ItemStack itemStack, BlockPos blockPos, boolean instant, int color){
        this.itemStack = itemStack;
        this.blockPos = blockPos;
        this.instant = instant;
        this.color = color;
    }

    public static void encode(SAddBrewParticlesPacket packet, FriendlyByteBuf buffer) {
        buffer.writeItemStack(packet.itemStack, true);
        buffer.writeBlockPos(packet.blockPos);
        buffer.writeBoolean(packet.instant);
        buffer.writeInt(packet.color);
    }

    public static SAddBrewParticlesPacket decode(FriendlyByteBuf buffer) {
        return new SAddBrewParticlesPacket(buffer.readItem(), buffer.readBlockPos(), buffer.readBoolean(), buffer.readInt());
    }

    public static void consume(SAddBrewParticlesPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientLevel clientWorld = Minecraft.getInstance().level;
            if (clientWorld != null){
                int area = BrewUtils.getAreaOfEffect(packet.itemStack) + 4;
                int areaSqr = Mth.square(area);
                Vec3 vec3 = Vec3.atBottomCenterOf(packet.blockPos);

                for(int l = 0; l < 8; ++l) {
                    clientWorld.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.SPLASH_POTION)), vec3.x, vec3.y, vec3.z, clientWorld.random.nextGaussian() * 0.15D, clientWorld.random.nextDouble() * 0.2D, clientWorld.random.nextGaussian() * 0.15D);
                }

                float f4 = (float)(packet.color >> 16 & 255) / 255.0F;
                float f6 = (float)(packet.color >> 8 & 255) / 255.0F;
                float f8 = (float)(packet.color >> 0 & 255) / 255.0F;
                ParticleOptions particleoptions = packet.instant ? ParticleTypes.INSTANT_EFFECT : ParticleTypes.EFFECT;

                for(int j3 = 0; j3 < 100 * (BrewUtils.getAreaOfEffect(packet.itemStack) + 1); ++j3) {
                    double d21 = clientWorld.random.nextDouble() * area;
                    double d26 = clientWorld.random.nextDouble() * Math.PI * (area / 2.0D);
                    double d30 = Math.cos(d26) * d21;
                    double d2 = 0.01D + clientWorld.random.nextDouble() * 0.5D + (area / 10.0D);
                    double d4 = Math.sin(d26) * d21;
                    Particle particle = ParticleUtil.addParticleInternal(particleoptions, particleoptions.getType().getOverrideLimiter(), vec3.x + d30 * 0.1D, vec3.y + 0.3D, vec3.z + d4 * 0.1D, d30, d2, d4);
                    if (particle != null) {
                        float f3 = 0.75F + clientWorld.random.nextFloat() * 0.25F;
                        particle.setColor(f4 * f3, f6 * f3, f8 * f3);
                        particle.setPower((float)d21);
                    }
                }

                clientWorld.playLocalSound(packet.blockPos.getX(), packet.blockPos.getY(), packet.blockPos.getZ(), SoundEvents.SPLASH_POTION_BREAK, SoundSource.NEUTRAL, 1.0F, clientWorld.random.nextFloat() * 0.1F + 0.9F, false);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
