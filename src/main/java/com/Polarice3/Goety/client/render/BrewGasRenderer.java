package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.BlockModel;
import com.Polarice3.Goety.common.entities.util.BrewGas;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class BrewGasRenderer extends EntityRenderer<BrewGas> {
    private static final ResourceLocation LOCATION = Goety.location("textures/entity/brew_gas.png");
    private final BlockModel<BrewGas> model;

    public BrewGasRenderer(EntityRendererProvider.Context p_i47208_1_) {
        super(p_i47208_1_);
        this.model = new BlockModel<>(p_i47208_1_.bakeLayer(ModModelLayer.BLOCK));
    }

    public void render(BrewGas pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        pMatrixStack.pushPose();
        pMatrixStack.mulPose(Axis.YP.rotationDegrees(90.0F - pEntity.getYRot()));
        pMatrixStack.scale(-1.0F, -1.0F, 1.0F);
        pMatrixStack.translate(0.0D, -1.5D, 0.0D);
        pMatrixStack.scale(1.0F, 1.0F, 1.0F);
        VertexConsumer ivertexbuilder = pBuffer.getBuffer(RenderType.entityTranslucent(getTextureLocation(pEntity)));
        int i = pEntity.getColor();
        float f = (float)(i >> 16 & 255) / 255.0F;
        float f1 = (float)(i >> 8 & 255) / 255.0F;
        float f2 = (float)(i & 255) / 255.0F;
        this.model.renderToBuffer(pMatrixStack, ivertexbuilder, pPackedLight, OverlayTexture.NO_OVERLAY, f, f1, f2, 0.5F);
        pMatrixStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(BrewGas p_114482_) {
        return LOCATION;
    }
}
