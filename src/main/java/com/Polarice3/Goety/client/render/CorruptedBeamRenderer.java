package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.projectiles.CorruptedBeam;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Beam render codes based on BeamEntityRenderer on @Thelnfamous1's Dungeon Gears
 */
public class CorruptedBeamRenderer<T extends CorruptedBeam> extends EntityRenderer<T> {
    private final static ResourceLocation beaconBeamCore = Goety.location("textures/entity/corrupted/beacon_beam_core.png");
    private final static ResourceLocation beaconBeamMain = Goety.location("textures/entity/corrupted/beacon_beam_main.png");
    private final static ResourceLocation beaconBeamGlow = Goety.location("textures/entity/corrupted/beacon_beam_glow.png");

    public CorruptedBeamRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    public void render(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        double distance = pEntity.beamTraceDistance(CorruptedBeam.MAX_RAYTRACE_DISTANCE, 1.0f, false);
        float speedModifier = -0.02f;
        drawBeams(distance, pEntity, pPartialTicks, speedModifier, pMatrixStack);
    }

    private static void drawBeams(double distance, CorruptedBeam entity, float ticks, float speedModifier, PoseStack pMatrixStack) {
        long gameTime = entity.level.getGameTime();
        double velocity = gameTime * speedModifier;
        float additiveThickness = (entity.getBeamWidth() * 1.75f) * calculateLaserFlickerModifier(gameTime);

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        pMatrixStack.pushPose();
        pMatrixStack.mulPose(Vector3f.YP.rotationDegrees((Mth.lerp(ticks, boundDegrees(-entity.getYRot()), boundDegrees(-entity.yRotO)))));
        pMatrixStack.mulPose(Vector3f.XP.rotationDegrees(Mth.lerp(ticks, boundDegrees(entity.getXRot()), boundDegrees(entity.xRotO))));

        PoseStack.Pose matrixstack$entry = pMatrixStack.last();
        Matrix3f matrixNormal = matrixstack$entry.normal();
        Matrix4f positionMatrix = matrixstack$entry.pose();
        drawClosingBeam(buffer.getBuffer(ModRenderType.magicBeam(beaconBeamGlow)), positionMatrix, matrixNormal, additiveThickness, distance / 10, 0.5, 1, ticks, 0.9f);
        drawBeam(buffer.getBuffer(ModRenderType.magicBeam(beaconBeamMain)), positionMatrix, matrixNormal, entity.getBeamWidth(), distance, velocity, velocity + distance * 1.5, ticks, 0.7f);
        drawBeam(buffer.getBuffer(ModRenderType.magicBeam(beaconBeamCore)), positionMatrix, matrixNormal, entity.getBeamWidth() * 0.7f, distance, velocity, velocity + distance * 1.5, ticks, 1f);
        pMatrixStack.popPose();
        buffer.endBatch();
    }

    private static float boundDegrees(float rotation) {
        return (rotation % 360 + 360) % 360;
    }

    private static float calculateLaserFlickerModifier(long gameTime) {
        return 0.9f + 0.1f * Mth.sin(gameTime * 0.99f) * Mth.sin(gameTime * 0.3f) * Mth.sin(gameTime * 0.1f);
    }

    private static void drawBeam(VertexConsumer builder, Matrix4f positionMatrix, Matrix3f matrixNormalIn, float thickness, double distance, double v1, double v2, float ticks, float alpha) {
        Vector3f vector3f = new Vector3f(0.0f, 1.0f, 0.0f);
        vector3f.transform(matrixNormalIn);
        float xMin = -thickness;
        float xMax = thickness;
        float yMin = -thickness - 0.115f;
        float yMax = thickness - 0.115f;
        float zMin = 0;
        float zMax = (float) distance;

        Vector4f vec1 = new Vector4f(xMin, yMin, zMin, 1.0F);
        vec1.transform(positionMatrix);
        Vector4f vec2 = new Vector4f(xMin, yMin, zMax, 1.0F);
        vec2.transform(positionMatrix);
        Vector4f vec3 = new Vector4f(xMin, yMax, zMax, 1.0F);
        vec3.transform(positionMatrix);
        Vector4f vec4 = new Vector4f(xMin, yMax, zMin, 1.0F);
        vec4.transform(positionMatrix);
        drawQuad(builder, (float) v1, (float) v2, alpha, vector3f, vec1, vec2, vec3, vec4);

        vec1 = new Vector4f(xMax, yMin, zMin, 1.0F);
        vec1.transform(positionMatrix);
        vec2 = new Vector4f(xMax, yMin, zMax, 1.0F);
        vec2.transform(positionMatrix);
        vec3 = new Vector4f(xMax, yMax, zMax, 1.0F);
        vec3.transform(positionMatrix);
        vec4 = new Vector4f(xMax, yMax, zMin, 1.0F);
        vec4.transform(positionMatrix);
        drawQuad(builder, (float) v1, (float) v2, alpha, vector3f, vec1, vec2, vec3, vec4);

        vec1 = new Vector4f(xMin, yMax, zMin, 1.0F);
        vec1.transform(positionMatrix);
        vec2 = new Vector4f(xMin, yMax, zMax, 1.0F);
        vec2.transform(positionMatrix);
        vec3 = new Vector4f(xMax, yMax, zMax, 1.0F);
        vec3.transform(positionMatrix);
        vec4 = new Vector4f(xMax, yMax, zMin, 1.0F);
        vec4.transform(positionMatrix);
        drawQuad(builder, (float) v1, (float) v2, alpha, vector3f, vec1, vec2, vec3, vec4);

        vec1 = new Vector4f(xMin, yMin, zMin, 1.0F);
        vec1.transform(positionMatrix);
        vec2 = new Vector4f(xMin, yMin, zMax, 1.0F);
        vec2.transform(positionMatrix);
        vec3 = new Vector4f(xMax, yMin, zMax, 1.0F);
        vec3.transform(positionMatrix);
        vec4 = new Vector4f(xMax, yMin, zMin, 1.0F);
        vec4.transform(positionMatrix);
        drawQuad(builder, (float) v1, (float) v2, alpha, vector3f, vec1, vec2, vec3, vec4);
    }

    private static void drawClosingBeam(VertexConsumer builder, Matrix4f positionMatrix, Matrix3f matrixNormalIn, float thickness, double distance, double v1, double v2, float ticks, float alpha) {
        Vector3f vector3f = new Vector3f(0.0f, 1.0f, 0.0f);
        vector3f.transform(matrixNormalIn);

        float xMin = -thickness;
        float xMax = thickness;
        float yMin = -thickness - 0.115f;
        float yMax = thickness - 0.115f;
        float zMin = 0;
        float zMax = (float) distance;

        Vector4f vec1 = new Vector4f(xMin, yMin, zMin, 1.0F);
        vec1.transform(positionMatrix);
        Vector4f vec2 = new Vector4f(0, 0, zMax, 1.0F);
        vec2.transform(positionMatrix);
        Vector4f vec3 = new Vector4f(0, 0, zMax, 1.0F);
        vec3.transform(positionMatrix);
        Vector4f vec4 = new Vector4f(xMin, yMax, zMin, 1.0F);
        vec4.transform(positionMatrix);
        drawQuad(builder, (float) v1, (float) v2, alpha, vector3f, vec1, vec2, vec3, vec4);

        vec1 = new Vector4f(xMax, yMin, zMin, 1.0F);
        vec1.transform(positionMatrix);
        vec2 = new Vector4f(0, 0, zMax, 1.0F);
        vec2.transform(positionMatrix);
        vec3 = new Vector4f(0, 0, zMax, 1.0F);
        vec3.transform(positionMatrix);
        vec4 = new Vector4f(xMax, yMax, zMin, 1.0F);
        vec4.transform(positionMatrix);
        drawQuad(builder, (float) v1, (float) v2, alpha, vector3f, vec1, vec2, vec3, vec4);

        vec1 = new Vector4f(xMin, yMax, zMin, 1.0F);
        vec1.transform(positionMatrix);
        vec2 = new Vector4f(0, 0, zMax, 1.0F);
        vec2.transform(positionMatrix);
        vec3 = new Vector4f(0, 0, zMax, 1.0F);
        vec3.transform(positionMatrix);
        vec4 = new Vector4f(xMax, yMax, zMin, 1.0F);
        vec4.transform(positionMatrix);
        drawQuad(builder, (float) v1, (float) v2, alpha, vector3f, vec1, vec2, vec3, vec4);

        vec1 = new Vector4f(xMin, yMin, zMin, 1.0F);
        vec1.transform(positionMatrix);
        vec2 = new Vector4f(0, 0, zMax, 1.0F);
        vec2.transform(positionMatrix);
        vec3 = new Vector4f(0, 0, zMax, 1.0F);
        vec3.transform(positionMatrix);
        vec4 = new Vector4f(xMax, yMin, zMin, 1.0F);
        vec4.transform(positionMatrix);
        drawQuad(builder, (float) v1, (float) v2, alpha, vector3f, vec1, vec2, vec3, vec4);
    }

    private static void drawQuad(VertexConsumer builder, float v1, float v2, float alpha, Vector3f vector3f, Vector4f vec1, Vector4f vec2, Vector4f vec3, Vector4f vec4) {
        builder.vertex(vec4.x(), vec4.y(), vec4.z(), 1.0F, 1.0F, 1.0F, alpha, 0, v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
        builder.vertex(vec3.x(), vec3.y(), vec3.z(), 1.0F, 1.0F, 1.0F, alpha, 0, v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
        builder.vertex(vec2.x(), vec2.y(), vec2.z(), 1.0F, 1.0F, 1.0F, alpha, 1, v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
        builder.vertex(vec1.x(), vec1.y(), vec1.z(), 1.0F, 1.0F, 1.0F, alpha, 1, v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
    }

    @Override
    public ResourceLocation getTextureLocation(T p_110775_1_) {
        return beaconBeamCore;
    }
}
