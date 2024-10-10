package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.HellChantModel;
import com.Polarice3.Goety.common.entities.projectiles.HellChant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class HellChantRenderer<T extends HellChant> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE = Goety.location("textures/entity/projectiles/hell_chant.png");
    private final HellChantModel<T> model;

    public HellChantRenderer(EntityRendererProvider.Context p_i47208_1_) {
        super(p_i47208_1_);
        this.model = new HellChantModel<>(p_i47208_1_.bakeLayer(ModModelLayer.SCREAM));
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(T scream, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        poseStack.pushPose();
        poseStack.translate(0.0F, -0.7F, 0.0F);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(scream.getYRot()));

        VertexConsumer vertexConsumer = bufferIn.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(scream)));
        this.model.setupAnim(scream, 0.0F, 0.0F, partialTicks, scream.getYRot(), scream.getXRot());
        this.model.renderToBuffer(poseStack, vertexConsumer, 240, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
        super.render(scream, entityYaw, partialTicks, poseStack, bufferIn, 240);
    }

    @Override
    public ResourceLocation getTextureLocation(T p_114482_) {
        return TEXTURE;
    }
}
