package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ally.SpriteMob;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class SpriteMobRenderer extends EntityRenderer<SpriteMob> {
    private static final ResourceLocation TEXTURE_LOCATION = Goety.location("textures/entity/servants/sprite_core.png");
    private static final ResourceLocation CAGE_LOCATION = Goety.location("textures/entity/servants/sprite_cage.png");
    private static final RenderType RENDER_TYPE = RenderType.eyes(TEXTURE_LOCATION);
    private static final RenderType RENDER_TYPE2 = RenderType.itemEntityTranslucentCull(CAGE_LOCATION);
    private static final float SIN_45 = (float)Math.sin((Math.PI / 4D));
    private final ModelPart glass;

    public SpriteMobRenderer(EntityRendererProvider.Context context) {
        super(context);
        ModelPart modelpart = context.bakeLayer(ModelLayers.END_CRYSTAL);
        this.glass = modelpart.getChild("glass");
    }

    protected int getBlockLightLevel(SpriteMob entity, BlockPos blockPos) {
        return 15;
    }

    public void render(SpriteMob entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        float f1 = Mth.sin(entity.tickCount / 5.0F) * 0.2F + 0.2F;
        poseStack.scale(1.0F + f1, 1.0F + f1, 1.0F + f1);
        poseStack.translate(0.0D, 0.25D, 0.0D);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        PoseStack.Pose posestack$pose = poseStack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        Matrix3f matrix3f = posestack$pose.normal();
        VertexConsumer vertexconsumer = buffer.getBuffer(RENDER_TYPE);
        float f2 = entity.tickCount % 16 / 16.0F;
        float f3 = f2 + 0.0625F;
        vertex(vertexconsumer, matrix4f, matrix3f, packedLight, -0.5F, -0.5F, f2, 1.0F);
        vertex(vertexconsumer, matrix4f, matrix3f, packedLight,  0.5F, -0.5F, f3, 1.0F);
        vertex(vertexconsumer, matrix4f, matrix3f, packedLight, 0.5F, 0.5F, f3, 0.0F);
        vertex(vertexconsumer, matrix4f, matrix3f, packedLight, -0.5F, 0.5F, f2, 0.0F);
        poseStack.popPose();
        poseStack.pushPose();
        float f4 = (entity.tickCount + partialTick) * 9.0F;
        VertexConsumer vertexconsumer2 = buffer.getBuffer(RENDER_TYPE2);
        poseStack.pushPose();
        float f5 = 1.25F;
        poseStack.scale(f5, f5, f5);
        int i = OverlayTexture.NO_OVERLAY;
        poseStack.mulPose(Axis.YP.rotationDegrees(f4));
        poseStack.translate(0.0D, 0.25F, 0.0D);
        poseStack.mulPose((new Quaternionf()).setAngleAxis(((float)Math.PI / 3F), SIN_45, 0.0F, SIN_45));
        this.glass.render(poseStack, vertexconsumer2, packedLight, i, 1.0F, 1.0F, 1.0F, 0.25F);
        poseStack.scale(0.875F, 0.875F, 0.875F);
        poseStack.mulPose((new Quaternionf()).setAngleAxis(((float)Math.PI / 3F), SIN_45, 0.0F, SIN_45));
        poseStack.mulPose(Axis.YP.rotationDegrees(f4));
        this.glass.render(poseStack, vertexconsumer2, packedLight, i, 1.0F, 1.0F, 1.0F, 0.5F);
        poseStack.popPose();
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix4f, Matrix3f matrix3f, int packedLight, float xPos, float yPos, float u, float v) {
        consumer.vertex(matrix4f, xPos, yPos , 0.0F)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(matrix3f, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    public ResourceLocation getTextureLocation(SpriteMob entity) {
        return TEXTURE_LOCATION;
    }
}
