package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class EndFireSparkParticle extends TextureSheetParticle {
    private static final float DRAG = 1.0F - 3.0F / 20.0F;
    private static final float R0 = 0.59F, G0 = 1.0F, B0 = 0.18F;
    private static final float R1 = 0.20F, G1 = 0.81F, B1 = 0.36F;

    private final float baseSize;
    private final double orbitRadius;
    private final float orbitRate;
    private float orbitAngle;
    private final double orbitY;
    private double lastOrbitX;
    private double lastOrbitZ;

    protected EndFireSparkParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites) {
        super(level, x, y, z);
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.friction = DRAG;
        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.lifetime = 30 + this.random.nextInt(11);
        this.baseSize = (0.05F + this.random.nextFloat() * 0.05F) / 2;
        this.quadSize = this.baseSize;
        double ox = (this.random.nextDouble() * 2.0D - 1.0D) * 0.10D;
        double oz = (this.random.nextDouble() * 2.0D - 1.0D) * 0.10D;
        this.orbitRadius = Math.sqrt(ox * ox + oz * oz);
        this.orbitAngle = (float) Mth.atan2(oz, ox);
        this.orbitRate = this.random.nextFloat() * (float) (Math.PI * 2.0D) / 20.0F;
        this.orbitY = (this.random.nextDouble() * 2.0D - 1.0D) * 0.10D;
        this.lastOrbitX = Mth.cos(this.orbitAngle) * this.orbitRadius;
        this.lastOrbitZ = Mth.sin(this.orbitAngle) * this.orbitRadius;
        this.setPos(x + this.lastOrbitX, y + this.orbitY, z + this.lastOrbitZ);
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.pickSprite(sprites);
        this.setColor(R0, G0, B0);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isAlive()) {
            return;
        }
        this.orbitAngle += this.orbitRate;
        double ox = Mth.cos(this.orbitAngle) * this.orbitRadius;
        double oz = Mth.sin(this.orbitAngle) * this.orbitRadius;
        this.setPos(this.x + ox - this.lastOrbitX, this.y, this.z + oz - this.lastOrbitZ);
        this.lastOrbitX = ox;
        this.lastOrbitZ = oz;
        float t = (float) this.age / (float) this.lifetime;
        this.setColor(Mth.lerp(t, R0, R1), Mth.lerp(t, G0, G1), Mth.lerp(t, B0, B1));
    }

    @Override
    public float getQuadSize(float partialTicks) {
        float t = Mth.clamp((this.age + partialTicks) / (float) this.lifetime, 0.0F, 1.0F);
        return this.baseSize * Mth.lerp(t, 1.0F, 0.5F);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new EndFireSparkParticle(level, x, y, z, xd, yd, zd, this.sprites);
        }
    }
}
