package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class GroundAuraParticle extends GroundCircleParticle {
    private final float rotSpeed;
    public final int ownerId;
    public final Vec3 origin;

    protected GroundAuraParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet spriteSet, int ownerId, float size) {
        super(level, x, y, z);
        this.ownerId = ownerId;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.origin = new Vec3(x, y, z);
        this.hasPhysics = false;
        this.quadSize = size;
        this.lifetime = 20;
        this.setSpriteFromAge(spriteSet);
        this.rotSpeed = ((float)Math.random() - 0.5F) * 0.1F;
        this.roll = (float)Math.random() * ((float)Math.PI * 2F);
    }

    public Vec3 getPosition() {
        Entity owner = this.getEntity();
        return owner != null ? new Vec3(owner.getX(), owner.getY() + 0.25F, owner.getZ()) : this.origin;
    }

    public Entity getEntity() {
        return this.ownerId == -1 ? null : this.level.getEntity(this.ownerId);
    }

    public int getLightColor(final float partialTicks) {
        return 240;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        Vec3 vec3 = this.getPosition();
        this.setPos(vec3.x, vec3.y, vec3.z);
        if (++this.age >= this.lifetime || this.getEntity() == null) {
            this.remove();
        }
        this.oRoll = this.roll;
        this.roll += (float)Math.PI * this.rotSpeed * 2.0F;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<GroundAuraParticleOption> {
        private final SpriteSet sprites;

        public Provider(SpriteSet p_i50607_1_) {
            this.sprites = p_i50607_1_;
        }

        public Particle createParticle(GroundAuraParticleOption pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new GroundAuraParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites, pType.getOwnerId(), pType.getSize());
        }
    }
}
