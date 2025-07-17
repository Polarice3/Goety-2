package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.CherryParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class FallingLeavesParticle extends CherryParticle {
    public FallingLeavesParticle(ClientLevel p_277612_, double p_278010_, double p_277614_, double p_277673_, SpriteSet p_277465_) {
        super(p_277612_, p_278010_, p_277614_, p_277673_, p_277465_);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet p_106827_) {
            this.sprite = p_106827_;
        }

        public Particle createParticle(SimpleParticleType p_106838_, ClientLevel p_106839_, double p_106840_, double p_106841_, double p_106842_, double p_106843_, double p_106844_, double p_106845_) {
            return new FallingLeavesParticle(p_106839_, p_106840_, p_106841_, p_106842_, this.sprite);
        }
    }
}
