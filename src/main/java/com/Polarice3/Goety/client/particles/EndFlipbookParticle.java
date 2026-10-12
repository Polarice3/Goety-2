package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class EndFlipbookParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final int frames;
    private final boolean masked;

    protected EndFlipbookParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites, int frames, boolean upsideDown, boolean masked) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.frames = frames;
        this.masked = masked;
        this.xd = 0.0D;
        this.yd = 0.0D;
        this.zd = 0.0D;
        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.lifetime = 20;
        this.quadSize = 0.4F;
        if (upsideDown) {
            this.roll = Mth.PI;
            this.oRoll = Mth.PI;
        }
        this.updateFrame();
    }

    private void updateFrame() {
        if (this.sprites != null) {
            this.setSprite(this.sprites.get(this.age % this.frames, this.frames - 1));
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.updateFrame();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return this.masked ? ParticleRenderType.PARTICLE_SHEET_OPAQUE : ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        private final int frames;
        private final boolean upsideDown;
        private final boolean masked;

        private Provider(SpriteSet sprites, int frames, boolean upsideDown, boolean masked) {
            this.sprites = sprites;
            this.frames = frames;
            this.upsideDown = upsideDown;
            this.masked = masked;
        }

        public static Provider Fire(SpriteSet sprites) {
            return new Provider(sprites, 10, false, false);
        }

        public static Provider FireUpsideDown(SpriteSet sprites) {
            return new Provider(sprites, 10, true, false);
        }

        public static Provider Orb(SpriteSet sprites) {
            return new Provider(sprites, 20, false, true);
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new EndFlipbookParticle(level, x, y, z, this.sprites, this.frames, this.upsideDown, this.masked);
        }
    }
}
