package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class RollingParticle extends TextureSheetParticle {
    private final float rotSpeed;

    protected RollingParticle(ClientLevel p_106610_, double p_106611_, double p_106612_, double p_106613_, double p_106614_, double p_106615_, double p_106616_, SpriteSet p_106617_) {
        super(p_106610_, p_106611_, p_106612_, p_106613_);
        this.xd = p_106614_;
        this.yd = p_106615_;
        this.zd = p_106616_;
        this.hasPhysics = false;
        this.quadSize = 0.5F;
        this.lifetime = 20;
        this.setSpriteFromAge(p_106617_);
        this.rotSpeed = ((float)Math.random() - 0.5F) * 0.1F;
        this.roll = (float)Math.random() * ((float)Math.PI * 2F);
    }

    public float getQuadSize(float p_106860_) {
        return Math.max(0.0F, this.quadSize - (this.quadSize * (this.age + p_106860_)) / this.lifetime);
    }

    public int getLightColor(final float partialTicks) {
        return 240;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (++this.age >= this.lifetime) {
            this.remove();
        }
        this.oRoll = this.roll;
        this.roll += (float)Math.PI * this.rotSpeed * 2.0F;
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
            return new RollingParticle(p_107422_, p_107423_, p_107424_, p_107425_, p_107426_, p_107427_, p_107428_, this.sprite);
        }
    }

    public static class QuickProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public QuickProvider(SpriteSet p_106884_) {
            this.sprite = p_106884_;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType p_107421_, ClientLevel p_107422_, double p_107423_, double p_107424_, double p_107425_, double p_107426_, double p_107427_, double p_107428_) {
            Particle particle =  new RollingParticle(p_107422_, p_107423_, p_107424_, p_107425_, p_107426_, p_107427_, p_107428_, this.sprite);
            particle.setLifetime(5);
            return particle;
        }
    }

    public static class EnchantProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public EnchantProvider(SpriteSet p_106884_) {
            this.sprite = p_106884_;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType p_107421_, ClientLevel p_107422_, double p_107423_, double p_107424_, double p_107425_, double p_107426_, double p_107427_, double p_107428_) {
            RollingParticle particle = new RollingParticle(p_107422_, p_107423_, p_107424_, p_107425_, 0.0D, 0.0D, 0.0D, this.sprite);
            particle.setColor((float) p_107426_, (float) p_107427_, (float) p_107428_);
            particle.pickSprite(this.sprite);
            particle.quadSize = 0.25F;
            return particle;
        }
    }
}
