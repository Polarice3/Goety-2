package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.MathHelper;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;

public class RisingCircleParticle extends GroundCircleParticle {
    private int delay;

    public RisingCircleParticle(ClientLevel p_233976_, double p_233977_, double p_233978_, double p_233979_, int p_233980_) {
        super(p_233976_, p_233977_, p_233978_, p_233979_, 0.0D, 0.0D, 0.0D);
        this.quadSize = 1.0F;
        this.delay = p_233980_;
        this.lifetime = 15;
        this.gravity = 0.0F;
        this.xd = 0.0D;
        this.yd = 0.25D;
        this.zd = 0.0D;
    }

    public void render(VertexConsumer p_233985_, Camera p_233986_, float p_233987_) {
        if (this.delay <= 0) {
            this.renderRotatedParticle(p_233985_, p_233986_, p_233987_, (p_234005_) -> {
                p_234005_.mul(Vector3f.YP.rotation(0.0F));
                p_234005_.mul(Vector3f.XP.rotation(-MathHelper.modelDegrees(90)));
            });
            this.renderRotatedParticle(p_233985_, p_233986_, p_233987_, (p_234000_) -> {
                p_234000_.mul(Vector3f.YP.rotation(-(float)Math.PI));
                p_234000_.mul(Vector3f.XP.rotation(MathHelper.modelDegrees(90)));
            });
        }
    }

    public int getLightColor(float p_233983_)  {
        return LightTexture.FULL_BRIGHT;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void tick() {
        if (this.delay > 0) {
            --this.delay;
        } else {
            super.tick();
        }
    }

    public static class Provider implements ParticleProvider<RisingCircleParticleOption> {
        private final SpriteSet sprite;

        public Provider(SpriteSet p_234008_) {
            this.sprite = p_234008_;
        }

        public Particle createParticle(RisingCircleParticleOption p_234019_, ClientLevel p_234020_, double p_234021_, double p_234022_, double p_234023_, double p_234024_, double p_234025_, double p_234026_) {
            RisingCircleParticle shriekparticle = new RisingCircleParticle(p_234020_, p_234021_, p_234022_, p_234023_, p_234019_.getDelay());
            shriekparticle.pickSprite(this.sprite);
            shriekparticle.setAlpha(1.0F);
            return shriekparticle;
        }
    }
}
