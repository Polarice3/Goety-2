package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;

public class StainGroundParticle extends TextureSheetParticle {
    public boolean isGlow;

    public StainGroundParticle(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet spriteSet, double pXSpeed, double pYSpeed, double pZSpeed, boolean isGlow) {
        super(pLevel, pX, pY, pZ, 0.0F, 0.0F, 0.0F);
        this.xd = 0.0F;
        this.yd = 0.0F;
        this.zd = 0.0F;
        this.quadSize *= 1.0F;
        this.scale(1.0F + (float) Math.random());
        this.lifetime = 40 + (int) (Math.random() * 41);
        this.gravity = 1.0F;
        this.setSpriteFromAge(spriteSet);
        this.rCol = (float) pXSpeed;
        this.gCol = (float) pYSpeed;
        this.bCol = (float) pZSpeed;
        this.isGlow = isGlow;
    }

    @Override
    protected int getLightColor(float p_107249_) {
        if (this.isGlow) {
            return LightTexture.FULL_BRIGHT;
        }
        return super.getLightColor(p_107249_);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new StainGroundParticle(level, x, y, z, this.sprites, xd, yd, zd, false);
        }
    }

    public static class GlowProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public GlowProvider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new StainGroundParticle(level, x, y, z, this.sprites, xd, yd, zd, true);
        }
    }
}
