package com.Polarice3.Goety.client.render.layer;

import com.Polarice3.Goety.Goety;
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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;

public class StrayServantBandsLayer<T extends Mob & RangedAttackMob, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation TEXTURES = Goety.location("textures/entity/servants/stray_servant_bands.png");
    private final SkeletonModel<T> layerModel;

    public StrayServantBandsLayer(RenderLayerParent<T, M> p_i50919_1_, EntityModelSet p_174555_) {
        super(p_i50919_1_);
        this.layerModel = new SkeletonModel<>(p_174555_.bakeLayer(ModelLayers.SKELETON));
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entitylivingbaseIn instanceof StrayServant) {
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, TEXTURES, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
        }
    }
}
