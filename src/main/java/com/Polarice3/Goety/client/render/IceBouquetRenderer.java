package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.client.render.model.IceBouquetModel;
import com.Polarice3.Goety.common.entities.projectiles.IceBouquet;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class IceBouquetRenderer extends EntityRenderer<IceBouquet> {
    private final IceBouquetModel<IceBouquet> model;

    public IceBouquetRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
        this.model = new IceBouquetModel<>(renderManagerIn.bakeLayer(ModModelLayer.ICE_BOUQUET));
    }

    public void render(IceBouquet pEntity, float entityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource bufferIn, int packedLightIn) {
        float f = pEntity.getAnimationProgress(pPartialTicks);
        if (f != 0.0F) {
            float f1 = 1.0F;
            if (f > 0.9F) {
                f1 = (float)((double)f1 * ((1.0D - (double)f) / (double)0.1F));
            }
            if (pEntity.getAnimation() < 13){
                f1 = 0.5F;
            }
            f1 *= 0.8F;
            pMatrixStack.pushPose();
            pMatrixStack.mulPose(Axis.YP.rotationDegrees(90.0F - pEntity.getYRot()));
            pMatrixStack.scale(-f1, -1.25F, f1);
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

    protected int getBlockLightLevel(IceBouquet pEntity, BlockPos pPos) {
        return 15;
    }

    public ResourceLocation getTextureLocation(IceBouquet entity) {
        return entity.getResourceLocation();
    }
}
