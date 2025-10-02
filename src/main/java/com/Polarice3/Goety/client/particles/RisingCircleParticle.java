package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.MathHelper;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

public class RisingCircleParticle extends GroundCircleParticle {
    private int delay;

    public RisingCircleParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, int delay) {
        super(pLevel, pX, pY, pZ, 0.0D, 0.0D, 0.0D);
        this.quadSize = 1.0F;
        this.delay = delay;
        this.lifetime = 15;
        this.gravity = 0.0F;
        this.xd = 0.0D;
        this.yd = 0.25D;
        this.zd = 0.0D;
        this.rCol = (float) pXSpeed;
        this.gCol = (float) pYSpeed;
        this.bCol = (float) pZSpeed;
    }

    public void render(VertexConsumer p_233985_, Camera p_233986_, float p_233987_) {
        if (this.delay <= 0) {
            this.alpha = 1.0F - Mth.clamp(((float)this.age + p_233987_) / (float)this.lifetime, 0.0F, 1.0F);
            this.renderRotatedParticle(p_233985_, p_233986_, p_233987_, (p_234005_) -> {
                p_234005_.mul(new Quaternionf()).rotationX(-MathHelper.modelDegrees(90));
            });
            this.renderRotatedParticle(p_233985_, p_233986_, p_233987_, (p_234000_) -> {
                p_234000_.mul((new Quaternionf()).rotationYXZ(-(float)Math.PI, MathHelper.modelDegrees(90), 0.0F));
            });
        }
    }

    public int getLightColor(float p_233983_) {
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

        public Particle createParticle(RisingCircleParticleOption pOption, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            RisingCircleParticle shriekparticle = new RisingCircleParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pOption.getDelay());
            shriekparticle.pickSprite(this.sprite);
            shriekparticle.setAlpha(1.0F);
            return shriekparticle;
        }
    }
}
