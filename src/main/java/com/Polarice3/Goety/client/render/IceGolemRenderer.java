package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.IceGolemModel;
import com.Polarice3.Goety.common.entities.ally.IceGolem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class IceGolemRenderer<T extends IceGolem> extends MobRenderer<T, IceGolemModel<T>> {
    private static final ResourceLocation TEXTURES = Goety.location("textures/entity/servants/ice_golem.png");

    public IceGolemRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new IceGolemModel<>(renderManagerIn.bakeLayer(ModModelLayer.ICE_GOLEM)), 1.0F);
        this.addLayer(new IceGolemFrostLayer<>(this, renderManagerIn.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURES;
    }

    public static class IceGolemFrostLayer<T extends IceGolem, M extends IceGolemModel<T>> extends RenderLayer<T, M> {
        private static final ResourceLocation TEXTURES = Goety.location("textures/entity/servants/ice_golem_spikes.png");
        private final IceGolemModel<T> layerModel;

        public IceGolemFrostLayer(RenderLayerParent<T, M> p_117346_, EntityModelSet p_174555_) {
            super(p_117346_);
            this.layerModel = new IceGolemModel<>(p_174555_.bakeLayer(ModModelLayer.ICE_GOLEM));
        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T golem, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (golem.isUpgraded()) {
                coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, TEXTURES, matrixStackIn, bufferIn, packedLightIn, golem, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
