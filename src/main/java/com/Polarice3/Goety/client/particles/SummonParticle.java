package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class SummonParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected SummonParticle(ClientLevel clientLevel, double x, double y, double z, double xd, double yd, double zd, float size, SpriteSet spriteSet) {
        super(clientLevel, x, y, z, 0.0, 0.0, 0.0);
        this.sprites = spriteSet;
        this.friction = 0.96F;
        this.gravity = -0.1F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.xd *= 0.0;
        this.yd *= 0.9;
        this.zd *= 0.0;
        this.xd += xd;
        this.yd += yd;
        this.zd += zd;
        this.quadSize *= 0.75F * size;
        this.lifetime = (int)(8.0F / Mth.randomBetween(this.random, 0.5F, 1.0F) * size);
        this.lifetime = Math.max(this.lifetime, 1);
        this.setSpriteFromAge(spriteSet);
        this.hasPhysics = true;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getLightColor(float f) {
        return 240;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public float getQuadSize(float f) {
        return this.quadSize * Mth.clamp(((float)this.age + f) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            return new SummonParticle(clientLevel, d, e, f, g, h, i, 1.5F, this.sprites);
        }
    }
}
