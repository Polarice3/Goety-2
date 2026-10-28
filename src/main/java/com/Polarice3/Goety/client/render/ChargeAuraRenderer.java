package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.common.entities.util.ChargeAura;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public final class ChargeAuraRenderer {
    private static final int SEGMENTS = 32;
    private static final int RINGS = 12;

    private static final float U_TILE = 3.0F;
    private static final float V_TILE = 6.0F;
    private static final float SPEED_X = 3.0F;

    private static final float[] SIN = new float[SEGMENTS + 1];
    private static final float[] COS = new float[SEGMENTS + 1];
    private static final float[] RING_SIN = new float[RINGS + 1];
    private static final float[] RING_COS = new float[RINGS + 1];

    static {
        for (int i = 0; i <= SEGMENTS; ++i) {
            float a = Mth.TWO_PI * i / SEGMENTS;
            SIN[i] = Mth.sin(a);
            COS[i] = Mth.cos(a);
        }
        for (int j = 0; j <= RINGS; ++j) {
            float polar = Mth.HALF_PI * (1.0F - (float) j / RINGS);
            RING_SIN[j] = Mth.sin(polar);
            RING_COS[j] = Mth.cos(polar);
        }
    }

    private ChargeAuraRenderer() {
    }

    public static void render(LivingEntity entity, ChargeAura aura, float partialTicks, PoseStack poseStack, MultiBufferSource buffer) {
        if (!aura.isVisible()) {
            return;
        }
        VertexConsumer consumer = buffer.getBuffer(ModRenderType.CHARGE_AURA);
        Matrix4f pose = poseStack.last().pose();
        ChargeAura.Settings settings = aura.settings;
        float now = entity.tickCount + partialTicks;
        float bodyYaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        Vec3 center = aura.localCenter(entity, bodyYaw);

        for (int layer = 0; layer < 2; ++layer) {
            float spinPerSecond = layer == 0 ? 72.0F : -54.0F;
            float speedY = layer == 0 ? 3.0F : 4.0F;
            int first = Math.max(0, Mth.floor((now - ChargeAura.LAYER_LIFE - aura.startTick - layer) / ChargeAura.LAYER_INTERVAL));
            for (int i = first; ; ++i) {
                int spawn = aura.startTick + layer + i * ChargeAura.LAYER_INTERVAL;
                if (spawn > now || (aura.endTick >= 0 && spawn >= aura.endTick)) {
                    break;
                }
                float age = now - spawn;
                if (age >= ChargeAura.LAYER_LIFE) {
                    continue;
                }
                float p = age / ChargeAura.LAYER_LIFE;
                float seed = hash(i, layer);
                float intensity = sinSq(p);
                float dissolve = (float) Math.pow(1.0F - p, 2.5F);
                drawDome(consumer, pose, settings, center, bodyYaw, spinPerSecond * age / 20.0F,
                        age / 20.0F, speedY, seed, intensity, dissolve);
            }
        }

        double ex = Mth.lerp(partialTicks, entity.xo, entity.getX());
        double ey = Mth.lerp(partialTicks, entity.yo, entity.getY());
        double ez = Mth.lerp(partialTicks, entity.zo, entity.getZ());
        for (ChargeAura.Wave wave : aura.waves) {
            float age = now - wave.spawnTick();
            if (age < 0.0F || age >= ChargeAura.WAVE_LIFE) {
                continue;
            }
            float p = age / ChargeAura.WAVE_LIFE;
            float rad = wave.yaw() * Mth.DEG_TO_RAD;
            double back = age * ChargeAura.WAVE_SPEED;
            Vec3 c = new Vec3(wave.origin().x + Mth.sin(rad) * back - ex, wave.origin().y - ey, wave.origin().z - Mth.cos(rad) * back - ez);
            drawDome(consumer, pose, settings, c, wave.yaw(), wave.spin() * age / 20.0F,
                    age / 20.0F, 3.0F, wave.seed(), sinSq(p) * 0.8F, (float) Math.exp(-8.0F * p));
        }
    }

    private static void drawDome(VertexConsumer consumer, Matrix4f pose, ChargeAura.Settings s, Vec3 center, float yaw, float spinDegrees, float seconds, float speedY, float seed, float intensity, float dissolve) {
        if (intensity <= 0.002F) {
            return;
        }
        float rad = yaw * Mth.DEG_TO_RAD;
        float fx = -Mth.sin(rad), fz = Mth.cos(rad);
        float rx = -fz;
        float spin = spinDegrees * Mth.DEG_TO_RAD;
        float cs = Mth.cos(spin), sn = Mth.sin(spin);

        float red = Mth.lerp(seed, s.tintRMin(), s.tintGMax());
        float green = Mth.lerp(seed, s.tintGMin(), s.tintGMax());
        float blue = Mth.lerp(seed, s.tintBMin(), s.tintGMax());
        float k = intensity * s.brightness();
        int r = Mth.clamp((int) (red * k * 255.0F), 0, 255);
        int g = Mth.clamp((int) (green * k * 255.0F), 0, 255);
        int b = Mth.clamp((int) (blue * k * 255.0F), 0, 255);
        int a = Mth.clamp((int) (dissolve * 255.0F), 0, 255);

        float uOffset = SPEED_X * seconds + seed * 7.0F;
        float vOffset = speedY * seconds + seed * 13.0F;
        float cx = (float) center.x, cy = (float) center.y, cz = (float) center.z;

        for (int j = 0; j < RINGS; ++j) {
            for (int i = 0; i < SEGMENTS; ++i) {
                vertex(consumer, pose, s, cx, cy, cz, fx, fz, rx, fx, cs, sn, i, j, uOffset, vOffset, r, g, b, a);
                vertex(consumer, pose, s, cx, cy, cz, fx, fz, rx, fx, cs, sn, i + 1, j, uOffset, vOffset, r, g, b, a);
                vertex(consumer, pose, s, cx, cy, cz, fx, fz, rx, fx, cs, sn, i + 1, j + 1, uOffset, vOffset, r, g, b, a);
                vertex(consumer, pose, s, cx, cy, cz, fx, fz, rx, fx, cs, sn, i, j + 1, uOffset, vOffset, r, g, b, a);
            }
        }
    }

    private static void vertex(VertexConsumer consumer, Matrix4f pose, ChargeAura.Settings s, float cx, float cy, float cz, float fx, float fz, float rx, float rz, float cs, float sn, int i, int j, float uOffset, float vOffset, int r, int g, int b, int a) {
        float lx = COS[i] * cs - SIN[i] * sn;
        float ly = SIN[i] * cs + COS[i] * sn;
        float side = RING_SIN[j] * s.radius();
        float x = cx + rx * lx * side + fx * RING_COS[j] * s.depth();
        float y = cy + ly * side;
        float z = cz + rz * lx * side + fz * RING_COS[j] * s.depth();

        float lat = (float) j / RINGS;
        float lon = (float) i / SEGMENTS;
        consumer.vertex(pose, x, y, z)
                .color(r, g, b, a)
                .uv(lat * 0.5F * U_TILE + uOffset, lon * V_TILE + vOffset)   // mesh UV x tiling + scroll
                .uv2(Math.round(lat * 1000.0F), Math.round(lon * 1000.0F))   // raw mesh UV for gradients
                .endVertex();
    }

    private static float sinSq(float p) {
        float v = Mth.sin(Mth.PI * p);
        return v * v;
    }

    private static float hash(int a, int b) {
        int h = a * 0x27D4EB2D ^ b * 0x165667B1;
        h ^= h >>> 15;
        h *= 0x2C1B3C6D;
        h ^= h >>> 12;
        return (h & 0xFFFF) / 65535.0F;
    }
}