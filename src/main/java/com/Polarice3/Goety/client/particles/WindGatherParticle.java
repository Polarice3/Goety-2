package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.Goety;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class WindGatherParticle extends WindTrailParticle {
    private static final ResourceLocation TEXTURE = Goety.location("textures/particle/gathering_trail.png");

    private final Entity owner;
    private final int width;
    private final float height;
    private float yaw, radius;
    private final float yawSpeed, radiusSpeed, minRadius;

    public WindGatherParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd, float red, float green, float blue, int width, float height, int life, int ownerId) {
        super(world, x, y, z, xd, yd, zd, red, green, blue);
        this.width = width;
        this.height = height;
        this.lifetime = life;
        this.owner = this.level.getEntity(ownerId);
        if (this.owner != null) {
            this.yaw = (float) (Mth.atan2(x - this.owner.getX(), z - this.owner.getZ()));
            this.radius = (float) new Vec3(x - this.owner.getX(), 0, z - this.owner.getZ()).length();
        }
        this.yawSpeed = (this.random.nextBoolean() ? 1 : -1) * (0.15F + this.random.nextFloat() * 0.1F);
        this.radiusSpeed = this.radius / 20 * (0.8F + this.random.nextFloat() * 0.4F);
        this.minRadius = 0.75F + this.random.nextFloat() * 0.2F;
    }

    @Override
    public void tick() {
        if (this.age < this.lifetime - (sampleSize() - 1) * sampleStep()) {
            this.yaw += this.yawSpeed;
            this.radius -= this.radiusSpeed;
            if (this.radius < this.minRadius) {
                this.radius = this.minRadius;
            }
            if (this.owner != null) {
                Vec3 pos = this.owner.position().add(this.radius * Math.cos(this.yaw), 0, this.radius * Math.sin(this.yaw));
                this.x = pos.x();
                this.y = this.owner.getY() + (Math.sin(this.age * 0.2) + 1) * 0.5 * this.owner.getBbHeight();
                this.z = pos.z();
            }
        }
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
        this.trail();
    }

    @Override
    public ResourceLocation getTexture() {
        return TEXTURE;
    }

    @Override
    public float getTrailHeight() {
        return this.height;
    }

    @Override
    public int sampleSize() {
        return this.width;
    }

    @Override
    public int getLightColor(float pPartialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    public static class Provider implements ParticleProvider<WindGatherParticleOption> {
        public Provider(SpriteSet p_172490_) {
        }

        public Particle createParticle(WindGatherParticleOption typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new WindGatherParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getRed(), typeIn.getGreen(), typeIn.getBlue(), typeIn.getWidth(), typeIn.getHeight(), typeIn.getLife(), typeIn.getOwnerId());
        }
    }
}
