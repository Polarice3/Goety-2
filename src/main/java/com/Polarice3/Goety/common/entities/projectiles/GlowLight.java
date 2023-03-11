package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.entities.ModEntityType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.NetworkHooks;

public class GlowLight extends LightProjectile {

    public GlowLight(EntityType<? extends GlowLight> p_i50147_1_, Level p_i50147_2_) {
        super(p_i50147_1_, p_i50147_2_);
    }

    public GlowLight(final Level Level, final double x, final double y, final double z) {
        super(Level, x, y, z);
    }

    public GlowLight(final Level Level, final LivingEntity shooter) {
        super(Level, shooter);
    }

    @Override
    public ParticleOptions sourceParticle() {
        return ModParticleTypes.GLOW_LIGHT_EFFECT.get();
    }

    @Override
    public ParticleOptions trailParticle() {
        return ModParticleTypes.GLOW_EFFECT.get();
    }

    @Override
    public Block LightBlock() {
        return ModBlocks.GLOW_LIGHT_BLOCK.get();
    }

    @Override
    public EntityType<?> getType() {
        return ModEntityType.GLOW_LIGHT.get();
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
