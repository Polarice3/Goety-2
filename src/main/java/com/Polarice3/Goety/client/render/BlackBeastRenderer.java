package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.BlackBeastModel;
import com.Polarice3.Goety.common.entities.ally.BlackBeast;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;

public class BlackBeastRenderer<T extends BlackBeast> extends MobRenderer<T, BlackBeastModel<T>> {
    private static final ResourceLocation TEXTURES = Goety.location("textures/entity/servants/black_wolf/black_beast.png");
    private static final ResourceLocation PRINCESS = Goety.location("textures/entity/servants/black_wolf/black_beast_princess.png");
    protected static final ResourceLocation DEATH = Goety.location("textures/entity/servants/black_wolf/black_beast_death.png");

    public BlackBeastRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new BlackBeastModel<>(renderManagerIn.bakeLayer(ModModelLayer.BLACK_BEAST)), 0.0F);
        this.addLayer(new BlackBeastEyesLayer<>(this));
    }

    @Override
    public void render(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
        if (pEntity.deathTime > 0){
            pMatrixStack.pushPose();
            boolean flag = pEntity.hurtTime > 0;
            float f = Mth.rotLerp(pPartialTicks, pEntity.yBodyRotO, pEntity.yBodyRot);
            float f1 = Mth.rotLerp(pPartialTicks, pEntity.yHeadRotO, pEntity.yHeadRot);
            float f2 = f1 - f;
            float f6 = Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot());
            float f71 = this.getBob(pEntity, pPartialTicks);
            this.setupRotations(pEntity, pMatrixStack, f71, f, pPartialTicks);
            pMatrixStack.scale(-1.0F, -1.0F, 1.0F);
            this.scale(pEntity, pMatrixStack, pPartialTicks);
            pMatrixStack.translate(0.0D, (double)-1.501F, 0.0D);
            this.model.prepareMobModel(pEntity, 0.0F, 0.0F, pPartialTicks);
            this.model.setupAnim(pEntity, 0.0F, 0.0F, f71, f2, f6);
            float f8 = 40.0F;
            float f9 = (float)pEntity.deathTime / f8;
            float f10 = 1.0F - (pEntity.deathTime / 40.0F);
            VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.dragonExplosionAlpha(DEATH));
            this.model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, f9);
            VertexConsumer ivertexbuilder1 = pBuffer.getBuffer(RenderType.entityDecal(this.getTextureLocation(pEntity)));
            this.model.renderToBuffer(pMatrixStack, ivertexbuilder1, pPackedLight, OverlayTexture.pack(0.0F, flag), f10, f10, f10, 1.0F);
            pMatrixStack.popPose();
        }
    }

    @Nullable
    protected RenderType getRenderType(T p_230496_1_, boolean p_230496_2_, boolean p_230496_3_, boolean p_230496_4_) {
        if (p_230496_1_.deathTime > 0){
            return RenderType.dragonExplosionAlpha(DEATH);
        } else {
            return super.getRenderType(p_230496_1_, p_230496_2_, p_230496_3_, p_230496_4_);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        if (entity.isPretty()){
            return PRINCESS;
        }
        return TEXTURES;
    }

    public static class BlackBeastEyesLayer<T extends BlackBeast, M extends BlackBeastModel<T>> extends EyesLayer<T, M>{
        private static final ResourceLocation EYES = Goety.location("textures/entity/servants/black_wolf/black_beast_eyes.png");

        public BlackBeastEyesLayer(RenderLayerParent<T, M> p_116981_) {
            super(p_116981_);
        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T blackHound, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!blackHound.isDeadOrDying()) {
                super.render(matrixStackIn, bufferIn, packedLightIn, blackHound, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }
        }

        @Override
        public RenderType renderType() {
            return RenderType.eyes(EYES);
        }
    }
}
