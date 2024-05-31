package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.ModGhastModel;
import com.Polarice3.Goety.common.entities.ally.GhastServant;
import com.Polarice3.Goety.config.MobsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class GhastServantRenderer extends MobRenderer<GhastServant, ModGhastModel<GhastServant>> {
    private static final ResourceLocation GHAST_LOCATION = new ResourceLocation("textures/entity/ghast/ghast.png");
    private static final ResourceLocation GHAST_SHOOTING_LOCATION = new ResourceLocation("textures/entity/ghast/ghast_shooting.png");

    public GhastServantRenderer(EntityRendererProvider.Context p_i50961_1_) {
        super(p_i50961_1_, new ModGhastModel<>(p_i50961_1_.bakeLayer(ModModelLayer.MALGHAST)), 1.0F);
        this.addLayer(new ServantBandsLayer(this, p_i50961_1_.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(GhastServant pEntity) {
        if (pEntity.isCharging()){
            return GHAST_SHOOTING_LOCATION;
        }
        return GHAST_LOCATION;
    }

    protected void scale(GhastServant pLivingEntity, PoseStack pMatrixStack, float pPartialTickTime) {
        float f = pLivingEntity.getSwelling(pPartialTickTime);
        if (f < 0.0F) {
            f = 0.0F;
        }
        f = 1.0F / (f * f * f * f * f * 2.0F + 1.0F);
        float var5 = (8.0F + f) / 2.0F;
        float var6 = (8.0F + 1.0F / f) / 2.0F;
        pMatrixStack.scale(var6, var5, var6);
    }

    public static class ServantBandsLayer extends RenderLayer<GhastServant, ModGhastModel<GhastServant>> {
        private static final ResourceLocation TEXTURES = Goety.location("textures/entity/servants/ghast_servant_bands.png");
        private final ModGhastModel<GhastServant> layerModel;

        public ServantBandsLayer(RenderLayerParent<GhastServant, ModGhastModel<GhastServant>> p_i50919_1_, EntityModelSet p_174555_) {
            super(p_i50919_1_);
            this.layerModel = new ModGhastModel<>(p_174555_.bakeLayer(ModModelLayer.MALGHAST));
        }

        @Override
        public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, GhastServant pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            if (MobsConfig.GhastServantTexture.get() && !pLivingEntity.isHostile()) {
                coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, TEXTURES, pMatrixStack, pBuffer, pPackedLight, pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch, pPartialTicks, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
