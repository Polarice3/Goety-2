package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.VoidShockBombModel;
import com.Polarice3.Goety.common.entities.projectiles.VoidShockBomb;
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
import org.joml.Quaternionf;

public class VoidShockBombRenderer extends EntityRenderer<VoidShockBomb> {
    private static final ResourceLocation OUTER_TEXTURES = Goety.location("textures/entity/projectiles/void_shock_bomb_outer.png");
    private static final ResourceLocation INNER_TEXTURES = Goety.location("textures/entity/projectiles/void_shock_bomb_inner.png");
    private static final ResourceLocation TRAIL_TEXTURE = Goety.location("textures/entity/projectiles/trail.png");
    private final VoidShockBombModel<VoidShockBomb> model;
    private final RandomSource random = RandomSource.create();

    public VoidShockBombRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
        this.model = new VoidShockBombModel<>(renderManagerIn.bakeLayer(ModModelLayer.VOID_SHOCK_BOMB));
    }

    @Override
    public void render(VoidShockBomb entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        if (entityIn.growTick <= 0) {
            matrixStackIn.pushPose();
            matrixStackIn.mulPose((new Quaternionf()).setAngleAxis(entityYaw * ((float)Math.PI / 180F), 0, -1.0F, 0));
            VertexConsumer VertexConsumer = bufferIn.getBuffer(RenderType.eyes(this.getTextureLocation(entityIn)));
            model.setupAnim(entityIn, 0, 0, entityIn.tickCount + partialTicks, 0, 0);
            model.renderToBuffer(matrixStackIn, VertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
            VertexConsumer VertexConsumer2 = bufferIn.getBuffer(RenderType.eyes(OUTER_TEXTURES));
            model.renderToBuffer(matrixStackIn, VertexConsumer2, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 0.4F);
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
        } else {
            matrixStackIn.pushPose();
            matrixStackIn.mulPose((new Quaternionf()).setAngleAxis(entityYaw * ((float)Math.PI / 180F), 0, -1.0F, 0));
            float scale2 = entityIn.size;
            matrixStackIn.translate(0, scale2 / 4.0D, 0);
            matrixStackIn.scale(-scale2, -scale2, scale2);
            VertexConsumer VertexConsumer = bufferIn.getBuffer(RenderType.eyes(this.getTextureLocation(entityIn)));
            model.setupAnim(entityIn, 0, 0, entityIn.tickCount + partialTicks, 0, 0);
            model.renderToBuffer(matrixStackIn, VertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, entityIn.alpha);
            matrixStackIn.popPose();
        }
    }

    private void renderTrail(VoidShockBomb entityIn, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, float trailR, float trailG, float trailB, float trailA, int packedLightIn) {
        int sampleSize = 10;
        float trailHeight = 0.5F;
        float trailYRot = 0;
        float trailZRot = 0;
        Vec3 topAngleVec = new Vec3(trailHeight, trailHeight, 0).yRot(trailYRot).zRot(trailZRot);
        Vec3 bottomAngleVec = new Vec3(-trailHeight, -trailHeight, 0).yRot(trailYRot).zRot(trailZRot);
        Vec3 drawFrom = entityIn.getTrailPosition(0, partialTicks);
        PoseStack.Pose posestack$pose = poseStack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        Matrix3f matrix3f = posestack$pose.normal();
        VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderType.entityTranslucent(TRAIL_TEXTURE));

        for (int samples = 0; samples < sampleSize; samples++) {
            Vec3 sample = entityIn.getTrailPosition(samples + 2, partialTicks);
            float u1 = samples / (float) sampleSize;
            float u2 = u1 + 1 / (float) sampleSize;

            addVertex(vertexconsumer, matrix4f, matrix3f, drawFrom, bottomAngleVec, trailR, trailG, trailB, u1, 1F, packedLightIn);
            addVertex(vertexconsumer, matrix4f, matrix3f, sample, bottomAngleVec,  trailR, trailG, trailB, u2, 1F, packedLightIn);
            addVertex(vertexconsumer, matrix4f, matrix3f, sample, topAngleVec, trailR, trailG, trailB, u2, 0F, packedLightIn);
            addVertex(vertexconsumer, matrix4f, matrix3f, drawFrom, topAngleVec, trailR, trailG, trailB,  u1, 0F, packedLightIn);

            drawFrom = sample;
        }


    }

    private void addVertex(VertexConsumer consumer, Matrix4f matrix,Matrix3f matrix3, Vec3 pos, Vec3 offset,float r,float g,float b, float u, float v, int light) {
        consumer.vertex(matrix,
                        (float) (pos.x + offset.x),
                        (float) (pos.y + offset.y),
                        (float) (pos.z + offset.z))
                .color(r, g, b, 1.0F)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(matrix3,0.0F, 1.0F, 0.0F).endVertex();
    }

    protected int getBlockLightLevel(VoidShockBomb entityIn, BlockPos pos) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(VoidShockBomb entity) {
        return INNER_TEXTURES;
    }

}
