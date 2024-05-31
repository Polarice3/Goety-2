package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.layer.SkeletonServantClothingLayer;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.AbstractSkeletonServant;
import com.Polarice3.Goety.config.MobsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class WitherSkeletonServantRenderer extends HumanoidMobRenderer<AbstractSkeletonServant, SkeletonModel<AbstractSkeletonServant>> {
   private static final ResourceLocation TEXTURES = Goety.location("textures/entity/servants/skeleton/wither_skeleton_servant.png");
   private static final ResourceLocation SKELETON_LOCATION = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");

   public WitherSkeletonServantRenderer(EntityRendererProvider.Context p_174380_) {
      this(p_174380_, ModelLayers.WITHER_SKELETON, ModelLayers.WITHER_SKELETON_INNER_ARMOR, ModelLayers.WITHER_SKELETON_OUTER_ARMOR);
   }

   public WitherSkeletonServantRenderer(EntityRendererProvider.Context p_174382_, ModelLayerLocation p_174383_, ModelLayerLocation p_174384_, ModelLayerLocation p_174385_) {
      super(p_174382_, new SkeletonModel<>(p_174382_.bakeLayer(p_174383_)), 0.5F);
      this.addLayer(new HumanoidArmorLayer<>(this, new SkeletonModel<>(p_174382_.bakeLayer(p_174384_)), new SkeletonModel<>(p_174382_.bakeLayer(p_174385_)), p_174382_.getModelManager()));
      this.addLayer(new SkeletonServantClothingLayer<>(this, p_174382_.getModelSet()));
   }

   public ResourceLocation getTextureLocation(AbstractSkeletonServant servant) {
      if (servant.isHostile() || !MobsConfig.WitherSkeletonServantTexture.get()){
         return SKELETON_LOCATION;
      } else {
         return TEXTURES;
      }
   }

   protected void scale(AbstractSkeletonServant p_116460_, PoseStack p_116461_, float p_116462_) {
      p_116461_.scale(1.2F, 1.2F, 1.2F);
   }
}