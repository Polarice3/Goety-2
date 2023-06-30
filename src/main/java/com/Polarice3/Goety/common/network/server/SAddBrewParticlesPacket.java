package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.common.items.ModItems;
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
import net.minecraft.world.item.ItemStack;
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
                Vec3 vec3 = Vec3.atBottomCenterOf(packet.blockPos);

                for(int l = 0; l < 8; ++l) {
                    clientWorld.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ModItems.SPLASH_BREW.get())), vec3.x, vec3.y, vec3.z, clientWorld.random.nextGaussian() * 0.15D, clientWorld.random.nextDouble() * 0.2D, clientWorld.random.nextGaussian() * 0.15D);
                }

                float f0 = (float)(packet.color >> 16 & 255) / 255.0F;
                float f1 = (float)(packet.color >> 8 & 255) / 255.0F;
                float f2 = (float)(packet.color & 255) / 255.0F;
                ParticleOptions particleoptions = packet.instant ? ParticleTypes.INSTANT_EFFECT : ParticleTypes.EFFECT;

                for(int j3 = 0; j3 < 100 * (BrewUtils.getAreaOfEffect(packet.itemStack) + 1); ++j3) {
                    float f3 = clientWorld.random.nextFloat() * area;
                    float f4 = clientWorld.random.nextFloat() * (float)(Math.PI * (area / 2.0F));
                    double d1 = Math.cos(f4) * f3;
                    double d2 = 0.01D + clientWorld.random.nextDouble() * 0.5D + (area / 10.0D);
                    double d3 = Math.sin(f4) * f3;
                    Particle particle = ParticleUtil.addParticleInternal(particleoptions, particleoptions.getType().getOverrideLimiter(), vec3.x + d1 * 0.1D, vec3.y + 0.3D, vec3.z + d3 * 0.1D, d1, d2, d3);
                    if (particle != null) {
                        float f5 = 0.75F + clientWorld.random.nextFloat() * 0.25F;
                        particle.setColor(f0 * f5, f1 * f5, f2 * f5);
                        particle.setPower((float)f3);
                    }
                }

                clientWorld.playLocalSound(packet.blockPos.getX(), packet.blockPos.getY(), packet.blockPos.getZ(), SoundEvents.SPLASH_POTION_BREAK, SoundSource.NEUTRAL, 1.0F, clientWorld.random.nextFloat() * 0.1F + 0.9F, false);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
