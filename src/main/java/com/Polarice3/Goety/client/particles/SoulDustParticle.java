package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.client.render.ModRenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SoulDustParticle extends Particle {
    private static final float DT = 0.05F;
    private static final float DRAG = 12.0F;
    private static final float BUOYANCY = 0.0025F;

    private final float halfSize;
    private final int texCoord;
    private final int materialRandom;

    protected SoulDustParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, MCDEffectOption option) {
        super(level, x, y, z);
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.halfSize = option.size * 0.5F;
        this.lifetime = Math.max(option.life, 1);
        this.setColor(option.a, option.b, option.c);
        this.texCoord = Math.round((0.2F + this.random.nextFloat() * 0.3F) * 1000.0F);
        this.materialRandom = Math.round(this.random.nextFloat() * 1000.0F);
        this.roll = this.oRoll = this.random.nextFloat() * Mth.TWO_PI;
        this.hasPhysics = false;
        this.friction = 1.0F;
        this.gravity = 0.0F;
        this.updateColor(0.0F);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ModRenderType.PARTICLE_DITHER_SMOKE;
    }

    @Override
    public boolean shouldCull() {
        return false;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.oRoll = this.roll;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        float p = (float) this.age / this.lifetime;
        this.yd += BUOYANCY;
        double damp = 1.0D / (1.0D + DRAG * (1.0F - p) * DT);
        this.xd *= damp;
        this.yd *= damp;
        this.zd *= damp;
        this.move(this.xd, this.yd, this.zd);
        this.updateColor(p);
    }

    private void updateColor(float p) {
        this.alpha = 1.0F - p * p * (3.0F - 2.0F * p);
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 cam = camera.getPosition();
        float px = (float) (Mth.lerp(partialTicks, this.xo, this.x) - cam.x);
        float py = (float) (Mth.lerp(partialTicks, this.yo, this.y) - cam.y);
        float pz = (float) (Mth.lerp(partialTicks, this.zo, this.z) - cam.z);
        Quaternionf rot = new Quaternionf(camera.rotation()).rotateZ(Mth.lerp(partialTicks, this.oRoll, this.roll));
        Vector3f[] c = {new Vector3f(-1, -1, 0), new Vector3f(-1, 1, 0), new Vector3f(1, 1, 0), new Vector3f(1, -1, 0)};
        float[][] uv = {{1, 1}, {1, 0}, {0, 0}, {0, 1}};
        float p = Mth.clamp((this.age - 1 + partialTicks) / this.lifetime, 0.0F, 1.0F);
        int emissive = Math.round(10.0F * (1.0F - p) * 3.0F);
        int data = this.texCoord | (emissive << 10);
        for (int i = 0; i < 4; i++) {
            c[i].rotate(rot).mul(this.halfSize).add(px, py, pz);
            buffer.vertex(c[i].x, c[i].y, c[i].z).uv(uv[i][0], uv[i][1])
                    .color(this.rCol, this.gCol, this.bCol, this.alpha)
                    .uv2(data, this.materialRandom).endVertex();
        }
    }

    public static class Provider implements ParticleProvider<MCDEffectOption> {
        @Override
        public Particle createParticle(MCDEffectOption option, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new SoulDustParticle(level, x, y, z, xd, yd, zd, option);
        }
    }
}