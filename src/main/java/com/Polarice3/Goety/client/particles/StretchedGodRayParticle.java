package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.client.render.ModRenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class StretchedGodRayParticle extends TextureSheetParticle {

    public StretchedGodRayParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y + 2.0, z);
        this.lifetime = 20;
        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.xd = 0.0D;
        this.yd = 0.0D;
        this.zd = 0.0D;
        this.quadSize = 1.0F;
        this.alpha = 0.0F;
        this.rCol = (float) xSpeed;
        this.gCol = (float) ySpeed;
        this.bCol = (float) zSpeed;
    }

    @Override
    public void tick() {
        super.tick();
        float agePercent = (float) this.age / this.lifetime;
        float maxAlpha = 0.6F;
        if (agePercent <= 0.2F) {
            this.alpha = (agePercent / 0.2F) * maxAlpha;
        } else if (agePercent >= 0.7F) {
            this.alpha = maxAlpha * (1.0F - ((agePercent - 0.7F) / 0.3F));
        } else {
            this.alpha = maxAlpha;
        }
    }

    @Override
    protected int getLightColor(float light) {
        return LightTexture.FULL_BLOCK;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 camPos = camera.getPosition();
        float x = (float)(Mth.lerp(partialTicks, this.xo, this.x) - camPos.x());
        float y = (float)(Mth.lerp(partialTicks, this.yo, this.y) - camPos.y());
        float z = (float)(Mth.lerp(partialTicks, this.zo, this.z) - camPos.z());

        Quaternionf quaternion = new Quaternionf().rotationY(-camera.getYRot() * Mth.DEG_TO_RAD);

        float xSize = 1.0F;
        float ySize = 4.0F;

        Vector3f[] vector3fs = new Vector3f[]{
                new Vector3f(-xSize, -ySize, 0.0F),
                new Vector3f(-xSize, ySize, 0.0F),
                new Vector3f(xSize, ySize, 0.0F),
                new Vector3f(xSize, -ySize, 0.0F)
        };

        float size = this.getQuadSize(partialTicks);

        for(int i = 0; i < 4; ++i) {
            Vector3f vertex = vector3fs[i];
            vertex.rotate(quaternion);
            vertex.mul(size);
            vertex.add(x, y, z);
        }

        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        int light = this.getLightColor(partialTicks);

        buffer.vertex(vector3fs[0].x(), vector3fs[0].y(), vector3fs[0].z())
                .uv(u1, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        buffer.vertex(vector3fs[1].x(), vector3fs[1].y(), vector3fs[1].z())
                .uv(u1, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        buffer.vertex(vector3fs[2].x(), vector3fs[2].y(), vector3fs[2].z())
                .uv(u0, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        buffer.vertex(vector3fs[3].x(), vector3fs[3].y(), vector3fs[3].z())
                .uv(u0, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();

        buffer.vertex(vector3fs[3].x(), vector3fs[3].y(), vector3fs[3].z())
                .uv(u0, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        buffer.vertex(vector3fs[2].x(), vector3fs[2].y(), vector3fs[2].z())
                .uv(u0, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        buffer.vertex(vector3fs[1].x(), vector3fs[1].y(), vector3fs[1].z())
                .uv(u1, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        buffer.vertex(vector3fs[0].x(), vector3fs[0].y(), vector3fs[0].z())
                .uv(u1, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
    }

    @Override
    public boolean shouldCull() {
        return false;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ModRenderType.PARTICLE_ADDITIVE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            StretchedGodRayParticle particle = new StretchedGodRayParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
