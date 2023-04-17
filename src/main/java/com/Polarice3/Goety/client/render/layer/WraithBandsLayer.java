package com.Polarice3.Goety.client.render.layer;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.model.WraithModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

public class WraithBandsLayer<T extends Mob> extends RenderLayer<T, WraithModel<T>> {
    private static final ResourceLocation TEXTURES = new ResourceLocation(Goety.MOD_ID, "textures/entity/wraith_servant_bands.png");
    private final WraithModel<T> layerModel;

    public WraithBandsLayer(RenderLayerParent<T, WraithModel<T>> p_i50919_1_, EntityModelSet p_174555_) {
        super(p_i50919_1_);
        this.layerModel = new WraithModel<>(p_174555_.bakeLayer(ModModelLayer.WRAITH));
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, TEXTURES, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);

    }
}
