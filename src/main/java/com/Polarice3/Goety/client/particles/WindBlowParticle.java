package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;

public class WindBlowParticle extends WindTrailParticle {
    public final int width;
    public final float height;

    public WindBlowParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd, float red, float green, float blue, int width, float height, int life) {
        super(world, x, y, z, 0, 0, 0, red, green, blue);
        this.gravity = 0.0F;
        this.xd *= (double)0.1F;
        this.yd *= (double)0.1F;
        this.zd *= (double)0.1F;
        this.xd += xd;
        this.yd += yd;
        this.zd += zd;
        if (life <= 0){
            this.lifetime = 20 + this.random.nextInt(20);
        } else {
            this.lifetime = life;
        }
        this.width = width;
        this.height = height;
    }

    public void tick() {
        super.tick();
        this.trailA = 1.0F - (float) this.age / (float) this.lifetime;
    }

    public float getTrailHeight() {
        return this.height;
    }

    public int sampleSize() {
        return this.width;
    }

    public int getLightColor(float pPartialTick) {
        return LightTexture.FULL_BLOCK;
    }

    public static class Provider implements ParticleProvider<WindBlowParticleOption> {

        public Provider(SpriteSet p_172490_) {
        }

        public Particle createParticle(WindBlowParticleOption typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new WindBlowParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getRed(), typeIn.getGreen(), typeIn.getBlue(), typeIn.getWidth(), typeIn.getHeight(), typeIn.getLife());
        }
    }
}
