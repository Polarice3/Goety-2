package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.client.render.model.IceBouquetModel;
import com.Polarice3.Goety.common.entities.projectiles.Hellfire;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class HellfireRenderer extends EntityRenderer<Hellfire> {
    private final IceBouquetModel<Hellfire> model;

    public HellfireRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
        this.model = new IceBouquetModel<>(renderManagerIn.bakeLayer(ModModelLayer.ICE_BOUQUET));
    }

    public void render(Hellfire pEntity, float entityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource bufferIn, int packedLightIn) {
        float f = pEntity.getAnimationProgress(pPartialTicks);
        if (f != 0.0F) {
            pMatrixStack.pushPose();
            pMatrixStack.mulPose(Axis.YP.rotationDegrees(90.0F - pEntity.getYRot()));
            pMatrixStack.scale(-f, -(f + 0.25F), f);
            pMatrixStack.translate(0.0D, -1.45D, 0.0D);
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.model.renderType(this.getTextureLocation(pEntity)));
            this.model.setupAnim(pEntity, 0.0F, 0.0F, pPartialTicks/10, 0, 0);
            this.model.renderToBuffer(pMatrixStack, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.15F);
            VertexConsumer glow = bufferIn.getBuffer(ModRenderType.wraith(this.getTextureLocation(pEntity)));
            this.model.renderToBuffer(pMatrixStack, glow, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            pMatrixStack.popPose();
            super.render(pEntity, entityYaw, pPartialTicks, pMatrixStack, bufferIn, packedLightIn);
        }
    }

    protected int getBlockLightLevel(Hellfire pEntity, BlockPos pPos) {
        return 15;
    }

    public ResourceLocation getTextureLocation(Hellfire entity) {
        return entity.getResourceLocation();
    }
}
