package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ally.HoglinServant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HoglinModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class HoglinServantRenderer extends MobRenderer<HoglinServant, HoglinModel<HoglinServant>> {
   private static final ResourceLocation HOGLIN_LOCATION = new ResourceLocation("textures/entity/hoglin/hoglin.png");

   public HoglinServantRenderer(EntityRendererProvider.Context p_174165_) {
      super(p_174165_, new HoglinModel<>(p_174165_.bakeLayer(ModelLayers.HOGLIN)), 0.7F);
      this.addLayer(new BandsLayer<>(this, p_174165_.getModelSet()));
   }

   public ResourceLocation getTextureLocation(HoglinServant p_114862_) {
      return HOGLIN_LOCATION;
   }

   protected boolean isShaking(HoglinServant p_114864_) {
      return super.isShaking(p_114864_) || p_114864_.isConverting();
   }

   public static class BandsLayer<T extends HoglinServant, M extends HoglinModel<T>> extends RenderLayer<T, M> {
      private static final ResourceLocation TEXTURES = Goety.location("textures/entity/servants/hoglin_servant_bands.png");
      private final HoglinModel<T> layerModel;

      public BandsLayer(RenderLayerParent<T, M> p_117346_, EntityModelSet p_174555_) {
         super(p_117346_);
         this.layerModel = new HoglinModel<>(p_174555_.bakeLayer(ModelLayers.HOGLIN));
      }

      @Override
      public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T hoglin, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
         coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, TEXTURES, matrixStackIn, bufferIn, packedLightIn, hoglin, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
      }
   }
}