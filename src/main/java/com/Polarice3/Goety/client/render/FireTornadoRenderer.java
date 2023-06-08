package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.FireTornadoModel;
import com.Polarice3.Goety.common.entities.projectiles.FireTornado;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class FireTornadoRenderer extends EntityRenderer<FireTornado> {
    private static final ResourceLocation TEXTURES = new ResourceLocation(Goety.MOD_ID,"textures/entity/projectiles/fire_tornado.png");
    private final FireTornadoModel<FireTornado> model;

    public FireTornadoRenderer(EntityRendererProvider.Context p_i46179_1_) {
        super(p_i46179_1_);
        this.model = new FireTornadoModel<>(p_i46179_1_.bakeLayer(ModModelLayer.FIRE_TORNADO));
    }

    public void render(FireTornado entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(getRenderType(entityIn));
        this.model.setupAnim(entityIn, 0.0F, 0.0F, partialTicks/10, 0, 0);
        matrixStackIn.translate(0.0D, (double)(entityIn.getBbHeight() + 1.5F), 0.0D);
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        matrixStackIn.scale(2.0F, 2.0F, 2.0F);
        this.model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.15F);
        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    protected RenderType getRenderType(FireTornado p_230496_1_) {
        return this.model.renderType(getTextureLocation(p_230496_1_));
    }

    @Override
    public ResourceLocation getTextureLocation(FireTornado pEntity) {
        return TEXTURES;
    }
}
