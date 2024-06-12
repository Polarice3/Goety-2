package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.RedstoneMonstrosityModel;
import com.Polarice3.Goety.common.entities.ally.golem.RedstoneMonstrosity;
import com.Polarice3.Goety.config.MobsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
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

public class RedstoneMonstrosityRenderer<T extends RedstoneMonstrosity> extends MobRenderer<T, RedstoneMonstrosityModel<T>> {
    private static final ResourceLocation TEXTURES = Goety.location("textures/entity/servants/redstone_monstrosity/redstone_monstrosity.png");
    private static final ResourceLocation ACTIVE = Goety.location("textures/entity/servants/redstone_monstrosity/redstone_monstrosity_active.png");
    private static final ResourceLocation GLOW = Goety.location("textures/entity/servants/redstone_monstrosity/redstone_monstrosity_glow.png");

    public RedstoneMonstrosityRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new RedstoneMonstrosityModel<>(renderManagerIn.bakeLayer(ModModelLayer.REDSTONE_MONSTROSITY)), 2.0F);
        this.addLayer(new GlowEyesLayer<>(this));
        this.addLayer(new ActiveLayer<>(this));
        this.addLayer(new NonActiveLayer<>(this));
        this.addLayer(new RMBandsLayer<>(this));
        this.addLayer(new RMEmissiveLayer<>(this, GLOW, (entity, partialTicks, ageInTicks) -> {
            return !entity.isDeadOrDying()
                    && entity.bigGlow > 0 ? entity.bigGlow : 0.0F;
        }));
        this.addLayer(new RMEmissiveLayer<>(this, ACTIVE, (entity, partialTicks, ageInTicks) -> {
            return !entity.isActivating()
                    && !entity.isDeadOrDying()
                    && entity.bigGlow <= 0 ? entity.minorGlow : 0.0F;
        }));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURES;
    }

    public static class GlowEyesLayer<T extends RedstoneMonstrosity, M extends RedstoneMonstrosityModel<T>> extends EyesLayer<T, M>{
        private static final ResourceLocation EYES = Goety.location("textures/entity/servants/redstone_monstrosity/redstone_monstrosity_eyes.png");

        public GlowEyesLayer(RenderLayerParent<T, M> p_116981_) {
            super(p_116981_);
        }

        @Override
        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (p_116986_.deathTime < 24) {
                super.render(p_116983_, p_116984_, p_116985_, p_116986_, p_116987_, p_116988_, p_116989_, p_116990_, p_116991_, p_116992_);
            }
        }

        @Override
        public RenderType renderType() {
            return RenderType.eyes(EYES);
        }
    }

    public static class ActiveLayer<T extends RedstoneMonstrosity, M extends RedstoneMonstrosityModel<T>> extends RenderLayer<T, M>{
        private static final ResourceLocation LOCATION = Goety.location("textures/entity/servants/redstone_monstrosity/redstone_monstrosity_lines.png");

        public ActiveLayer(RenderLayerParent<T, M> p_116981_) {
            super(p_116981_);
        }

        @Override
        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (!p_116986_.isActivating() && !p_116986_.isDeadOrDying()) {
                VertexConsumer vertexconsumer = p_116984_.getBuffer(this.renderType());
                this.getParentModel().renderToBuffer(p_116983_, vertexconsumer, LightTexture.FULL_BLOCK, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        public RenderType renderType() {
            return RenderType.entityCutoutNoCull(LOCATION);
        }
    }

    public static class NonActiveLayer<T extends RedstoneMonstrosity, M extends RedstoneMonstrosityModel<T>> extends RenderLayer<T, M>{
        private static final ResourceLocation NON_ACTIVE = Goety.location("textures/entity/servants/redstone_monstrosity/redstone_monstrosity_non_active.png");

        public NonActiveLayer(RenderLayerParent<T, M> p_116981_) {
            super(p_116981_);
        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T monstrosity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (monstrosity.isActivating()){
                VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderType.entityCutoutNoCull(NON_ACTIVE));
                this.getParentModel().renderToBuffer(matrixStackIn, vertexconsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    public static class RMEmissiveLayer<T extends RedstoneMonstrosity, M extends RedstoneMonstrosityModel<T>> extends RenderLayer<T, M> {
        private final ResourceLocation texture;
        private final AlphaFunction<T> alphaFunction;

        public RMEmissiveLayer(RenderLayerParent<T, M> p_234885_, ResourceLocation p_234886_, AlphaFunction<T> p_234887_) {
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

        public interface AlphaFunction<T extends RedstoneMonstrosity> {
            float apply(T p_234920_, float p_234921_, float p_234922_);
        }
    }

    public static class RMBandsLayer<T extends RedstoneMonstrosity> extends RenderLayer<T, RedstoneMonstrosityModel<T>> {
        private static final ResourceLocation TEXTURES = Goety.location("textures/entity/servants/redstone_monstrosity/redstone_monstrosity_bands.png");

        public RMBandsLayer(RenderLayerParent<T, RedstoneMonstrosityModel<T>> p_i50919_1_) {
            super(p_i50919_1_);
        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!entitylivingbaseIn.isHostile() && MobsConfig.RedstoneMonstrosityTexture.get()) {
                renderColoredCutoutModel(this.getParentModel(), TEXTURES, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, 1.0F, 1.0F, 1.0F);
            }
        }
    }

}
