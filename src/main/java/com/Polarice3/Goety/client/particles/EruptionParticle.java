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
import org.joml.Vector3f;

public class EruptionParticle extends Particle {
    private static final float DT = 0.05F;
    public static final float[] ERUPTION_SHADER = {1200.0F, 1125.09F, 935.5F, 683.96F, 423.19F, 205.92F, 84.88F, 65.17F, 50.06F, 36.83F, 21.71F, 6.59F, 3.0F, 3.0F, 3.0F, 3.0F, 3.0F};
    private static final float[] LAVA_SCALE_Y = {0.0F, 3.85F, 5.84F, 5.52F, 5.15F, 4.78F, 4.4F, 4.03F, 3.72F, 3.34F, 2.97F, 2.6F, 2.23F, 1.86F, 1.54F, 1.17F, 0.8F};
    private static final float BASE_LENGTH = 0.07F;
    private final float width;
    private final float a, b, c;
    private final float red, green, blue;
    private final float lavaGravity;

    protected EruptionParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, MCDEffectOption option, float red, float green, float blue) {
        super(level, x, y, z);
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.width = option.size;
        this.lifetime = Math.max(option.life, 1);
        this.a = option.a;
        this.b = option.b;
        this.c = option.c;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.lavaGravity = -(1000.0F + this.random.nextFloat() * 1000.0F) / 40000.0F;
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
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        float p = (float) this.age / this.lifetime;
        double drag = 8.0D * (1.0F - p);
        this.yd += this.lavaGravity;
        double damp = 1.0D / (1.0D + drag * DT);
        this.xd *= damp;
        this.yd *= damp;
        this.zd *= damp;
        this.move(this.xd, this.yd, this.zd);
        this.updateColor(p);
    }

    private void updateColor(float p) {
        float v = ColorUtil.sample(ERUPTION_SHADER, p);
        ParticleUtil.setHdrColor(this, (this.red * v) * this.a, (this.green * v) * this.b, (this.blue * v) * this.c);
        this.alpha = 1.0F;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 cam = camera.getPosition();
        Vector3f pos = new Vector3f(
                (float) (Mth.lerp(partialTicks, this.xo, this.x) - cam.x),
                (float) (Mth.lerp(partialTicks, this.yo, this.y) - cam.y),
                (float) (Mth.lerp(partialTicks, this.zo, this.z) - cam.z));
        float p = Mth.clamp((this.age - 1 + partialTicks) / this.lifetime, 0.0F, 1.0F);
        Vector3f vel = new Vector3f((float) this.xd, (float) this.yd, (float) this.zd);
        float speed = vel.length();
        Vector3f up = speed < 1.0E-5F ? new Vector3f(0.0F, 1.0F, 0.0F) : vel.div(speed);
        Vector3f side = up.cross(new Vector3f(pos).negate(), new Vector3f());
        if (side.lengthSquared() < 1.0E-10F) {
            side.set(camera.getLeftVector());
        }
        side.normalize();
        float uuPerSecond = speed * 2000.0F;
        float scaleX = Mth.lerp(p, 1.0F, 0.4F);
        float t = Mth.clamp(uuPerSecond / 6000.0F, 0.0F, 1.0F);
        float halfW = this.width * scaleX * Mth.lerp(t, 1.0F, 1.5F) * 0.5F;
        float length = BASE_LENGTH * ColorUtil.sample(LAVA_SCALE_Y, p) * Mth.lerp(t, 0.5F, 20.0F);
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
        public float red, green, blue;

        public Provider(float red, float green, float blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        @Override
        public Particle createParticle(MCDEffectOption option, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new EruptionParticle(level, x, y, z, xd, yd, zd, option, this.red, this.green, this.blue);
        }
    }
}
