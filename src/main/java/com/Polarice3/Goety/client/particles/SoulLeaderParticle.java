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

public class SoulLeaderParticle extends Particle {
    public static final int MODE_LEADER = 0;
    public static final int MODE_TRAIL = 1;
    public static final int MODE_UNTEXTURED = 2;

    public enum Behaviour {
        SUMMON,
        ORBIT,
        BURST
    }

    private static final float TICK = 0.05F;
    private static final int TRAIL_TICKS = 8;
    private static final float TRAIL_HALF_WIDTH = 0.15F;
    private static final int MAX_SUBSTEPS = 3;
    private static final int MAX_POINTS = (TRAIL_TICKS + 2) * MAX_SUBSTEPS;

    public static final float[] SOUL_GREEN_SHADER = {800.0F, 748.13F, 616.85F, 442.67F, 262.1F, 111.66F, 27.84F, 18.41F, 14.42F, 10.93F, 6.94F, 2.95F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F};
    public static final float[] SOUL_BLUE_SHADER = {1200.0F, 1125.09F, 935.5F, 683.96F, 423.19F, 205.92F, 84.88F, 65.17F, 50.06F, 36.83F, 21.71F, 6.59F, 3.0F, 3.0F, 3.0F, 3.0F, 3.0F};
    private static final float[] BURST_I = {20.0F, 19.81F, 19.17F, 18.29F, 16.99F, 15.63F, 13.9F, 12.29F, 10.38F, 8.71F, 7.1F, 5.37F, 4.01F, 2.71F, 1.83F, 1.19F, 1.0F};
    private static final float[] BURST_DRAG = {1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.984F, 0.9F, 0.724F, 0.432F, 0.0F};
    private static final float[] TRAIL_RGB = ColorUtil.toLdr(0.33F, 1.920377F, 2.5F);
    private static final float[] ORBIT_RGB = ColorUtil.toLdr(0.0F, 5.0F, 5.0F);
    private static final float[] NECRO_TRAIL_RGB = ColorUtil.toLdr(NecroVFXPalette.BRIGHT_HUE[0] * 2.5F, NecroVFXPalette.BRIGHT_HUE[1] * 2.5F, NecroVFXPalette.BRIGHT_HUE[2] * 2.5F);
    private static final float[] NECRO_ORBIT_RGB = ColorUtil.toLdr(NecroVFXPalette.LIME_HUE[0] * 5.0F, NecroVFXPalette.LIME_HUE[1] * 5.0F, NecroVFXPalette.LIME_HUE[2] * 5.0F);
    private static final float[] BURST_HUE = {0.0F, 0.4F, 1.0F};

    private final Behaviour behaviour;
    private final double cx, cy, cz;
    private final float greenMul;
    private final float[] hue;
    private final float width;
    private final float length;
    private final int soulLife;
    private final int substeps;
    private final float vortex;
    private final float drag;
    private final float seed;
    private final boolean necro;

    private final double[] hX = new double[MAX_POINTS];
    private final double[] hY = new double[MAX_POINTS];
    private final double[] hZ = new double[MAX_POINTS];
    private final float[] hT = new float[MAX_POINTS];
    private int hCount;

    protected SoulLeaderParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, MCDEffectOption option, Behaviour behaviour) {
        super(level, x, y, z);
        this.behaviour = behaviour;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.cx = x + option.a;
        this.cy = y;
        this.cz = z + option.c;
        this.greenMul = option.b;
        this.necro = option.alternate;
        if (this.necro) {
            this.hue = behaviour == Behaviour.BURST ? NecroVFXPalette.GREEN_HUE : NecroVFXPalette.lerp((option.b - 0.5F) * 2.0F, NecroVFXPalette.BRIGHT_HUE, NecroVFXPalette.CHARTREUSE_HUE);
        } else {
            this.hue = behaviour == Behaviour.BURST ? BURST_HUE : new float[]{0.0F, option.b, 1.0F};
        }
        this.width = option.size;
        this.soulLife = Math.max(option.life, 1);
        this.lifetime = this.soulLife + TRAIL_TICKS + 2;
        this.seed = this.random.nextFloat() * 100.0F;
        switch (behaviour) {
            case ORBIT -> {
                this.length = option.size * (80.0F / 55.0F);
                this.substeps = 3;
                this.vortex = (90000.0F + this.random.nextFloat() * 10000.0F) / 100.0F;
                this.drag = 20.0F;
            }
            case BURST -> {
                this.length = option.size * (80.0F / 55.0F);
                this.substeps = 3;
                this.vortex = (this.random.nextFloat() * 4000.0F - 2000.0F) / 100.0F;
                this.drag = 3.0F + this.random.nextFloat();
            }
            default -> {
                this.length = option.size;
                this.substeps = 1;
                this.vortex = 200.0F * 0.57735F;
                this.drag = 4.0F;
            }
        }
        this.hasPhysics = false;
        this.friction = 1.0F;
        this.gravity = 0.0F;
        this.pushPoint(0.0F);
        this.updateColor(0.0F);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ModRenderType.PARTICLE_SOUL;
    }

    @Override
    public boolean shouldCull() {
        return false;
    }

    private boolean soulAlive() {
        return this.age <= this.soulLife;
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
        if (!this.soulAlive()) {
            return;
        }
        float p = (float) this.age / this.soulLife;
        float dt = TICK / this.substeps;
        double vx = this.xd * 20.0D, vy = this.yd * 20.0D, vz = this.zd * 20.0D;
        for (int s = 1; s <= this.substeps; s++) {
            double ax = 0.0D, ay = 0.0D, az = 0.0D;
            double dragNow;
            double tangent = switch (this.behaviour) {
                case ORBIT, BURST -> this.vortex;
                default -> (this.random.nextBoolean() ? 1.0D : -1.0D) * this.vortex * (1.0F - p);
            };
            double dx = this.cx - this.x, dy = this.cy - this.y, dz = this.cz - this.z;
            double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (len > 1.0E-5D) {
                ax += -dz / len * tangent;
                az += dx / len * tangent;
            }
            if (this.behaviour == Behaviour.BURST) {
                double f = 0.6D;
                ax += 15.0D * Math.sin(this.y * f + this.seed) * Math.cos(this.z * f * 1.3D);
                ay += 15.0D * Math.sin(this.z * f + this.seed * 0.7D) * Math.cos(this.x * f * 1.3D) + 2.0D;
                az += 15.0D * Math.sin(this.x * f + this.seed * 1.3D) * Math.cos(this.y * f * 1.3D);
                dragNow = this.drag * ColorUtil.sample(BURST_DRAG, p);
            } else if (this.behaviour == Behaviour.ORBIT) {
                dragNow = this.drag;
            } else {
                dragNow = this.drag * (1.0F - p);
            }
            double damp = 1.0D / (1.0D + dragNow * dt);
            vx = (vx + ax * dt) * damp;
            vy = (vy + ay * dt) * damp;
            vz = (vz + az * dt) * damp;
            this.setPos(this.x + vx * dt, this.y + vy * dt, this.z + vz * dt);
            this.pushPoint(this.age - 1 + (float) s / this.substeps);
        }
        this.xd = vx / 20.0D;
        this.yd = vy / 20.0D;
        this.zd = vz / 20.0D;
        this.updateColor(p);
    }

    private void pushPoint(float time) {
        int n = Math.min(this.hCount, MAX_POINTS - 1);
        System.arraycopy(this.hX, 0, this.hX, 1, n);
        System.arraycopy(this.hY, 0, this.hY, 1, n);
        System.arraycopy(this.hZ, 0, this.hZ, 1, n);
        System.arraycopy(this.hT, 0, this.hT, 1, n);
        this.hX[0] = this.x;
        this.hY[0] = this.y;
        this.hZ[0] = this.z;
        this.hT[0] = time;
        this.hCount = n + 1;
    }

    private void updateColor(float p) {
        float fade = 1.0F - p * p * (3.0F - 2.0F * p);
        switch (this.behaviour) {
            case ORBIT -> {
                float[] c = this.necro ? NECRO_ORBIT_RGB : ORBIT_RGB;
                this.setColor(c[0], c[1], c[2]);
                this.alpha = fade;
            }
            case BURST -> {
                float i = ColorUtil.sample(BURST_I, p);
                ParticleUtil.setHdrColor(this, this.hue[0] * i, this.hue[1] * i, this.hue[2] * i);
                this.alpha = fade;
            }
            default -> {
                float i = ColorUtil.sample(SOUL_BLUE_SHADER, p);
                float g = this.necro ? i : ColorUtil.sample(SOUL_GREEN_SHADER, p);
                ParticleUtil.setHdrColor(this, this.hue[0] * i, this.hue[1] * g, this.hue[2] * i);
                this.alpha = 1.0F;
            }
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 cam = camera.getPosition();
        float renderTime = this.age - 1 + partialTicks;
        boolean alive = this.soulAlive();
        Vector3f head = new Vector3f(
                (float) (Mth.lerp(partialTicks, this.xo, this.x) - cam.x),
                (float) (Mth.lerp(partialTicks, this.yo, this.y) - cam.y),
                (float) (Mth.lerp(partialTicks, this.zo, this.z) - cam.z));

        this.renderTrail(buffer, camera, cam, head, alive, renderTime);
        if (alive) {
            this.renderLeader(buffer, camera, head);
        }
    }

    private void renderTrail(VertexConsumer buffer, Camera camera, Vec3 cam, Vector3f head, boolean alive, float renderTime) {
        Vector3f[] pts = new Vector3f[MAX_POINTS + 2];
        float[] ages = new float[MAX_POINTS + 2];
        int n = 0;
        if (alive) {
            pts[n] = head;
            ages[n++] = 0.0F;
        }
        for (int k = 0; k < this.hCount && n < pts.length; k++) {
            float a = renderTime - this.hT[k];
            if (a <= 0.0F && alive) {
                continue;
            }
            Vector3f p = new Vector3f((float) (this.hX[k] - cam.x), (float) (this.hY[k] - cam.y), (float) (this.hZ[k] - cam.z));
            if (a >= TRAIL_TICKS) {
                if (n > 0) {
                    float t = (TRAIL_TICKS - ages[n - 1]) / Math.max(a - ages[n - 1], 1.0E-4F);
                    pts[n] = new Vector3f(pts[n - 1]).lerp(p, Mth.clamp(t, 0.0F, 1.0F));
                    ages[n++] = TRAIL_TICKS;
                }
                break;
            }
            pts[n] = p;
            ages[n++] = Math.max(a, 0.0F);
        }
        if (n < 2) {
            return;
        }

        float[] u = new float[n];
        float total = 0.0F;
        for (int i = 1; i < n; i++) {
            total += pts[i].distance(pts[i - 1]);
            u[i] = total;
        }
        if (total < 1.0E-4F) {
            return;
        }
        for (int i = 1; i < n; i++) {
            u[i] /= total;
        }

        Vector3f[] sides = new Vector3f[n];
        for (int i = 0; i < n; i++) {
            Vector3f tangent = new Vector3f(pts[Math.max(i - 1, 0)]).sub(pts[Math.min(i + 1, n - 1)]);
            Vector3f side = tangent.cross(new Vector3f(pts[i]).negate(), new Vector3f());
            if (side.lengthSquared() < 1.0E-10F) {
                side = i > 0 ? new Vector3f(sides[i - 1]) : new Vector3f(camera.getLeftVector());
            }
            sides[i] = side.normalize().mul(TRAIL_HALF_WIDTH);
        }
        for (int i = 0; i < n - 1; i++) {
            float[] trail = this.necro ? NECRO_TRAIL_RGB : TRAIL_RGB;
            if (pts[i].distanceSquared(pts[i + 1]) < 1.0E-8F) {
                continue;
            }
            quad(buffer, MODE_TRAIL,
                    new Vector3f(pts[i]).add(sides[i]), u[i], 0.0F,
                    new Vector3f(pts[i + 1]).add(sides[i + 1]), u[i + 1], 0.0F,
                    new Vector3f(pts[i + 1]).sub(sides[i + 1]), u[i + 1], 1.0F,
                    new Vector3f(pts[i]).sub(sides[i]), u[i], 1.0F,
                    trail[0], trail[1], trail[2], 1.0F);
        }
    }

    private void renderLeader(VertexConsumer buffer, Camera camera, Vector3f c) {
        Vector3f axis = new Vector3f((float) this.xd, (float) this.yd, (float) this.zd);
        float speed = axis.length();
        if (speed < 1.0E-4F) {
            axis.set(0.0F, 1.0F, 0.0F);
        } else {
            axis.div(speed);
        }
        Vector3f side = axis.cross(new Vector3f(c).negate(), new Vector3f());
        if (side.lengthSquared() < 1.0E-10F) {
            side.set(camera.getLeftVector());
        }
        side.normalize();

        float halfW = this.width * 0.5F;
        float halfL;
        if (this.behaviour == Behaviour.SUMMON) {
            halfL = this.length * Mth.lerp(Mth.clamp(speed / 3.0F, 0.0F, 1.0F), 1.5F, 2.0F) * 0.5F;
        } else {
            halfL = this.length * 0.5F;
        }
        Vector3f top = new Vector3f(axis).mul(halfL).add(c);
        Vector3f bottom = new Vector3f(axis).mul(-halfL).add(c);
        Vector3f sw = new Vector3f(side).mul(halfW);

        quad(buffer, MODE_LEADER,
                new Vector3f(top).sub(sw), 0.0F, 0.0F,
                new Vector3f(top).add(sw), 1.0F, 0.0F,
                new Vector3f(bottom).add(sw), 1.0F, 1.0F,
                new Vector3f(bottom).sub(sw), 0.0F, 1.0F,
                this.rCol, this.gCol, this.bCol, this.alpha);
    }

    static void quad(VertexConsumer b, int mode, Vector3f p0, float u0, float v0, Vector3f p1, float u1, float v1, Vector3f p2, float u2, float v2, Vector3f p3, float u3, float v3, float r, float g, float bl, float a) {
        b.vertex(p0.x, p0.y, p0.z).uv(u0, v0).color(r, g, bl, a).uv2(mode, 0).endVertex();
        b.vertex(p1.x, p1.y, p1.z).uv(u1, v1).color(r, g, bl, a).uv2(mode, 0).endVertex();
        b.vertex(p2.x, p2.y, p2.z).uv(u2, v2).color(r, g, bl, a).uv2(mode, 0).endVertex();
        b.vertex(p3.x, p3.y, p3.z).uv(u3, v3).color(r, g, bl, a).uv2(mode, 0).endVertex();
    }

    public static class Provider implements ParticleProvider<MCDEffectOption> {
        private final Behaviour behaviour;

        public Provider() {
            this(Behaviour.SUMMON);
        }

        public Provider(Behaviour behaviour) {
            this.behaviour = behaviour;
        }

        @Override
        public Particle createParticle(MCDEffectOption option, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new SoulLeaderParticle(level, x, y, z, xd, yd, zd, option, this.behaviour);
        }
    }
}