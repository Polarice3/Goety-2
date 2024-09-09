package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.WildfireModel;
import com.Polarice3.Goety.common.entities.neutral.Wildfire;
import com.Polarice3.Goety.config.MobsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class WildfireRenderer extends MobRenderer<Wildfire, WildfireModel<Wildfire>> {
    private static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/blaze/wildfire.png");

   public WildfireRenderer(EntityRendererProvider.Context p_173933_) {
      super(p_173933_, new WildfireModel<>(p_173933_.bakeLayer(ModModelLayer.WILDFIRE)), 0.5F);
      this.addLayer(new ServantBandsLayer(this, p_173933_.getModelSet()));
   }

   protected int getBlockLightLevel(Wildfire p_113910_, BlockPos p_113911_) {
      return 15;
   }

   @Override
   protected void scale(Wildfire pEntity, PoseStack pMatrixStack, float pParticleTicks) {
      pMatrixStack.scale(1.4F, 1.4F, 1.4F);
   }

   public ResourceLocation getTextureLocation(Wildfire p_113908_) {
      return TEXTURE;
   }

   public static class ServantBandsLayer extends RenderLayer<Wildfire, WildfireModel<Wildfire>> {
      private static final ResourceLocation TEXTURES = Goety.location("textures/entity/servants/blaze/wildfire_servant_bands.png");
      private final WildfireModel<Wildfire> layerModel;

      public ServantBandsLayer(RenderLayerParent<Wildfire, WildfireModel<Wildfire>> p_i50919_1_, EntityModelSet p_174555_) {
         super(p_i50919_1_);
         this.layerModel = new WildfireModel<>(p_174555_.bakeLayer(ModModelLayer.WILDFIRE));
      }

      @Override
      public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, Wildfire pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
         if (MobsConfig.WildfireTexture.get() && !pLivingEntity.isHostile()) {
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, TEXTURES, pMatrixStack, pBuffer, pPackedLight, pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch, pPartialTicks, 1.0F, 1.0F, 1.0F);
         }
      }
   }

}