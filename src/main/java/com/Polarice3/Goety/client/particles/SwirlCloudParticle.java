package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.client.render.ModRenderType;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class SwirlCloudParticle extends TextureSheetParticle {
    public final boolean reversed;
    public final float rotSpeed;
    public final double radius;
    public final float speed;
    public final int ownerId;
    public final Vec3 center;
    public float time;

    protected SwirlCloudParticle(ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet, SwirlCloudParticleOption option) {
        super(clientLevel, x, y, z);
        this.reversed = option.isReversed();
        this.ownerId = option.getOwnerId();
        this.quadSize = option.getSize();
        this.radius = option.getRadius();
        this.time = option.getTime();
        this.speed = option.getSpeed();
        Vec3 offset = option.getOffset();
        this.center = offset != null && !offset.equals(Vec3.ZERO) ? offset : new Vec3(x, y, z);
        this.rotSpeed = option.getRoll();
        this.lifetime = 40 + this.random.nextInt(60);
        this.hasPhysics = false;
        this.gravity = 0.0F;

        Vec3 start = this.orbitPos(this.time);
        this.setPos(start.x, start.y, start.z);
        this.xo = start.x;
        this.yo = start.y;
        this.zo = start.z;
        this.rCol = (float) xSpeed;
        this.gCol = (float) ySpeed;
        this.bCol = (float) zSpeed;
        this.alpha = 0.0F;
        this.setSpriteFromAge(spriteSet);
    }

    private Vec3 orbitPos(float angle) {
        double a = Math.cos(angle) * this.radius;
        double b = Math.sin(angle) * this.radius;
        return this.reversed ? this.center.add(b, 0.0D, a) : this.center.add(a, 0.0D, b);
    }

    public Entity getEntity() {
        return this.ownerId == -1 ? null : this.level.getEntity(this.ownerId);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.rotSpeed > 0.0F) {
            this.oRoll = this.roll;
        }
        if (this.age++ >= this.lifetime || this.getEntity() == null) {
            this.remove();
            return;
        }

        this.time += this.speed;
        Vec3 pos = this.orbitPos(this.time);
        this.setPos(pos.x, pos.y, pos.z);
        if (this.rotSpeed > 0.0F) {
            this.roll += (float) Math.PI * this.rotSpeed * 2.0F;
        }

        if (this.age >= this.lifetime - 20) {
            this.alpha = Math.max(this.alpha - 0.1F, 0.0F);
        } else if (this.age >= 5) {
            this.alpha = Math.min(this.alpha + 0.01F, 1.0F);
        }
    }

    public ParticleRenderType getRenderType() {
        return ModRenderType.PARTICLE_TRANSLUCENT;
    }

    public int getLightColor(float p_106921_) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public boolean shouldCull() {
        return false;
    }

    public static class Provider implements ParticleProvider<SwirlCloudParticleOption> {
        private final SpriteSet sprite;

        public Provider(SpriteSet p_234008_) {
            this.sprite = p_234008_;
        }

        public Particle createParticle(SwirlCloudParticleOption p_234019_, ClientLevel p_234020_, double p_234021_, double p_234022_, double p_234023_, double p_234024_, double p_234025_, double p_234026_) {
            SwirlCloudParticle particle = new SwirlCloudParticle(p_234020_, p_234021_, p_234022_, p_234023_, p_234024_, p_234025_, p_234026_, this.sprite, p_234019_);
            particle.setAlpha(1.0F);
            return particle;
        }
    }
}
