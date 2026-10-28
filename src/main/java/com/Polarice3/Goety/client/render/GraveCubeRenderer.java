package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.RedstoneCubeModel;
import com.Polarice3.Goety.common.entities.ally.undead.GraveCube;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class GraveCubeRenderer extends MobRenderer<GraveCube, RedstoneCubeModel<GraveCube>> {
    private static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/grave_monstrosity/grave_cube.png");

    public GraveCubeRenderer(EntityRendererProvider.Context p_174435_) {
        super(p_174435_, new RedstoneCubeModel<>(p_174435_.bakeLayer(ModModelLayer.REDSTONE_CUBE)), 0.5F);
        this.addLayer(new CubeEyesLayer<>(this));
    }

    public ResourceLocation getTextureLocation(GraveCube p_116292_) {
        return TEXTURE;
    }

    public static class CubeEyesLayer<T extends GraveCube, M extends RedstoneCubeModel<T>> extends EyesLayer<T, M> {
        private static final RenderType EYES = RenderType.eyes(Goety.location("textures/entity/servants/grave_monstrosity/grave_cube_eyes.png"));

        public CubeEyesLayer(RenderLayerParent<T, M> p_117507_) {
            super(p_117507_);
        }

        public RenderType renderType() {
            return EYES;
        }
    }
}
