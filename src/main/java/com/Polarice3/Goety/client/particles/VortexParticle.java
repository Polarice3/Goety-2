package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.client.render.ModRenderType;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;

public abstract class VortexParticle extends TextureSheetParticle {
    protected static final float DRAG = (float) Math.exp(-2.0D / 20.0D);
    protected final double centerX;
    protected final double centerZ;
    protected final float swirl;
    protected final float tintR;
    protected final float tintG;
    protected final float tintB;

    protected VortexParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, double centerX, double centerZ, float swirl, float tintR, float tintG, float tintB, int lifetime) {
        super(level, x, y, z);
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.swirl = swirl;
        this.tintR = tintR;
        this.tintG = tintG;
        this.tintB = tintB;
        this.lifetime = lifetime;
        this.hasPhysics = false;
        this.gravity = 0.0F;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ModRenderType.PARTICLE_ADDITIVE;
    }

    @Override
    protected int getLightColor(float partialTicks) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        double dx = this.x - this.centerX;
        double dz = this.z - this.centerZ;
        double radius = Math.sqrt(dx * dx + dz * dz);
        if (radius > 1.0E-4D) {
            this.xd += -dz / radius * this.swirl;
            this.zd += dx / radius * this.swirl;
        }
        this.xd *= DRAG;
        this.yd *= DRAG;
        this.zd *= DRAG;
        this.move(this.xd, this.yd, this.zd);
        this.updateLook((float) this.age / (float) this.lifetime);
    }

    protected abstract void updateLook(float t);

    protected void applyHeat(float heat, float alpha) {
        float white = 0.5F * heat * heat * heat;
        float brightness = 0.35F + 0.65F * heat;
        this.rCol = Mth.lerp(white, this.tintR, 1.0F) * brightness;
        this.gCol = Mth.lerp(white, this.tintG, 1.0F) * brightness;
        this.bCol = Mth.lerp(white, this.tintB, 1.0F) * brightness;
        this.alpha = alpha;
    }
}
