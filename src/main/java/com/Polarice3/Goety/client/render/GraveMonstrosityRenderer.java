package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.GraveMonstrosityModel;
import com.Polarice3.Goety.common.entities.ally.undead.GraveMonstrosity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class GraveMonstrosityRenderer<T extends GraveMonstrosity> extends MobRenderer<T, GraveMonstrosityModel<T>> {
    private static final ResourceLocation TEXTURES = Goety.location("textures/entity/servants/grave_monstrosity/grave_monstrosity.png");
    private static final ResourceLocation GLOW = Goety.location("textures/entity/servants/grave_monstrosity/grave_monstrosity_glow.png");

    public GraveMonstrosityRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new GraveMonstrosityModel<>(renderManagerIn.bakeLayer(ModModelLayer.GRAVE_MONSTROSITY)), 2.0F);
        this.addLayer(new GlowEyesAndMouthLayer<>(this));
        this.addLayer(new GMFireLayer<>(this));
        this.addLayer(new GMContainedLayer<>(this));
        this.addLayer(new GMEmissiveLayer<>(this, GLOW, (entity, partialTicks, ageInTicks) -> {
            return !entity.isDeadOrDying() ? entity.getGlow : 0.0F;
        }));
    }

    @Override
    public void render(T pEntity, float entityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(pEntity, entityYaw, pPartialTicks, pMatrixStack, bufferIn, packedLightIn);
        ChargeAuraRenderer.render(pEntity, pEntity.chargeAura, pPartialTicks, pMatrixStack, bufferIn);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURES;
    }

    public static class GlowEyesAndMouthLayer<T extends GraveMonstrosity, M extends GraveMonstrosityModel<T>> extends EyesLayer<T, M>{
        private static final ResourceLocation EYES = Goety.location("textures/entity/servants/grave_monstrosity/grave_monstrosity_eyes.png");

        public GlowEyesAndMouthLayer(RenderLayerParent<T, M> p_116981_) {
            super(p_116981_);
        }

        @Override
        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            super.render(p_116983_, p_116984_, p_116985_, p_116986_, p_116987_, p_116988_, p_116989_, p_116990_, p_116991_, p_116992_);
        }

        @Override
        public RenderType renderType() {
            return RenderType.eyes(EYES);
        }
    }

    public static class GMFireLayer<T extends GraveMonstrosity, M extends GraveMonstrosityModel<T>> extends EyesLayer<T, M> {
        private static final RenderType RENDER_TYPE = ModRenderType.wraith(Goety.location("textures/entity/servants/grave_monstrosity/fire/0.png"));

        public GMFireLayer(RenderLayerParent<T, M> p_i50919_1_) {
            super(p_i50919_1_);
        }

        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (!p_116986_.isInvisible() && !p_116986_.isDeadOrDying()) {
                ResourceLocation texture = Goety.location("textures/entity/servants/grave_monstrosity/fire/" + p_116986_.level.getGameTime() % 7 + ".png");
                VertexConsumer ivertexbuilder = p_116984_.getBuffer(this.getParentModel().renderType(texture));
                this.getParentModel().renderToBuffer(p_116983_, ivertexbuilder, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.15F);
                VertexConsumer vertexconsumer = p_116984_.getBuffer(ModRenderType.wraith(texture));
                this.getParentModel().renderToBuffer(p_116983_, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        @Override
        public RenderType renderType() {
            return RENDER_TYPE;
        }
    }

    public static class GMContainedLayer<T extends GraveMonstrosity, M extends GraveMonstrosityModel<T>> extends EyesLayer<T, M>{
        private static final ResourceLocation EYES = Goety.location("textures/entity/servants/grave_monstrosity/grave_monstrosity_contained.png");

        public GMContainedLayer(RenderLayerParent<T, M> p_116981_) {
            super(p_116981_);
        }

        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (p_116986_.hasInventory){
                super.render(p_116983_, p_116984_, p_116985_, p_116986_, p_116987_, p_116988_, p_116989_, p_116990_, p_116991_, p_116992_);
            }
        }

        @Override
        public RenderType renderType() {
            return RenderType.eyes(EYES);
        }
    }

    public static class GMEmissiveLayer<T extends GraveMonstrosity, M extends GraveMonstrosityModel<T>> extends RenderLayer<T, M> {
        private final ResourceLocation texture;
        private final GMEmissiveLayer.AlphaFunction<T> alphaFunction;

        public GMEmissiveLayer(RenderLayerParent<T, M> p_234885_, ResourceLocation p_234886_, GMEmissiveLayer.AlphaFunction<T> p_234887_) {
            super(p_234885_);
            this.texture = p_234886_;
            this.alphaFunction = p_234887_;
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!entity.isInvisible()) {
                VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderType.entityTranslucentEmissive(this.texture));
                this.getParentModel().renderToBuffer(matrixStackIn, vertexconsumer, packedLightIn, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, this.alphaFunction.apply(entity, partialTicks, ageInTicks));
            }
        }

        public interface AlphaFunction<T extends GraveMonstrosity> {
            float apply(T p_234920_, float p_234921_, float p_234922_);
        }
    }

}
