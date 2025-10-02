package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.EndersentModel;
import com.Polarice3.Goety.common.entities.hostile.ender.Endersent;
import com.Polarice3.Goety.utils.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;

public class EndersentRenderer<T extends Endersent> extends MobRenderer<T, EndersentModel<T>> {
    protected static final ResourceLocation TEXTURE_LOCATION = Goety.location("textures/entity/enderling/endersent.png");
    protected static final ResourceLocation DEATH = Goety.location("textures/entity/enderling/endersent_death.png");

    public EndersentRenderer(EntityRendererProvider.Context p_i47208_1_) {
        super(p_i47208_1_, new EndersentModel<>(p_i47208_1_.bakeLayer(ModModelLayer.ENDERSENT)), 0.75F);
        this.addLayer(new EnchantedLayer<>(this, p_i47208_1_.getModelSet()));
        this.addLayer(new GlowLayer<>(this));
        this.addLayer(new EnderEyeLayer<>(this));
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
            float f8 = MathHelper.secondsToTicks(4);
            float f9 = (float)pEntity.deathTime / f8;
            float f10 = 1.0F - (pEntity.deathTime / f8);
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

    public ResourceLocation getTextureLocation(T pEntity) {
        return TEXTURE_LOCATION;
    }

    public static class GlowLayer<T extends Endersent, M extends EndersentModel<T>> extends EyesLayer<T, M> {
        private static final RenderType RENDER_TYPE = RenderType.eyes(Goety.location("textures/entity/enderling/endersent_glow.png"));

        public GlowLayer(RenderLayerParent<T, M> p_i50919_1_) {
            super(p_i50919_1_);
        }

        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (!p_116986_.isInvisible() && !p_116986_.isDeadOrDying()) {
                VertexConsumer vertexconsumer = p_116984_.getBuffer(this.renderType());
                this.getParentModel().renderToBuffer(p_116983_, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        @Override
        public RenderType renderType() {
            return RENDER_TYPE;
        }
    }

    public static class EnderEyeLayer<T extends Endersent, M extends EndersentModel<T>> extends EyesLayer<T, M> {
        private static final RenderType RENDER_TYPE = RenderType.entityCutout(Goety.location("textures/entity/enderling/endersent_eye.png"));

        public EnderEyeLayer(RenderLayerParent<T, M> p_i50919_1_) {
            super(p_i50919_1_);
        }

        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (!p_116986_.isInvisible() && !p_116986_.isDeadOrDying() && p_116986_.hasEye()) {
                VertexConsumer vertexconsumer = p_116984_.getBuffer(this.renderType());
                this.getParentModel().renderToBuffer(p_116983_, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        @Override
        public RenderType renderType() {
            return RENDER_TYPE;
        }
    }

    public static class EnchantedLayer<T extends Endersent, M extends EndersentModel<T>> extends RenderLayer<T, M> {
        private static final ResourceLocation ARMOR = Goety.location("textures/entity/enderling/endersent_glint.png");
        private final EndersentModel<T> model;

        public EnchantedLayer(RenderLayerParent<T, M> p_116967_, EntityModelSet p_174555_) {
            super(p_116967_);
            this.model = new EndersentModel<>(p_174555_.bakeLayer(ModModelLayer.ENDERSENT));
        }

        public void render(PoseStack p_116970_, MultiBufferSource p_116971_, int p_116972_, T p_116973_, float p_116974_, float p_116975_, float p_116976_, float p_116977_, float p_116978_, float p_116979_) {
            if (p_116973_.hasEye() && !p_116973_.isInvisible() && !p_116973_.isDeadOrDying()) {
                float f = (float)p_116973_.tickCount + p_116976_;
                EndersentModel<T> entitymodel = this.model;
                entitymodel.prepareMobModel(p_116973_, p_116974_, p_116975_, p_116976_);
                this.getParentModel().copyPropertiesTo(entitymodel);
                VertexConsumer vertexconsumer = p_116971_.getBuffer(RenderType.energySwirl(ARMOR, this.xOffset(f) % 1.0F, f * 0.01F % 1.0F));
                entitymodel.setupAnim(p_116973_, p_116974_, p_116975_, p_116977_, p_116978_, p_116979_);
                entitymodel.renderToBuffer(p_116970_, vertexconsumer, p_116972_, OverlayTexture.NO_OVERLAY, 0.5F, 0.5F, 0.5F, 1.0F);
                entitymodel.ender_eye.visible = false;
            }
        }

        protected float xOffset(float p_225634_1_) {
            return Mth.cos(p_225634_1_ * 0.02F) * 3.0F;
        }
    }
}
