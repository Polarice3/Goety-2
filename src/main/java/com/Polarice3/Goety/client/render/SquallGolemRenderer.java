package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.SquallGolemModel;
import com.Polarice3.Goety.common.entities.ally.SquallGolem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class SquallGolemRenderer<T extends SquallGolem> extends MobRenderer<T, SquallGolemModel<T>> {
    private static final ResourceLocation TEXTURES = Goety.location("textures/entity/servants/squall_golem/squall_golem.png");

    public SquallGolemRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new SquallGolemModel<>(renderManagerIn.bakeLayer(ModModelLayer.SQUALL_GOLEM)), 1.0F);
        this.addLayer(new SquallGolemGlowLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURES;
    }

    public static class SquallGolemGlowLayer<T extends SquallGolem, M extends SquallGolemModel<T>> extends EyesLayer<T, M> {
        private static final ResourceLocation GLOW = Goety.location("textures/entity/servants/squall_golem/squall_golem_glow.png");

        public SquallGolemGlowLayer(RenderLayerParent<T, M> p_116981_) {
            super(p_116981_);
        }

        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (p_116986_.isStartingUp() || p_116986_.isActivated()){
                super.render(p_116983_, p_116984_, p_116985_, p_116986_, p_116987_, p_116988_, p_116989_, p_116990_, p_116991_, p_116992_);
            }
        }

        @Override
        public RenderType renderType() {
            return RenderType.eyes(GLOW);
        }
    }
}
