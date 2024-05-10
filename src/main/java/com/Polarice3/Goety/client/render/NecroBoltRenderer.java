package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.projectiles.NecroBolt;
import com.Polarice3.Goety.utils.ColorUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class NecroBoltRenderer<T extends NecroBolt> extends EntityRenderer<T> {
    public static final ResourceLocation BASE = Goety.location("textures/particle/necro_bolt_base.png");
    public static final ResourceLocation MID = Goety.location("textures/particle/necro_bolt_mid.png");
    public static final ResourceLocation TOP = Goety.location("textures/particle/necro_bolt_top.png");
    private static final ResourceLocation TRAIL = Goety.location("textures/entity/projectiles/trail.png");

    public NecroBoltRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    protected int getBlockLightLevel(T entityIn, BlockPos partialTicks) {
        return 15;
    }

    public void render(T entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();
        PoseStack.Pose matrixstack$entry = matrixStackIn.last();
        Matrix4f matrix4f = matrixstack$entry.pose();
        Matrix3f matrix3f = matrixstack$entry.normal();
        RenderType renderType = RenderType.entityTranslucent(this.getTextureLocation(entityIn));
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(renderType);
        matrixStackIn.translate(0, 0.5, 0);
        this.render(ivertexbuilder, this.entityRenderDispatcher.camera, entityIn, partialTicks, packedLightIn, matrix4f, matrix3f, 0);
        RenderType renderType2 = RenderType.entityTranslucent(MID);
        VertexConsumer ivertexbuilder2 = bufferIn.getBuffer(renderType2);
        this.render(ivertexbuilder2, this.entityRenderDispatcher.camera, entityIn, partialTicks, packedLightIn, matrix4f, matrix3f, 0);
        RenderType renderType3 = RenderType.entityTranslucent(TOP);
        VertexConsumer ivertexbuilder3 = bufferIn.getBuffer(renderType3);
        this.render(ivertexbuilder3, this.entityRenderDispatcher.camera, entityIn, partialTicks, packedLightIn, matrix4f, matrix3f, 1);
        matrixStackIn.popPose();
        if (entityIn.hasTrail()) {
            double x = Mth.lerp(partialTicks, entityIn.xOld, entityIn.getX());
            double y = Mth.lerp(partialTicks, entityIn.yOld, entityIn.getY());
            double z = Mth.lerp(partialTicks, entityIn.zOld, entityIn.getZ());
            matrixStackIn.pushPose();
            matrixStackIn.translate(-x, -y, -z);
            ColorUtil colorUtil = new ColorUtil(0x9df530);
            renderTrail(entityIn, partialTicks, matrixStackIn, bufferIn, colorUtil.red, colorUtil.green, colorUtil.blue, 0.6F, packedLightIn);
            matrixStackIn.popPose();
        }
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    public void render(VertexConsumer vertexConsumer, Camera camera, NecroBolt projectile, float partialTicks, int packedLightIn, Matrix4f matrix4f, Matrix3f matrix3f, int mode) {
        Quaternionf quaternionf = new Quaternionf(camera.rotation());
        float f3 = Mth.lerp(partialTicks, projectile.oRoll, projectile.roll);
        float f4 = 0.5F;
        if (mode == 1){
            f3 *= 0.5F;
            f4 *= projectile.getGlow;
        }
        quaternionf.rotateZ(f3);

        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};

        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotate(quaternionf);
            vector3f.mul(f4);
        }

        vertexConsumer.vertex(matrix4f, avector3f[0].x, avector3f[0].y, avector3f[0].z).color(255, 255, 255, 255).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix4f, avector3f[1].x, avector3f[1].y, avector3f[1].z).color(255, 255, 255, 255).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix4f, avector3f[2].x, avector3f[2].y, avector3f[2].z).color(255, 255, 255, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix4f, avector3f[3].x, avector3f[3].y, avector3f[3].z).color(255, 255, 255, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
    }

    /**
     * Based on codes from @AlexModGuy: <a href="https://github.com/AlexModGuy/AlexsCaves/blob/main/src/main/java/com/github/alexmodguy/alexscaves/client/render/entity/WaterBoltRenderer.java#L49">...</a>
     */
    private void renderTrail(NecroBolt entityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, float red, float green, float blue, float alpha, int packedLightIn) {
        int samples = 0;
        int sampleSize = 1;
        double trailHeight = 0.5D;
        float trailZRot = 0;
        Vec3 topAngleVec = new Vec3(0.0D, trailHeight, 0.0D).zRot(trailZRot);
        Vec3 bottomAngleVec = new Vec3(0.0D, -trailHeight, 0.0D).zRot(trailZRot);
        Vec3 drawFrom = entityIn.getTrailPosition(0, partialTicks);
        VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderType.entityTranslucent(TRAIL));
        while (samples < sampleSize) {
            Vec3 sample = entityIn.getTrailPosition(samples + 8, partialTicks);
            Vec3 draw1 = drawFrom;

            PoseStack.Pose posestack$pose = matrixStackIn.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            float f2 = entityIn.tickCount % 8 / 8.0F;
            float f3 = f2 + 0.5F;
            vertexconsumer.vertex(matrix4f, (float) draw1.x + (float) bottomAngleVec.x, (float) draw1.y + (float) bottomAngleVec.y, (float) draw1.z + (float) bottomAngleVec.z).color(red, green, blue, alpha).uv(f2, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) sample.x + (float) bottomAngleVec.x, (float) sample.y + (float) bottomAngleVec.y, (float) sample.z + (float) bottomAngleVec.z).color(red, green, blue, alpha).uv(f3, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) sample.x + (float) topAngleVec.x, (float) sample.y + (float) topAngleVec.y, (float) sample.z + (float) topAngleVec.z).color(red, green, blue, alpha).uv(f3, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) draw1.x + (float) topAngleVec.x, (float) draw1.y + (float) topAngleVec.y, (float) draw1.z + (float) topAngleVec.z).color(red, green, blue, alpha).uv(f2, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            samples++;
            drawFrom = sample;
        }
    }

    public ResourceLocation getTextureLocation(T entity){
        return BASE;
    }
}
