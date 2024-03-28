package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MobsConfig;
import com.Polarice3.Goety.client.render.model.WhispererModel;
import com.Polarice3.Goety.common.entities.ally.Wavewhisperer;
import com.Polarice3.Goety.common.entities.ally.Whisperer;
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

public class WhispererRenderer<T extends Whisperer> extends MobRenderer<T, WhispererModel<T>> {
    private static final ResourceLocation TEXTURE_LOCATION = Goety.location("textures/entity/servants/whisperer/whisperer.png");
    private static final ResourceLocation ORIGINAL = Goety.location("textures/entity/servants/whisperer/whisperer_original.png");
    private static final ResourceLocation WAVE = Goety.location("textures/entity/servants/whisperer/wavewhisperer.png");

    public WhispererRenderer(EntityRendererProvider.Context p_i47208_1_) {
        super(p_i47208_1_, new WhispererModel<>(p_i47208_1_.bakeLayer(ModModelLayer.WHISPERER)), 0.5F);
        this.addLayer(new GlowLayer<>(this));
    }

    public ResourceLocation getTextureLocation(T pEntity) {
        if (pEntity instanceof Wavewhisperer){
            return WAVE;
        } else if (pEntity.isHostile() || !MobsConfig.WhispererTexture.get()){
            return ORIGINAL;
        } else {
            return TEXTURE_LOCATION;
        }
    }

    public static class GlowLayer<T extends Whisperer, M extends WhispererModel<T>> extends EyesLayer<T, M> {
        private static final RenderType RENDER_TYPE = RenderType.eyes(Goety.location("textures/entity/servants/whisperer/whisperer_glow.png"));
        private static final RenderType ORIGINAL = RenderType.eyes(Goety.location("textures/entity/servants/whisperer/whisperer_original_glow.png"));
        private static final RenderType WAVE = RenderType.eyes(Goety.location("textures/entity/servants/whisperer/wavewhisperer_glow.png"));

        public GlowLayer(RenderLayerParent<T, M> p_i50919_1_) {
            super(p_i50919_1_);
        }

        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (!p_116986_.isInvisible()) {
                VertexConsumer vertexconsumer = p_116984_.getBuffer(this.renderType());
                if (p_116986_ instanceof Wavewhisperer){
                    vertexconsumer = p_116984_.getBuffer(WAVE);
                } else if (p_116986_.isHostile() || !MobsConfig.WhispererTexture.get()) {
                    vertexconsumer = p_116984_.getBuffer(ORIGINAL);
                }
                this.getParentModel().renderToBuffer(p_116983_, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        @Override
        public RenderType renderType() {
            return RENDER_TYPE;
        }
    }
}
