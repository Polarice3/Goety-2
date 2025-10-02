package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.MathHelper;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class ConnectionParticle extends TextureSheetParticle {
    private final double xStart;
    private final double yStart;
    private final double zStart;
    private final DynamicAlpha dynamicAlpha = new DynamicAlpha(0.0F, 0.6F, 0.25F, 1.0F);

    public ConnectionParticle(ClientLevel p_106464_, double p_106465_, double p_106466_, double p_106467_, double p_106468_, double p_106469_, double p_106470_) {
        super(p_106464_, p_106465_, p_106466_, p_106467_);
        this.xd = p_106468_;
        this.yd = p_106469_;
        this.zd = p_106470_;
        this.xStart = p_106465_;
        this.yStart = p_106466_;
        this.zStart = p_106467_;
        this.xo = p_106465_ + p_106468_;
        this.yo = p_106466_ + p_106469_;
        this.zo = p_106467_ + p_106470_;
        this.x = this.xo;
        this.y = this.yo;
        this.z = this.zo;
        this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.2F);
        float f = this.random.nextFloat() * 0.6F + 0.4F;
        this.rCol = 0.9F * f;
        this.gCol = 0.9F * f;
        this.bCol = f;
        this.hasPhysics = false;
        this.lifetime = (int)(Math.random() * 10.0D) + 30;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public void move(double p_106482_, double p_106483_, double p_106484_) {
        this.setBoundingBox(this.getBoundingBox().move(p_106482_, p_106483_, p_106484_));
        this.setLocationFromBoundingbox();
    }

    public int getLightColor(float p_106486_) {
        return 240;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            float f = (float)this.age / (float)this.lifetime;
            f = 1.0F - f;
            float f1 = 1.0F - f;
            f1 *= f1;
            f1 *= f1;
            this.x = this.xStart + this.xd * (double)f;
            this.y = this.yStart + this.yd * (double)f - (double)(f1 * 1.2F);
            this.z = this.zStart + this.zd * (double)f;
            this.setPos(this.x, this.y, this.z); // FORGE: update the particle's bounding box
        }
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        this.setAlpha(this.dynamicAlpha.getAlpha(this.age, this.lifetime, tickDelta));
        super.render(vertexConsumer, camera, tickDelta);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
            ConnectionParticle connectionParticle = new ConnectionParticle(clientWorld, d, e, f, g, h, i);
            connectionParticle.scale(1.5F);
            connectionParticle.pickSprite(this.spriteSet);
            return connectionParticle;
        }
    }

    public record DynamicAlpha(float startAlpha, float endAlpha, float startAtNormalizedAge, float endAtNormalizedAge) {
        public static final DynamicAlpha OPAQUE = new DynamicAlpha(1.0F, 1.0F, 0.0F, 1.0F);

        public boolean isOpaque() {
            return this.startAlpha >= 1.0F && this.endAlpha >= 1.0F;
        }

        public float getAlpha(int age, int maxAge, float tickDelta) {
            if (MathHelper.approximatelyEquals(this.startAlpha, this.endAlpha)) {
                return this.startAlpha;
            } else {
                float f = MathHelper.getLerpProgress((age + tickDelta) / maxAge, this.startAtNormalizedAge, this.endAtNormalizedAge);
                return MathHelper.clampedLerp(this.startAlpha, this.endAlpha, f);
            }
        }
    }
}
