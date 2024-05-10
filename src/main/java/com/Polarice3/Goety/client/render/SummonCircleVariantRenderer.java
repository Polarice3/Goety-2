package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.client.render.model.SummonCircleModel;
import com.Polarice3.Goety.common.entities.util.SummonCircleVariant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class SummonCircleVariantRenderer extends EntityRenderer<SummonCircleVariant> {
    private final SummonCircleModel<SummonCircleVariant> model;

    public SummonCircleVariantRenderer(EntityRendererProvider.Context p_i46179_1_) {
        super(p_i46179_1_);
        this.model = new SummonCircleModel<>(p_i46179_1_.bakeLayer(ModModelLayer.SUMMON_CIRCLE));
    }

    public void render(SummonCircleVariant entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.model.renderType(this.getTextureLocation(entityIn)));
        this.model.setupAnim(entityIn, 0.0F, 0.0F, 0.0F, entityIn.getYRot(), entityIn.getXRot());
        matrixStackIn.scale(1.5F, 1.5F, 1.5F);
        matrixStackIn.translate(0.0D, 1.6D, 0.0D);
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        this.model.renderToBuffer(matrixStackIn, ivertexbuilder, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.15F);
        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, LightTexture.FULL_BRIGHT);
    }

    @Override
    public ResourceLocation getTextureLocation(SummonCircleVariant pEntity) {
        return pEntity.getResourceLocation();
    }
}
