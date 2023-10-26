package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.ViciousToothModel;
import com.Polarice3.Goety.common.entities.projectiles.ViciousTooth;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class ViciousToothRenderer extends EntityRenderer<ViciousTooth> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Goety.MOD_ID,"textures/entity/projectiles/vicious_tooth.png");
    private final ViciousToothModel<ViciousTooth> model;

    public ViciousToothRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
        this.model = new ViciousToothModel<>(renderManagerIn.bakeLayer(ModModelLayer.VICIOUS_TOOTH));
    }

    public void render(ViciousTooth pEntity, float entityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource bufferIn, int packedLightIn) {
        pMatrixStack.pushPose();
        pMatrixStack.mulPose(Axis.YP.rotationDegrees(90.0F - pEntity.getYRot()));
        float f;
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.model.renderType(this.getTextureLocation(pEntity)));
        if (pEntity.isStarting()){
            f = pEntity.hovering / 20.0F;
        } else {
            f = 1.0F;
        }
        pMatrixStack.scale(-f, -f, f);
        pMatrixStack.translate(0.0D, -2.1D, 0.0D);
        this.model.setupAnim(pEntity, 0.0F, 0.0F, pPartialTicks, 0, 0);
        this.model.renderToBuffer(pMatrixStack, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.15F);
        pMatrixStack.popPose();
        super.render(pEntity, entityYaw, pPartialTicks, pMatrixStack, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getTextureLocation(ViciousTooth pEntity) {
        return TEXTURE;
    }
}
