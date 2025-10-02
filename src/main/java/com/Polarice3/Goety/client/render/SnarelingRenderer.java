package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.SnarelingModel;
import com.Polarice3.Goety.common.entities.neutral.ender.AbstractSnareling;
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

public class SnarelingRenderer<T extends AbstractSnareling> extends MobRenderer<T, SnarelingModel<T>> {
    private static final ResourceLocation TEXTURE_LOCATION = Goety.location("textures/entity/enderling/snareling.png");
    private static final ResourceLocation SERVANT_LOCATION = Goety.location("textures/entity/enderling/servants/snareling.png");

    public SnarelingRenderer(EntityRendererProvider.Context p_i47208_1_) {
        super(p_i47208_1_, new SnarelingModel<>(p_i47208_1_.bakeLayer(ModModelLayer.SNARELING)), 0.5F);
        this.addLayer(new GlowLayer<>(this));
    }

    public ResourceLocation getTextureLocation(T pEntity) {
        if (pEntity.isHostile() || !MobsConfig.SnarelingServantTexture.get()) {
            return TEXTURE_LOCATION;
        }
        return SERVANT_LOCATION;
    }

    public static class GlowLayer<T extends AbstractSnareling, M extends SnarelingModel<T>> extends EyesLayer<T, M> {
        private static final RenderType RENDER_TYPE = ModRenderType.wraith(Goety.location("textures/entity/enderling/snareling_glow.png"));
        private static final RenderType SERVANT_TYPE = RenderType.eyes(Goety.location("textures/entity/enderling/servants/snareling_glow.png"));

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
            if (pEntity.isHostile() || !MobsConfig.SnarelingServantTexture.get()) {
                return this.renderType();
            }
            return SERVANT_TYPE;
        }

        @Override
        public RenderType renderType() {
            return RENDER_TYPE;
        }
    }
}
