package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class GatherSquareParticle extends TextureSheetParticle {
    public final Vec3 fromPos;
    public final Vec3 toPos;

    protected GatherSquareParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd, float red, float green, float blue, Vec3 end, SpriteSet spriteSet) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.fromPos = new Vec3(x, y, z);
        this.toPos = end;
        this.xo = this.fromPos.x();
        this.yo = this.fromPos.y();
        this.zo = this.fromPos.z();
        this.x = this.xo;
        this.y = this.yo;
        this.z = this.zo;
        this.xd = this.toPos.x - this.fromPos.x;
        this.yd = this.toPos.y - this.fromPos.y;
        this.zd = this.toPos.z - this.fromPos.z;
        this.quadSize = this.random.nextFloat() * 0.19F + 0.01F;
        this.rCol = red;
        this.gCol = green;
        this.bCol = blue;
        this.lifetime = Math.round(this.random.nextFloat() * 3.0F) + 5;
        this.pickSprite(spriteSet);
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            double speed = 0.25D;
            this.move(this.xd * speed, this.yd * speed, this.zd * speed);

            if (this.getPos().distanceTo(this.toPos) <= 0.5D) {
                this.remove();
            }
        }
    }

    public int getLightColor(float p_233983_) {
        return LightTexture.FULL_BRIGHT;
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

    public @NotNull Vec3 getPos() {
        return new Vec3(this.x, this.y, this.z);
    }

    public static class Provider implements ParticleProvider<GatherSquareParticleOption> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(GatherSquareParticleOption pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new GatherSquareParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pType.getRed(), pType.getGreen(), pType.getBlue(), new Vec3(pType.getEndX(), pType.getEndY(), pType.getEndZ()), sprites);
        }
    }
}
