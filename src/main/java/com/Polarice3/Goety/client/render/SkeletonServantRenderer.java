package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MobsConfig;
import com.Polarice3.Goety.client.render.layer.SkeletonServantClothingLayer;
import com.Polarice3.Goety.client.render.layer.StrayServantBandsLayer;
import com.Polarice3.Goety.common.entities.ally.AbstractSkeletonServant;
import com.Polarice3.Goety.common.entities.ally.StrayServant;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class SkeletonServantRenderer extends HumanoidMobRenderer<AbstractSkeletonServant, SkeletonModel<AbstractSkeletonServant>> {
   private static final ResourceLocation TEXTURES = new ResourceLocation(Goety.MOD_ID, "textures/entity/servants/skeleton_servant.png");
   private static final ResourceLocation SKELETON_LOCATION = new ResourceLocation("textures/entity/skeleton/skeleton.png");
   private static final ResourceLocation STRAY = new ResourceLocation("textures/entity/skeleton/stray.png");

   public SkeletonServantRenderer(EntityRendererProvider.Context p_174380_) {
      this(p_174380_, ModelLayers.SKELETON, ModelLayers.SKELETON_INNER_ARMOR, ModelLayers.SKELETON_OUTER_ARMOR);
   }

   public SkeletonServantRenderer(EntityRendererProvider.Context p_174382_, ModelLayerLocation p_174383_, ModelLayerLocation p_174384_, ModelLayerLocation p_174385_) {
      super(p_174382_, new SkeletonModel<>(p_174382_.bakeLayer(p_174383_)), 0.5F);
      this.addLayer(new HumanoidArmorLayer<>(this, new SkeletonModel(p_174382_.bakeLayer(p_174384_)), new SkeletonModel(p_174382_.bakeLayer(p_174385_)), p_174382_.getModelManager()));
      this.addLayer(new SkeletonServantClothingLayer<>(this, p_174382_.getModelSet()));
      this.addLayer(new StrayServantBandsLayer<>(this, p_174382_.getModelSet()));
   }

   public ResourceLocation getTextureLocation(AbstractSkeletonServant p_115941_) {
      if (p_115941_ instanceof StrayServant){
         return STRAY;
      } else if (p_115941_.isHostile() || !MobsConfig.SkeletonServantTexture.get()){
         return SKELETON_LOCATION;
      } else {
         return TEXTURES;
      }
   }

   protected boolean isShaking(AbstractSkeletonServant p_174389_) {
      return p_174389_.isShaking();
   }
}