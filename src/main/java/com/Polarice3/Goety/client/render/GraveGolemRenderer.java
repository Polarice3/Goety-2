package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.GraveGolemModel;
import com.Polarice3.Goety.common.entities.ally.GraveGolem;
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
import net.minecraft.resources.ResourceLocation;

public class GraveGolemRenderer<T extends GraveGolem> extends MobRenderer<T, GraveGolemModel<T>> {
    private static final ResourceLocation TEXTURES = Goety.location("textures/entity/servants/grave_golem/grave_golem.png");
    private static final ResourceLocation GLOW = Goety.location("textures/entity/servants/grave_golem/grave_golem_glow.png");

    public GraveGolemRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new GraveGolemModel<>(renderManagerIn.bakeLayer(ModModelLayer.GRAVE_GOLEM)), 1.5F);
        this.addLayer(new GraveGolemEyesLayer<>(this));
        this.addLayer(new GraveGolemContainedLayer<>(this));
        this.addLayer(new GGEmissiveLayer<>(this, GLOW, (entity, partialTicks, ageInTicks) -> {
            return !entity.isDeadOrDying() ? entity.getGlow : 0.0F;
        }));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURES;
    }

    public static class GraveGolemEyesLayer<T extends GraveGolem, M extends GraveGolemModel<T>> extends EyesLayer<T, M>{
        private static final ResourceLocation EYES = Goety.location("textures/entity/servants/grave_golem/grave_golem_eyes.png");

        public GraveGolemEyesLayer(RenderLayerParent<T, M> p_116981_) {
            super(p_116981_);
        }

        @Override
        public RenderType renderType() {
            return RenderType.eyes(EYES);
        }
    }

    public static class GraveGolemContainedLayer<T extends GraveGolem, M extends GraveGolemModel<T>> extends EyesLayer<T, M>{
        private static final ResourceLocation EYES = Goety.location("textures/entity/servants/grave_golem/grave_golem_contained.png");

        public GraveGolemContainedLayer(RenderLayerParent<T, M> p_116981_) {
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

    public static class GGEmissiveLayer<T extends GraveGolem, M extends GraveGolemModel<T>> extends RenderLayer<T, M> {
        private final ResourceLocation texture;
        private final AlphaFunction<T> alphaFunction;

        public GGEmissiveLayer(RenderLayerParent<T, M> p_234885_, ResourceLocation p_234886_, AlphaFunction<T> p_234887_) {
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

        public interface AlphaFunction<T extends GraveGolem> {
            float apply(T p_234920_, float p_234921_, float p_234922_);
        }
    }
}
