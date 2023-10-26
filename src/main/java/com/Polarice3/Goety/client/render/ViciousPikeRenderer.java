package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.ViciousPikeModel;
import com.Polarice3.Goety.common.entities.projectiles.ViciousPike;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class ViciousPikeRenderer extends EntityRenderer<ViciousPike> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Goety.MOD_ID,"textures/entity/projectiles/vicious_pike.png");
    private final ViciousPikeModel<ViciousPike> model;

    public ViciousPikeRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
        this.model = new ViciousPikeModel<>(renderManagerIn.bakeLayer(ModModelLayer.VICIOUS_PIKE));
    }

    public void render(ViciousPike pEntity, float entityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource bufferIn, int packedLightIn) {
        float f = pEntity.getAnimationProgress(pPartialTicks);
        if (f != 0.0F) {
            float f1 = 2.25F;
            if (f > 0.9F) {
                f1 *= (1.0F - f) / 0.1F;
            }
            pMatrixStack.pushPose();
            pMatrixStack.mulPose(Axis.YP.rotationDegrees(90.0F - pEntity.getYRot()));
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.model.renderType(this.getTextureLocation(pEntity)));
            pMatrixStack.scale(-f1, -f1, f1);
            pMatrixStack.translate(0.0D, -0.73D, 0.0D);
            pMatrixStack.scale(0.5F, 0.5F, 0.5F);
            float f7 = this.getBob(pEntity, pPartialTicks);
            this.model.setupAnim(pEntity, 0.0F, 0.0F, f7, 0, 0);
            this.model.renderToBuffer(pMatrixStack, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.15F);
            pMatrixStack.popPose();
            super.render(pEntity, entityYaw, pPartialTicks, pMatrixStack, bufferIn, packedLightIn);
        }
    }

    protected float getBob(ViciousPike p_115305_, float p_115306_) {
        return (float)p_115305_.tickCount + p_115306_;
    }

    @Override
    public ResourceLocation getTextureLocation(ViciousPike pEntity) {
        return TEXTURE;
    }
}
