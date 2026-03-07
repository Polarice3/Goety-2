package com.Polarice3.Goety.client.render.visual;

import com.Polarice3.Goety.utils.TrailEffect;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

// A trail renderer that forces all quads in the trail to perfectly face the camera
// A glitch might happen in first-person perspective because the sight vector shares the same direction as the trail vector, thus the cross product cannot be computed correctly
// Offset a little bit to prevent the sight vector from sharing the exact same direction as the trail, or use a different TrailOffsetFunction
public class TrailRenderer {
    public static void render(TrailEffect effect, VertexConsumer consumer, PoseStack stack, TrailEffect.TrailOffsetFunction function, boolean solid, float r, float g, float b, float a, int light) {
        render(effect, consumer, stack, function, solid, false, r, g, b, a, 1, 0, 0, 1, light);
    }

    public static void render(TrailEffect effect, VertexConsumer consumer, PoseStack stack, TrailEffect.TrailOffsetFunction function, boolean solid, boolean particleFormat, float r, float g, float b, float a, float u0, float u1, float v0, float v1, int light) {
        int size = effect.renderPoints.size();
        if (size < 2) return;

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        float halfWidth = effect.getWidth() / 2;

        Vec3[] tangents = new Vec3[size];
        for (int i = 0; i < size; i++) {
            if (i == 0) {
                Vec3 delta = effect.renderPoints.get(1).pos().subtract(effect.renderPoints.get(0).pos());
                tangents[i] = delta.lengthSqr() < 1e-8 ? new Vec3(0, 0, 1) : delta.normalize();
            } else if (i == size - 1) {
                Vec3 delta = effect.renderPoints.get(size - 1).pos().subtract(effect.renderPoints.get(size - 2).pos());
                tangents[i] = delta.lengthSqr() < 1e-8 ? tangents[i - 1] : delta.normalize();
            } else {
                Vec3 prevToNext = effect.renderPoints.get(i + 1).pos().subtract(effect.renderPoints.get(i - 1).pos());
                tangents[i] = prevToNext.lengthSqr() < 1e-8 ? tangents[i - 1] : prevToNext.normalize();
            }
        }

        Vec3[] upperOffsets = new Vec3[size];
        Vec3[] lowerOffsets = new Vec3[size];
        for (int i = 0; i < size; i++) {
            Vec3 tangent = tangents[i];
            if (tangent.lengthSqr() < 0.5) {
                tangent = new Vec3(0, 1, 0);
            }
            Vec3 offsetDir = function.calculateTrailOffset(effect.renderPoints.get(i).pos().subtract(camera.getPosition()), camera.getXRot(), camera.getYRot(), tangent).normalize();
            if (offsetDir.lengthSqr() < 0.5) {
                offsetDir = new Vec3(0, 1, 0);
            }
            upperOffsets[i] = offsetDir.scale(halfWidth);
            lowerOffsets[i] = offsetDir.scale(-halfWidth);
            if (i > 0 && upperOffsets[i].normalize().dot(upperOffsets[i - 1].normalize()) < 0) {
                upperOffsets[i] = upperOffsets[i].reverse();
                lowerOffsets[i] = lowerOffsets[i].reverse();
            }
        }

        PoseStack.Pose pose = stack.last();
        for (int i = 0; i < size - 1; i++) {
            TrailEffect.TrailPoint from = effect.renderPoints.get(i);
            TrailEffect.TrailPoint to = effect.renderPoints.get(i + 1);

            Vec3 fromUpper = from.pos().add(upperOffsets[i]);
            Vec3 toUpper = to.pos().add(upperOffsets[i + 1]);
            Vec3 toLower = to.pos().add(lowerOffsets[i + 1]);
            Vec3 fromLower = from.pos().add(lowerOffsets[i]);

            float fromAlpha = solid ? 1 : Mth.clamp(a * from.progressFactor(), 0, 1);
            float toAlpha = solid ? 1 : Mth.clamp(a * to.progressFactor(), 0, 1);

            vertex(consumer, pose, fromUpper, Mth.lerp(from.progressFactor(), u0, u1), v0, r, g, b, fromAlpha, light, particleFormat);
            vertex(consumer, pose, toUpper, Mth.lerp(to.progressFactor(), u0, u1), v0, r, g, b, toAlpha, light, particleFormat);
            vertex(consumer, pose, toLower, Mth.lerp(to.progressFactor(), u0, u1), v1, r, g, b, toAlpha, light, particleFormat);
            vertex(consumer, pose, fromLower, Mth.lerp(from.progressFactor(), u0, u1), v1, r, g, b, fromAlpha, light, particleFormat);
        }
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, Vec3 pos, float u, float v, float r, float g, float b, float alpha, int light, boolean particleFormat) {
        if (particleFormat) {
            consumer.vertex(pose.pose(), (float) pos.x(), (float) pos.y(), (float) pos.z())
                    .uv(u, v).color(r, g, b, alpha).uv2(light).endVertex();
        } else {
            consumer.vertex(pose.pose(), (float) pos.x(), (float) pos.y(), (float) pos.z())
                    .color(r, g, b, alpha).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(pose.normal(), 0, 1, 0).endVertex();
        }
    }
}
