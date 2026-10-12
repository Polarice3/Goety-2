package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.projectiles.AbstractBeam;
import com.Polarice3.Goety.common.entities.projectiles.CorruptedBeam;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Beam render codes based on BeamEntityRenderer on @Thelnfamous1's Dungeon Gears
 */
public class CorruptedBeamRenderer<T extends CorruptedBeam> extends EntityRenderer<T> {
    private final static ResourceLocation beaconBeamCore = Goety.location("textures/entity/corrupted/beacon_beam_core.png");
    private final static ResourceLocation beaconBeamMain = Goety.location("textures/entity/corrupted/beacon_beam_main.png");
    private final static ResourceLocation beaconBeamGlow = Goety.location("textures/entity/corrupted/beacon_beam_glow.png");
    private static final List<QueuedBeam> QUEUE = new ArrayList<>();

    public CorruptedBeamRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public void render(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        double distance = pEntity.beamTraceDistance(AbstractBeam.MAX_RAYTRACE_DISTANCE, 1.0f, false);
        float speedModifier = -0.02f;
        drawBeams(distance, pEntity, pPartialTicks, speedModifier, pMatrixStack);
    }

    private static void drawBeams(double distance, CorruptedBeam entity, float ticks, float speedModifier, PoseStack pMatrixStack) {
        long gameTime = entity.level.getGameTime();
        double velocity = gameTime * speedModifier;
        float additiveThickness = (entity.getBeamWidth() * 1.75f) * calculateLaserFlickerModifier(gameTime);

        float yRot = -Mth.rotLerp(ticks, entity.yRotO, entity.getYRot());
        float xRot = Mth.rotLerp(ticks, entity.xRotO, entity.getXRot());
        Vec3 offset = Vec3.ZERO;
        Optional<LivingEntity> optionalOwner = entity.getOptionalOwner();
        if (optionalOwner.isPresent()) {
            LivingEntity owner = optionalOwner.get();
            yRot = -Mth.rotLerp(ticks, owner.yRotO, owner.getYRot());
            xRot = Mth.rotLerp(ticks, owner.xRotO, owner.getXRot());
            offset = new Vec3(
                    Mth.lerp(ticks, owner.xo, owner.getX()) - Mth.lerp(ticks, entity.xo, entity.getX()),
                    Mth.lerp(ticks, owner.yo, owner.getY()) - Mth.lerp(ticks, entity.yo, entity.getY()),
                    Mth.lerp(ticks, owner.zo, owner.getZ()) - Mth.lerp(ticks, entity.zo, entity.getZ())
            ).add(entity.getOffsetVector(owner, ticks));
        }

        pMatrixStack.pushPose();
        pMatrixStack.translate(offset.x, offset.y, offset.z);
        pMatrixStack.mulPose(Axis.YP.rotationDegrees(yRot));
        pMatrixStack.mulPose(Axis.XP.rotationDegrees(xRot));
        PoseStack.Pose matrixstack$entry = pMatrixStack.last();
        QUEUE.add(new QueuedBeam(new Matrix4f(matrixstack$entry.pose()), new Matrix3f(matrixstack$entry.normal()), distance, entity.getBeamWidth(), additiveThickness, velocity, ticks));
        pMatrixStack.popPose();
    }

    public static void renderQueued(MultiBufferSource.BufferSource buffer) {
        if (QUEUE.isEmpty()) {
            return;
        }
        for (QueuedBeam beam : QUEUE) {
            drawClosingBeam(buffer.getBuffer(ModRenderType.magicBeam(beaconBeamGlow)), beam.pose(), beam.normal(), beam.additiveThickness(), beam.distance() / 10, 0.5, 1, beam.ticks(), 0.9f);
        }
        buffer.endBatch(ModRenderType.magicBeam(beaconBeamGlow));
        for (QueuedBeam beam : QUEUE) {
            drawBeam(buffer.getBuffer(ModRenderType.magicBeam(beaconBeamMain)), beam.pose(), beam.normal(), beam.width(), beam.distance(), beam.velocity(), beam.velocity() + beam.distance() * 1.5, beam.ticks(), 0.7f);
        }
        buffer.endBatch(ModRenderType.magicBeam(beaconBeamMain));
        for (QueuedBeam beam : QUEUE) {
            drawBeam(buffer.getBuffer(ModRenderType.magicBeam(beaconBeamCore)), beam.pose(), beam.normal(), beam.width() * 0.7f, beam.distance(), beam.velocity(), beam.velocity() + beam.distance() * 1.5, beam.ticks(), 1f);
        }
        buffer.endBatch(ModRenderType.magicBeam(beaconBeamCore));
        QUEUE.clear();
    }

    private static float boundDegrees(float rotation) {
        return (rotation % 360 + 360) % 360;
    }

    private static float calculateLaserFlickerModifier(long gameTime) {
        return 0.9f + 0.1f * Mth.sin(gameTime * 0.99f) * Mth.sin(gameTime * 0.3f) * Mth.sin(gameTime * 0.1f);
    }

    private static void drawBeam(VertexConsumer builder, Matrix4f positionMatrix, Matrix3f matrixNormalIn, float thickness, double distance, double v1, double v2, float ticks, float alpha) {
        Vector3f vector3f = new Vector3f(0.0f, 1.0f, 0.0f);
        matrixNormalIn.transform(vector3f);
        float xMin = -thickness;
        float xMax = thickness;
        float yMin = -thickness - 0.115f;
        float yMax = thickness - 0.115f;
        float zMin = 0;
        float zMax = (float) distance;

        Vector4f vec1 = new Vector4f(xMin, yMin, zMin, 1.0F);
        positionMatrix.transform(vec1);
        Vector4f vec2 = new Vector4f(xMin, yMin, zMax, 1.0F);
        positionMatrix.transform(vec2);
        Vector4f vec3 = new Vector4f(xMin, yMax, zMax, 1.0F);
        positionMatrix.transform(vec3);
        Vector4f vec4 = new Vector4f(xMin, yMax, zMin, 1.0F);
        positionMatrix.transform(vec4);
        drawQuad(builder, (float) v1, (float) v2, alpha, vector3f, vec1, vec2, vec3, vec4);

        vec1 = new Vector4f(xMax, yMin, zMin, 1.0F);
        positionMatrix.transform(vec1);
        vec2 = new Vector4f(xMax, yMin, zMax, 1.0F);
        positionMatrix.transform(vec2);
        vec3 = new Vector4f(xMax, yMax, zMax, 1.0F);
        positionMatrix.transform(vec3);
        vec4 = new Vector4f(xMax, yMax, zMin, 1.0F);
        positionMatrix.transform(vec4);
        drawQuad(builder, (float) v1, (float) v2, alpha, vector3f, vec1, vec2, vec3, vec4);

        vec1 = new Vector4f(xMin, yMax, zMin, 1.0F);
        positionMatrix.transform(vec1);
        vec2 = new Vector4f(xMin, yMax, zMax, 1.0F);
        positionMatrix.transform(vec2);
        vec3 = new Vector4f(xMax, yMax, zMax, 1.0F);
        positionMatrix.transform(vec3);
        vec4 = new Vector4f(xMax, yMax, zMin, 1.0F);
        positionMatrix.transform(vec4);
        drawQuad(builder, (float) v1, (float) v2, alpha, vector3f, vec1, vec2, vec3, vec4);

        vec1 = new Vector4f(xMin, yMin, zMin, 1.0F);
        positionMatrix.transform(vec1);
        vec2 = new Vector4f(xMin, yMin, zMax, 1.0F);
        positionMatrix.transform(vec2);
        vec3 = new Vector4f(xMax, yMin, zMax, 1.0F);
        positionMatrix.transform(vec3);
        vec4 = new Vector4f(xMax, yMin, zMin, 1.0F);
        positionMatrix.transform(vec4);
        drawQuad(builder, (float) v1, (float) v2, alpha, vector3f, vec1, vec2, vec3, vec4);
    }

    private static void drawClosingBeam(VertexConsumer builder, Matrix4f positionMatrix, Matrix3f matrixNormalIn, float thickness, double distance, double v1, double v2, float ticks, float alpha) {
        Vector3f vector3f = new Vector3f(0.0f, 1.0f, 0.0f);
        matrixNormalIn.transform(vector3f);

        float xMin = -thickness;
        float xMax = thickness;
        float yMin = -thickness - 0.115f;
        float yMax = thickness - 0.115f;
        float zMin = 0;
        float zMax = (float) distance;

        Vector4f vec1 = new Vector4f(xMin, yMin, zMin, 1.0F);
        positionMatrix.transform(vec1);
        Vector4f vec2 = new Vector4f(0, 0, zMax, 1.0F);
        positionMatrix.transform(vec2);
        Vector4f vec3 = new Vector4f(0, 0, zMax, 1.0F);
        positionMatrix.transform(vec3);
        Vector4f vec4 = new Vector4f(xMin, yMax, zMin, 1.0F);
        positionMatrix.transform(vec4);
        drawQuad(builder, (float) v1, (float) v2, alpha, vector3f, vec1, vec2, vec3, vec4);

        vec1 = new Vector4f(xMax, yMin, zMin, 1.0F);
        positionMatrix.transform(vec1);
        vec2 = new Vector4f(0, 0, zMax, 1.0F);
        positionMatrix.transform(vec2);
        vec3 = new Vector4f(0, 0, zMax, 1.0F);
        positionMatrix.transform(vec3);
        vec4 = new Vector4f(xMax, yMax, zMin, 1.0F);
        positionMatrix.transform(vec4);
        drawQuad(builder, (float) v1, (float) v2, alpha, vector3f, vec1, vec2, vec3, vec4);

        vec1 = new Vector4f(xMin, yMax, zMin, 1.0F);
        positionMatrix.transform(vec1);
        vec2 = new Vector4f(0, 0, zMax, 1.0F);
        positionMatrix.transform(vec2);
        vec3 = new Vector4f(0, 0, zMax, 1.0F);
        positionMatrix.transform(vec3);
        vec4 = new Vector4f(xMax, yMax, zMin, 1.0F);
        positionMatrix.transform(vec4);
        drawQuad(builder, (float) v1, (float) v2, alpha, vector3f, vec1, vec2, vec3, vec4);

        vec1 = new Vector4f(xMin, yMin, zMin, 1.0F);
        positionMatrix.transform(vec1);
        vec2 = new Vector4f(0, 0, zMax, 1.0F);
        positionMatrix.transform(vec2);
        vec3 = new Vector4f(0, 0, zMax, 1.0F);
        positionMatrix.transform(vec3);
        vec4 = new Vector4f(xMax, yMin, zMin, 1.0F);
        positionMatrix.transform(vec4);
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

    private record QueuedBeam(Matrix4f pose, Matrix3f normal, double distance, float width, float additiveThickness, double velocity, float ticks) {
    }
}
