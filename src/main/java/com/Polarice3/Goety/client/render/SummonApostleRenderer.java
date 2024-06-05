package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.util.SummonApostle;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class SummonApostleRenderer extends EntityRenderer<SummonApostle> {
    public static final ResourceLocation TEXTURE = Goety.location("textures/entity/cultist/summon_apostle.png");
    public static final ResourceLocation RING = Goety.location("textures/entity/cultist/summon_apostle_ring.png");

    public SummonApostleRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    public void render(SummonApostle entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        float f1 = (float)entityIn.tickCount + partialTicks;
        float f2 = Mth.clamp(entityIn.tickCount * 0.1F, 0.0F, 3.0F);
        matrixStackIn.pushPose();
        VertexConsumer consumer = bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        matrixStackIn.translate(0.0F, 0.001F, 0.0F);
        matrixStackIn.scale(f2, f2, f2);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F + f1));
        PoseStack.Pose pose = matrixStackIn.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        this.drawRing(matrix4f, matrix3f, consumer);
        matrixStackIn.popPose();
        matrixStackIn.pushPose();
        VertexConsumer consumer2 = bufferIn.getBuffer(RenderType.entityCutoutNoCull(RING));
        matrixStackIn.translate(0.0F, 2.0F, 0.0F);
        matrixStackIn.scale(f2, f2, f2);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F - (f1 / 2)));
        PoseStack.Pose pose2 = matrixStackIn.last();
        Matrix4f matrix4f2 = pose2.pose();
        Matrix3f matrix3f2 = pose2.normal();
        this.drawRing(matrix4f2, matrix3f2, consumer2);
        matrixStackIn.popPose();
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.0F, 4.0F, 0.0F);
        matrixStackIn.scale(f2, f2, f2);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F + (f1 / 2)));
        PoseStack.Pose pose3 = matrixStackIn.last();
        Matrix4f matrix4f3 = pose3.pose();
        Matrix3f matrix3f3 = pose3.normal();
        this.drawRing(matrix4f3, matrix3f3, consumer2);
        matrixStackIn.popPose();
    }

    public void drawRing(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer consumer) {
        this.drawVertex(matrix4f, matrix3f, consumer, -1.0F, 0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F);
        this.drawVertex(matrix4f, matrix3f, consumer, -1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F);
        this.drawVertex(matrix4f, matrix3f, consumer, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F);
        this.drawVertex(matrix4f, matrix3f, consumer, 1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F);
    }

    public void drawVertex(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer vertexConsumer, float x, float y, float z, float u, float v, float normalX, float normalY, float normalZ) {
        vertexConsumer.vertex(matrix4f, x, y, z).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(matrix3f, normalX, normalY, normalZ).endVertex();
    }

    public boolean shouldRender(SummonApostle p_114491_, Frustum p_114492_, double p_114493_, double p_114494_, double p_114495_) {
        if (p_114491_.noCulling) {
            return true;
        } else {
            double size = 32.0D;
            AABB aabb = p_114491_.getBoundingBoxForCulling().inflate(16.0D);
            if (aabb.hasNaN() || aabb.getSize() == 0.0D) {
                aabb = new AABB(p_114491_.getX() - size, p_114491_.getY() - size, p_114491_.getZ() - size, p_114491_.getX() + size, p_114491_.getY() + size, p_114491_.getZ() + size);
            }

            return p_114492_.isVisible(aabb);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(SummonApostle p_114482_) {
        return TEXTURE;
    }
}
