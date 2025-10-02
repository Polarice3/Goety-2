package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.BioMineModel;
import com.Polarice3.Goety.common.entities.projectiles.BioMine;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class BioMineRenderer<T extends BioMine> extends LayeredEntityRenderer<T, BioMineModel<T>> {

    public BioMineRenderer(EntityRendererProvider.Context p_174289_) {
        super(p_174289_, new BioMineModel<>(p_174289_.bakeLayer(ModModelLayer.BIOMINE)));
        this.addLayer(new GlowLayer<>(this, new BioMineModel<>(p_174289_.bakeLayer(ModModelLayer.BIOMINE))));
    }

    public void render(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        pMatrixStack.pushPose();
        pMatrixStack.scale(-1.0F, -1.0F, 1.0F);
        pMatrixStack.translate(0.0D, -1.5D, 0.0D);
        float f = Mth.rotLerp(pPartialTicks, pEntity.yRotO, pEntity.getYRot());
        float f1 = Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot());
        VertexConsumer ivertexbuilder = pBuffer.getBuffer(this.model.renderType(this.getTextureLocation(pEntity)));
        this.model.setupAnim(f, f1);
        this.model.renderToBuffer(pMatrixStack, ivertexbuilder, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        pMatrixStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return Goety.location("textures/entity/projectiles/biomine/" + entity.level.getGameTime() % 19 + ".png");
    }

    public static class GlowLayer<T extends BioMine, M extends BioMineModel<T>> extends RenderLayer<T, M> {
        private static final ResourceLocation TEXTURE = Goety.location("textures/entity/projectiles/biomine/glow.png");
        protected M model;

        public GlowLayer(RenderLayerParent<T, M> p_116967_, M model) {
            super(p_116967_);
            this.model = model;
        }

        public void render(PoseStack p_116970_, MultiBufferSource p_116971_, int p_116972_, T pEntity, float p_116974_, float p_116975_, float pPartialTicks, float p_116977_, float p_116978_, float p_116979_) {
            p_116970_.pushPose();
            p_116970_.scale(-1.0F, -1.0F, 1.0F);
            p_116970_.translate(0.0D, -1.5D, 0.0D);
            float f = Mth.rotLerp(pPartialTicks, pEntity.yRotO, pEntity.getYRot());
            float f1 = Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot());
            VertexConsumer vertexconsumer = p_116971_.getBuffer(RenderType.eyes(TEXTURE));
            this.model().setupAnim(f, f1);
            this.model().renderToBuffer(p_116970_, vertexconsumer, p_116972_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            p_116970_.popPose();
        }

        protected float xOffset(float p_117702_) {
            return Mth.cos(p_117702_ * 0.02F) * 3.0F;
        }

        protected BioMineModel<T> model() {
            return this.model;
        }
    }
}
