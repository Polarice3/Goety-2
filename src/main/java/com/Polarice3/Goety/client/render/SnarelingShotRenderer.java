package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.WebShotModel;
import com.Polarice3.Goety.common.entities.projectiles.SnarelingShot;
import com.Polarice3.Goety.utils.ColorUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class SnarelingShotRenderer<T extends SnarelingShot> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE = Goety.location("textures/entity/projectiles/snareling_shot.png");
    private static final ResourceLocation TRAIL = Goety.location("textures/entity/projectiles/minor_trail.png");
    public WebShotModel<T> model;

    public SnarelingShotRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
        this.model = new WebShotModel<>(p_174008_.bakeLayer(ModModelLayer.WEB_SHOT));
        this.shadowRadius = 0.5F;
    }

    public void render(T pEntity, float pYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.translate(0, pEntity.getBoundingBox().getYsize() * 0.5F, 0);
        this.scale(pEntity, pPoseStack, 1.0F);
        Vec3 vec3 = pEntity.getDeltaMovement();
        float xRot = -((float) (Mth.atan2(vec3.horizontalDistance(), vec3.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (Mth.atan2(vec3.z, vec3.x) * (double) (180.0F / (float) Math.PI)) + 90.0F);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(xRot));
        VertexConsumer consumer = pBuffer.getBuffer(RenderType.eyes(getTextureLocation(pEntity)));
        this.model.renderToBuffer(pPoseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.5F);
        pPoseStack.popPose();
        if (pEntity.hasTrail()) {
            double x = Mth.lerp(pPartialTicks, pEntity.xOld, pEntity.getX());
            double y = Mth.lerp(pPartialTicks, pEntity.yOld, pEntity.getY());
            double z = Mth.lerp(pPartialTicks, pEntity.zOld, pEntity.getZ());
            pPoseStack.pushPose();
            pPoseStack.translate(-x, -y, -z);
            ColorUtil colorUtil = new ColorUtil(0xfdffc2);
            renderTrail(pEntity, pPartialTicks, pPoseStack, pBuffer, colorUtil.red, colorUtil.green, colorUtil.blue, 0.6F, pPackedLight);
            pPoseStack.popPose();
        }
        super.render(pEntity, pYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(T p_114482_) {
        return TEXTURE;
    }

    protected void scale(T p_116294_, PoseStack p_116295_, float p_116296_) {
        p_116295_.scale(p_116296_, p_116296_, p_116296_);
    }

    /**
     * Based on codes from @AlexModGuy: <a href="https://github.com/AlexModGuy/AlexsCaves/blob/main/src/main/java/com/github/alexmodguy/alexscaves/client/render/entity/WaterBoltRenderer.java#L49">...</a>
     */
    private void renderTrail(T entityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, float red, float green, float blue, float alpha, int packedLightIn) {
        int samples = 0;
        int sampleSize = 1;
        double trailHeight = 0.25D;
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

}
