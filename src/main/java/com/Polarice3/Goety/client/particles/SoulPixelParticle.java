package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.client.render.ModRenderType;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.ParticleUtil;
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

public class SoulPixelParticle extends Particle {
    private static final float DT = 0.05F;
    private static final float[] FLASH_RGB = {10.0F, 9.82F, 9.43F, 8.75F, 8.02F, 7.08F, 6.09F, 5.26F, 4.39F, 3.67F, 3.24F, 3.01F, 3.0F, 3.0F, 3.0F, 3.0F, 3.0F};
    private static final float[] FLASH_A = {1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.91F, 0.69F, 0.36F, 0.12F, 0.0F};
    private static final float GRAVITY_150 = -0.00375F;
    private static final float BASE_LENGTH = 0.07F;

    private final Mode mode;
    private final float width;
    private final float a, b, c;
    private final float[] hue;
    private final boolean necro;

    protected SoulPixelParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, MCDEffectOption option, Mode mode) {
        super(level, x, y, z);
        this.mode = mode;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.width = option.size;
        this.lifetime = Math.max(option.life, 1);
        this.a = option.a;
        this.b = option.b;
        this.c = option.c;
        this.necro = option.alternate;
        this.hue = this.necro ? NecroVFXPalette.greenHue(option.a) : new float[]{0.0F, option.a, 1.0F};
        this.hasPhysics = false;
        this.friction = 1.0F;
        this.gravity = 0.0F;
        this.updateColor(0.0F);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ModRenderType.PARTICLE_SOUL;
    }

    @Override
    public boolean shouldCull() {
        return this.mode == Mode.SPARK;
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
        float p = (float) this.age / this.lifetime;
        double drag;
        switch (this.mode) {
            case FLASH, STREAK -> {
                this.yd += GRAVITY_150;
                drag = 12.0D;
            }
            default -> {
                this.yd += this.c;
                drag = 10.0D * Math.max(0.0F, 1.0F - p / 0.6F);
            }
        }
        double damp = 1.0D / (1.0D + drag * DT);
        this.xd *= damp;
        this.yd *= damp;
        this.zd *= damp;
        this.move(this.xd, this.yd, this.zd);
        this.updateColor(p);
    }

    private void updateColor(float p) {
        switch (this.mode) {
            case FLASH -> {
                float v = ColorUtil.sample(FLASH_RGB, p);
                ParticleUtil.setHdrColor(this, v * this.a, v * this.b, v * this.c);
                this.alpha = ColorUtil.sample(FLASH_A, p);
            }
            case STREAK -> {
                float v = ColorUtil.sample(SoulLeaderParticle.SOUL_BLUE_SHADER, p);
                ParticleUtil.setHdrColor(this, this.hue[0] * v, this.hue[1] * v, this.hue[2] * v);
                this.alpha = 1.0F;
            }
            default -> {
                float k = this.b * 100.0F;
                ParticleUtil.setHdrColor(this, this.hue[0] * k, this.hue[1] * k, this.hue[2] * k);
                this.alpha = 1.0F - p * p * (3.0F - 2.0F * p);
            }
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 cam = camera.getPosition();
        Vector3f pos = new Vector3f(
                (float) (Mth.lerp(partialTicks, this.xo, this.x) - cam.x),
                (float) (Mth.lerp(partialTicks, this.yo, this.y) - cam.y),
                (float) (Mth.lerp(partialTicks, this.zo, this.z) - cam.z));
        float p = Mth.clamp((this.age - 1 + partialTicks) / this.lifetime, 0.0F, 1.0F);
        Vector3f up, side;
        float halfW, length;
        if (this.mode == Mode.SPARK) {
            Quaternionf rot = camera.rotation();
            up = new Vector3f(0.0F, 1.0F, 0.0F).rotate(rot);
            side = new Vector3f(1.0F, 0.0F, 0.0F).rotate(rot);
            halfW = this.width * 0.5F;
            length = this.width;
        } else {
            Vector3f vel = new Vector3f((float) this.xd, (float) this.yd, (float) this.zd);
            float speed = vel.length();
            up = speed < 1.0E-5F ? new Vector3f(0.0F, 1.0F, 0.0F) : vel.div(speed);
            side = up.cross(new Vector3f(pos).negate(), new Vector3f());
            if (side.lengthSquared() < 1.0E-10F) {
                side.set(camera.getLeftVector());
            }
            side.normalize();
            float uuPerSecond = speed * 2000.0F;
            float scaleX = Mth.lerp(p, 1.0F, 0.4F);
            switch (this.mode) {
                case STREAK -> {
                    float t = Mth.clamp(uuPerSecond / 10000.0F, 0.0F, 1.0F);
                    halfW = this.width * scaleX * Mth.lerp(t, 0.5F, 1.5F) * 0.5F;
                    length = BASE_LENGTH * Mth.lerp(p, 6.0F, 0.8F) * Mth.lerp(t, 1.0F, 20.0F);
                }
                default -> {
                    halfW = this.width * scaleX * 0.5F;
                    length = BASE_LENGTH * Mth.lerp(p, 6.0F, 0.8F);
                }
            }
        }
        if (length < 1.0E-4F) {
            return;
        }
        Vector3f bottom = new Vector3f(up).mul(-length).add(pos);
        Vector3f sw = side.mul(halfW);
        SoulLeaderParticle.quad(buffer, SoulLeaderParticle.MODE_UNTEXTURED,
                new Vector3f(pos).sub(sw), 0.0F, 0.0F,
                new Vector3f(pos).add(sw), 1.0F, 0.0F,
                new Vector3f(bottom).add(sw), 1.0F, 1.0F,
                new Vector3f(bottom).sub(sw), 0.0F, 1.0F,
                this.rCol, this.gCol, this.bCol, this.alpha);
    }

    public static class Provider implements ParticleProvider<MCDEffectOption> {
        private final Mode mode;

        public Provider(Mode mode) {
            this.mode = mode;
        }

        @Override
        public Particle createParticle(MCDEffectOption option, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new SoulPixelParticle(level, x, y, z, xd, yd, zd, option, this.mode);
        }
    }

    public static class FlashProvider extends Provider {
        public FlashProvider() {
            super(Mode.FLASH);
        }
    }

    public static class SparkProvider extends Provider {
        public SparkProvider() {
            super(Mode.SPARK);
        }
    }

    public enum Mode {
        FLASH,
        SPARK,
        STREAK
    }
}