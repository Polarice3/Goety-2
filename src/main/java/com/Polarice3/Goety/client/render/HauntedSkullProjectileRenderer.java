package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.HauntedSkullModel;
import com.Polarice3.Goety.common.entities.projectiles.HauntedSkullProjectile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class HauntedSkullProjectileRenderer extends LayeredEntityRenderer<HauntedSkullProjectile, HauntedSkullModel<HauntedSkullProjectile>> {

    public HauntedSkullProjectileRenderer(EntityRendererProvider.Context p_174435_) {
        super(p_174435_, new HauntedSkullModel<>(p_174435_.bakeLayer(ModModelLayer.HAUNTED_SKULL)));
        this.addLayer(new ChargeLayer<>(this, new HauntedSkullModel<>(p_174435_.bakeLayer(ModModelLayer.HAUNTED_SKULL_FIRELESS))));
    }

    protected int getBlockLightLevel(HauntedSkullProjectile pEntity, BlockPos pPos) {
        return 15;
    }

    public void render(HauntedSkullProjectile pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        pMatrixStack.pushPose();
        pMatrixStack.scale(-1.0F, -1.0F, 1.0F);
        pMatrixStack.translate(0.0D, -1.5D, 0.0D);
        float f = Mth.rotLerp(pPartialTicks, pEntity.yRotO, pEntity.getYRot());
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

    public static class ChargeLayer<T extends HauntedSkullProjectile, M extends HauntedSkullModel<T>> extends RenderLayer<T, M> {
        private static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/haunted_skull/attack/skull_charge.png");
        protected M model;

        public ChargeLayer(RenderLayerParent<T, M> p_116967_, M model) {
            super(p_116967_);
            this.model = model;
        }

        public void render(PoseStack p_116970_, MultiBufferSource p_116971_, int p_116972_, T pEntity, float p_116974_, float p_116975_, float pPartialTicks, float p_116977_, float p_116978_, float p_116979_) {
            p_116970_.pushPose();
            p_116970_.scale(-1.0F, -1.0F, 1.0F);
            p_116970_.translate(0.0D, -1.5D, 0.0D);
            if (pEntity.isPowered()) {
                float f = Mth.rotLerp(pPartialTicks, pEntity.yRotO, pEntity.getYRot());
                float f0 = (float)pEntity.tickCount + pPartialTicks;
                float f1 = Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot());
                VertexConsumer vertexconsumer = p_116971_.getBuffer(RenderType.energySwirl(TEXTURE, this.xOffset(f0) % 1.0F, f0 * 0.01F % 1.0F));
                this.model().setupAnim(0.0F, f, f1);
                this.model().renderToBuffer(p_116970_, vertexconsumer, p_116972_, OverlayTexture.NO_OVERLAY, 0.5F, 0.5F, 0.5F, 1.0F);
            }
            p_116970_.popPose();
        }

        protected float xOffset(float p_117702_) {
            return Mth.cos(p_117702_ * 0.02F) * 3.0F;
        }

        protected HauntedSkullModel<T> model() {
            return this.model;
        }
    }
}
