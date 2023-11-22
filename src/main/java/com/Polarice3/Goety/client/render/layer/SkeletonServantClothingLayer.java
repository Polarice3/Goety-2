package com.Polarice3.Goety.client.render.layer;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.common.entities.ally.AbstractSkeletonServant;
import com.Polarice3.Goety.common.entities.ally.StrayServant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class SkeletonServantClothingLayer<T extends AbstractSkeletonServant, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation TEXTURES = new ResourceLocation(Goety.MOD_ID, "textures/entity/servants/skeleton_servant_overlay.png");
    private static final ResourceLocation STRAY = new ResourceLocation("textures/entity/skeleton/stray_overlay.png");
    private final SkeletonModel<T> layerModel;

    public SkeletonServantClothingLayer(RenderLayerParent<T, M> p_i50919_1_, EntityModelSet p_174555_) {
        super(p_i50919_1_);
        this.layerModel = new SkeletonModel<>(p_174555_.bakeLayer(ModelLayers.SKELETON));
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if ((!entitylivingbaseIn.isHostile() && MainConfig.ServantBands.get()) || entitylivingbaseIn instanceof StrayServant) {
            ResourceLocation resourceLocation = TEXTURES;
            if (entitylivingbaseIn instanceof StrayServant) {
                resourceLocation = STRAY;
            }
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, resourceLocation, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
        }

    }
}
