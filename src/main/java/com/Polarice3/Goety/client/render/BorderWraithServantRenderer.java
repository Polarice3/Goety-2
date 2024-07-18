package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.layer.WraithBandsLayer;
import com.Polarice3.Goety.client.render.layer.WraithSecretLayer;
import com.Polarice3.Goety.client.render.model.WraithModel;
import com.Polarice3.Goety.common.entities.neutral.AbstractWraith;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

public class BorderWraithServantRenderer<T extends AbstractWraith> extends AbstractWraithRenderer<T> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/wraith/border_wraith.png");

    public BorderWraithServantRenderer(EntityRendererProvider.Context renderManagerIn){
        super(renderManagerIn, new WraithModel<>(renderManagerIn.bakeLayer(ModModelLayer.WRAITH)), 0.5F);
        this.addLayer(new GlowLayer<>(this));
        this.addLayer(new WraithBandsLayer<>(this, renderManagerIn.getModelSet()));
        this.addLayer(new WraithSecretLayer<>(this, renderManagerIn.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }

    public static class GlowLayer<T extends Mob, M extends WraithModel<T>> extends EyesLayer<T, M> {
        private static final RenderType RENDER_TYPE = ModRenderType.wraith(Goety.location("textures/entity/wraith/border_wraith_glow.png"));

        public GlowLayer(RenderLayerParent<T, M> p_i50919_1_) {
            super(p_i50919_1_);
        }

        @Override
        public RenderType renderType() {
            return RENDER_TYPE;
        }
    }

}
