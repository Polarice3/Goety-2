package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.WebSpiderModel;
import com.Polarice3.Goety.common.entities.hostile.WebSpider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class WebSpiderRenderer<T extends WebSpider> extends MobRenderer<T, WebSpiderModel<T>> {
   private static final ResourceLocation SPIDER_LOCATION = Goety.location("textures/entity/servants/spider/web_spider.png");

   public WebSpiderRenderer(EntityRendererProvider.Context p_173946_) {
      super(p_173946_, new WebSpiderModel<>(p_173946_.bakeLayer(ModModelLayer.WEB_SPIDER)), 0.8F);
      this.addLayer(new SpiderEyesLayer<>(this));
   }

   protected float getFlipDegrees(T p_116011_) {
      return 180.0F;
   }

   public ResourceLocation getTextureLocation(WebSpider p_113972_) {
      return SPIDER_LOCATION;
   }

   public static class SpiderEyesLayer<T extends Entity, M extends WebSpiderModel<T>> extends EyesLayer<T, M> {
      private static final RenderType SPIDER_EYES = RenderType.eyes(Goety.location("textures/entity/servants/spider/spider_servant_eyes.png"));

      public SpiderEyesLayer(RenderLayerParent<T, M> p_117507_) {
         super(p_117507_);
      }

      public RenderType renderType() {
         return SPIDER_EYES;
      }
   }
}