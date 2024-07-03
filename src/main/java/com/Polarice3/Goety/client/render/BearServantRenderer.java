package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.BearServantModel;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.BearServant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class BearServantRenderer extends MobRenderer<BearServant, BearServantModel<BearServant>> {
   private static final ResourceLocation BEAR_LOCATION = Goety.location("textures/entity/servants/bear_servant/bear_servant.png");
   private static final ResourceLocation CAVE_BEAR_LOCATION = Goety.location("textures/entity/servants/bear_servant/cave_bear_servant.png");
   private static final ResourceLocation POLAR_BEAR_LOCATION = Goety.location("textures/entity/servants/bear_servant/polar_bear_servant.png");

   public BearServantRenderer(EntityRendererProvider.Context p_174356_) {
      super(p_174356_, new BearServantModel<>(p_174356_.bakeLayer(ModModelLayer.BEAR)), 0.9F);
      this.addLayer(new RuneLayer<>(this, p_174356_.getModelSet()));
   }

   public ResourceLocation getTextureLocation(BearServant p_115732_) {
      if (p_115732_.getType() == ModEntityType.POLAR_BEAR_SERVANT.get()){
         return POLAR_BEAR_LOCATION;
      } else if (p_115732_.isCave()){
         return CAVE_BEAR_LOCATION;
      }
      return BEAR_LOCATION;
   }

   protected void scale(BearServant p_115734_, PoseStack p_115735_, float p_115736_) {
      p_115735_.scale(1.2F, 1.2F, 1.2F);
      super.scale(p_115734_, p_115735_, p_115736_);
   }

   public static class RuneLayer<T extends BearServant, M extends BearServantModel<T>> extends RenderLayer<T, M> {
      private static final ResourceLocation TEXTURES = Goety.location("textures/entity/servants/bear_servant/bear_servant_runes.png");
      private final BearServantModel<T> layerModel;

      public RuneLayer(RenderLayerParent<T, M> p_117346_, EntityModelSet p_174555_) {
         super(p_117346_);
         this.layerModel = new BearServantModel<>(p_174555_.bakeLayer(ModModelLayer.BEAR));
      }

      @Override
      public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T bear, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
         if (bear.isUpgraded()) {
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, TEXTURES, matrixStackIn, bufferIn, packedLightIn, bear, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
         }
      }
   }
}