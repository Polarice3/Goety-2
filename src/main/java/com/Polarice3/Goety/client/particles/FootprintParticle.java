package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class FootprintParticle extends GroundCircleParticle {
    public SpriteSet spriteSet;
    protected float initAlpha;

    public FootprintParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ, 0.0D, 0.0D, 0.0D);
        this.setAlpha(1.0F);
        this.setColor((float) pXSpeed, (float) pYSpeed, (float) pZSpeed);
        this.quadSize = 0.5F;
        this.lifetime = 20;
        this.gravity = 0.0F;
        this.xd = 0.0D;
        this.yd = 0.0D;
        this.zd = 0.0D;
        this.spriteSet = pSprites;
        this.setSprite(spriteSet.get(this.random));
    }

    @Override
    protected void setAlpha(float p_107272_) {
        super.setAlpha(p_107272_);
        this.initAlpha = p_107272_;
    }

    @Override
    public void tick() {
        if (this.age > this.lifetime / 2) {
            this.alpha -= this.initAlpha / this.lifetime * 2.0F;
        }

        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet p_106827_) {
            this.sprite = p_106827_;
        }

        public Particle createParticle(SimpleParticleType p_106838_, ClientLevel p_106839_, double p_106840_, double p_106841_, double p_106842_, double p_106843_, double p_106844_, double p_106845_) {
            return new FootprintParticle(p_106839_, p_106840_, p_106841_, p_106842_, p_106843_, p_106844_, p_106845_, this.sprite);
        }
    }
}
