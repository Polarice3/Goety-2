package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class EndGlowParticle extends TextureSheetParticle {
    private static final float START_SIZE = 0.6F;
    private static final float END_SIZE = 0.75F;

    protected EndGlowParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z);
        this.xd = 0.0D;
        this.yd = 0.0D;
        this.zd = 0.0D;
        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.lifetime = 40;
        this.quadSize = START_SIZE;
        this.setColor(0.69F, 0.94F, 0.0F);
        this.alpha = 0.4F;
        this.pickSprite(sprites);
    }

    @Override
    public float getQuadSize(float partialTicks) {
        float t = Mth.clamp((this.age + partialTicks) / (float) this.lifetime, 0.0F, 1.0F);
        return Mth.lerp(t, START_SIZE, END_SIZE);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    public static class HaloProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public HaloProvider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new EndGlowParticle(level, x, y, z, this.sprites);
        }
    }
}
