package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;

public class WaterJetParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    public WaterJetParticle(ClientLevel level, double xCoord, double yCoord, double zCoord, SpriteSet spriteSet, double xd, double yd, double zd) {
        super(level, xCoord, yCoord, zCoord, xd, yd, zd);
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize *= 1.5F;
        this.lifetime = 5 + (int) (level.getRandom().nextDouble() * 10);
        this.sprites = spriteSet;
        this.gravity = 0.5F;
        this.setSpriteFromAge(spriteSet);
        this.rCol = 1.0F;
        this.gCol = 1.0F;
        this.bCol = 1.0F;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
        if (this.onGround) {
            if (this.level.getRandom().nextFloat() <= 0.25F) {
                this.level.addParticle(ParticleTypes.RAIN, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
            }
            this.remove();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new WaterJetParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}
