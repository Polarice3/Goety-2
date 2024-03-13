package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;

public class PulsatingCircleParticle extends GroundCircleParticle {
    private SpriteSet spriteSet;
    public float radiusAmount = 0.1F;

    PulsatingCircleParticle(ClientLevel p_233976_, double p_233977_, double p_233978_, double p_233979_, SpriteSet spriteSet) {
        super(p_233976_, p_233977_, p_233978_, p_233979_, 0.0D, 0.0D, 0.0D);
        this.quadSize = 2.0F;
        this.lifetime = 30;
        this.gravity = 0.0F;
        this.xd = 0.0D;
        this.yd = 0.0D;
        this.zd = 0.0D;
        this.spriteSet = spriteSet;
        this.setSprite(spriteSet.get(this.random));
    }

    public float getQuadSize(float p_234003_) {
        return this.quadSize;
    }

    public int getLightColor(float p_233983_) {
        return LightTexture.FULL_BRIGHT;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.radiusPulse();
        }
    }

    private void radiusPulse(){
        this.quadSize = Mth.clamp(this.quadSize + this.radiusAmount, 1.0F, 1.5F);
        if (this.quadSize == 1.0F || this.quadSize == 1.5F) {
            this.setSprite(this.spriteSet.get(this.random));
            this.radiusAmount *= -1;
        }
    }

    public static class Provider implements ParticleProvider<PulsatingCircleParticleOption> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet p_234008_) {
            this.spriteSet = p_234008_;
        }

        public Particle createParticle(PulsatingCircleParticleOption p_234019_, ClientLevel p_234020_, double p_234021_, double p_234022_, double p_234023_, double p_234024_, double p_234025_, double p_234026_) {
            PulsatingCircleParticle particle = new PulsatingCircleParticle(p_234020_, p_234021_, p_234022_, p_234023_, this.spriteSet);
            particle.quadSize = p_234019_.size();
            return particle;
        }
    }
}
