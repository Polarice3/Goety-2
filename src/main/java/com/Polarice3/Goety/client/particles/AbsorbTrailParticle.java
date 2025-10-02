package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.ColorUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class AbsorbTrailParticle extends TextureSheetParticle {
    private final Vec3 target;

    public AbsorbTrailParticle(ClientLevel clientLevel, double x, double y, double z, double xd, double yd, double zd, Vec3 vec3, int color) {
        super(clientLevel, x, y, z, xd, yd, zd);
        color = ColorUtil.ARGB.scaleRGB(color, 0.875F + this.random.nextFloat() * 0.25F, 0.875F + this.random.nextFloat() * 0.25F, 0.875F + this.random.nextFloat() * 0.25F);
        this.rCol = (float)ColorUtil.ARGB.red(color) / 255.0F;
        this.gCol = (float)ColorUtil.ARGB.green(color) / 255.0F;
        this.bCol = (float)ColorUtil.ARGB.blue(color) / 255.0F;
        this.quadSize = 0.26F;
        this.target = vec3;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            int i = this.lifetime - this.age;
            double d = 1.0 / (double)i;
            this.x = Mth.lerp(d, this.x, this.target.x());
            this.y = Mth.lerp(d, this.y, this.target.y());
            this.z = Mth.lerp(d, this.z, this.target.z());
        }
    }

    @Override
    public int getLightColor(float f) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public boolean shouldCull() {
        return false;
    }

    public static class Provider implements ParticleProvider<AbsorbTrailParticleOption> {
        private final SpriteSet sprite;

        public Provider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        public Particle createParticle(AbsorbTrailParticleOption trailParticleOption, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            AbsorbTrailParticle trailParticle = new AbsorbTrailParticle(clientLevel, d, e, f, g, h, i, trailParticleOption.target(), trailParticleOption.color());
            trailParticle.pickSprite(this.sprite);
            trailParticle.setLifetime(trailParticleOption.duration());
            return trailParticle;
        }
    }
}
