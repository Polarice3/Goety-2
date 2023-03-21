package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.common.entities.projectiles.ScytheSlash;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class ScytheSlashRenderer extends EntityRenderer<ScytheSlash> {

    public ScytheSlashRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    protected int getBlockLightLevel(ScytheSlash pEntity, BlockPos pPos) {
        return 15;
    }

    public void render(ScytheSlash pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        pMatrixStack.pushPose();
        pMatrixStack.scale(1.0F, 1.0F, 1.0F);
        pMatrixStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        PoseStack.Pose matrixstack$entry = pMatrixStack.last();
        Matrix4f matrix4f = matrixstack$entry.pose();
        Matrix3f matrix3f = matrixstack$entry.normal();
        ResourceLocation texture = pEntity.getResourceLocation();
        RenderType renderType = RenderType.entityCutoutNoCull(texture);
        VertexConsumer ivertexbuilder = pBuffer.getBuffer(renderType);
        vertex(ivertexbuilder, matrix4f, matrix3f, pPackedLight, 0.0F, 0, 0, 1);
        vertex(ivertexbuilder, matrix4f, matrix3f, pPackedLight, 1.0F, 0, 1, 1);
        vertex(ivertexbuilder, matrix4f, matrix3f, pPackedLight, 1.0F, 1, 1, 0);
        vertex(ivertexbuilder, matrix4f, matrix3f, pPackedLight, 0.0F, 1, 0, 0);
        pMatrixStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    private static void vertex(VertexConsumer p_229045_0_, Matrix4f p_229045_1_, Matrix3f p_229045_2_, int p_229045_3_, float p_229045_4_, int p_229045_5_, int p_229045_6_, int p_229045_7_) {
        p_229045_0_.vertex(p_229045_1_, p_229045_4_ - 0.5F, (float)p_229045_5_ - 0.25F, 0.0F).color(255, 255, 255, 255).uv((float)p_229045_6_, (float)p_229045_7_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_229045_3_).normal(p_229045_2_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    public ResourceLocation getTextureLocation(ScytheSlash entity) {
        return entity.getResourceLocation();
    }
}
