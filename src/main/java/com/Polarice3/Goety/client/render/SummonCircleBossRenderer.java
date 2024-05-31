package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.SummonCircleBossModel;
import com.Polarice3.Goety.common.entities.util.SummonCircleBoss;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SummonCircleBossRenderer extends EntityRenderer<SummonCircleBoss> {
    private static final ResourceLocation TEXTURES = Goety.location("textures/entity/boss_summon.png");
    private final SummonCircleBossModel<SummonCircleBoss> model;

    public SummonCircleBossRenderer(EntityRendererProvider.Context p_i46179_1_) {
        super(p_i46179_1_);
        this.model = new SummonCircleBossModel<>(p_i46179_1_.bakeLayer(ModModelLayer.SUMMON_CIRCLE_BOSS));
    }

    public void render(SummonCircleBoss entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.model.renderType(this.getTextureLocation(entityIn)));
        this.model.setupAnim(entityIn, 0.0F, 0.0F, 0.0F, entityIn.getYRot(), entityIn.getXRot());
        matrixStackIn.scale(1.5F, 1.5F, 1.5F);
        matrixStackIn.translate(0.0D, 1.6D, 0.0D);
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        int i = getOverlayCoords(this.getWhiteOverlayProgress(entityIn, partialTicks));
        this.model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, i, 1.0F, 1.0F, 1.0F, 0.15F);
        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    public static int getOverlayCoords(float p_115340_) {
        return OverlayTexture.pack(OverlayTexture.u(p_115340_), OverlayTexture.v(false));
    }

    protected float getWhiteOverlayProgress(SummonCircleBoss p_114043_, float p_114044_) {
        float f = p_114043_.getSwelling(p_114044_);
        return (int)(f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(SummonCircleBoss pEntity) {
        return TEXTURES;
    }
}
