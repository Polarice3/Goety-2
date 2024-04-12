package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.projectiles.NecroBolt;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class NecroBoltRenderer<T extends NecroBolt> extends EntityRenderer<T> {
    private static final ResourceLocation BASE = Goety.location("textures/particle/necro_bolt_base.png");
    private static final ResourceLocation MID = Goety.location("textures/particle/necro_bolt_mid.png");
    private static final ResourceLocation TOP = Goety.location("textures/particle/necro_bolt_top.png");

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
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    public void render(VertexConsumer vertexConsumer, Camera camera, NecroBolt projectile, float partialTicks, int packedLightIn, Matrix4f matrix4f, Matrix3f matrix3f, int mode) {
        Quaternion quaternion = new Quaternion(camera.rotation());
        float f3 = Mth.lerp(partialTicks, projectile.oRoll, projectile.roll);
        float f4 = 0.5F;
        if (mode == 1){
            f3 *= 0.5F;
            f4 *= projectile.getGlow;
        }
        quaternion.mul(Vector3f.ZP.rotation(f3));

        Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
        vector3f1.transform(quaternion);
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};

        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.transform(quaternion);
            vector3f.mul(f4);
        }

        vertexConsumer.vertex(matrix4f, avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).color(255, 255, 255, 255).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix4f, avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).color(255, 255, 255, 255).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix4f, avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).color(255, 255, 255, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix4f, avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).color(255, 255, 255, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
    }

    public ResourceLocation getTextureLocation(T entity){
        return BASE;
    }
}
