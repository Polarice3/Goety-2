package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.client.render.model.HauntedSkullModel;
import com.Polarice3.Goety.common.entities.projectiles.HauntedSkullProjectile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class HauntedSkullProjectileRenderer extends EntityRenderer<HauntedSkullProjectile> {
    private final HauntedSkullModel<HauntedSkullProjectile> model;

    public HauntedSkullProjectileRenderer(EntityRendererProvider.Context p_174435_) {
        super(p_174435_);
        this.model = new HauntedSkullModel<>(p_174435_.bakeLayer(ModModelLayer.HAUNTED_SKULL));
    }

    protected int getBlockLightLevel(HauntedSkullProjectile pEntity, BlockPos pPos) {
        return 15;
    }

    public void render(HauntedSkullProjectile pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        pMatrixStack.pushPose();
        pMatrixStack.scale(-1.0F, -1.0F, 1.0F);
        pMatrixStack.translate(0.0D, -1.5D, 0.0D);
        float f = Mth.rotlerp(pEntity.yRotO, pEntity.getYRot(), pPartialTicks);
        float f1 = Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot());
        VertexConsumer ivertexbuilder = pBuffer.getBuffer(this.model.renderType(this.getTextureLocation(pEntity)));
        this.model.setupAnim(0.0F, f, f1);
        this.model.renderToBuffer(pMatrixStack, ivertexbuilder, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        pMatrixStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    public ResourceLocation getTextureLocation(HauntedSkullProjectile pEntity) {
        return pEntity.getResourceLocation();
    }
}
