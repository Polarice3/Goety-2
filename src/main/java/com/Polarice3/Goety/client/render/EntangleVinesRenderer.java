package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.EntangleVinesModel;
import com.Polarice3.Goety.common.entities.projectiles.EntangleVines;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class EntangleVinesRenderer<T extends EntangleVines> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE_LOCATION = Goety.location("textures/entity/projectiles/entangle_vines.png");
    private final EntangleVinesModel<T> model;

    public EntangleVinesRenderer(EntityRendererProvider.Context p_i47208_1_) {
        super(p_i47208_1_);
        this.model = new EntangleVinesModel<>(p_i47208_1_.bakeLayer(ModModelLayer.ENTANGLE_VINES));
        this.shadowRadius = 0.0F;
    }

    public void render(T pEntity, float entityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource bufferIn, int packedLightIn) {
        pMatrixStack.pushPose();
        pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(90.0F - pEntity.getYRot()));
        pMatrixStack.scale(-1.0F, -1.0F, 1.0F);
        pMatrixStack.translate(0.0D, -1.45D, 0.0D);
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.model.renderType(this.getTextureLocation(pEntity)));
        this.model.setupAnim(pEntity, 0.0F, 0.0F, pEntity.tickCount + pPartialTicks, 0, 0);
        this.model.renderToBuffer(pMatrixStack, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        pMatrixStack.popPose();
        super.render(pEntity, entityYaw, pPartialTicks, pMatrixStack, bufferIn, packedLightIn);
    }

    public ResourceLocation getTextureLocation(T pEntity) {
        return TEXTURE_LOCATION;
    }
}
