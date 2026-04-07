package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.world.phys.Vec3;

public class WindShockwaveParticle extends WindParticle{
    public float increase;

    public WindShockwaveParticle(ClientLevel world, double x, double y, double z, float red, float green, float blue, float width, float height, float increase, float startRot, int life, int ownerId) {
        super(world, x, y, z, red, green, blue, width, height, life, ownerId);
        this.increase = increase;
        this.initYRot = startRot * 360.0F;
        this.rotateAge = (10.0F + startRot * 10.0F);
    }

    @Override
    public Vec3 getOrbitPosition() {
        Vec3 position = this.getPosition();
        Vec3 vec3 = new Vec3(0.0D, this.height, this.width + (this.age * this.increase)).yRot((float)Math.toRadians(this.initYRot + this.rotateAge * (float)this.age));
        return position.add(vec3);
    }

    @Override
    public float getTrailHeight() {
        return 1.0F;
    }

    public static class Provider implements ParticleProvider<WindShockwaveParticleOption> {

        public Provider(SpriteSet p_172490_) {
        }

        public Particle createParticle(WindShockwaveParticleOption typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new WindShockwaveParticle(worldIn, x, y, z, typeIn.getRed(), typeIn.getGreen(), typeIn.getBlue(), typeIn.getWidth(), typeIn.getHeight(), typeIn.getIncrease(), typeIn.getStartYRot(), typeIn.getLife(), typeIn.getOwnerId());
        }
    }
}
