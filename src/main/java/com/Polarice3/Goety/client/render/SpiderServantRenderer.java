package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ally.spider.SpiderServant;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class SpiderServantRenderer<T extends SpiderServant> extends MobRenderer<T, SpiderModel<T>> {
   private static final ResourceLocation SPIDER_LOCATION = Goety.location("textures/entity/servants/spider/spider_servant.png");

   public SpiderServantRenderer(EntityRendererProvider.Context p_174401_) {
      this(p_174401_, ModelLayers.SPIDER);
   }

   public SpiderServantRenderer(EntityRendererProvider.Context p_174403_, ModelLayerLocation p_174404_) {
      super(p_174403_, new SpiderModel<>(p_174403_.bakeLayer(p_174404_)), 0.8F);
      this.addLayer(new SpiderEyesLayer<>(this));
   }

   protected float getFlipDegrees(T p_116011_) {
      return 180.0F;
   }

   public ResourceLocation getTextureLocation(T p_116009_) {
      return SPIDER_LOCATION;
   }

   public static class SpiderEyesLayer<T extends Entity, M extends SpiderModel<T>> extends EyesLayer<T, M> {
      private static final RenderType SPIDER_EYES = RenderType.eyes(Goety.location("textures/entity/servants/spider/spider_servant_eyes.png"));

      public SpiderEyesLayer(RenderLayerParent<T, M> p_117507_) {
         super(p_117507_);
      }

      public RenderType renderType() {
         return SPIDER_EYES;
      }
   }
}