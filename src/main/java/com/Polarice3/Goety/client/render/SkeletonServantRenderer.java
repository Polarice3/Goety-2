package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MobsConfig;
import com.Polarice3.Goety.client.render.layer.SkeletonServantClothingLayer;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.AbstractSkeletonServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.MossySkeletonServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.StrayServant;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class SkeletonServantRenderer extends HumanoidMobRenderer<AbstractSkeletonServant, SkeletonModel<AbstractSkeletonServant>> {
   private static final ResourceLocation TEXTURES = Goety.location("textures/entity/servants/skeleton/skeleton_servant.png");
   private static final ResourceLocation SKELETON_LOCATION = new ResourceLocation("textures/entity/skeleton/skeleton.png");
   private static final ResourceLocation STRAY = Goety.location("textures/entity/servants/skeleton/stray_servant.png");
   private static final ResourceLocation STRAY_ORIGINAL = new ResourceLocation("textures/entity/skeleton/stray.png");
   private static final ResourceLocation MOSSY = Goety.location("textures/entity/servants/skeleton/mossy_skeleton_servant.png");
   private static final ResourceLocation MOSSY_ORIGINAL = Goety.location("textures/entity/servants/skeleton/mossy_skeleton.png");

   public SkeletonServantRenderer(EntityRendererProvider.Context p_174380_) {
      this(p_174380_, ModelLayers.SKELETON, ModelLayers.SKELETON_INNER_ARMOR, ModelLayers.SKELETON_OUTER_ARMOR);
   }

   public SkeletonServantRenderer(EntityRendererProvider.Context p_174382_, ModelLayerLocation p_174383_, ModelLayerLocation p_174384_, ModelLayerLocation p_174385_) {
      super(p_174382_, new SkeletonModel<>(p_174382_.bakeLayer(p_174383_)), 0.5F);
      this.addLayer(new HumanoidArmorLayer<>(this, new SkeletonModel<>(p_174382_.bakeLayer(p_174384_)), new SkeletonModel<>(p_174382_.bakeLayer(p_174385_))));
      this.addLayer(new SkeletonServantClothingLayer<>(this, p_174382_.getModelSet()));
   }

   public ResourceLocation getTextureLocation(AbstractSkeletonServant servant) {
      if (servant instanceof MossySkeletonServant){
         if (servant.isHostile() || !MobsConfig.MossySkeletonServantTexture.get()){
            return MOSSY_ORIGINAL;
         }
         return MOSSY;
      } else if (servant instanceof StrayServant){
         if (servant.isHostile() || !MobsConfig.StrayServantTexture.get()){
            return STRAY_ORIGINAL;
         }
         return STRAY;
      } else if (servant.isHostile() || !MobsConfig.SkeletonServantTexture.get()){
         return SKELETON_LOCATION;
      } else {
         return TEXTURES;
      }
   }

   protected boolean isShaking(AbstractSkeletonServant p_174389_) {
      return p_174389_.isShaking();
   }
}