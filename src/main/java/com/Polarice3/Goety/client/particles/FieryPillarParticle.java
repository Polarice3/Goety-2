package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class FieryPillarParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    public FieryPillarParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ);
        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;
        this.quadSize = 0.4F + pLevel.random.nextFloat() * 0.25F;
        this.lifetime = 10 + pLevel.random.nextInt(20);
        this.friction = 0.99F;
        this.sprites = pSprites;
        this.setSpriteFromAge(this.sprites);
    }

    public void tick() {
        this.setSpriteFromAge(this.sprites);
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        float f = (float) this.age / this.lifetime;

        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.quadSize = 1.0F + f * 0.5F;
            this.move(this.xd, this.yd, this.zd);
            this.xd *= this.friction;
            this.yd *= this.friction;
            this.zd *= this.friction;
        }

    }

    @Override
    protected int getLightColor(float p_107249_) {
        return LightTexture.FULL_BLOCK;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet p_106884_) {
            this.sprite = p_106884_;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType p_107421_, ClientLevel p_107422_, double p_107423_, double p_107424_, double p_107425_, double p_107426_, double p_107427_, double p_107428_) {
            return new FieryPillarParticle(p_107422_, p_107423_, p_107424_, p_107425_, p_107426_, p_107427_, p_107428_, this.sprite);
        }
    }
}
