package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.neutral.BlazeServant;
import com.Polarice3.Goety.config.MobsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.BlazeModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class BlazeServantRenderer extends MobRenderer<BlazeServant, BlazeModel<BlazeServant>> {
    private static final ResourceLocation ORIGINAL = new ResourceLocation("textures/entity/blaze.png");

   public BlazeServantRenderer(EntityRendererProvider.Context p_173933_) {
      super(p_173933_, new BlazeModel<>(p_173933_.bakeLayer(ModelLayers.BLAZE)), 0.5F);
      this.addLayer(new ServantBandsLayer(this, p_173933_.getModelSet()));
   }

   protected int getBlockLightLevel(BlazeServant p_113910_, BlockPos p_113911_) {
      return 15;
   }

   public ResourceLocation getTextureLocation(BlazeServant p_113908_) {
      return ORIGINAL;
   }

   public static class ServantBandsLayer extends RenderLayer<BlazeServant, BlazeModel<BlazeServant>> {
      private static final ResourceLocation TEXTURES = Goety.location("textures/entity/servants/blaze_servant_bands.png");
      private final BlazeModel<BlazeServant> layerModel;

      public ServantBandsLayer(RenderLayerParent<BlazeServant, BlazeModel<BlazeServant>> p_i50919_1_, EntityModelSet p_174555_) {
         super(p_i50919_1_);
         this.layerModel = new BlazeModel<>(p_174555_.bakeLayer(ModelLayers.BLAZE));
      }

      @Override
      public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, BlazeServant pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
         if (MobsConfig.BlazeServantTexture.get() && !pLivingEntity.isHostile()) {
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, TEXTURES, pMatrixStack, pBuffer, pPackedLight, pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch, pPartialTicks, 1.0F, 1.0F, 1.0F);
         }
      }
   }
}