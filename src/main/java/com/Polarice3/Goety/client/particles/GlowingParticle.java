package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GlowingParticle extends RisingParticle {
    private GlowingParticle(ClientLevel p_i232392_1_, double p_i232392_2_, double p_i232392_4_, double p_i232392_6_, double p_i232392_8_, double p_i232392_10_, double p_i232392_12_) {
        super(p_i232392_1_, p_i232392_2_, p_i232392_4_, p_i232392_6_, p_i232392_8_, p_i232392_10_, p_i232392_12_);
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public void move(double pX, double pY, double pZ) {
        this.setBoundingBox(this.getBoundingBox().move(pX, pY, pZ));
        this.setLocationFromBoundingbox();
    }

    public float getQuadSize(float pScaleFactor) {
        float f = ((float)this.age + pScaleFactor) / (float)this.lifetime;
        return this.quadSize * (1.0F - f * f * 0.5F);
    }

    public int getLightColor(float pPartialTick) {
        int i = super.getLightColor(pPartialTick);
        int k = i >> 16 & 255;
        return 240 | k << 16;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            GlowingParticle flameparticle = new GlowingParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
            flameparticle.pickSprite(this.sprite);
            return flameparticle;
        }
    }
}
