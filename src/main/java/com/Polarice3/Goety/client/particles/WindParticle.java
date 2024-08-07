package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * Based on StormParticle code from L_Ender's Cataclysm:<a href="https://github.com/PandaLunatique/L_Ender-s-Cataclysm/blob/1142477a2f4159ce4f75a194c682000ded1369d5/src/main/java/com/github/L_Ender/cataclysm/client/particle/StormParticle.java">...</a>;
 */
public class WindParticle extends WindTrailParticle {
    public final int ownerId;
    public final boolean isEntity;
    public final float width;
    public final float height;
    public float initYRot;
    public float rotateAge;
    public final Vec3 origin;

    public WindParticle(ClientLevel world, double x, double y, double z, float red, float green, float blue, float width, float height, int ownerId) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D, red, green, blue);
        this.ownerId = ownerId;
        this.isEntity = ownerId > 0;
        this.gravity = 0.0F;
        this.lifetime = 20 + this.random.nextInt(20);
        this.initYRot = this.random.nextFloat() * 360.0F;
        this.rotateAge = (10.0F + this.random.nextFloat() * 10.0F) * (this.random.nextIntBetweenInclusive(-1, 1));
        this.width = width;
        this.height = height;
        this.origin = new Vec3(x, y, z);
        Vec3 vec3 = this.getOrbitPosition();
        this.x = this.xo = vec3.x;
        this.y = this.yo = vec3.y;
        this.z = this.zo = vec3.z;
        this.xd = 0.0D;
        this.yd = 0.0D;
        this.zd = 0.0D;
    }

    public Vec3 getPosition() {
        Entity owner = this.getEntity();
        return owner != null ? owner.position() : this.origin;
    }

    public Entity getEntity() {
        return this.ownerId == -1 ? null : this.level.getEntity(this.ownerId);
    }

    public Vec3 getOrbitPosition() {
        Vec3 position = this.getPosition();
        Vec3 vec3 = new Vec3(0.0D, this.height, this.width).yRot((float)Math.toRadians(this.initYRot + this.rotateAge * (float)this.age));
        return position.add(vec3);
    }

    public void tick() {
        super.tick();
        this.trailA = 1.0F - (float) this.age / (float) this.lifetime;
        Vec3 vec3 = this.getOrbitPosition();
        this.x = vec3.x;
        this.y = vec3.y;
        this.z = vec3.z;
        if (this.getEntity() == null && this.isEntity){
            this.remove();
        }
    }

    public int sampleSize() {
        return 4;
    }

    public int getLightColor(float pPartialTick) {
        return LightTexture.FULL_BLOCK;
    }

    public static class Provider implements ParticleProvider<WindParticleOption> {

        public Provider(SpriteSet p_172490_) {
        }

        public Particle createParticle(WindParticleOption typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new WindParticle(worldIn, x, y, z, typeIn.getRed(), typeIn.getGreen(), typeIn.getBlue(), typeIn.getWidth(), typeIn.getHeight(), typeIn.getOwnerId());
        }
    }
}
