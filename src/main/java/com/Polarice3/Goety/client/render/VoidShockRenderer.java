package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.VoidShockModel;
import com.Polarice3.Goety.common.entities.projectiles.VoidShock;
import com.Polarice3.Goety.utils.ColorUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class VoidShockRenderer extends EntityRenderer<VoidShock> {
    private static final ResourceLocation OUTER_TEXTURES = Goety.location("textures/entity/projectiles/void_shock_outer.png");
    private static final ResourceLocation INNER_TEXTURES = Goety.location("textures/entity/projectiles/void_shock_inner.png");
    private static final ResourceLocation TRAIL_TEXTURE = Goety.location("textures/particle/trail.png");
    private final VoidShockModel<VoidShock> model;
    private final RandomSource random = RandomSource.create();

    public VoidShockRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
        this.model = new VoidShockModel<>(renderManagerIn.bakeLayer(ModModelLayer.VOID_SHOCK));
    }

    @Override
    public void render(VoidShock entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();
        float f = Mth.rotLerp(partialTicks, entityIn.yRotO, entityIn.getYRot());
        float f1 = Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot());
        VertexConsumer VertexConsumer = bufferIn.getBuffer(RenderType.eyes(this.getTextureLocation(entityIn)));
        this.model.setupAnim(entityIn, f, f1);
        this.model.renderToBuffer(matrixStackIn, VertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        VertexConsumer VertexConsumer2 = bufferIn.getBuffer(RenderType.eyes(OUTER_TEXTURES));
        this.model.renderToBuffer(matrixStackIn, VertexConsumer2, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 0.4F);
        matrixStackIn.popPose();
        if (entityIn.hasTrail()) {
            double x = Mth.lerp(partialTicks, entityIn.xOld, entityIn.getX());
            double y = Mth.lerp(partialTicks, entityIn.yOld, entityIn.getY());
            double z = Mth.lerp(partialTicks, entityIn.zOld, entityIn.getZ());
            float randomF = 0.04F;
            ColorUtil colorUtil = new ColorUtil(ChatFormatting.DARK_PURPLE);
            float r = colorUtil.red() + this.random.nextFloat() * randomF;
            float g = colorUtil.green() + this.random.nextFloat() * randomF;
            float b = colorUtil.blue() + this.random.nextFloat() * randomF;
            matrixStackIn.pushPose();
            matrixStackIn.translate(-x, -y, -z);
            renderTrail(entityIn, partialTicks, matrixStackIn, bufferIn, r, g, b, 1.0F, packedLightIn);
            matrixStackIn.popPose();
        }
    }

    private void renderTrail(VoidShock entityIn, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, float red, float green, float blue, float alpha, int packedLightIn) {
        int samples = 0;
        int sampleSize = 1;
        double trailHeight = 0.25D;
        float trailZRot = 0;
        Vec3 topAngleVec = new Vec3(0.0D, trailHeight, 0.0D).zRot(trailZRot);
        Vec3 bottomAngleVec = new Vec3(0.0D, -trailHeight, 0.0D).zRot(trailZRot);
        Vec3 drawFrom = entityIn.getTrailPosition(0, partialTicks);
        VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderType.entityTranslucent(TRAIL_TEXTURE));
        while (samples < sampleSize) {
            Vec3 sample = entityIn.getTrailPosition(samples + 8, partialTicks);
            Vec3 draw1 = drawFrom;

            PoseStack.Pose posestack$pose = poseStack.last();
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

    protected int getBlockLightLevel(VoidShock entityIn, BlockPos pos) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(VoidShock entity) {
        return INNER_TEXTURES;
    }

}
