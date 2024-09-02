package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.SpecterModel;
import com.Polarice3.Goety.common.entities.neutral.Specter;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class SpecterRenderer<T extends Specter> extends MobRenderer<T, SpecterModel<T>> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/specter/specter.png");

    public SpecterRenderer(EntityRendererProvider.Context renderManagerIn){
        super(renderManagerIn, new SpecterModel<>(renderManagerIn.bakeLayer(ModModelLayer.SPECTER)), 0.0F);
        this.addLayer(new SpecterBandsLayer<>(this, renderManagerIn.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }

    public static class SpecterBandsLayer<T extends Specter> extends RenderLayer<T, SpecterModel<T>> {
        private static final ResourceLocation TEXTURES = Goety.location("textures/entity/specter/specter_servant_bands.png");
        private final SpecterModel<T> layerModel;

        public SpecterBandsLayer(RenderLayerParent<T, SpecterModel<T>> p_i50919_1_, EntityModelSet p_174555_) {
            super(p_i50919_1_);
            this.layerModel = new SpecterModel<>(p_174555_.bakeLayer(ModModelLayer.SPECTER));
        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T wraith, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!wraith.isHostile()) {
                coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, TEXTURES, matrixStackIn, bufferIn, packedLightIn, wraith, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
            }
        }
    }

}
