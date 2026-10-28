package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.FlameConstructModel;
import com.Polarice3.Goety.common.entities.ally.golem.FlameConstruct;
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

public class FlameConstructRenderer<T extends FlameConstruct> extends MobRenderer<T, FlameConstructModel<T>> {
    private static final ResourceLocation TEXTURE_LOCATION = Goety.location("textures/entity/servants/flame_construct/flame_construct.png");
    private static final ResourceLocation GLOW = Goety.location("textures/entity/servants/flame_construct/flame_construct_glow.png");

    public FlameConstructRenderer(EntityRendererProvider.Context p_i47208_1_) {
        super(p_i47208_1_, new FlameConstructModel<>(p_i47208_1_.bakeLayer(ModModelLayer.FLAME_CONSTRUCT)), 0.25F);
        this.addLayer(new FCEyeLayer<>(this));
        this.addLayer(new FCFireLayer<>(this));
        this.addLayer(new FCEmissiveLayer<>(this, GLOW, (entity, partialTicks, ageInTicks) -> {
            return !entity.isDeadOrDying() ? entity.getGlow : 0.0F;
        }));
    }

    public ResourceLocation getTextureLocation(T pEntity) {
        return TEXTURE_LOCATION;
    }

    public static class FCEyeLayer<T extends FlameConstruct, M extends FlameConstructModel<T>> extends EyesLayer<T, M> {
        private static final RenderType RENDER_TYPE = RenderType.eyes(Goety.location("textures/entity/servants/flame_construct/flame_construct_eyes.png"));

        public FCEyeLayer(RenderLayerParent<T, M> p_i50919_1_) {
            super(p_i50919_1_);
        }

        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (!p_116986_.isInvisible()) {
                VertexConsumer vertexconsumer = p_116984_.getBuffer(this.renderType());
                this.getParentModel().renderToBuffer(p_116983_, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        @Override
        public RenderType renderType() {
            return RENDER_TYPE;
        }
    }

    public static class FCFireLayer<T extends FlameConstruct, M extends FlameConstructModel<T>> extends EyesLayer<T, M> {
        private static final RenderType RENDER_TYPE = ModRenderType.wraith(Goety.location("textures/entity/servants/flame_construct/fire/0.png"));

        public FCFireLayer(RenderLayerParent<T, M> p_i50919_1_) {
            super(p_i50919_1_);
        }

        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (!p_116986_.isInvisible() && !p_116986_.isDeadOrDying()) {
                ResourceLocation texture = Goety.location("textures/entity/servants/flame_construct/fire/" + p_116986_.level.getGameTime() % 31 + ".png");
                VertexConsumer ivertexbuilder = p_116984_.getBuffer(this.getParentModel().renderType(texture));
                this.getParentModel().renderToBuffer(p_116983_, ivertexbuilder, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.5F);
                VertexConsumer vertexconsumer = p_116984_.getBuffer(ModRenderType.fire(texture));
                this.getParentModel().renderToBuffer(p_116983_, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        @Override
        public RenderType renderType() {
            return RENDER_TYPE;
        }
    }

    public static class FCEmissiveLayer<T extends FlameConstruct, M extends FlameConstructModel<T>> extends RenderLayer<T, M> {
        private final ResourceLocation texture;
        private final FCEmissiveLayer.AlphaFunction<T> alphaFunction;

        public FCEmissiveLayer(RenderLayerParent<T, M> p_234885_, ResourceLocation p_234886_, FCEmissiveLayer.AlphaFunction<T> p_234887_) {
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

        public interface AlphaFunction<T extends FlameConstruct> {
            float apply(T p_234920_, float p_234921_, float p_234922_);
        }
    }
}
