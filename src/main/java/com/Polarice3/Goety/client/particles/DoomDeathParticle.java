package com.Polarice3.Goety.client.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class DoomDeathParticle extends TextureSheetParticle {

    DoomDeathParticle(ClientLevel p_105733_, double p_105734_, double p_105735_, double p_105736_, double p_105737_, double p_105738_, double p_105739_) {
        super(p_105733_, p_105734_, p_105735_, p_105736_);
        this.gravity = -0.25F;
        this.friction = 0.85F;
        this.setSize(0.02F, 0.02F);
        this.quadSize = 0.5F;
        this.xd = 0.0D;
        this.yd = 0.1D;
        this.zd = 0.0D;
        this.lifetime = (int)(12.0D / (Math.random() * 0.8D + 0.2D));
    }

    public void render(VertexConsumer p_233985_, Camera p_233986_, float p_233987_) {
        super.render(p_233985_, p_233986_, p_233987_);
        this.alpha = 1.0F - Mth.clamp(((float) this.age + p_233987_) / (float) this.lifetime, 0.0F, 1.0F);
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public int getLightColor(float p_106821_) {
        return 255;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet p_105753_) {
            this.sprite = p_105753_;
        }

        public Particle createParticle(SimpleParticleType p_105764_, ClientLevel p_105765_, double p_105766_, double p_105767_, double p_105768_, double p_105769_, double p_105770_, double p_105771_) {
            DoomDeathParticle particle = new DoomDeathParticle(p_105765_, p_105766_, p_105767_, p_105768_, p_105769_, p_105770_, p_105771_);
            particle.pickSprite(this.sprite);
            return particle;
        }
    }
}
