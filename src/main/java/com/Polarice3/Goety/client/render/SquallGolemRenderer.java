package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.SquallGolemModel;
import com.Polarice3.Goety.common.entities.ally.golem.SquallGolem;
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

public class SquallGolemRenderer<T extends SquallGolem> extends MobRenderer<T, SquallGolemModel<T>> {
    private static final ResourceLocation TEXTURES = Goety.location("textures/entity/servants/squall_golem/squall_golem.png");
    private static final ResourceLocation HOSTILE = Goety.location("textures/entity/servants/squall_golem/squall_golem_hostile.png");

    public SquallGolemRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new SquallGolemModel<>(renderManagerIn.bakeLayer(ModModelLayer.SQUALL_GOLEM)), 1.0F);
        this.addLayer(new SquallGolemGlowLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        if (MobsConfig.SquallGolemTexture.get() && !entity.isHostile()) {
            return TEXTURES;
        }
        return HOSTILE;
    }

    public static class SquallGolemGlowLayer<T extends SquallGolem, M extends SquallGolemModel<T>> extends EyesLayer<T, M> {
        private static final ResourceLocation GLOW = Goety.location("textures/entity/servants/squall_golem/squall_golem_glow.png");
        private static final ResourceLocation GLOW_HOSTILE = Goety.location("textures/entity/servants/squall_golem/squall_golem_hostile_glow.png");

        public SquallGolemGlowLayer(RenderLayerParent<T, M> p_116981_) {
            super(p_116981_);
        }

        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (p_116986_.isStartingUp() || p_116986_.isActivated()){
                VertexConsumer vertexconsumer = p_116984_.getBuffer(RenderType.eyes(GLOW_HOSTILE));
                if (MobsConfig.SquallGolemTexture.get() && !p_116986_.isHostile()){
                    vertexconsumer = p_116984_.getBuffer(this.renderType());
                }
                this.getParentModel().renderToBuffer(p_116983_, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        @Override
        public RenderType renderType() {
            return RenderType.eyes(GLOW);
        }
    }
}
