package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MobsConfig;
import com.Polarice3.Goety.client.render.model.LeapleafModel;
import com.Polarice3.Goety.common.entities.ally.Leapleaf;
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

public class LeapleafRenderer<T extends Leapleaf> extends MobRenderer<T, LeapleafModel<T>> {
    private static final ResourceLocation TEXTURE_LOCATION = Goety.location("textures/entity/servants/leapleaf/leapleaf.png");
    private static final ResourceLocation ORIGINAL = Goety.location("textures/entity/servants/leapleaf/leapleaf_original.png");

    public LeapleafRenderer(EntityRendererProvider.Context p_i47208_1_) {
        super(p_i47208_1_, new LeapleafModel<>(p_i47208_1_.bakeLayer(ModModelLayer.LEAPLEAF)), 1.0F);
        this.addLayer(new GlowLayer<>(this));
    }

    public ResourceLocation getTextureLocation(T pEntity) {
        if (pEntity.isHostile() || !MobsConfig.LeapleafTexture.get()){
            return ORIGINAL;
        }
        return TEXTURE_LOCATION;
    }

    public static class GlowLayer<T extends Leapleaf, M extends LeapleafModel<T>> extends EyesLayer<T, M> {
        private static final RenderType RENDER_TYPE = RenderType.eyes(Goety.location("textures/entity/servants/leapleaf/leapleaf_glow.png"));
        private static final RenderType ORIGINAL = RenderType.eyes(Goety.location("textures/entity/servants/leapleaf/leapleaf_glow_original.png"));

        public GlowLayer(RenderLayerParent<T, M> p_i50919_1_) {
            super(p_i50919_1_);
        }

        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (!p_116986_.isInvisible()) {
                VertexConsumer vertexconsumer = p_116984_.getBuffer(this.renderType());
                if (p_116986_.isHostile() || !MobsConfig.LeapleafTexture.get()) {
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
