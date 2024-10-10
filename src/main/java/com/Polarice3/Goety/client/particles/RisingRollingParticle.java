package com.Polarice3.Goety.client.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class RisingRollingParticle extends RisingParticle {
    private final SpriteSet sprites;
    private final float rotSpeed;

    protected RisingRollingParticle(ClientLevel p_106800_, double p_106801_, double p_106802_, double p_106803_, SpriteSet p_107724_) {
        super(p_106800_, p_106801_, p_106802_, p_106803_, 0.0D, 0.25D, 0.0D);
        this.hasPhysics = false;
        this.scale(1.0F);
        this.lifetime = 20;
        this.sprites = p_107724_;
        this.setSpriteFromAge(p_107724_);
        this.rotSpeed = 0.05F;
        this.roll = (float)Math.random() * ((float)Math.PI * 2F);
    }

    public int getLightColor(final float partialTicks) {
        return 240;
    }

    public void render(VertexConsumer p_233985_, Camera p_233986_, float p_233987_) {
        this.alpha = 1.0F - Mth.clamp(((float) this.age + p_233987_) / (float) this.lifetime, 0.0F, 1.0F);
        super.render(p_233985_, p_233986_, p_233987_);
    }

    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
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
            Particle particle = new RisingRollingParticle(p_107422_, p_107423_, p_107424_, p_107425_, this.sprite);
            particle.setColor((float) p_107426_, (float) p_107427_, (float) p_107428_);
            return particle;
        }
    }

}
