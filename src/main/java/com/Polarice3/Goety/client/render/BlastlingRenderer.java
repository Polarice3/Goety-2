package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.BlastlingModel;
import com.Polarice3.Goety.common.entities.neutral.ender.AbstractBlastling;
import com.Polarice3.Goety.config.MobsConfig;
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

public class BlastlingRenderer<T extends AbstractBlastling> extends MobRenderer<T, BlastlingModel<T>> {
    private static final ResourceLocation TEXTURE_LOCATION = Goety.location("textures/entity/enderling/blastling.png");
    private static final ResourceLocation SERVANT_LOCATION = Goety.location("textures/entity/enderling/servants/blastling.png");

    public BlastlingRenderer(EntityRendererProvider.Context p_i47208_1_) {
        super(p_i47208_1_, new BlastlingModel<>(p_i47208_1_.bakeLayer(ModModelLayer.BLASTLING)), 0.5F);
        this.addLayer(new GlowLayer<>(this));
        this.addLayer(new FireLayer<>(this));
    }

    public ResourceLocation getTextureLocation(T pEntity) {
        if (pEntity.isHostile() || !MobsConfig.BlastlingServantTexture.get()) {
            return TEXTURE_LOCATION;
        }
        return SERVANT_LOCATION;
    }

    public static class GlowLayer<T extends AbstractBlastling, M extends BlastlingModel<T>> extends EyesLayer<T, M> {
        private static final RenderType RENDER_TYPE = RenderType.eyes(Goety.location("textures/entity/enderling/blastling_glow.png"));
        private static final RenderType SERVANT_TYPE = RenderType.eyes(Goety.location("textures/entity/enderling/servants/blastling_glow.png"));

        public GlowLayer(RenderLayerParent<T, M> p_i50919_1_) {
            super(p_i50919_1_);
        }

        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (!p_116986_.isInvisible()) {
                VertexConsumer vertexconsumer = p_116984_.getBuffer(this.getRenderType(p_116986_));
                this.getParentModel().renderToBuffer(p_116983_, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        public RenderType getRenderType(T pEntity) {
            if (pEntity.isHostile() || !MobsConfig.BlastlingServantTexture.get()) {
                return this.renderType();
            }
            return SERVANT_TYPE;
        }

        @Override
        public RenderType renderType() {
            return RENDER_TYPE;
        }
    }

    public static class FireLayer<T extends AbstractBlastling, M extends BlastlingModel<T>> extends EyesLayer<T, M> {
        private static final RenderType RENDER_TYPE = ModRenderType.wraith(Goety.location("textures/entity/enderling/fire/0.png"));

        public FireLayer(RenderLayerParent<T, M> p_i50919_1_) {
            super(p_i50919_1_);
        }

        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (!p_116986_.isInvisible()) {
                VertexConsumer vertexconsumer = p_116984_.getBuffer(ModRenderType.wraith(Goety.location("textures/entity/enderling/fire/" + p_116986_.tickCount % 31 + ".png")));
                this.getParentModel().renderToBuffer(p_116983_, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        @Override
        public RenderType renderType() {
            return RENDER_TYPE;
        }
    }
}
