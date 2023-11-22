package com.Polarice3.Goety.client.render.layer;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.model.ZPiglinModel;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class ZPiglinBandsLayer<T extends Summoned> extends RenderLayer<T, ZPiglinModel<T>> {
    private static final ResourceLocation TEXTURES = Goety.location("textures/entity/servants/zpiglin_servant_bands.png");
    private final ZPiglinModel<T> layerModel;

    public ZPiglinBandsLayer(RenderLayerParent<T, ZPiglinModel<T>> p_i50919_1_, EntityModelSet p_174555_) {
        super(p_i50919_1_);
        this.layerModel = new ZPiglinModel<>(p_174555_.bakeLayer(ModModelLayer.ZPIGLIN_SERVANT));
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entitylivingbaseIn.getTrueOwner() instanceof Player && MainConfig.ServantBands.get()) {
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, TEXTURES, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
        }
    }
}
