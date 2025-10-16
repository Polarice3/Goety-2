package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class MudGasParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;

    protected MudGasParticle(ClientLevel world, double x, double y, double z, SpriteSet spriteSet) {
        super(world, x, y, z);
        this.spriteSet = spriteSet;
        this.scale(world.getRandom().nextIntBetweenInclusive(1, 4) + world.getRandom().nextFloat());
        this.setSize(0.25F, 0.25F);
        this.lifetime = 78; //Equation: (Total Frames * Interval) - 2
        this.gravity = 3.0E-6F;
        this.xd = 0.0F;
        this.yd = 0.01F;
        this.zd = 0.0F;
        this.setSpriteFromAge(spriteSet);
    }

    public void tick() {
        super.tick();
        if (!this.removed) {
            this.setSprite(this.spriteSet.get((this.age / 10) % 8 + 1, 8));
        }
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet p_105899_) {
            this.sprites = p_105899_;
        }

        public Particle createParticle(SimpleParticleType p_105910_, ClientLevel p_105911_, double p_105912_, double p_105913_, double p_105914_, double p_105915_, double p_105916_, double p_105917_) {
            return new MudGasParticle(p_105911_, p_105912_, p_105913_, p_105914_, this.sprites);
        }
    }
}
