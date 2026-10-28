package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.common.entities.projectiles.Ripple;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class RippleRenderer extends EntityRenderer<Ripple> {
    public static final List<Ripple> QUEUE = new ArrayList<>();
    public static final float SEGMENT_LENGTH = 0.3F;
    public static final int ELECTRIC_FRAMES = 4;
    public static final float ELECTRIC_TILING = 1.2F;

    public RippleRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(Ripple ripple, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
        if (ripple.getStyle() != Ripple.RippleStyle.NONE && !QUEUE.contains(ripple)) {
            QUEUE.add(ripple);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(Ripple ripple) {
        return InventoryMenu.BLOCK_ATLAS;
    }

    public static void drawRipple(Ripple ripple, float partialTicks, PoseStack poseStack, VertexConsumer consumer, Vec3 cam, Pass pass) {
        Ripple.RippleStyle style = ripple.getStyle();
        Ripple.Band[] bands = style.bands(ripple);
        if (bands.length == 0) {
            return;
        }
        float age = ripple.getAge(partialTicks);
        int flicker = (int) (age / Math.max(1, style.flickerTicks));

        poseStack.pushPose();
        poseStack.translate(ripple.getX() - cam.x, ripple.getY() - cam.y, ripple.getZ() - cam.z);
        Matrix4f pose = poseStack.last().pose();
        Vec3 camLocal = cam.subtract(ripple.getX(), ripple.getY(), ripple.getZ());

        int[] range = ripple.getActiveWaveRange(age);
        for (int wave = range[0]; wave <= range[1]; ++wave) {
            float progress = ripple.getWaveProgress(wave, age);
            if (progress < 0) {
                continue;
            }
            float radius = ripple.getWaveRadius(progress);
            float alpha = style.fade.alpha(progress);
            if (radius < 0.05F || alpha <= 0.003F) {
                continue;
            }
            for (int b = 0; b < bands.length; ++b) {
                if (Pass.of(bands[b]) == pass) {
                    drawBand(ripple, bands[b], radius, alpha, flicker, wave * 31 + b * 7, pose, consumer, camLocal);
                }
            }
        }
        poseStack.popPose();
    }

    private static void drawBand(Ripple ripple, Ripple.Band band, float radius, float alpha, int flicker, int salt, Matrix4f pose, VertexConsumer consumer, Vec3 camLocal) {
        float arc = ripple.getAngle();
        boolean closed = arc >= 360.0F;
        float startYaw = closed ? 0.0F : ripple.getFacingYaw() - arc * 0.5F;
        float length = radius * arc * Mth.DEG_TO_RAD;
        int segments = Mth.clamp(Mth.ceil(length / SEGMENT_LENGTH), 12, 360);

        float[][] in = new float[segments + 1][3];
        float[][] mid = new float[segments + 1][3];
        float[][] out = new float[segments + 1][3];

        for (int i = 0; i <= segments; ++i) {
            int idx = closed && i == segments ? 0 : i; // closed rings share the seam
            float yaw = (startYaw + arc * i / segments) * Mth.DEG_TO_RAD;
            float sx = -Mth.sin(yaw), sz = Mth.cos(yaw);

            float jag = band.jag() > 0 ? band.jag() * (noise(idx, segments, 2, flicker, salt, false) * 2.0F - 1.0F) : 0.0F;
            float jagY = band.jag() > 0 ? band.jag() * 0.5F * (noise(idx, segments, 2, flicker, salt + 101, false) * 2.0F - 1.0F) : 0.0F;
            float r = Math.max(0.0F, radius + jag);
            float px = sx * r, py = band.yOffset() + jagY, pz = sz * r;

            float width = band.width() * (1.0F + band.widthJitter() * noise(idx, segments, 4, flicker, salt + 53, true));
            float half = width * 0.5F;

            float wx, wy, wz;
            switch (band.facing()) {
                case FLAT -> { wx = sx; wy = 0; wz = sz; }
                case WALL -> { wx = 0; wy = 1; wz = 0; }
                default -> {
                    float tx = -sz;
                    float vx = (float) camLocal.x - px, vy = (float) camLocal.y - py, vz = (float) camLocal.z - pz;
                    wx = -sx * vy;
                    wy = sx * vx - tx * vz;
                    wz = tx * vy;
                    float len = Mth.sqrt(wx * wx + wy * wy + wz * wz);
                    if (len < 1.0E-5F) { wx = sx; wy = 0; wz = sz; } else { wx /= len; wy /= len; wz /= len; }
                }
            }
            in[i][0] = px - wx * half; in[i][1] = py - wy * half; in[i][2] = pz - wz * half;
            mid[i][0] = px; mid[i][1] = py; mid[i][2] = pz;
            out[i][0] = px + wx * half; out[i][1] = py + wy * half; out[i][2] = pz + wz * half;
        }

        if (band.electric()) {
            float tiles = closed ? Math.max(1, Math.round(length / ELECTRIC_TILING)) : length / ELECTRIC_TILING;
            float u0 = hash(flicker, salt, 7);
            int frame = Math.min(ELECTRIC_FRAMES - 1, (int) (hash(flicker, salt, 13) * ELECTRIC_FRAMES));
            float vIn = (float) frame / ELECTRIC_FRAMES;
            float vOut = (float) (frame + 1) / ELECTRIC_FRAMES;
            float vMid = (vIn + vOut) * 0.5F;
            for (int i = 0; i < segments; ++i) {
                float a0 = alpha * tip(i, segments, closed), a1 = alpha * tip(i + 1, segments, closed);
                float ua = u0 + tiles * i / segments, ub = u0 + tiles * (i + 1) / segments;
                vertexUV(consumer, pose, in[i], band.inner(), a0, ua, vIn);
                vertexUV(consumer, pose, mid[i], band.center(), a0, ua, vMid);
                vertexUV(consumer, pose, mid[i + 1], band.center(), a1, ub, vMid);
                vertexUV(consumer, pose, in[i + 1], band.inner(), a1, ub, vIn);

                vertexUV(consumer, pose, mid[i], band.center(), a0, ua, vMid);
                vertexUV(consumer, pose, out[i], band.outer(), a0, ua, vOut);
                vertexUV(consumer, pose, out[i + 1], band.outer(), a1, ub, vOut);
                vertexUV(consumer, pose, mid[i + 1], band.center(), a1, ub, vMid);
            }
            return;
        }

        for (int i = 0; i < segments; ++i) {
            float a0 = alpha * tip(i, segments, closed), a1 = alpha * tip(i + 1, segments, closed);
            quad(consumer, pose, in[i], mid[i], mid[i + 1], in[i + 1], band.inner(), band.center(), band.center(), band.inner(), a0, a0, a1, a1);
            quad(consumer, pose, mid[i], out[i], out[i + 1], mid[i + 1], band.center(), band.outer(), band.outer(), band.center(), a0, a0, a1, a1);
        }
    }

    private static float tip(int i, int segments, boolean closed) {
        if (closed) {
            return 1.0F;
        }
        float edge = Math.min(i, segments - i) / (segments * 0.08F);
        return Mth.clamp(edge, 0.0F, 1.0F);
    }

    private static void quad(VertexConsumer consumer, Matrix4f pose, float[] p0, float[] p1, float[] p2, float[] p3, int c0, int c1, int c2, int c3, float a0, float a1, float a2, float a3) {
        vertex(consumer, pose, p0, c0, a0);
        vertex(consumer, pose, p1, c1, a1);
        vertex(consumer, pose, p2, c2, a2);
        vertex(consumer, pose, p3, c3, a3);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f pose, float[] p, int argb, float alpha) {
        float a = ((argb >>> 24) & 255) / 255.0F * alpha;
        consumer.vertex(pose, p[0], p[1], p[2])
                .color((argb >> 16) & 255, (argb >> 8) & 255, argb & 255, Mth.clamp((int) (a * 255.0F), 0, 255))
                .endVertex();
    }

    private static void vertexUV(VertexConsumer consumer, Matrix4f pose, float[] p, int argb, float alpha, float u, float v) {
        float a = ((argb >>> 24) & 255) / 255.0F * alpha;
        consumer.vertex(pose, p[0], p[1], p[2])
                .color((argb >> 16) & 255, (argb >> 8) & 255, argb & 255, Mth.clamp((int) (a * 255.0F), 0, 255))
                .uv(u, v)
                .endVertex();
    }

    private static float noise(int i, int segments, int spacing, int time, int salt, boolean smooth) {
        int controls = Math.max(1, Math.round((float) segments / spacing));
        float u = (float) i * controls / segments;
        int i0 = Mth.floor(u);
        float f = u - i0;
        if (smooth) {
            f = f * f * (3.0F - 2.0F * f);
        }
        return Mth.lerp(f, hash(Math.floorMod(i0, controls), time, salt), hash(Math.floorMod(i0 + 1, controls), time, salt));
    }

    private static float hash(int a, int b, int c) {
        int h = a * 0x27D4EB2D ^ b * 0x165667B1 ^ c * 0x1B873593;
        h ^= h >>> 15;
        h *= 0x2C1B3C6D;
        h ^= h >>> 12;
        h *= 0x297A2D39;
        h ^= h >>> 15;
        return (h & 0xFFFFFF) / (float) 0x1000000;
    }

    public static void clientTick(Ripple ripple) {
        Minecraft mc = Minecraft.getInstance();
        ParticleStatus status = mc.options.particles().get();
        if (status == ParticleStatus.MINIMAL || ripple.getStyle() == Ripple.RippleStyle.NONE) {
            return;
        }
        Ripple.Emitter[] emitters = ripple.getStyle().emitters(ripple);
        if (emitters.length == 0) {
            return;
        }
        float density = ripple.getParticleDensity() * (status == ParticleStatus.DECREASED ? 0.4F : 1.0F);
        Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();
        RandomSource random = ripple.level.getRandom();
        float age = ripple.getAge(0.0F);
        float arc = ripple.getAngle();

        int[] range = ripple.getActiveWaveRange(age);
        for (int wave = range[0]; wave <= range[1]; ++wave) {
            float progress = ripple.getWaveProgress(wave, age);
            if (progress < 0) {
                continue;
            }
            float radius = ripple.getWaveRadius(progress);
            if (radius < 0.05F) {
                continue;
            }
            for (Ripple.Emitter emitter : emitters) {
                float rate = emitter.perTick() * emitter.ramp().apply(progress) * density;
                int count = (int) rate + (random.nextFloat() < rate - (int) rate ? 1 : 0);
                for (int n = 0; n < count; ++n) {
                    float yaw = (arc >= 360.0F ? random.nextFloat() * 360.0F : ripple.getFacingYaw() + (random.nextFloat() - 0.5F) * arc) * Mth.DEG_TO_RAD;
                    float sx = -Mth.sin(yaw), sz = Mth.cos(yaw);
                    float tubeAngle = random.nextFloat() * Mth.TWO_PI;
                    float tube = Mth.sqrt(random.nextFloat()) * emitter.tube();
                    float r = radius + Mth.cos(tubeAngle) * tube;
                    double x = ripple.getX() + sx * r;
                    double y = ripple.getY() + emitter.yOffset() + Mth.sin(tubeAngle) * tube;
                    double z = ripple.getZ() + sz * r;
                    if (cam.distanceToSqr(x, y, z) > 64 * 64) {
                        continue;
                    }
                    float t = random.nextFloat();
                    int ca = emitter.colorA(), cb = emitter.colorB();
                    float red = Mth.lerp(t, (ca >> 16 & 255), (cb >> 16 & 255)) / 255.0F;
                    float green = Mth.lerp(t, (ca >> 8 & 255), (cb >> 8 & 255)) / 255.0F;
                    float blue = Mth.lerp(t, (ca & 255), (cb & 255)) / 255.0F;
                    float size = Mth.lerp(random.nextFloat(), emitter.sizeMin(), emitter.sizeMax());
                    int life = Mth.nextInt(random, emitter.lifeMin(), Math.max(emitter.lifeMin(), emitter.lifeMax()));
                    float outward = Mth.lerp(random.nextFloat(), emitter.outwardMin(), emitter.outwardMax());
                    float up = Mth.lerp(random.nextFloat(), emitter.upMin(), emitter.upMax());

                    Particle particle = mc.particleEngine.createParticle(emitter.factory().create(red, green, blue, size, life), x, y, z, 0, 0, 0);
                    if (particle != null) {
                        particle.setParticleSpeed(sx * outward, up, sz * outward);
                        particle.setLifetime(life);
                    }
                }
            }
        }
    }

    public enum Pass {
        TRANSLUCENT,
        ADDITIVE,
        ELECTRIC;

        public static Pass of(Ripple.Band band) {
            return band.electric() ? ELECTRIC : band.additive() ? ADDITIVE : TRANSLUCENT;
        }
    }
}