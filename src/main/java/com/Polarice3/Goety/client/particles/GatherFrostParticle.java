package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.ColorUtil;
import net.minecraft.Util;
import net.minecraft.client.Timer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class GatherFrostParticle extends TextureSheetParticle {
    public Timer timer;
    public final Vec3 origin;
    public final Vec3 end;

    public GatherFrostParticle(ClientLevel world, double x, double y, double z, Vec3 end) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.quadSize *= 0.5F;
        this.origin = new Vec3(x, y, z);
        this.end = end;
        this.xo = this.origin.x();
        this.yo = this.origin.y();
        this.zo = this.origin.z();
        this.x = this.xo;
        this.y = this.yo;
        this.z = this.zo;
        this.xd = this.end.x - this.origin.x;
        this.yd = this.end.y - this.origin.y;
        this.zd = this.end.z - this.origin.z;
        ColorUtil colorUtil = new ColorUtil(11141290);
        this.rCol = colorUtil.red();
        this.gCol = colorUtil.green();
        this.bCol = colorUtil.blue();
        this.hasPhysics = false;
        this.lifetime = (int)(Math.random() * 10.0D) + 30;
        this.timer = new Timer(this.lifetime + 1, 0);
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime || this.getPos().distanceTo(this.end) < 0.25D) {
            this.remove();
        } else {
            this.move(this.xd * 0.05D, this.yd * 0.05D, this.zd * 0.05D);

            this.timer.advanceTime(Util.getMillis());
            float lerp = (this.age + this.timer.partialTick) / this.lifetime;
            int newColor = FastColor.ARGB32.lerp(lerp, 11141290, 16733695);
            ColorUtil colorUtil = new ColorUtil(newColor);
            this.rCol = colorUtil.red();
            this.gCol = colorUtil.green();
            this.bCol = colorUtil.blue();
            this.alpha -= 0.05F;
        }
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void move(double pX, double pY, double pZ) {
        this.setBoundingBox(this.getBoundingBox().move(pX, pY, pZ));
        this.setLocationFromBoundingbox();
    }

    public float getQuadSize(float pScaleFactor) {
        float f = ((float)this.age + pScaleFactor) / (float)this.lifetime;
        return this.quadSize * (1.0F - f * f * 0.5F);
    }

    public int getLightColor(float pPartialTick) {
        float f = ((float)this.age + pPartialTick) / (float)this.lifetime;
        f = Mth.clamp(f, 0.0F, 1.0F);
        int i = super.getLightColor(pPartialTick);
        int j = i & 255;
        int k = i >> 16 & 255;
        j = j + (int)(f * 15.0F * 16.0F);
        if (j > 240) {
            j = 240;
        }

        return j | k << 16;
    }

    public @NotNull Vec3 getPos() {
        return new Vec3(this.x, this.y, this.z);
    }

    public static class Provider implements ParticleProvider<GatherFrostParticleOption> {
        private final SpriteSet sprite;

        public Provider(SpriteSet p_i50607_1_) {
            this.sprite = p_i50607_1_;
        }

        public Particle createParticle(GatherFrostParticleOption pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            GatherFrostParticle particle = new GatherFrostParticle(pLevel, pX, pY, pZ, new Vec3(pType.getEndX(), pType.getEndY(), pType.getEndZ()));
            particle.pickSprite(this.sprite);
            return particle;
        }
    }
}
