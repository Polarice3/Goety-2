package com.Polarice3.Goety.client.render.layer;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.TormentorModel;
import com.Polarice3.Goety.common.entities.hostile.illagers.Tormentor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class TormentorVisageLayer<T extends Tormentor, M extends TormentorModel<T>> extends EyesLayer<T, M> {
    private static final RenderType VISAGE = RenderType.eyes(new ResourceLocation(Goety.MOD_ID, "textures/entity/illagers/tormentor_visage.png"));

    public TormentorVisageLayer(RenderLayerParent<T, M> p_i50921_1_) {
        super(p_i50921_1_);
    }

    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entitylivingbaseIn.isCharging()) {
            super.render(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }

    }

    public RenderType renderType() {
        return VISAGE;
    }
}