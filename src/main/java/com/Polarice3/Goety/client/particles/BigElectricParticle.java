package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class BigElectricParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    public BigElectricParticle(ClientLevel p_172136_, double p_172137_, double p_172138_, double p_172139_, double p_172140_, double p_172141_, double p_172142_, SpriteSet p_172143_) {
        super(p_172136_, p_172137_, p_172138_, p_172139_, p_172140_, p_172141_, p_172142_);
        this.friction = 0.96F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = p_172143_;
        this.quadSize *= 2.0F;
        this.hasPhysics = false;
        this.setSpriteFromAge(p_172143_);
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public int getLightColor(float p_172146_) {
        float f = ((float)this.age + p_172146_) / (float)this.lifetime;
        f = Mth.clamp(f, 0.0F, 1.0F);
        int i = super.getLightColor(p_172146_);
        int j = i & 255;
        int k = i >> 16 & 255;
        j += (int)(f * 15.0F * 16.0F);
        if (j > 240) {
            j = 240;
        }

        return j | k << 16;
    }

    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet p_172151_) {
            this.sprite = p_172151_;
        }

        public Particle createParticle(SimpleParticleType p_172162_, ClientLevel p_172163_, double p_172164_, double p_172165_, double p_172166_, double p_172167_, double p_172168_, double p_172169_) {
            BigElectricParticle particle = new BigElectricParticle(p_172163_, p_172164_, p_172165_, p_172166_, 0.0D, 0.0D, 0.0D, this.sprite);
            particle.setColor(1.0F, 0.9F, 1.0F);
            particle.setParticleSpeed(p_172167_ * 0.25D, p_172168_ * 0.25D, p_172169_ * 0.25D);
            particle.setLifetime(p_172163_.random.nextInt(2) + 2);
            return particle;
        }
    }
}
